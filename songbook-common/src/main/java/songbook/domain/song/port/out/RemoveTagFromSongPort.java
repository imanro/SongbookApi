package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.tag.entity.Tag;

public interface RemoveTagFromSongPort {
    Song removeTagFromSong(Song song, Tag tag);
}
