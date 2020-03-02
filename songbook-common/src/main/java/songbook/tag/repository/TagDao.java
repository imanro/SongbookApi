package songbook.tag.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import songbook.tag.entity.Tag;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface TagDao extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    List<Tag> findByTitleAllIgnoreCase(String title);

    Page<Tag> findByTitleAllIgnoreCase(String title, Pageable pageable);

    List<Tag> findByTitleContainsAllIgnoreCase(String titlePart);

    Page<Tag> findByTitleContainsAllIgnoreCase(String titlePart, Pageable pageable);
}
