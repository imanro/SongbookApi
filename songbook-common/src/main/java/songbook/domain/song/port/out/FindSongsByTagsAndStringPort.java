package songbook.domain.song.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import java.util.List;

public interface FindSongsByTagsAndStringPort {
    Page<Song> findSongsByTagsAndString(String searchString, List<Long> tagIds, User user, Pageable pageable);
}
