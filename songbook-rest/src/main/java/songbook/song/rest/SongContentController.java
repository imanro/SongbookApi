package songbook.song.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.content.service.PdfProcessor;
import songbook.content.service.PdfProcessorException;
import songbook.song.entity.SongContent;
import songbook.song.repository.SongContentDao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/song-content")
public class SongContentController {

    @Autowired
    private SongContentDao songContentDao;

    @Autowired
    private PdfProcessor pdfProcessor;


    @GetMapping("{id}")
    @ResponseBody
    public SongContent getContentById(@PathVariable("id") long id){
        return songContentDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    @RequestMapping("")
    public List<SongContent> getContentsByIds(@RequestParam List<Long> ids)
    {
        List<SongContent> songContents  = songContentDao.findAllById(ids);
        return songContents;
    }

    @RequestMapping("/song/{id}")
    public List<SongContent> getContentBySongId(@PathVariable("id") long songId)
    {
        List<SongContent> songContents  = songContentDao.findBySongId(songId);
        return songContents;
    }

    @RequestMapping("/pdfCompile")
    public ResponseEntity<byte[]> pdfCompile(@RequestParam List<Long> ids) {

        List<SongContent> songContents  = songContentDao.findAllById(ids);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/pdf");
        headers.add("Content-Description", "File Transfer");

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        headers.add("Content-Disposition", "inline; filename=\"revival.l." + formatter.format(date) +  ".pdf\"");
        headers.add("Content-Transfer-Encoding", "binary");
        headers.add("Connection", "Keep-Alive");
        headers.add("Expires", "0");
        headers.add("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        headers.add("Pragma", "public");

        try {
            byte[] bytes = pdfProcessor.pdfCompile(songContents);

            headers.add("Content-Length", String.valueOf(bytes.length));
            
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch(PdfProcessorException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
