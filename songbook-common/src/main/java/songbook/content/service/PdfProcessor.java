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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.song.service.SongServiceException;

@Service
public class PdfProcessor {

    @Autowired
    CloudDao cloudDao;

    @Autowired
    SongService songService;

    private final String pdfMimeType = "application/pdf";

    public byte[] pdfCompile(List<SongContent> songContents) throws PdfProcessorException {

        // create stream from this list
        // filter songContent to leave only cloud and mimeType=pdf

        List<SongContent> needleContents = songContents.stream().filter(songContent -> {
            return songContent.getType() == SongContentTypeEnum.GDRIVE_CLOUD_FILE &&
                    songContent.getMimeType().equals(this.pdfMimeType);
        }).collect(Collectors.toList());

        // if no files left - throw an exception - UT1
        if(needleContents.size() == 0) {
            throw new PdfProcessorException("There's no PDFs in song contents list");
        }

        // Read cloud files first
        List<byte[]> pdfContents = new ArrayList();

        for (SongContent content : needleContents) {
            // enhanced for

            CloudFile cloudFile;

            // UT 2 - unable to create cloudContent
            try {
                cloudFile = songService.createCloudContentFromSongContent(content);
            } catch (SongServiceException e) {
                throw new PdfProcessorException("Unable to create PDF, an exception occurred when creating cloud file", e);
            }

            ByteArrayOutputStream bs;

            // UT 3 - unable to download file
            try {
                bs = cloudDao.getFileContents(cloudFile);
            } catch (CloudException e) {
                throw new PdfProcessorException("Unable to create PDF, an exception occurred when downloading a content", e);
            }

            byte[] byteContent = bs.toByteArray();
            pdfContents.add(byteContent);
        }

        // Now, create target document
        // PDDocument outDocument = new PDDocument(MemoryUsageSetting.setupTempFileOnly());
        PDDocument outDocument = new PDDocument();

        for (byte[] pdfContent : pdfContents) {

            // create pdf from this file
            // UT 5 - unable to load document
            PDDocument srcDocument = loadPdf(pdfContent);

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

    public PDDocument loadPdf(byte[] byteContent) throws PdfProcessorException {
        PDDocument srcDocument;

        try {
            // srcDocument = PDDocument.load(byteContent, "", null, null, MemoryUsageSetting.setupMainMemoryOnly());
            srcDocument = PDDocument.load(byteContent);
        } catch (IOException e) {
            throw new PdfProcessorException("Unable to create PDF, an exception occurred while parsing src document", e);
        }

        return srcDocument;
    }
}
