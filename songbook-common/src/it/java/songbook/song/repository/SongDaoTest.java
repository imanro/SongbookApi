package songbook.song.repository;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import songbook.common.BaseIt;
import songbook.domain.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


// https://www.baeldung.com/junit-5-runwith
// https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/
@SpringBootTest
class SongDaoTest extends BaseIt {

    Song song;

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongContentDao songContentDao;

    @Autowired
    private UserDao userDao;

    private UserDao userDao2;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;


    // SongDao songDao;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        song = new Song();
    }

    @AfterEach
    void deleteAll() {
        // songContentDao.deleteAll();
        // songDao.deleteAll();
        // userDao.deleteAll();
    }


    Long prepareFindByIdWithHeader(User user, User user2) {
        return transactionTemplate.execute(status -> {

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

            return song.getId();
        });
    }

    @Test
    @DisplayName("Can find by id with header")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Commit
    void findByIdWithHeader() {

        User user = addUser("someemail@example.com");
        User user2 = addUser("someanotheremail@example.com");

        Long needleSongId = prepareFindByIdWithHeader(user, user2);


        transactionTemplate.execute(status -> {
            songDao.initContentUserFilter(user);

            // songDao.initContentUserFilter(user);
            Song foundSong = songDao.findById(needleSongId).orElse(null);

            assertNotNull(foundSong, "The song was not added");

            Collection<SongContent> headers = foundSong.getHeaders();
            assertNotNull(headers, "The headers was not set");
            assertEquals(3, headers.size(), "The count of headers is wrong");

            Iterator<SongContent> it = foundSong.getHeaders().iterator();
            SongContent header = it.next();

            assertTrue(header.getIsFavorite(), "The first header in the list should have \"isFavorite\" property set");
            return true;
        });
    }

    void prepareFindAllWithHeaders(User user, User user2) {
        transactionTemplate.execute(status -> {

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

            return true;
        });
    }

    @Test
    @DisplayName("Can find all with header")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Commit
    void findAllWithHeaders() {

        User user = addUser("someemail@example.com");
        User user2 = addUser("someanotheremail@example.com");

        prepareFindAllWithHeaders(user, user2);

        transactionTemplate.execute(status -> {

            songDao.initContentUserFilter(user);
            List<Song> foundSongs = songDao.findAllWithHeadersAndTags();

            assertEquals(2, foundSongs.size(), "The size is wrong");

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


            return true;
        });
    }

    @Test
    @DisplayName("Can find by header with headers")
    void findByHeaderWithHeaders() {

        User user = addUser("nobody@example.com");

        transactionTemplate.execute(status -> {
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

            return true;
        });

        transactionTemplate.execute(status -> {
            List<Song> foundSongs = songDao.findAllByHeaderWithHeadersAndTags("name");
            Assertions.assertEquals(1, foundSongs.size(), "The found songs size is wrong");
            return true;
        });
    }

}

