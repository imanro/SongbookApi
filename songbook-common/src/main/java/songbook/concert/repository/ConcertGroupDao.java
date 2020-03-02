package songbook.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import songbook.concert.entity.ConcertGroup;
import songbook.concert.entity.ConcertItem;
import java.util.List;

public interface ConcertGroupDao extends JpaRepository<ConcertGroup, Long> {
    List<ConcertItem> findByConcertId(long songID);

    Page<ConcertItem> findByConcertId(long songID, Pageable pageable);
}
