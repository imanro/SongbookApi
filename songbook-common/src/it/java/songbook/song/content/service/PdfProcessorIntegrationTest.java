package songbook.song.content.service;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import songbook.content.service.PdfProcessor;
import songbook.content.service.PdfProcessorException;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.util.file.FileException;
import songbook.util.file.TmpResourceResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "/cloud-dao.properties")
public class PdfProcessorIntegrationTest {
    // To avoid mapping errors of errors, another name
    @Autowired
    private PdfProcessor pdfProcessor;

    @Autowired
    TmpResourceResolver tmpResourceResolver;

    @Test
    void canCompilePdfBySongContents() throws PdfProcessorException, FileException {
        SongContent songContent1 = new SongContent();
        songContent1.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent1.setMimeType("application/pdf");
        songContent1.setContent("1vA092O5FP1DtZ7p__JtYyJbx-jF8WIvo");

        SongContent songContent2 = new SongContent();
        songContent2.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent2.setMimeType("application/pdf");
        songContent2.setContent("1ram7gM4LLQc0m5s-_w8Jn9SnfhCClNSW");

        List<SongContent> contents = new ArrayList<>();
        contents.add(songContent1);
        contents.add(songContent2);

        byte[] bytes = pdfProcessor.pdfCompile(contents);

        Assertions.assertTrue(bytes.length > 0, "The returned content is zero");

        File tmpDir = getTmpDir();

        tmpDir.mkdir();

        String tmpFileName = tmpResourceResolver.getFileNameBasedOnRandomNumber() + ".pdf";

        String path = tmpDir.getAbsolutePath() + "/" + tmpFileName;

        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(bytes);
        } catch(FileNotFoundException e ) {
            System.out.println("Unable to write into file " + path + " An exception occurred");
        } catch(IOException e) {
            System.out.println("Unable to write into file " + path + " An exception occurred");
        }

        System.out.println("The result file has been written into " + path + ", check it");
    }

    private File getTmpDir() throws FileException {
        return tmpResourceResolver.getTmpDir();
    }
}
