package songbook.domain.song.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import songbook.adapter.song.out.ConcertRdbmAdapter;
import songbook.adapter.song.out.SongRdbmAdapter;
import songbook.adapter.tag.out.TagRdbmAdapter;
import songbook.common.BaseIt;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.domain.song.entity.Song;
import songbook.domain.song.port.in.MergeSongsSongNotFoundException;
import songbook.song.entity.SongContent;
import songbook.song.entity.SongContentTypeEnum;
import songbook.tag.entity.Tag;
import songbook.user.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SyncSongContentCommand it")
public class SyncSongContentCommandIntegrationTest extends BaseIt {

    private final songbook.domain.song.port.in.MergeSongsCommand mergeSongsCommand;

    private final PlatformTransactionManager transactionManager;

    private final SongRdbmAdapter songRdbmAdapter;

    private final ConcertRdbmAdapter concertRdbmAdapter;

    private final TagRdbmAdapter tagRdbmAdapter;

    private TransactionTemplate transactionTemplate;

    @Autowired
    public SyncSongContentCommandIntegrationTest(songbook.domain.song.port.in.MergeSongsCommand mergeSongsCommand,
                                                 PlatformTransactionManager transactionManager,
                                                 SongRdbmAdapter songRdbmAdapter,
                                                 ConcertRdbmAdapter concertRdbmAdapter,
                                                 TagRdbmAdapter tagRdbmAdapter
                                                 ) {
        this.mergeSongsCommand = mergeSongsCommand;
        this.transactionManager = transactionManager;
        this.songRdbmAdapter = songRdbmAdapter;
        this.concertRdbmAdapter = concertRdbmAdapter;
        this.tagRdbmAdapter = tagRdbmAdapter;
    }

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void songCanBeMerged() throws MergeSongsSongNotFoundException{

        // one transaction
        // create user
        User user = addUser("somebody@example.com");

        Concert concert = transactionTemplate.execute(status -> {
            Concert transactionConcert = new Concert();
            Date concerTime = parseDateString("2011-05-08 12:00:00");
            transactionConcert.setTime(concerTime);
            transactionConcert.setUser(user);

            concertRdbmAdapter.saveConcert(transactionConcert);
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

        assertNotNull(masterSong, "The master song was not stored correctly");
        assertNotNull(mergedSong, "The merged song was not stored correctly");
        assertNotNull(concert, "The concert was not stored correctly");

        Song masterSongRead = transactionTemplate.execute(status -> {
            return songRdbmAdapter.findSongById(masterSong.getId(), user).orElse(null);
        });

        Song mergedSongRead = transactionTemplate.execute(status -> {
            return songRdbmAdapter.findSongById(mergedSong.getId(), user).orElse(null);
        });

        // perform merge
        transactionTemplate.execute(status -> {
            try {
                mergeSongsCommand.mergeSongs(mergedSongRead.getId(), masterSongRead.getId(), user);
            } catch (MergeSongsSongNotFoundException e) {
                System.out.println("An exception has occurred");
                e.printStackTrace();
            }
            return true;
        });

        // check the content of master song (new transaction)

        Song checkMasterSong = transactionTemplate.execute(status -> {
            return songRdbmAdapter.findSongById(masterSong.getId(), user).orElse(null);
        });

        Concert checkConcert = transactionTemplate.execute(status -> {
            return concertRdbmAdapter.findConcertById(concert.getId()).orElse(null);
        });


        assertNotNull(checkMasterSong, "The check song wasn't found");
        assertNotNull(checkConcert, "The check concert wasn't found");

        // check that song has 4 content
        assertEquals(4, checkMasterSong.getContent().size(), "The content size is wrong");

        // check that song has 4 tags
        assertEquals(4, checkMasterSong.getTags().size(), "The tags size is wrong");

        // check that concert items points on master song
        Set<ConcertItem> checkItems = checkConcert.getItems();

        checkItems.stream().forEach(item -> {
            assertEquals(masterSong.getId(), item.getSong().getId(), "The referenced song id is not right");
        });

        // check that merged song no longer exist
        Song checkMergedSong = transactionTemplate.execute(status -> {
            return songRdbmAdapter.findSongById(mergedSong.getId(), user).orElse(null);
        });

        assertNull(checkMergedSong, "The merged song should be deleted");
    }

    private Song prepareSongCanBeMergedAddSong(User user, List<String> tagStrings, List<String> titleStrings, Concert concert) {

        Song song = new Song();
        song.setTitle("song1");

        tagStrings.stream().forEach(tagString -> {
            Tag tag = new Tag();
            tag.setTitle(tagString);
            tagRdbmAdapter.saveTag(tag);

            song.getTags().add(tag);
        });

        songRdbmAdapter.saveSong(song);

        titleStrings.stream().forEach(title -> {
            SongContent content = new SongContent();
            content.setContent(title);
            content.setType(SongContentTypeEnum.HEADER);
            content.setUser(user);

            content.setSong(song);

            songRdbmAdapter.saveSongContent(content);
        });

        ConcertItem concertItem = new ConcertItem();
        concertItem.setSong(song);
        concertItem.setConcert(concert);

        concertRdbmAdapter.saveConcertItem(concertItem);

        return song;
    }

}
