package songbook.song.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.common.controller.BaseController;
import songbook.song.entity.Song;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.service.SongService;
import songbook.song.service.SongServiceException;
import songbook.song.view.Details;
import songbook.song.view.HeaderSummary;
import songbook.song.view.HeaderTagSummary;
import songbook.song.view.Summary;
import songbook.tag.entity.Tag;
import songbook.tag.repository.TagDao;
import songbook.user.entity.User;
import songbook.song.repository.SongDao;
import songbook.user.repository.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("multiple")
    public List<Song> getSongsByIds(@RequestParam(name="ids") List<Long> ids)
    {
        initFilters();
        List<Song> songs  = songDao.findAllById(ids);
        return songs;
    }


    // TODO: rename to /search
    @GetMapping("")
    @JsonView(HeaderTagSummary.class)
    public Page<Song> findSongsByHeader(@RequestParam(required = false, name = "search") String search, Pageable pageable) throws ResponseStatusException {
        initFilters();

        if (search == null || search.length() < 2) {
            System.out.println("Find all");
            Page<Song> page = songDao.findAllWithHeadersAndTags(pageable);
            System.out.println(page.getTotalElements() + " found");
            return page;

        } else {
            System.out.println("Find by title cont");

            System.out.println("Search string: ");
            System.out.println(search);
            // return songDao.findAllByTitleContaining(search, pageable);
            return songDao.findAllByHeaderWithHeadersAndTags(search, pageable);
        }
    }

    @GetMapping("/tags")
    @JsonView(HeaderTagSummary.class)
    public Page<Song> findSongsByTags(@RequestParam("ids") List<Long> ids, @RequestParam(required=false, name="search") String search, Pageable pageable) throws ResponseStatusException {
        initFilters();
        if (search == null || search.length() < 2) {
            System.out.println("Find all by tags");
            return songDao.findAllByTags(ids, pageable);
        } else {
            System.out.println("Find all by tags AND header");
            return songDao.findAllByTagsAndContent(ids, search, pageable);
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

    @PostMapping("merge/{mergedId}/{masterId}")
    public ResponseEntity<Map<String, String>> mergeSong(@PathVariable("mergedId") Long mergedId, @PathVariable("masterId") Long masterId) throws ResponseStatusException {

        Song masterSong = songDao.findById(masterId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The master song wasn't found"));
        Song mergedSong = songDao.findById(mergedId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The merged song wasn't found"));

        try {
            songService.mergeSong(mergedSong, masterSong);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to merge the songs due to exception: " + e.getMessage());
        }

        Map<String, String> response = new HashMap<String, String>(){{put("result", "ok");}};
        return ResponseEntity.status(HttpStatus.OK).body(response);
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

        songDao.refresh(song);
        // to return tags in right order, re-read the song
        Optional<Song> songOptUpdated = songDao.findById(songId);
        return songOptUpdated.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song is not found"));
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
