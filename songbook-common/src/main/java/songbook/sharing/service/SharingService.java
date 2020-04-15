package songbook.sharing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.cloud.repository.CloudDao;
import songbook.content.service.ContentService;
import songbook.content.service.ContentServiceException;
import songbook.sharing.provider.MailSender;
import songbook.sharing.provider.MailSenderException;
import songbook.song.entity.SongContent;
import songbook.song.service.SongService;
import songbook.user.entity.User;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.TmpResourceResolver;
import songbook.util.file.entity.FileHolder;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SharingService {

    @Autowired
    TmpResourceResolver tmpResourceResolver;

    @Autowired
    SongService songService;

    @Autowired
    CloudDao cloudDao;

    @Autowired
    ContentService contentService;

    @Autowired
    MailSender mailSender;

    public void shareSongContentViaMail(User user, String recipients, List<SongContent> contents, String subject, String mailBody, boolean isEmbedContent, boolean isAddSequenceToFileNames) throws SharingServiceException {

        List<FileHolder> files;
        List<SongContent> unprocessedContent = new ArrayList<>();

        if (isEmbedContent) {

            TmpDirStorage storage;

            try {
                storage = contentService.downloadSongContent(contents);
            } catch(ContentServiceException e) {
                throw new SharingServiceException("Unable to download content files due to an exception", e);
            }

            files = storage.getFiles();

            // UT: 1 (that we can dustinguish unprocessed files)
            // THINK: is it right method, to compare by file name?
            for (SongContent content : contents) {
                FileHolder foundFile = files.stream().filter(curFileHolder ->
                        (curFileHolder.getOriginalName() != null && content.getFileName() != null) &&
                        curFileHolder.getOriginalName().equals(content.getFileName())
                ).findAny().orElse(null);

                if (foundFile == null) {
                    unprocessedContent.add(content);
                }
            }
            // ... and, write another IT for this, with cloud files and youtube links :)

        } else {
            files = new ArrayList<>();
            unprocessedContent = contents;
        }

        // now, rename files in storage, if such option has been given
        if(isAddSequenceToFileNames) {
            addSequenceToFileNames(files);
        }

        // now, process placeholder in mailBody
        mailBody = processFilesPlaceholder(unprocessedContent, mailBody);

        // then,

        // and, then, call mailSender
        MimeMessage message;

        try {
            message = mailSender.createContentMail(recipients, subject, mailBody, user, files);
        } catch (MailSenderException e) {
            throw new SharingServiceException("Unable to create content mail due to exception", e);
        }

        this.sendMail(message);
    }

    public String processFilesPlaceholder(List<SongContent> contents, String mailBody) {
        if (mailBody.contains(getFileListPlaceholder())) {
            return mailBody.replaceFirst(getFileListPlaceholder(), getContentLinks(contents));
        } else {
            return mailBody + "\n\n" + getContentLinks(contents);
        }
    }

    public String getContentLink(SongContent content) {
        switch(content.getType()) {
            case LINK:
            default:
                return content.getContent();
            case GDRIVE_CLOUD_FILE:
                return getGdriveLinkPrefix() + content.getContent();
        }
    }

    public void addSequenceToFileNames(List<FileHolder> files) {
        int counter = 1;
        // Pattern pattern = Pattern.compile();
        for(FileHolder fileHolder : files) {
            String newName = fileHolder.getOriginalName();
            newName = newName.replaceFirst("^\\s*\\d+\\.\\s*", "");
            newName = counter++ + ". " + newName;
            fileHolder.setOriginalName(newName);
        }
    }

    public String getContentLinks(List<SongContent> contents) {
        List<String> lines = new ArrayList<>();

        for (SongContent content : contents) {
            lines.add(getContentLink(content));
        }

        return String.join("\n", lines);
    }

    public void sendMail(MimeMessage message) throws SharingServiceException {
        Transport transport;

        try {
            transport = mailSender.getMailSession().getTransport();
        } catch (NoSuchProviderException e) {
            throw new SharingServiceException("Unable to obtain the transport, the mail session threw an exception", e);
        }

        try {
            transport.connect();
            Transport.send(message);
            transport.close();
        } catch(MessagingException e) {
            throw new SharingServiceException("Unable to send the message, an exception has occurred", e);
        }
    }

    private String getGdriveLinkPrefix() {
        return "https://drive.google.com/open?id=";
    }

    private String getFileListPlaceholder() {
        return "%fileList%";
    }


}
