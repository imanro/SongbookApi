package songbook.content.service;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.cloud.CloudException;
import songbook.cloud.entity.CloudFile;
import songbook.cloud.repository.CloudDao;
import songbook.song.entity.SongContent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.song.service.SongServiceException;
import songbook.util.file.TmpDirStorage;
import songbook.util.file.entity.FileHolder;

@Service
public class PdfProcessor {

    @Autowired
    CloudDao cloudDao;

    @Autowired
    SongService songService;

    @Autowired
    ContentService contentService;

    private final String pdfMimeType = "application/pdf";

    public byte[] pdfCompile(List<SongContent> songContents) throws PdfProcessorException {

        // create stream from this list
        // filter songContent to leave only cloud and mimeType=pdf

        Predicate<SongContent> byMimeType = songContent -> songContent.getMimeType().equals("application/pdf");

        TmpDirStorage storage;
        try {
            storage = contentService.downloadSongContent(songContents, byMimeType);
        } catch(ContentServiceException e) {
            throw new PdfProcessorException("Unable to download song content due to exception", e);
        }

        // if no files left - throw an exception - UT1
        if(storage.getFiles().size() == 0) {
            throw new PdfProcessorException("There's no PDFs in song contents list");
        }

        // maybe, we should move this to some common place

        // Read cloud files first

        // Now, create target document
        // PDDocument outDocument = new PDDocument(MemoryUsageSetting.setupTempFileOnly());
        PDDocument outDocument = new PDDocument();

        for (FileHolder fileHolder : storage.getFiles()) {

            // create pdf from this file
            // UT 5 - unable to load document
            PDDocument srcDocument = loadPdf(fileHolder.getFile());

            int pageCount = srcDocument.getNumberOfPages();


            // clone by pages, add to original pdf
            for (int i = 0; i < pageCount; i++) {
                PDPage page = srcDocument.getPage(i);
                outDocument.addPage(page);
            }
            // end of loop
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            outDocument.save(out);
        } catch(IOException e) {
            throw new PdfProcessorException("Unable to save the result document, an exception has occurred: " +  e.getMessage());
        }

        return out.toByteArray();
    }

    public PDDocument loadPdf(File file) throws PdfProcessorException {
        PDDocument srcDocument;

        try {
            // srcDocument = PDDocument.load(byteContent, "", null, null, MemoryUsageSetting.setupMainMemoryOnly());
            srcDocument = PDDocument.load(file);
        } catch (IOException e) {
            throw new PdfProcessorException("Unable to create PDF, an exception occurred while parsing src document", e);
        }

        return srcDocument;
    }
}
