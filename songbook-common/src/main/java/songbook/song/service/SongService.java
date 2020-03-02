package songbook.song.service;

import org.springframework.stereotype.Service;
import songbook.song.entity.Song;
import songbook.song.entity.SongContent;
import songbook.tag.entity.Tag;

import java.util.List;

public interface SongService {

    boolean syncCloudContent(long songID);

    List<Song> findAllByHeader(String header);

    List<Song> getAllByUser(long userID);

    List<Song> getCollectionByConcert(long concertID);

    List<Song> getCollectionLongNotUsed();

    List<Song> getCollectionUsedLastMonths();

    List<Song> getCollectionTakenLastMonths();

    List<Song> getCollectionPopular();

    int mergeSongs(long masterID, List<Long> toMerge);

    Song createSong();

    SongContent createSongContent();

    void deleteSong(Song song);

    boolean isCloudContentShouldBeSynced(Song song);
}
