package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.song.entity.SongContent;

import java.util.List;

public interface FindSongContentsBySongPort {
    List<SongContent> findSongContents(Song song);
}
