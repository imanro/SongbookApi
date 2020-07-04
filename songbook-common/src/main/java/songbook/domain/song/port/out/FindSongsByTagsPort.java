package songbook.domain.song.port.out;

import songbook.domain.song.entity.Song;
import songbook.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FindSongsByTagsPort {
    Page<Song> findSongsByTags(List<Long> tagIds, User user, Pageable pageable);
}
