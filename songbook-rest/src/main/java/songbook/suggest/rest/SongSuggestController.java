package songbook.suggest.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.common.controller.BaseController;
import songbook.concert.entity.Concert;
import songbook.concert.repository.ConcertDao;
import songbook.domain.song.entity.Song;
import songbook.song.repository.SongDao;
import songbook.suggest.entity.SongCountProj;
import songbook.suggest.entity.SongProj;
import songbook.suggest.entity.PopularSongProj;
import songbook.suggest.repository.SongSuggestDao;
import songbook.suggest.service.SongStatService;
import songbook.suggest.view.Summary;
import songbook.domain.user.entity.User;
import songbook.user.repository.UserDao;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/song-suggest")
public class SongSuggestController extends BaseController  {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SongSuggestDao songSuggestDao;

    @Autowired
    private ConcertDao concertDao;

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongStatService songStatService;

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/popular")
    @JsonView(Summary.class)
    Page<PopularSongProj> findPopularSongs(Pageable pageable) {
        initFilters();

        User user = getDefaultUser();
        Page<PopularSongProj> items = songSuggestDao.findPopularConcertItems(pageable, user);

        // hack: attaching concert entities manually
        List<Long> concertIds = songStatService.extractConcertIds(items);
        Page<PopularSongProj> newItems;
        if(concertIds.size() > 0) {
            List<Concert> concerts = concertDao.findAllById(concertIds);
            newItems = songStatService.attachConcertsToStat(items, concerts, pageable);
        } else {
            newItems = items;
        }

        return newItems;
    }

    @GetMapping("/recent/{fromDate}")
    @JsonView(Summary.class)
    Page<SongProj> findRecentSongs(
            @PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            Pageable pageable
    ) {
        initFilters();
        User user = getDefaultUser();
        return songSuggestDao.findRecentSongs(pageable, user, convertToDate(fromDate));
    }

    @GetMapping("/abandoned/{fromDate}")
    @JsonView(Summary.class)
    Page<SongCountProj> findAbandonedSongs(
            @PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) Optional<Long> performancesThreshold,
            Pageable pageable
    ) {
        initFilters();
        User user = getDefaultUser();

        long performancesAmount = performancesThreshold.orElse(1L);
        return songSuggestDao.findAbandonedSongs(pageable, user, convertToDate(fromDate), performancesAmount);
    }

    @GetMapping("/before/{id}")
    @JsonView(Summary.class)
    Page<SongProj> findSongsBefore(
            @PathVariable("id") long songId,
            Pageable pageable
    ) throws ResponseStatusException {
        initFilters();
        User user = getDefaultUser();

        Song sampleSong = songDao.findById(songId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The sample song was not found"));
        return songSuggestDao.findSongsBefore(pageable, user, sampleSong);
    }

    @GetMapping("/after/{id}")
    @JsonView(Summary.class)
    Page<SongProj> findSongsAfter(
            @PathVariable("id") long songId,
            Pageable pageable
    ) throws ResponseStatusException {
        initFilters();
        User user = getDefaultUser();

        Song sampleSong = songDao.findById(songId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The sample song was not found"));
        return songSuggestDao.findSongsAfter(pageable, user, sampleSong);
    }

    private void initFilters() {
        User user = getDefaultUser();
        songDao.initContentUserFilter(user);
    }

    private Date convertToDate(LocalDate localDate) {
        return Date.from( localDate.atStartOfDay( ZoneId.systemDefault()).toInstant());
    }

}
