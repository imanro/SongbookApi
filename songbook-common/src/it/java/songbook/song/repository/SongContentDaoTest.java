package songbook.song.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import songbook.song.entity.SongContent;
import songbook.song.entity.Song;
import songbook.song.entity.SongContentTypeEnum;
import songbook.user.entity.User;
import songbook.user.entity.repository.UserDao;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;


// https://www.baeldung.com/junit-5-runwith
// https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/
@SpringBootTest
class SongContentDaoTest {

    Song song;

    User user;

    ArrayList<SongContent> contents;

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
        user = new User().
                setEmail("nobody@example.com");
    }

    @Test
    void saveContent() {
        songDao.save(song);
        userDao.save(user);

        SongContent content1 = new SongContent().
                setSong(song).
                setUser(user).
                setContent("My long content").
                setUrl("http://my.url").
                setType(SongContentTypeEnum.HEADER).
                setFileName("I am file.txt").
                setMimeType("mime/type1").
                setIsFavorite(true);

        songContentDao.save(content1);

        SongContent content2 = new SongContent().
                setSong(song).
                setUser(user).
                setContent("My long content 2").
                setUrl("http://my.anotherurl").
                setType(SongContentTypeEnum.GDRIVE_CLOUD_FILE).
                setFileName("I am another file.txt").
                setMimeType("mime/type2").
                setIsFavorite(true);

        songContentDao.save(content2);

        Assertions.assertNotNull(content1.getId());
        Assertions.assertNotNull(content2.getId());
        Assertions.assertTrue(content1.getId() > 0, "The value is wrong");
        // assertEquals(111, song.getId(), "The assigned id is not match to that expected");
    }
}

