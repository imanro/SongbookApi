package songbook.suggest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import songbook.concert.entity.ConcertItem;
import songbook.suggest.entity.SongStat;
import songbook.suggest.entity.SongStatProj;



public interface SongSuggestDao extends JpaRepository<ConcertItem, Long> {

    // If you'll have similar problems, google for the topic: Projections (open, closed) or DTO
    // I use open projection here, and I managed to access to method of TupletMap.get() to obtain my entity along with total
    // You can use @Formula for counts as well but I thought that count(1) is not related to the concert item

    @Query(value="SELECT count(1) as total, e as entity from ConcertItem e JOIN FETCH e.song " +
            "GROUP BY e.song ORDER BY count(1) DESC",
    countQuery = "SELECT count(1) from ConcertItem e GROUP BY e.song")
    Page<SongStatProj> findPopularConcertItems(Pageable page);
}
