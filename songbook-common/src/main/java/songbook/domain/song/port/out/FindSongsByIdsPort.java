package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import java.util.List;

public interface FindSongsByIdsPort {
    List<Song> findSongsByIds(List<Long> ids, User user);
}
