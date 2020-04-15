package songbook.sharing.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.common.controller.BaseController;
import songbook.rest.model.Errors;
import songbook.sharing.rest.model.SongContentMailRequest;
import songbook.sharing.service.SharingService;
import songbook.sharing.service.SharingServiceException;
import songbook.song.entity.SongContent;
import songbook.song.repository.SongContentDao;
import songbook.user.entity.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sharing")
public class SharingController extends BaseController {

    @Autowired
    SongContentDao songContentDao;

    @Autowired
    SharingService sharingService;

    @PostMapping("send-song-content-mail")
    @ResponseBody
    public ResponseEntity<Object> sendSongContentMail(@RequestBody @Valid  SongContentMailRequest request, BindingResult bindingResult) throws ResponseStatusException {

        if (bindingResult.hasErrors()) {
            Errors errors = new Errors("Bad request!");
            errors.convertFieldErrors(bindingResult.getFieldErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        User user = getDefaultUser();

        List<SongContent> contents;

        if(request.getContentIds() != null && request.getContentIds().size() > 0) {
            contents = songContentDao.findAllById(request.getContentIds());
        } else {
            // allow not to send anything
             contents = new ArrayList<>();
        }

        System.out.println("content size: " + contents.size());
        System.out.println("content will be embedded: " + request.getIsEmbedContent());

        // find song contents

        try {
            sharingService.shareSongContentViaMail(user, request.getMailRecipients(), contents, request.getMailSubject(), request.getMailBody(), request.getIsEmbedContent(), request.getIsAddSequenceToFileNames());
        } catch(SharingServiceException e) {
            System.err.println("An exception has occurred: " + e.toString());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send mail due to error");
        }

        Map<String, String> response = new HashMap<>();
        response.put("result", "ok");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
