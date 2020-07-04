package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.song.entity.SongContent;

public interface DeleteSongContentPort {

    boolean deleteSongContent(Song song, SongContent content);

}
