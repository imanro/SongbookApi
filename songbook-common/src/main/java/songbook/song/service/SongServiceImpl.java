package songbook.song.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.song.repository.SongDao;
import songbook.song.repository.SongContentDao;

import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongContentDao songContentDao;


    @Override
    public List<Song> findAllByHeader(String header) {
        return null;
    }

    @Override
    public List<Song> getAllByUser(long userID) {
        return null;
    }

    @Override
    public List<Song> getCollectionByConcert(long concertID) {
        return null;
    }

    @Override
    public List<Song> getCollectionLongNotUsed() {
        return null;
    }

    @Override
    public List<Song> getCollectionUsedLastMonths() {
        return null;
    }

    @Override
    public List<Song> getCollectionTakenLastMonths() {
        return null;
    }

    @Override
    public List<Song> getCollectionPopular() {
        return null;
    }

    @Override
    public int mergeSongs(long masterID, List<Long> toMerge) {
        return 0;
    }

    @Override
    public boolean syncCloudContent(long songID) {
        return false;
    }

    @Override
    public Song createSong() {
        return new Song();
    }

    @Override
    public SongContent createSongContent() {
        return new SongContent();
    }

    @Override
    public void deleteSong(Song song) {

    }

    @Override
    public boolean isCloudContentShouldBeSynced(Song song) {
        return false;
    }
}
