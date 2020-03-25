package songbook.song.repository;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


// https://www.baeldung.com/junit-5-runwith
// https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/
@SpringBootTest
class SongDaoTest {

    Song song;

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongContentDao songContentDao;

    @Autowired
    private UserDao userDao;

    // SongDao songDao;

    @BeforeEach
    void init() {
        song = new Song();
    }

    // @Test
    void saveSong() {
        song.setId(111);
        songDao.save(song);
        // assertEquals(111, song.getId(), "The assigned id is not match to that expected");
    }


    @Test
    @DisplayName("Can find by id with header")
    void findByIdWithHeader() {

        User user = new User();
        user.setEmail("nobody@example.com");

        userDao.save(user);

        User user2 = new User();
        user2.setEmail("nobody2@example.com");

        userDao.save(user2);

        song.setTitle("Some creative title");
        songDao.save(song);

        SongContent header1 = new SongContent().
                setSong(song).
                setContent("1111").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        SongContent favoriteHeader = new SongContent().
                setSong(song).
                setContent("2222").
                setType(SongContentTypeEnum.HEADER).
                setIsFavorite(true).
                setUser(user);

        SongContent header3 = new SongContent().
                setSong(song).
                setContent("3333").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        SongContent strangeUserHeader = new SongContent().
                setType(SongContentTypeEnum.HEADER).
                setUser(user2).
                setSong(song);

        Song anotherSong = new Song().
                setTitle("Another song");
        songDao.save(anotherSong);

        SongContent anothersSongHeader = new SongContent().
                setContent("Another's song header").
                setUser(user).
                setType(SongContentTypeEnum.HEADER).
                setSong(anotherSong);

        songContentDao.save(header1);
        songContentDao.save(header3);
        songContentDao.save(favoriteHeader);
        songContentDao.save(strangeUserHeader);
        songContentDao.save(anothersSongHeader);

        Optional<Song> foundSongOpt = songDao.findByIdWithHeaders(song.getId(), user);
        assertTrue(foundSongOpt.isPresent(), "The song is not found");

        Song foundSong = foundSongOpt.get();
        assertEquals(3, foundSong.getHeaders().size(), "The count of headers is wrong");

        try {
            // Think about types, may be not set, but arraylist
            Iterator it = foundSong.getHeaders().iterator();
            Object header = it.next();
            assertTrue(((SongContent) header).getIsFavorite(), "The value is wrong");

        } catch (IndexOutOfBoundsException ce) {
            throw ce;
        }

        // foundSong.ifPresent(x -> System.out.println(x.getHeaders().getContent()));
    }

    @Test
    @DisplayName("Can find all with header")
    void findAllWithHeaders() {

        User user = new User();
        user.setEmail("nobody@example.com");

        userDao.save(user);

        User user2 = new User();
        user2.setEmail("nobody223@example.com");

        userDao.save(user2);

        song.setTitle("Some creative title");
        songDao.save(song);

        Song song2 = new Song();
        song2.setTitle("Some creative title");
        songDao.save(song2);

        SongContent song1Header1 = new SongContent().
                setSong(song).
                setContent("just header").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        SongContent song1FavoriteHeader = new SongContent().
                setSong(song).
                setContent("favorite header").
                setType(SongContentTypeEnum.HEADER).
                setIsFavorite(true).
                setUser(user);

        SongContent song1Header3 = new SongContent().
                setSong(song).
                setContent("just header 3").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        SongContent song1Header4AnotherUser = new SongContent().
                setSong(song).
                setContent("just header 4").
                setType(SongContentTypeEnum.HEADER).
                setUser(user2);


        SongContent song2Header1 = new SongContent().
                setSong(song2).
                setContent("just header song 2").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        SongContent song2FavoriteHeader = new SongContent().
                setType(SongContentTypeEnum.HEADER).
                setIsFavorite(true).
                setContent("favorite header song 2").
                setUser(user).
                setSong(song2);

        songContentDao.save(song1Header1);
        songContentDao.save(song1FavoriteHeader);
        songContentDao.save(song1Header3);
        songContentDao.save(song1Header4AnotherUser);
        songContentDao.save(song2Header1);
        songContentDao.save(song2FavoriteHeader);

        List<Song> foundSongs = songDao.findAllWithHeaders(user);

        assertEquals(2, foundSongs.size(), "The size is wrong");

        try {
            Song foundSong1 = foundSongs.get(0);
            assertEquals(3, foundSong1.getHeaders().size(), "The count of headers of found song 1 is wrong");

            try {
                // Think about types, may be not set, but arraylist
                Iterator it = foundSong1.getHeaders().iterator();
                Object header = it.next();
                assertTrue(((SongContent) header).getIsFavorite(), "The value is wrong");

            } catch (IndexOutOfBoundsException ce) {
                throw ce;
            }

        } catch (IndexOutOfBoundsException e) {
            throw e;
        }
        try {
            Song foundSong2 = foundSongs.get(1);
            assertEquals(2, foundSong2.getHeaders().size(), "The count of headers of found song 2 is wrong");

            try {
                // Think about types, may be not set, but arraylist
                Iterator it = foundSong2.getHeaders().iterator();
                Object header = it.next();
                assertTrue(((SongContent) header).getIsFavorite(), "The value is wrong");

            } catch (IndexOutOfBoundsException ce) {
                throw ce;
            }

        } catch (IndexOutOfBoundsException e) {
            throw e;
        }
    }


    // @Test
    @DisplayName("Can find by header with headers")
    void findByHeaderWithHeaders() {
        User user = new User();
        user.setEmail("nobody@example.com");
        userDao.save(user);

        song.setTitle("Some creative title");
        songDao.save(song);

        SongContent header1 = new SongContent().
                setSong(song).
                setContent("String in which Name is in the middle").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        Song anotherSong = new Song();
        songDao.save(anotherSong);

        SongContent header2 = new SongContent().
                setSong(anotherSong).
                setContent("String in which param is not presented").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);

        songContentDao.save(header1);
        songContentDao.save(header2);

        // List<Song> foundSongs = songDao.findByHeaderWithHeaders("name", user);
        // System.out.println(String.format("%s records obtained", foundSongs.size()));
    }

    void prepareFindByHeaderWithHeadersPageable() {
        User user = new User();
        user.setEmail("nobody3@example.com");
        userDao.save(user);

        Song song1 = new Song();
        song1.setTitle("Song A");
        songDao.save(song1);

        Song song2 = new Song();
        song2.setTitle("Song B");
        songDao.save(song2);

        SongContent header1 = new SongContent().
                setSong(song).
                setContent("Header Song A").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);
        songContentDao.save(header1);

        SongContent header2 = new SongContent().
                setSong(song).
                setContent("Header Song B").
                setType(SongContentTypeEnum.HEADER).
                setUser(user);
        songContentDao.save(header2);

        Pageable page = PageRequest.of(0, 1, Sort.Direction.ASC, "id");
    }
}

