package songbook.sharing.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;

public class SongContentMailRequest {
    List<Long> contentIds;

    @NotNull
    String mailBody;

    @NotNull
    String mailSubject;

    @NotNull
    @Size(min = 3)
    String mailRecipients;

    boolean isEmbedContent = true;

    boolean isAddSequenceToFileNames = true;

    public List<Long> getContentIds() {
        return contentIds;
    }

    public SongContentMailRequest setContentIds(List<Long> contentIds) {
        this.contentIds = contentIds;
        return this;
    }

    public String getMailBody() {
        return mailBody;
    }

    public SongContentMailRequest setMailBody(String mailBody) {
        this.mailBody = mailBody;
        return this;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public SongContentMailRequest setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
        return this;
    }

    public String getMailRecipients() {
        return mailRecipients;
    }

    public SongContentMailRequest setMailRecipients(String mailRecipients) {
        this.mailRecipients = mailRecipients;
        return this;
    }

    public boolean getIsEmbedContent() {
        return isEmbedContent;
    }

    public SongContentMailRequest setIsEmbedContent(boolean embedContent) {
        isEmbedContent = embedContent;
        return this;
    }

    public boolean getIsAddSequenceToFileNames() {
        return isAddSequenceToFileNames;
    }

    public SongContentMailRequest setIsAddSequenceToFileNames(boolean addSequenceToFileNames) {
        isAddSequenceToFileNames = addSequenceToFileNames;
        return this;
    }
}
