package songbook.sharing.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import songbook.cloud.repository.CloudDao;
import songbook.content.service.ContentService;
import songbook.content.service.ContentServiceException;
import songbook.sharing.provider.MailSender;
import songbook.sharing.provider.MailSenderException;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.domain.user.entity.User;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.entity.FileHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@ExtendWith(MockitoExtension.class)
public class SharingServiceTest {

    @Mock
    SongService songService;

    @Mock
    CloudDao cloudDao;

    @Mock
    ContentService contentService;

    @Mock
    MailSender mailSender;

    @Spy
    @InjectMocks
    // @Mock
    SharingService sharingService;

    // To @DisplayName work, you should turn on this option in Settings -> build, execution and deployment -> Build Tools -> Gradle -> run tests using IDEA
    // https://medium.com/@sorravitbunjongpean/fix-junit5-display-name-did-not-show-in-run-tab-intellij-a00c94f39679
    // and then, probably, delete .idea and reload the project
    // BTW, tests run faster then

    @Test
    @DisplayName("isEmbedContent argument should be processed when sharing song contents via mail")
    void isEmbedContentArgShouldBeProcessedWhenSharingSongContentsViaMail() throws ContentServiceException, SharingServiceException {
        // verify these dependants (check that we will call contentService.downloadSongContent)
        User user1 = new User();
        user1.setEmail("nobody@example.com");
        String subject = "Subject";
        String body = "mail body";
        String recipients = "nobody@example.com";

        List<SongContent> contents = getGdriveCloudSongContent();

        TmpDirStorage storage = createMockStorage(contents);
        when(contentService.downloadSongContent(any())).thenReturn(storage);

        sharingService.shareSongContentViaMail(user1, recipients, contents, subject, body, true, false);
        verify(contentService).downloadSongContent(any());
    }

    @Test
    void allContentShouldAddedWhenTheresNoIsEmbedOptionsWhenSharingSongContentsViaMail() throws SharingServiceException {
        // verify these dependants (check arguments of processFilesPlaceholders)
        User user1 = new User();
        user1.setEmail("nobody@example.com");
        String subject = "Subject";
        String body = "mail body";
        String recipients = "nobody@example.com";

        List<SongContent> gdriveSongContent = getGdriveCloudSongContent();
        List<SongContent> linkSongContent = getLinksSongContent();

        List<SongContent> contents = Stream.concat(gdriveSongContent.stream(), linkSongContent.stream())
                .collect(Collectors.toList());

        sharingService.shareSongContentViaMail(user1, recipients, contents, subject, body, false, false);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<SongContent>> songContentArgCaptor
                = ArgumentCaptor.forClass((Class) List.class);

        verify(sharingService).processFilesPlaceholder(songContentArgCaptor.capture(), any());

        List<SongContent> capturedFileHolders = songContentArgCaptor.getValue();

        assertEquals(contents.size(), capturedFileHolders.size(), "The passed files to be inserted as links in mail body is wrong");
    }

    @Test
    void isAddSequenceToFileNamesArgShouldBeProcessedWhenSharingSongContentsViaMail() throws SharingServiceException {
        // verify these dependants
        // verify these dependants (check arguments of processFilesPlaceholders)
        User user1 = new User();
        user1.setEmail("nobody@example.com");
        String subject = "Subject";
        String body = "mail body";
        String recipients = "nobody@example.com";

        List<SongContent> gdriveSongContent = getGdriveCloudSongContent();
        List<SongContent> linkSongContent = getLinksSongContent();

        List<SongContent> contents = Stream.concat(gdriveSongContent.stream(), linkSongContent.stream())
                .collect(Collectors.toList());

        sharingService.shareSongContentViaMail(user1, recipients, contents, subject, body, false, true);

        verify(sharingService).addSequenceToFileNames(any());
    }


    @Test
    @DisplayName("Mail body should be processed and mail should be created and sent when sharing song content via mail")
    void mailBodyShouldBeProcessedAndMailCreatedAndSentWhenSharingSongContentsViaMail() throws SharingServiceException, MailSenderException, MessagingException {
        // verify these dependants (check arguments of processFilesPlaceholders)
        User user1 = new User();
        user1.setEmail("nobody@example.com");
        String subject = "Subject";
        String body = "mail body";
        String recipients = "nobody@example.com";

        List<SongContent> gdriveSongContent = getGdriveCloudSongContent();
        List<SongContent> linkSongContent = getLinksSongContent();

        List<SongContent> contents = Stream.concat(gdriveSongContent.stream(), linkSongContent.stream())
                .collect(Collectors.toList());

        MimeMessage message = Mockito.mock(MimeMessage.class);

        when(mailSender.createContentMail(any(), any(), any(), any(), any())).thenReturn(message);

        doNothing().when(sharingService).sendMail(any());

        sharingService.shareSongContentViaMail(user1, recipients, contents, subject, body, false, false);

        verify(sharingService).processFilesPlaceholder(any(), any());

        verify(mailSender).createContentMail(any(), any(), any(), any(), any());

        verify(sharingService).sendMail(message);
    }


    @Test
    void nonDownloadableContentShouldBeFilteredWhenSharingSongContentsViaMail() throws SharingServiceException, ContentServiceException {

        User user1 = new User();
        user1.setEmail("nobody@example.com");
        String subject = "Subject";
        String body = "mail body";
        String recipients = "nobody@example.com";

        List<SongContent> gdriveSongContent = getGdriveCloudSongContent();
        List<SongContent> linkSongContent = getLinksSongContent();

        List<SongContent> contents = Stream.concat(gdriveSongContent.stream(), linkSongContent.stream())
                .collect(Collectors.toList());

        TmpDirStorage storage = createMockStorage(gdriveSongContent);
        when(contentService.downloadSongContent(any())).thenReturn(storage);

        // This way to check which arguments have been passed to method
        // @see https://www.javadoc.io/doc/org.mockito/mockito-core/2.7.9/org/mockito/ArgumentCaptor.html
        // @see https://stackoverflow.com/questions/5606541/how-to-capture-a-list-of-specific-type-with-mockito
        @SuppressWarnings("unchecked")
        final ArgumentCaptor<List<SongContent>> songContentArgCaptor
                = ArgumentCaptor.forClass((Class) List.class);

        // verify arguments of processFilesPlaceholder, it should be an array without of GDRIVE files
        sharingService.shareSongContentViaMail(user1, recipients, contents, subject, body, true, false);

        verify(sharingService).processFilesPlaceholder(songContentArgCaptor.capture(), any());

        List<SongContent> capturedSongContent = songContentArgCaptor.getValue();
        assertEquals(2, capturedSongContent.size(), "The size of songContent items that will be attached to mail body is wrong");
        assertEquals(SongContentTypeEnum.LINK, capturedSongContent.get(0).getType(), "The type of 0 element of content to be attached to mail body is wrong");
        assertEquals(SongContentTypeEnum.LINK, capturedSongContent.get(1).getType(), "The type of 1 element of content to be attached to mail body is wrong");
    }

    @Test
    void sequenceShouldBeAddedToFileNames() {

        File file1 = Mockito.mock(File.class);
        String fileName1 = "03.    Song slide.ppt";
        FileHolder fileHolder1 = new FileHolder(file1, fileName1);

        File file2 = Mockito.mock(File.class);
        String fileName2 = "9.Text_of_the_song.pdf";
        FileHolder fileHolder2 = new FileHolder(file2, fileName2);

        List<FileHolder> files = new ArrayList<>(Arrays.asList(fileHolder1, fileHolder2));

        sharingService.addSequenceToFileNames(files);

        assertEquals("1. Song slide.ppt", files.get(0).getOriginalName());
        assertEquals("2. Text_of_the_song.pdf", files.get(1).getOriginalName());
    }

    @Test
    void songContentWillBeConvertedToLink() {
        // 1) check that given content will be transformed to the links
        String link = "https://www.youtube.com/watch?v=4ktVQFisrGo";
        SongContent contentLink = new SongContent();
        contentLink.setType(SongContentTypeEnum.LINK);
        contentLink.setContent(link);

        Assertions.assertEquals(link, sharingService.getContentLink(contentLink));

        // 2) check that given content will be transformed to the links
        String cloudFileId = "1Qz5n0_SbKuqrqkCBe-SxSYbxUwNMchV9";

        SongContent contentCloudFile = new SongContent();
        contentCloudFile.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        contentCloudFile.setContent(cloudFileId);

        Assertions.assertEquals("https://drive.google.com/open?id=" + cloudFileId, sharingService.getContentLink(contentCloudFile));
    }


    @Test
    void placeholderInMailBodyShouldBeReplacedWithFileLinksWhenSharingSongContentsViaMail() {

        String contentLinks = "ContentLinksText";

        String mailBodyHeader = "The beginning of mail";
        String mailBodyFooter = "Sincerely yours";
        String mailBodyWithPlaceholder = mailBodyHeader + "\n\n" + "%fileList%" + mailBodyFooter;

        String mailBodyWithoutPlaceholder = mailBodyHeader + mailBodyFooter;

        doReturn(contentLinks).when(sharingService).getContentLinks(any());
        // when(sharingService.getContentLinks(any())).thenReturn(contentLinks);

        // 1) check that explicit placeholder are parsed (will be replaced with some generated text and rest of text is preserved)
        String processedWithExplicitPlaceholder = sharingService.processFilesPlaceholder(new ArrayList<>(), mailBodyWithPlaceholder);
        assertThat(processedWithExplicitPlaceholder, containsString(mailBodyHeader));
        assertThat(processedWithExplicitPlaceholder, containsString(contentLinks));
        assertThat(processedWithExplicitPlaceholder, containsString(mailBodyFooter));

        // and, + index of content links is before footer
        assertTrue(processedWithExplicitPlaceholder.indexOf(contentLinks) < processedWithExplicitPlaceholder.indexOf(mailBodyFooter));

        // 2) check that explicit placeholder are parsed (generated text will be appended to the end of the letter and the rest of text is preserved)
        String processedWithImplicitPlaceholder = sharingService.processFilesPlaceholder(new ArrayList<>(), mailBodyWithoutPlaceholder);
        assertThat(processedWithImplicitPlaceholder, containsString(mailBodyHeader));
        assertThat(processedWithImplicitPlaceholder, containsString(contentLinks));
        assertThat(processedWithImplicitPlaceholder, containsString(mailBodyFooter));
    }

    private List<SongContent> getGdriveCloudSongContent() {

        List<SongContent>  contents = new ArrayList<>();
        SongContent content1 = new SongContent();
        content1.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        content1.setFileName("Content name 1");

        contents.add(content1);

        SongContent content2 = new SongContent();
        content2.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        content2.setFileName("Content name 2");

        contents.add(content2);
        return contents;
    }

    private List<SongContent> getLinksSongContent() {
        List<SongContent>  contents = new ArrayList<>();

        SongContent content1 = new SongContent();
        content1.setType(SongContentTypeEnum.LINK);
        content1.setFileName("Link name 1");

        contents.add(content1);

        SongContent content2 = new SongContent();
        content2.setType(SongContentTypeEnum.LINK);
        content2.setFileName("Link name 2");

        contents.add(content2);

        return contents;
    }

    private TmpDirStorage createMockStorage(List<SongContent> contents) {

        List<FileHolder> fileHolders = new ArrayList<>();
        for(SongContent songContent : contents) {
            File file = Mockito.mock(File.class);
            fileHolders.add(new FileHolder(file, songContent.getFileName()));
        }

        TmpDirStorage storage = Mockito.mock(TmpDirStorage.class);
        when(storage.getFiles()).thenReturn(fileHolders);
        return storage;
    }

}
