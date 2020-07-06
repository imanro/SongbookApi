package songbook.domain.song.port.in;

import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import java.util.List;

public interface SongsByIdsQuery {
    List<Song> getSongsByIds(List<Long> ids, User user);
}
