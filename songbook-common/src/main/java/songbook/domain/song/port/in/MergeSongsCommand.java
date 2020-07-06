package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

public interface MergeSongsCommand {
    Song mergeSongs(long mergedSongId, long masterSongId, User user) throws MergeSongsSongNotFoundException;
}
