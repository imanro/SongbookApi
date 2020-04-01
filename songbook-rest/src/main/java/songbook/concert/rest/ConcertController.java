package songbook.concert.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import songbook.concert.entity.Concert;
import songbook.concert.repository.ConcertDao;
import songbook.song.entity.Song;
import songbook.song.repository.SongDao;
import songbook.song.service.SongService;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import songbook.concert.view.Details;
import songbook.concert.view.Summary;

@RestController
@RequestMapping("/concert")
public class ConcertController {

    @Autowired
    private ConcertDao concertDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongService songService;

    @GetMapping("{id}")
    @JsonView(Details.class)
    @ResponseBody
    public Concert findConcertById(@PathVariable("id") long id){
        System.out.println(id);
        User user = getDefaultUser();

        // + change on method findByIdAndUser
        return concertDao.findByIdWithHeaders(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    @GetMapping("")
    @JsonView(Summary.class)
    public Page<Concert> findAll(Pageable pageable){
        System.out.println(pageable.getOffset() + " offset");
        System.out.println(pageable.getPageSize() + " size");
        User user = getDefaultUser();

        // change on method findAllByUser + default sorting
        return concertDao.findAll(pageable);
    }

    private User getDefaultUser() {
        long defaultId = 1;
        return userDao.getOne(defaultId);
    }

}
