package songbook.song.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import songbook.song.entity.Tag;
import javax.transaction.Transactional;

@Transactional
@Repository
public interface TagDao extends JpaRepository<Tag, Long> {

}
