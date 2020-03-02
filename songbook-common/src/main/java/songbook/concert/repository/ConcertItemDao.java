package songbook.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import songbook.concert.entity.ConcertItem;

import java.util.List;

public interface ConcertItemDao extends JpaRepository<ConcertItem, Long> {
    List<ConcertItem> findBySongId(long songID);

    Page<ConcertItem> findBySongId(long songID, Pageable pageable);
}
