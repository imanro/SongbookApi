package songbook.song.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.common.controller.BaseController;
import songbook.song.entity.Song;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.song.service.SongServiceException;
import songbook.song.view.Details;
import songbook.song.view.HeaderSummary;
import songbook.song.view.Summary;
import songbook.tag.entity.Tag;
import songbook.tag.repository.TagDao;
import songbook.user.entity.User;
import songbook.song.repository.SongDao;
import songbook.user.repository.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@RestController
@RequestMapping("/song")
public class SongController extends BaseController {

    @Autowired
    private SongDao songDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private SongService songService;

    @PersistenceContext
    private EntityManager em;

    @GetMapping("{id}")
    @ResponseBody
    public Song getSongById(@PathVariable("id") long id){
        initFilters();
        return songDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }


    @GetMapping("")
    @JsonView(HeaderSummary.class)
    public Page<Song> findSongsByHeader(@RequestParam(required = false, name = "search") String search, Pageable pageable) throws ResponseStatusException {
        initFilters();

        if (search == null || search.length() < 2) {
            Page<Song> page = songDao.findAll(pageable);
            System.out.println(page.getNumber() + " found");
            return page;

        } else {
            return songDao.findAllByHeaderWithHeaders(search, pageable);
        }
    }

    @PostMapping("")
    public Song createSong(@RequestBody Song newSong) {
            return songDao.save(newSong);
    }

    @PostMapping("{songId}/tags/{tagId}")
    @JsonView(Details.class)
    public Song attachSongToTag(@PathVariable("songId") long songId, @PathVariable("tagId") long tagId) throws ResponseStatusException {
        initFilters();
        // search for song with such id

        Optional<Song> songOpt = songDao.findById(songId);
        if(!songOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song is not found");
        }

        Optional<Tag> tagOpt = tagDao.findById(tagId);
        if(!tagOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag is not found");
        }

        Song song = songOpt.get();
        Tag tag = tagOpt.get();

        // dont perform any checks
        song.getTags().add(tag);
        songDao.save(song);

        System.out.println("re-read the song");
        songDao.refresh(song);
        // to return tags in right order, re-read the song
        Optional<Song> songOptUpdated = songDao.findById(songId);
        if(!songOptUpdated.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song is not found");
        }

        return songOptUpdated.get();
    }

    @DeleteMapping("{songId}/tags/{tagId}")
    @JsonView(Details.class)
    public Song detachSongFromTag(@PathVariable("songId") long songId, @PathVariable("tagId") long tagId) throws ResponseStatusException {
        initFilters();
        // search for song with such id
        Optional<Song> songOpt = songDao.findById(songId);
        if(!songOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song is not found");
        }

        Optional<Tag> tagOpt = tagDao.findById(tagId);
        if(!tagOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag is not found");
        }

        Song song = songOpt.get();
        Tag tag = tagOpt.get();

        // dont perform any checks
        song.getTags().remove(tag);
        songDao.save(song);

        return song;
    }

    @PatchMapping("syncCloudContent/{songId}")
    @JsonView(Details.class)
    public Song syncSongContent(@PathVariable("songId") long songId) throws ResponseStatusException  {
        User user = getDefaultUser();
        Song song = songDao.findByIdWithHeaders(songId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));

        // + the problem is with auto ids in google drive - we should preserve old ids in the new db

        Song updatedSong;

        try {
            updatedSong = songService.syncCloudContent(song, user);
        } catch(SongServiceException e) {
            System.out.println("An exception has occurred: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A Server error occurred");
        }

        return updatedSong;
    }

    private void initFilters() {
        User user = getDefaultUser();
        songDao.initContentUserFilter(user);
    }

}
