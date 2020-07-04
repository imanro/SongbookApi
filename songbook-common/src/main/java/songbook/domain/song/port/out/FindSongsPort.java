package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindSongsPort {
    Page<Song> findSongs(User user, Pageable pageable);
}
