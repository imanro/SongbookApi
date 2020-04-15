package songbook.song.content.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.content.service.ContentService;
import songbook.content.service.ContentServiceException;
import songbook.content.service.PdfProcessor;
import songbook.content.service.PdfProcessorException;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.song.service.SongServiceException;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.entity.FileHolder;

import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// @ActiveProfiles("test")
// @SpringBootTest(classes = MockApplication.class)
@DisplayName("SongService test")
@ExtendWith(MockitoExtension.class) // this way to not use it approach
public class PdfProcessorTest {


    @Mock
    SongService songService;

    @Mock
    CloudDao cloudDao;

    @Mock
    ContentService contentService;


    @Spy
    @InjectMocks // this way to not use it approach
    private PdfProcessor pdfProcessor;

    // Positive tests
    @Test
    void pdfCompileShouldDownloadFiles() throws PdfProcessorException, ContentServiceException {

        int amountOfPdfs = 2;

        List<SongContent> contents = this.getSongContentsWithPdf(amountOfPdfs);

        // CloudFile cloudFile = new CloudFile();
        // when(songService.createCloudContentFromSongContent(any())).thenReturn(cloudFile);

        // mocking tmpStorage with one file to continue executing
        File file = Mockito.mock(File.class);
        String fileName = "fileName";
        FileHolder fileHolder = new FileHolder(file, fileName);

        TmpDirStorage storage = Mockito.mock(TmpDirStorage.class);
        when(storage.getFiles()).thenReturn(new ArrayList<>(Arrays.asList(fileHolder)));

        // mocking content behavior
        when(contentService.downloadSongContent(any(), any())).thenReturn(storage);

        PDDocument document = new PDDocument();

        doReturn(document).when(pdfProcessor).loadPdf(any());

        pdfProcessor.pdfCompile(contents);

        verify(contentService).downloadSongContent(any(), any());
    }

    // Negative tests
    @Test
    void pdfCompileWillThrowAnExceptionIfTheresNoMatchedFiles() throws ContentServiceException {

        List<SongContent> notValidSongContents = this.getSongContentsWithoutPdf();

        TmpDirStorage storage = Mockito.mock(TmpDirStorage.class);
        when(contentService.downloadSongContent(any(), any())).thenReturn(storage);

        Assertions.assertThrows(PdfProcessorException.class, () -> pdfProcessor.pdfCompile(notValidSongContents),
                "The exception wasn't thrown");
    }

    @Test
    void pdfCompileInabilityToDownloadSongContentShouldThrowAnException() throws ContentServiceException {

        List<SongContent> contents = this.getSongContentsWithPdf();

        when(contentService.downloadSongContent(any(), any())).thenThrow(ContentServiceException.class);

        Assertions.assertThrows(PdfProcessorException.class, () -> pdfProcessor.pdfCompile(contents),
                "The exception wasn't thrown");
    }


    private List<SongContent> getSongContentsWithoutPdf() {
        List<SongContent> songContents = new ArrayList<>();

        SongContent songContent1 = new SongContent();
        songContent1.setType(SongContentTypeEnum.HEADER);

        songContents.add(songContent1);

        SongContent songContent2 = new SongContent();
        songContent2.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
        songContent2.setMimeType("application/octet-stream");
        songContents.add(songContent2);


        return songContents;
    }

    private List<SongContent> getSongContentsWithPdf(int amount) {
        List<SongContent> songContents = new ArrayList<>();

        for(int i = 0; i < amount; i++) {
            SongContent songContent = new SongContent();
            songContent.setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE);
            songContent.setMimeType("application/pdf");
            songContents.add(songContent);
        }

        return songContents;
    }

    private List<SongContent> getSongContentsWithPdf() {
        List<SongContent> songContents = new ArrayList<>();

        SongContent songContent1 = new SongContent();
        songContent1.setType(SongContentTypeEnum.HEADER);
        songContents.add(songContent1);

        List<SongContent> pdfContent = getSongContentsWithPdf(1);
        songContents.addAll(pdfContent);

        return songContents;
    }
}
