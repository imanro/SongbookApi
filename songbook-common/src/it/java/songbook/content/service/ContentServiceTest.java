package songbook.content.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.entity.FileHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;

@SpringBootTest
@TestPropertySource(locations = "/cloud-dao.properties")
public class ContentServiceTest {

    @Autowired
    ContentService contentService;

    @Test
    void songContentCanBeDownloaded() throws ContentServiceException {

        List<SongContent> contents = new ArrayList<>();
        this.addPdfContent(contents);

        TmpDirStorage storage = contentService.downloadSongContent(contents);

        Assertions.assertEquals(2, storage.getFiles().size(), "The downloaded files size is wrong");
        Assertions.assertTrue(storage.getFiles().get(0).getFile().length() > 0, "The downloaded file 1 size is zero");
        Assertions.assertEquals(contents.get(0).getFileName(), storage.getFiles().get(0).getOriginalName(), "The downloaded file 1 name is wrong");

        Assertions.assertTrue(storage.getFiles().get(1).getFile().length() > 0, "The downloaded file 2 size is zero");
        Assertions.assertEquals(contents.get(1).getFileName(), storage.getFiles().get(1).getOriginalName(), "The downloaded file 2 name is wrong");
    }

    @Test
    void onlyNeedleContentWillBeDownloaded() throws ContentServiceException {

        List<SongContent> contents = new ArrayList<>();
        this.addPdfContent(contents);
        this.addStrangeContent(contents);

        Predicate<SongContent> byMimeType = songContent -> songContent.getMimeType().equals("application/pdf");

        TmpDirStorage storage = contentService.downloadSongContent(contents, byMimeType);

        Assertions.assertEquals(2, storage.getFiles().size(), "The downloaded files size is wrong");
        Assertions.assertTrue(storage.getFiles().get(0).getFile().length() > 0, "The downloaded file 1 size is zero");
        Assertions.assertEquals(contents.get(0).getFileName(), storage.getFiles().get(0).getOriginalName(), "The downloaded file 1 name is wrong");

        Assertions.assertTrue(storage.getFiles().get(1).getFile().length() > 0, "The downloaded file 2 size is zero");
        Assertions.assertEquals(contents.get(1).getFileName(), storage.getFiles().get(1).getOriginalName(), "The downloaded file 2 name is wrong");
    }

    private void addPdfContent(List<SongContent> list) {
        SongContent songContent1 = new SongContent();
        String file1Name = "file1.pdf";
        songContent1.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent1.setMimeType("application/pdf");
        songContent1.setFileName(file1Name);
        songContent1.setContent("1vA092O5FP1DtZ7p__JtYyJbx-jF8WIvo");

        SongContent songContent2 = new SongContent();
        String file2Name = "file2.pdf";
        songContent2.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent2.setMimeType("application/pdf");
        songContent2.setFileName(file2Name);
        songContent2.setContent("1ram7gM4LLQc0m5s-_w8Jn9SnfhCClNSW");

        list.add(songContent1);
        list.add(songContent2);
    }

    private void addStrangeContent(List<SongContent> list) {
        SongContent songContent1 = new SongContent();
        String file1Name = "file1.ppt";
        songContent1.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent1.setMimeType("application/vnd.ms-powerpoint");
        songContent1.setFileName(file1Name);
        songContent1.setContent("1pByRL34PDkSNG5c8KJArQV_-X_VTO5HI");

        SongContent songContent2 = new SongContent();
        String file2Name = "file2.odt";
        songContent2.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent2.setMimeType("application/vnd.oasis.opendocument.text");
        songContent2.setFileName(file2Name);
        songContent2.setContent("1MI4hFbnlRmjV8yfXMJbdCvvRBkhmTLy-");

        list.add(songContent1);
        list.add(songContent2);
    }
}
