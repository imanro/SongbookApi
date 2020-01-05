package songbook.song.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.song.entity.Song;
import songbook.song.view.Summary;
import songbook.user.entity.User;
import songbook.song.repository.SongDao;
import songbook.user.entity.repository.UserDao;

import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongDao songDao;

    @Autowired
    private UserDao userDao;

    @GetMapping("{id}")
    @ResponseBody
    public Song getSongById(@PathVariable("id") long id){
        System.out.println(id);
        User user = getDefaultUser();
        return songDao.findByIdWithHeaders(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }


    @GetMapping("")
    @JsonView(Summary.class)
    public Page<Song> findSongsByHeader(@RequestParam(required = false, name = "search") String search, Pageable pageable){
        System.out.println(pageable.getOffset() + " offset");
        System.out.println(pageable.getPageSize() + " size");
        User user = getDefaultUser();

        if (search == null || search.length() < 2) {
            Page<Song> page = songDao.findAll(pageable);
            System.out.println(page.getNumber() + " found");
            return page;

        } else {
            return songDao.findAllByHeaderWithHeaders(search, user, pageable);
        }
    }

    private User getDefaultUser() {
        long defaultId = 1;
        return userDao.getOne(defaultId);
    }
}
