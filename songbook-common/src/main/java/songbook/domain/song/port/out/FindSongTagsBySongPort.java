package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.tag.entity.Tag;

import java.util.List;

public interface FindSongTagsBySongPort {
    List<Tag> findSongTagsBySong(Song song);
}
