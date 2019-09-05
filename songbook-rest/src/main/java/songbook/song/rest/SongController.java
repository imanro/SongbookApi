package songbook.song.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import songbook.song.entity.Song;
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
        User user = getDefaultUser();
        Song song = songDao.findByIdWithHeaders(id, user);
        return song;
    }

    private User getDefaultUser() {
        long defaultId = 1;
        return userDao.getOne(defaultId);
    }
}
