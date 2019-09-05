package songbook.song.repository;

import org.springframework.stereotype.Repository;
import songbook.song.entity.SongContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface SongContentDao extends JpaRepository<SongContent, Long> {
    List<SongContent> findBySongId(long songID);

    Page<SongContent> findBySongId(long songID, Pageable pageable);

    List<SongContent> findBySongIdAndType(long songID, String type);

    Page<SongContent> findBySongIdAndType(long songID, Pageable type);
}
