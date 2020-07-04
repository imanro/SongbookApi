package songbook.song.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import songbook.common.BaseIt;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertItemDao;
import songbook.domain.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.song.repository.SongContentDao;
import songbook.song.repository.SongDao;
import songbook.tag.entity.Tag;
import songbook.tag.repository.TagDao;
import songbook.user.entity.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class SongServiceIntegrationTest extends BaseIt {

    @Autowired()
    SongService songService;

    @Autowired()
    SongDao songDao;

    @Autowired()
    SongContentDao songContentDao;

    @Autowired()
    ConcertItemDao concertItemDao;

    @Autowired()
    ConcertDao concertDao;

    @Autowired()
    TagDao tagDao;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @AfterEach
    void deleteAll() {

    }

    @Test
    void songCanBeMerged() throws Exception {

        // one transaction
        // create user
        User user = addUser("somebody@example.com");

        Concert concert = transactionTemplate.execute(status -> {
            Concert transactionConcert = new Concert();
            Date concerTime = parseDateString("2011-05-08 12:00:00");
            transactionConcert.setTime(concerTime);
            transactionConcert.setUser(user);

            concertDao.save(transactionConcert);
            return transactionConcert;
        });

        Song masterSong = transactionTemplate.execute(status -> {
            // create master song, tags, concert on behalf of that user
            return prepareSongCanBeMergedAddSong(user, Arrays.asList("tag1", "tag2"), Arrays.asList("title1", "title2"), concert);
        });

        Song mergedSong = transactionTemplate.execute(status -> {
            // create merged song, tags, concert on behalf of that user
            return prepareSongCanBeMergedAddSong(user, Arrays.asList("tag3", "tag4"), Arrays.asList("title3", "title4"), concert);
        });

        Assertions.assertNotNull(masterSong, "The master song was not stored correctly");
        Assertions.assertNotNull(mergedSong, "The merged song was not stored correctly");
        Assertions.assertNotNull(concert, "The concert was not stored correctly");

        Song masterSongRead = transactionTemplate.execute(status -> {
            return songDao.findById(masterSong.getId()).orElse(null);
        });

        Song mergedSongRead = transactionTemplate.execute(status -> {
            return songDao.findById(mergedSong.getId()).orElse(null);
        });

        // perform merge
        transactionTemplate.execute(status -> {
            songService.mergeSong(mergedSongRead, masterSongRead);
            return true;
        });

        // check the content of master song (new transaction)

        Song checkMasterSong = transactionTemplate.execute(status -> {
            return songDao.findById(masterSong.getId()).orElse(null);
        });

        Concert checkConcert = transactionTemplate.execute(status -> {
            return concertDao.findById(concert.getId()).orElse(null);
        });


        Assertions.assertNotNull(checkMasterSong, "The check song wasn't found");
        Assertions.assertNotNull(checkConcert, "The check concert wasn't found");


            // check that song has 4 content
        Assertions.assertEquals(4, checkMasterSong.getContent().size(), "The content size is wrong");

            // check that song has 4 tags
        Assertions.assertEquals(4, checkMasterSong.getTags().size(), "The tags size is wrong");

        // check that concert items points on master song
        Set<ConcertItem> checkItems = checkConcert.getItems();

        checkItems.stream().forEach(item -> {
            Assertions.assertEquals(masterSong.getId(), item.getSong().getId(), "The referenced song id is not right");
        });

        // check that merged song no longer exist
        Song checkMergedSong = transactionTemplate.execute(status -> {
            return songDao.findById(mergedSong.getId()).orElse(null);
        });

        Assertions.assertNull(checkMergedSong, "The merged song should be deleted");
    }

    private Song prepareSongCanBeMergedAddSong(User user, List<String> tagStrings, List<String> titleStrings, Concert concert) {

        Song song = new Song();
        song.setTitle("song1");

        tagStrings.stream().forEach(tagString -> {
            Tag tag = new Tag();
            tag.setTitle(tagString);
            tagDao.save(tag);

            song.getTags().add(tag);
        });

        songDao.save(song);

        titleStrings.stream().forEach(title -> {
            SongContent content = new SongContent();
            content.setContent(title);
            content.setType(SongContentTypeEnum.HEADER);
            content.setUser(user);

            content.setSong(song);

            songContentDao.save(content);
        });

        ConcertItem concertItem = new ConcertItem();
        concertItem.setSong(song);
        concertItem.setConcert(concert);

        concertItemDao.save(concertItem);

        return song;
    }


}
