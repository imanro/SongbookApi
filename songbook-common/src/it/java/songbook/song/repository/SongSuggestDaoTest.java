package songbook.song.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.suggest.entity.SongCountProj;
import songbook.suggest.entity.SongProj;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertItemDao;
import songbook.domain.song.entity.Song;
import songbook.suggest.entity.PopularSongProj;
import songbook.suggest.repository.SongSuggestDao;
import songbook.suggest.service.SongStatService;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class SongSuggestDaoTest {

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongContentDao songContentDao;

    @Autowired
    private ConcertDao concertDao;

    @Autowired
    private ConcertItemDao concertItemDao;

    @Autowired
    private SongSuggestDao songSuggestDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    SongStatService songStatService;

    @BeforeEach
    void init() {
        concertItemDao.deleteAll();
        concertDao.deleteAll();
        songContentDao.deleteAll();
        songDao.deleteAll();
        userDao.deleteAll();
    }

    @Test
    void popularConcertItemsCanBeObtained() {
        User user = this.addUser("nobody1@example.com");
        User strangeUser = this.addUser("nobody2@example.com");

        // add some songs
        Song song1 = this.addSong("Song");
        Song song2 = this.addSong("Song");
        Song song3 = this.addSong("Song");
        Song songStrangeUser = this.addSong("Song");

        // add concert
        Date concerDate1 = parseDateString("2011-05-08 12:00:00");
        Date concerDate2 = parseDateString("2011-06-08 12:00:00");

        Concert concert1 = this.addConcert(concerDate1, user);
        Concert concert2 = this.addConcert(concerDate2, user);
        Concert concertStrangeUser = this.addConcert(concerDate2, strangeUser);

        // ...and, some of concertItems
        ConcertItem concertItem1 = addConcertItem(song1, concert1);
        ConcertItem concertItem2 = addConcertItem(song2, concert1);
        ConcertItem concertItem3 = addConcertItem(song3, concert1);

        ConcertItem concertItem4 = addConcertItem(song1, concert2);
        ConcertItem concertItem5 = addConcertItem(song1, concertStrangeUser);
        ConcertItem concertItem6 = addConcertItem(songStrangeUser, concertStrangeUser);

        Pageable pageReq = new PageRequest(0, 10);
        Page<PopularSongProj> pageResult = songSuggestDao.findPopularConcertItems(pageReq, user);

        List<PopularSongProj> items = pageResult.getContent();

        Assertions.assertEquals(3, items.size(), "The result size is wrong");
        Assertions.assertEquals(2, items.get(0).getTotal(), "The amount of performances for the top item is wrong");
        Assertions.assertEquals(song1.getId(), items.get(0).getSong().getId(), "The song id at the top of the list is wrong");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(songStrangeUser)).findAny().orElse(null), "The song of strange user should not be presented in results");

        // List<SongStatProj> content = items.getContent();

        /*

        perhaps, move this into songstatservicetest
        List<Long> concertIds = songStatService.extractConcertIds(items);

        Page<SongStatProj> newItems;
        if(concertIds.size() > 0) {
            List<Concert> concerts = concertDao.findAllById(concertIds);
            newItems = songStatService.attachConcertsToStat(items, concerts, pageReq);
        } else {
            newItems = items;
        }
         */

        // now, check that we have proper amount (total) and received attached concert
    }

    @Test
    void recentSongsCanBeObtained() {
        // create user
        User user = this.addUser("nobody1@example.com");
        User strangeUser = this.addUser("nobody1@example.com");

        // create concert
        Date concerDate1 = parseDateString("2011-05-08 12:00:00");
        Concert concert1 = this.addConcert(concerDate1, user);
        Concert concertStrangeUser = this.addConcert(concerDate1, strangeUser);

        // create songs dated early
        Song song1 = this.addSong("Song");
        Song song2 = this.addSong("Song");
        Song song3 = this.addSong("Song");
        Song song4 = this.addSong("Song");
        Song song5 = this.addSong("Song");
        Song songStrangeUser = this.addSong("Song");

        // attach to ci:

        // one song presented only in early time
        Date ciDate1 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem1 = this.addConcertItem(song1, concert1, ciDate1);

        // two songs in both
        Date ciDate2 = parseDateString("2009-06-08 12:00:00");
        ConcertItem concertItem2 = this.addConcertItem(song2, concert1, ciDate2);

        Date ciDate3 = parseDateString("2018-06-12 12:00:00");
        ConcertItem concertItem3 = this.addConcertItem(song2, concert1, ciDate3);

        Date ciDate4 = parseDateString("2010-06-08 12:00:00");
        ConcertItem concertItem4 = this.addConcertItem(song3, concert1, ciDate4);

        Date ciDate5 = parseDateString("2018-11-12 12:00:00");
        ConcertItem concertItem5 = this.addConcertItem(song3, concert1, ciDate5);

        // two songs in late
        Date ciDate6 = parseDateString("2019-06-08 12:00:00");
        ConcertItem concertItem6 = this.addConcertItem(song4, concert1, ciDate6);

        Date ciDate7 = parseDateString("2018-11-12 12:00:00");
        ConcertItem concertItem7 = this.addConcertItem(song5, concert1, ciDate7);

        Date ciDate8 = parseDateString("2019-05-08 12:00:00");
        ConcertItem concertItem8 = this.addConcertItem(songStrangeUser, concertStrangeUser, ciDate8);


        // select items
        Pageable pageReq = new PageRequest(0, 10);
        Date fromDate = parseDateString("2018-05-01 12:00:00");
        Page<SongProj> pageResult = songSuggestDao.findRecentSongs(pageReq, user, fromDate);
        List<SongProj> items = pageResult.getContent();

        // check that all late songs are present
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song4)).findAny().orElse(null), "The late song4 was not found in results");
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song5)).findAny().orElse(null), "The late song5 was not found in results");

        // check that early songs are not present
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song1)).findAny().orElse(null), "The earl song1 was found in results");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song2)).findAny().orElse(null), "The earl song2 was found in results");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song3)).findAny().orElse(null), "The earl song3 was found in results");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(songStrangeUser)).findAny().orElse(null), "The Recent Song but of Strange User songStrangeUser was found in results");
    }

    @Test
    void abandonedSongsCanBeObtained() {
        // create user
        User user = this.addUser("nobody1@example.com");
        User strangeUser = this.addUser("nobody2@example.com");

        // create concert
        Date concerDate1 = parseDateString("2011-05-08 12:00:00");
        Concert concert1 = this.addConcert(concerDate1, user);
        Concert concertStrangeUser = this.addConcert(concerDate1, strangeUser);

        // create songs dated early
        Song song1 = this.addSong("Song");
        Song song2 = this.addSong("Song");
        Song song3 = this.addSong("Song");
        Song song4 = this.addSong("Song");
        Song song5 = this.addSong("Song");
        Song songStrangeUser = this.addSong("Song");

        // attach to ci:

        // one song presented only in early time, 2 times
        Date ciDate1 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem1 = this.addConcertItem(song1, concert1, ciDate1);

        Date ciDate2 = parseDateString("2009-06-08 12:00:00");
        ConcertItem concertItem2 = this.addConcertItem(song1, concert1, ciDate1);

        // another - only once
        Date ciDate3 = parseDateString("2009-06-08 12:00:00");
        ConcertItem concertItem3 = this.addConcertItem(song2, concert1, ciDate2);

        // one songs in both
        Date ciDate4 = parseDateString("2010-06-08 12:00:00");
        ConcertItem concertItem4 = this.addConcertItem(song3, concert1, ciDate4);

        Date ciDate5 = parseDateString("2018-11-12 12:00:00");
        ConcertItem concertItem5 = this.addConcertItem(song3, concert1, ciDate5);

        // two songs in late
        Date ciDate6 = parseDateString("2019-06-08 12:00:00");
        ConcertItem concertItem6 = this.addConcertItem(song4, concert1, ciDate6);

        Date ciDate7 = parseDateString("2018-11-12 12:00:00");
        ConcertItem concertItem7 = this.addConcertItem(song5, concert1, ciDate7);

        // songs of strange user
        Date ciDate8 = parseDateString("2009-11-12 12:00:00");
        ConcertItem concertItem8 = this.addConcertItem(songStrangeUser, concertStrangeUser, ciDate8);

        Date ciDate9 = parseDateString("2009-11-12 12:00:00");
        ConcertItem concertItem9 = this.addConcertItem(songStrangeUser, concertStrangeUser, ciDate9);

        // select items
        Pageable pageReq = new PageRequest(0, 10);
        Date fromDate = parseDateString("2018-05-01 12:00:00");
        Page<SongCountProj> pageResult = songSuggestDao.findAbandonedSongs(pageReq, user, fromDate, 2);
        List<SongCountProj> items = pageResult.getContent();

        // check that early song are _present_
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song1)).findAny().orElse(null), "The earl song1 was not found in results");

        // check that all late songs are NOT present
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song2)).findAny().orElse(null), "The earl song2 (performed only once) was found in results");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song3)).findAny().orElse(null), "The earl song3 (performed lately too) was found in results");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song4)).findAny().orElse(null), "The late song4 was found in results");
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(song5)).findAny().orElse(null), "The late song5 was found in results");

        // check that song of strange user are not present
        Assertions.assertNull(items.stream().filter(curItem -> curItem.getSong().equals(songStrangeUser)).findAny().orElse(null), "The strangeUser song was found in results");
    }

    @Test
    void beforeSongsCanBeObtained() {
        // create user
        User user = this.addUser("nobody1@example.com");
        User strangeUser = this.addUser("nobody2@example.com");

        // create concert
        Date concerDate1 = parseDateString("2011-05-08 12:00:00");
        Concert concert1 = this.addConcert(concerDate1, user);

        Date concerDate2 = parseDateString("2011-08-08 12:00:00");
        Concert concert2 = this.addConcert(concerDate2, user);

        Date concerDate3 = parseDateString("2012-08-08 12:00:00");
        Concert concert3 = this.addConcert(concerDate3, user);

        Date concertDate4 = parseDateString("2013-08-08 12:00:00");
        Concert concertStrangeUser = this.addConcert(concertDate4, strangeUser);

        // create songs dated early
        Song song1 = this.addSong("Song"); // original song
        Song song2 = this.addSong("Song"); // song before
        Song song3 = this.addSong("Song"); // another song before
        Song song4 = this.addSong("Song"); // another song before
        Song songStrangeUser = this.addSong("Song"); // another song before

        // - - - - - - - - -
        Date ciDate1 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem1 = this.addConcertItem(song1, concert1, ciDate1, 3);

        Date ciDate2 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem2 = this.addConcertItem(song2, concert1, ciDate1, 2);

        // another song of concert 1
        Date ciDate3 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem3 = this.addConcertItem(song4, concert1, ciDate1, 6);

        // - - - - - - - - -
        Date ciDate4 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem4 = this.addConcertItem(song1, concert2, ciDate1, 5);

        Date ciDate5 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem5 = this.addConcertItem(song3, concert2, ciDate1, 4);

        // - - - - - - - - -
        Date ciDate6 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem6 = this.addConcertItem(song1, concert3, ciDate1, 5);

        Date ciDate7 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem7 = this.addConcertItem(song4, concert3, ciDate1, 7);

        // - - - - - - - - -
        Date ciDate8 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem8 = this.addConcertItem(song1, concertStrangeUser, ciDate1, 6);

        Date ciDate9 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem9 = this.addConcertItem(songStrangeUser, concertStrangeUser, ciDate1, 5);


        // - - - - - - - - -
        // find songs by song1
        Pageable pageReq = new PageRequest(0, 10);
        Page<SongProj> pageResult = songSuggestDao.findSongsBefore(pageReq, user, song1);

        List<SongProj> items = pageResult.getContent();

        Assertions.assertEquals(2, items.size(), "The amount of results is wrong");

        // check that only song2 and song3 are present
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song2)).findAny().orElse(null), "The song2 was not found in results");
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song3)).findAny().orElse(null), "The song3 was not found in results");
        // findSongsBefore
    }

    @Test
    void afterSongsCanBeObtained() {
        // create user
        User user = this.addUser("nobody1@example.com");
        User strangeUser = this.addUser("nobody2@example.com");

        // create concert
        Date concerDate1 = parseDateString("2011-05-08 12:00:00");
        Concert concert1 = this.addConcert(concerDate1, user);

        Date concerDate2 = parseDateString("2011-08-08 12:00:00");
        Concert concert2 = this.addConcert(concerDate2, user);

        Date concerDate3 = parseDateString("2012-08-08 12:00:00");
        Concert concert3 = this.addConcert(concerDate3, user);


        Date concertDate4 = parseDateString("2013-08-08 12:00:00");
        Concert concertStrangeUser = this.addConcert(concertDate4, strangeUser);

        // create songs dated early
        Song song1 = this.addSong("Song"); // original song
        Song song2 = this.addSong("Song"); // song before
        Song song3 = this.addSong("Song"); // another song before
        Song song4 = this.addSong("Song"); // another song before
        Song songStrangeUser = this.addSong("Song"); // another song before

        // - - - - - - - - -
        Date ciDate1 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem1 = this.addConcertItem(song1, concert1, ciDate1, 2);

        Date ciDate2 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem2 = this.addConcertItem(song2, concert1, ciDate1, 3);

        // another song of concert 1
        Date ciDate3 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem3 = this.addConcertItem(song4, concert1, ciDate1, 6);

        // - - - - - - - - -
        Date ciDate4 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem4 = this.addConcertItem(song1, concert2, ciDate1, 4);

        Date ciDate5 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem5 = this.addConcertItem(song3, concert2, ciDate1, 5);

        // - - - - - - - - -
        Date ciDate6 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem6 = this.addConcertItem(song1, concert3, ciDate1, 5);

        Date ciDate7 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem7 = this.addConcertItem(song4, concert3, ciDate1, 7);

        // - - - - - - - - -
        Date ciDate8 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem8 = this.addConcertItem(song1, concertStrangeUser, ciDate1, 5);

        Date ciDate9 = parseDateString("2009-05-08 12:00:00");
        ConcertItem concertItem9 = this.addConcertItem(songStrangeUser, concertStrangeUser, ciDate1, 6);


        // - - - - - - - - -
        // find songs by song1
        Pageable pageReq = new PageRequest(0, 10);
        Page<SongProj> pageResult = songSuggestDao.findSongsAfter(pageReq, user, song1);

        List<SongProj> items = pageResult.getContent();

        Assertions.assertEquals(2, items.size(), "The amount of results is wrong");

        // check that only song2 and song3 are present
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song2)).findAny().orElse(null), "The song1 was not found in results");
        Assertions.assertNotNull(items.stream().filter(curItem -> curItem.getSong().equals(song3)).findAny().orElse(null), "The song3 was not found in results");
    }

    private User addUser(String email) {
        User user = new User().
                setEmail(email);

        userDao.save(user);

        return user;
    }

    private Song addSong(String title) {
        Song song = new Song();
        song.setTitle(title);

        songDao.save(song);

        return song;
    }

    private Concert addConcert(Date time, User user) {
        Concert concert = new Concert();
        concert.setTime(time);
        concert.setUser(user);

        return concertDao.save(concert);
    }

    private ConcertItem addConcertItem(Song song, Concert concert) {
        ConcertItem concertItem = new ConcertItem();
        concertItem.setSong(song);
        concertItem.setConcert(concert);

        concertItemDao.save(concertItem);

        return concertItem;
    }

    private ConcertItem addConcertItem(Song song, Concert concert, Date startDate) {
        ConcertItem concertItem = new ConcertItem();
        concertItem.setSong(song);
        concertItem.setConcert(concert);

        concertItemDao.save(concertItem);

        concertItem.setCreateTime(startDate);

        // double save
        concertItemDao.save(concertItem);

        return concertItem;
    }

    private ConcertItem addConcertItem(Song song, Concert concert, Date startDate, int orderValue) {
        ConcertItem concertItem = new ConcertItem();
        concertItem.setSong(song);
        concertItem.setConcert(concert);
        concertItem.setOrderValue(orderValue);

        concertItemDao.save(concertItem);

        concertItem.setCreateTime(startDate);

        // double save
        concertItemDao.save(concertItem);

        return concertItem;
    }



    protected Date parseDateString(String dateString) {
        LocalDateTime localDate = LocalDateTime.parse(
                dateString,
                DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss" )
        );

        return Date.from( localDate.atZone( ZoneId.systemDefault()).toInstant());
    }
}
