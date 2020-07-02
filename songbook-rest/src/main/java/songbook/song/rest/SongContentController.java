package songbook.song.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.content.service.PdfProcessor;
import songbook.content.service.PdfProcessorException;
import songbook.rest.model.Errors;
import songbook.song.entity.SongContent;
import songbook.song.repository.SongContentDao;
import songbook.util.list.ListSort;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/song-content")
public class SongContentController {

    private final SongContentDao songContentDao;

    private final PdfProcessor pdfProcessor;

    private final ListSort<SongContent> listSortUtil;

    @Autowired
    public SongContentController(
            SongContentDao songContentDao,
            PdfProcessor pdfProcessor,
            ListSort<SongContent> listSortUtil) {
        this.songContentDao = songContentDao;
        this.pdfProcessor = pdfProcessor;
        this.listSortUtil = listSortUtil;
    }

    @GetMapping("{id}")
    @ResponseBody
    public SongContent getContentById(@PathVariable("id") long id){
        return songContentDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    @GetMapping("")
    public List<SongContent> getContentsByIds(@RequestParam List<Long> ids)
    {
        List<SongContent> songContents  = songContentDao.findAllById(ids);
        listSortUtil.sortListEntitiesByIds(songContents, ids);
        return songContents;
    }

    @PostMapping("")
    public Object create(@RequestBody @Valid SongContent newContent, BindingResult bindingResult) throws ResponseStatusException {

        if (bindingResult.hasErrors()) {
            Errors errors = new Errors("Bad request!");
            errors.convertFieldErrors(bindingResult.getFieldErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        } else {
            // @RequestBody @Valid  SongContentMailRequest request, BindingResult bindingResult
            return songContentDao.save(newContent);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") long id) {
        SongContent songContent = songContentDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
        songContentDao.delete(songContent);

        Map<String, String> response = new HashMap<>();
        response.put("result", "ok");
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
        listSortUtil.sortListEntitiesByIds(songContents, ids);

        System.out.println("The order of ids is follow:");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println(ids.get(i) + "!!");
        }

        System.out.println("The order of content is follow:");
        for (int i = 0; i < songContents.size(); i++) {
            System.out.println(songContents.get(i).getId() + "!!");
        }

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
            String message;
            if(e.getCause() != null) {
                message = e.getMessage() + "[" + e.getCause().getMessage() + "]";
            } else {
                message = e.getMessage();
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
    }

}
