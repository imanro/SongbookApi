package songbook.song.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.suggest.entity.SongStat;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertItemDao;
import songbook.song.entity.Song;
import songbook.suggest.entity.SongStatProj;
import songbook.suggest.repository.SongSuggestDao;
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
    private ConcertDao concertDao;

    @Autowired
    private ConcertItemDao concertItemDao;

    @Autowired
    private SongSuggestDao songSuggestDao;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    void init() {

    }


    @Test
    void popularConcertItemsCanBeObtained() {
        User user = this.addUser("nobody1@example.com");

        // add some songs
        Song song1 = this.addSong("Song");
        Song song2 = this.addSong("Song");
        Song song3 = this.addSong("Song");

        // add concert
        Date concerDate = parseDateString("2011-05-08 12:00:00");
        Concert concert1 = this.addConcert(concerDate, user);

        // ...and, some of concertItems
        ConcertItem concertItem1 = addConcertItem(song1, concert1);
        ConcertItem concertItem2 = addConcertItem(song2, concert1);
        ConcertItem concertItem3 = addConcertItem(song3, concert1);

        System.out.println("The user's ID is: " + user.getId());

        Pageable pageReq = new PageRequest(0, 10);

        // Page<SongStat> items = songSuggestDao.findPopularConcertItems(user, pageReq);
        List<SongStatProj> items = songSuggestDao.findPopularConcertItems();

        // System.out.println("Total elements amount: " + items.getTotalElements());

        // SongStat item = items.get().findFirst().get();

        // System.out.println("The song id is:" + item.getSong().getId());


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

        concertDao.save(concert);

        return concert;
    }

    private ConcertItem addConcertItem(Song song, Concert concert) {
        ConcertItem concertItem = new ConcertItem();
        concertItem.setSong(song);
        concertItem.setConcert(concert);

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
