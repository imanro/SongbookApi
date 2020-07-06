package songbook.song.rest;

import songbook.common.controller.BaseController;
import songbook.domain.song.port.in.*;
import songbook.domain.song.entity.Song;
import songbook.domain.song.usecase.SearchSongsByTagsAndStringQuery;
import songbook.song.view.Details;
import songbook.song.view.HeaderTagSummary;
import songbook.domain.user.entity.User;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/song")
public class SongController extends BaseController {

    // Refactoring
    private final SongByIdQuery songByIdQuery;

    private final SongsByIdsQuery songsByIdsQuery;

    private final SearchSongsByStringQuery searchSongsByStringQuery;

    private final SearchSongsByTagsAndStringQuery searchSongsByTagsAndStringQuery;

    private final CreateSongCommand createSongCommand;

    private final AttachSongToTagCommand attachSongToTagCommand;

    private final DetachSongFromTagCommand detachSongFromTagCommand;

    private final SyncSongContentCommand syncSongContentCommand;

    private final MergeSongsCommand mergeSongsCommand;

    public SongController(SongByIdQuery songByIdQuery,
                          SongsByIdsQuery songsByIdsQuery,
                          SearchSongsByStringQuery searchSongsByStringQuery,
                          SearchSongsByTagsAndStringQuery searchSongsByTagsAndStringQuery,
                          CreateSongCommand createSongCommand,
                          AttachSongToTagCommand attachSongToTagCommand,
                          DetachSongFromTagCommand detachSongFromTagCommand,
                          SyncSongContentCommand syncSongContentCommand,
                          MergeSongsCommand mergeSongsCommand
    ) {

        this.songByIdQuery = songByIdQuery;
        this.songsByIdsQuery = songsByIdsQuery;
        this.searchSongsByStringQuery = searchSongsByStringQuery;
        this.searchSongsByTagsAndStringQuery = searchSongsByTagsAndStringQuery;
        this.createSongCommand = createSongCommand;
        this.attachSongToTagCommand = attachSongToTagCommand;
        this.detachSongFromTagCommand = detachSongFromTagCommand;
        this.syncSongContentCommand = syncSongContentCommand;
        this.mergeSongsCommand = mergeSongsCommand;
    }

    @GetMapping("{id}")
    @ResponseBody
    public Song getSongById(@PathVariable("id") long id){
        User user = getDefaultUser();
        return songByIdQuery.getSongById(id, user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    // CHECKME!!: renamed from /multiple to ""
    @GetMapping("")
    public List<Song> getSongsByIds(@RequestParam(name="ids") List<Long> ids, Pageable pageable)
    {
        User user = getDefaultUser();
        return songsByIdsQuery.getSongsByIds(ids, user);
    }

    // CHECKME!!: renamed from "" to /search
    @GetMapping("/search")
    @JsonView(HeaderTagSummary.class)
    public Page<Song> findSongsByHeader(@RequestParam(required = false, name = "search") String search, Pageable pageable) throws ResponseStatusException {
        User user = getDefaultUser();
        return this.searchSongsByStringQuery.searchSongs(search, user, pageable);
    }

    @GetMapping("/tags")
    @JsonView(HeaderTagSummary.class)
    public Page<Song> findSongsByTags(@RequestParam("ids") List<Long> ids, @RequestParam(required=false, name="search") String search, Pageable pageable) throws ResponseStatusException {
        User user = getDefaultUser();
        return this.searchSongsByTagsAndStringQuery.getSongsByTagsAndString(search, ids, user, pageable);
    }

    @PostMapping("")
    public Song createSong(@RequestBody Song newSong) {
        // todo: validation
        return this.createSongCommand.createSong(newSong);
    }

    @PostMapping("{songId}/tags/{tagId}")
    @JsonView(Details.class)
    public Song attachSongToTag(@PathVariable("songId") long songId, @PathVariable("tagId") long tagId) throws ResponseStatusException {

        User user = getDefaultUser();

        try {
            return this.attachSongToTagCommand.attachSongToTag(songId, tagId, user);
        } catch(AttachSongToTagSongNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song is not found");
        } catch(AttachSongToTagTagNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag is not found");
        }
    }

    @DeleteMapping("{songId}/tags/{tagId}")
    @JsonView(Details.class)
    public Song detachSongFromTag(@PathVariable("songId") long songId, @PathVariable("tagId") long tagId) throws ResponseStatusException {

        User user = getDefaultUser();

        try {
            return this.detachSongFromTagCommand.detachSongFromTag(songId, tagId, user);
        } catch(DetachSongFromTagSongNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song is not found");
        } catch(DetachSongFromTagTagNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag is not found");
        }
    }

    @PostMapping("merge/{mergedId}/{masterId}")
    public ResponseEntity<Map<String, String>> mergeSong(@PathVariable("mergedId") Long mergedId, @PathVariable("masterId") Long masterId) throws ResponseStatusException {
        User user = getDefaultUser();

        try {
            mergeSongsCommand.mergeSongs(mergedId, masterId, user);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to merge the songs due to exception: " + e.getMessage());
        }

        Map<String, String> response = new HashMap<String, String>(){{put("result", "ok");}};
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("syncCloudContent/{songId}")
    @JsonView(Details.class)
    public Song syncSongContent(@PathVariable("songId") long songId) throws ResponseStatusException  {
        User user = getDefaultUser();
        // + the problem is with auto ids in google drive - we should preserve old ids in the new db

        Song updatedSong;

        try {
            updatedSong = this.syncSongContentCommand.syncSongContent(songId, user);
        } catch (SyncSongContentSongNotFoundException e) {
            System.out.println("An exception has occurred, song not found: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A Server error occurred");

        } catch (SyncSongContentUnableToGetSongCloudFilesException e) {
            System.out.println("An exception has occurred, Unable to get song files, " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A Server error occurred");

        } catch (SyncSongContentNotInitializedException e) {
            System.out.println("An exception has occurred, the content has not been initialized, " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A Server error occurred");
        }

        return updatedSong;
    }

}
