package songbook.song.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import songbook.song.repository.SongDao;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;

// different name to not clash with unit tests
@SpringBootTest
public class SongIntegrationalTest {

    @Autowired
    private SongDao songDao;

    @Test
    void isCloudContentNeedsToBeSyncedShouldReturnFalseWhenTheValueIsActual() {
        // we have hardcoded "2" minute in the "Song entity" as threshold of the time that content should be synced with cloud storage
        Song song = new Song();

        LocalDateTime syncDateTime = LocalDateTime.now().minusMinutes(1);
        Date syncTime = this.convertToDate(syncDateTime);

        song.setCloudContentSyncTime(syncTime);

        this.songDao.save(song);

        Optional<Song> repoSong = this.songDao.findById(song.getId());

        Assertions.assertTrue(repoSong.isPresent(), "The song was not found");
        Assertions.assertFalse(repoSong.get().getIsCloudContentNeedsToBeSynced(), "The isCloudContentNeedsToBeSynced value should be true");
    }

    @Test
    void isCloudContentNeedsToBeSyncedShouldReturnFalseWhenThereWasNoValue() {
        Song song = new Song();

        this.songDao.save(song);

        Optional<Song> repoSong = this.songDao.findById(song.getId());
        Assertions.assertTrue(repoSong.isPresent(), "The song was not found");
        Assertions.assertTrue(repoSong.get().getIsCloudContentNeedsToBeSynced(), "The isCloudContentNeedsToBeSynced value should be true");
    }

    @Test
    void isCloudContentNeedsToBeSyncedShouldReturnFalseWhenTheValueIsObsolete() {
        // we have hardcoded "2" minute in the "Song entity" as threshold of the time that content should be synced with cloud storage
        Song song = new Song();

        LocalDateTime syncDateTime = LocalDateTime.now().minusMinutes(3);
        Date syncTime = this.convertToDate(syncDateTime);

        song.setCloudContentSyncTime(syncTime);

        this.songDao.save(song);
        Optional<Song> repoSong = this.songDao.findById(song.getId());
        Assertions.assertTrue(repoSong.isPresent(), "The song was not found");
        Assertions.assertTrue(repoSong.get().getIsCloudContentNeedsToBeSynced(), "The isCloudContentNeedsToBeSynced value should be true");
    }

    private Date convertToDate(LocalDateTime localDateTime) {
        return Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    }
}
