package songbook.song.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import songbook.song.entity.Song;
import songbook.tag.entity.Tag;
import static org.junit.jupiter.api.Assertions.*;

// https://www.baeldung.com/junit-5-runwith
// https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/
@SpringBootTest
class TagDaoTest {

    Song song;
    Tag tag;

    @Autowired
    private SongDao songDao;


    // SongDao songDao;

    @BeforeEach
    void init() {
    }

    @Test
    void saveSong() {
        System.out.println("Iio iio");

        Song song = new Song();
        Tag tag1 = new Tag();
        tag1.setTitle("I am title");

        Tag tag2 = new Tag();
        tag2.setTitle("I am title 2");


        song.getTags().add(tag1);
        song.getTags().add(tag2);

        songDao.save(song);
    }
}

