package songbook.domain.song.port.out;

import songbook.tag.entity.Tag;

import java.util.Optional;

public interface FindTagByIdPort {
    Optional<Tag> findTagById(long id);
}
