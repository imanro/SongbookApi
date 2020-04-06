package songbook.suggest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import songbook.concert.entity.ConcertItem;
import songbook.suggest.entity.SongProj;
import songbook.suggest.entity.SongStat;
import songbook.suggest.entity.SongStatProj;

import java.time.LocalDate;
import java.util.Date;


public interface SongSuggestDao extends JpaRepository<ConcertItem, Long> {

    // If you'll have similar problems, google for the topic: Projections (open, closed) or DTO
    // I use open projection here, and I managed to access to method of TupletMap.get() to obtain my entity along with total
    // You can use @Formula for counts as well but I thought that count(1) is not related to the concert item

    @Query(value="SELECT count(1) as total, e as entity, e.song as song, max(e.concert) as lastConcert from ConcertItem e " +
            "GROUP BY e.song ORDER BY count(1) DESC",
            countQuery = "SELECT count(1) from ConcertItem e GROUP BY e.song")
    Page<SongStatProj> findPopularConcertItems(Pageable page);

    @Query(value="SELECT ci1.song as song FROM ConcertItem ci1 LEFT JOIN ConcertItem ci2 ON ci1.song=ci2.song AND ci2.createTime < :fromDate WHERE ci2.id IS NULL GROUP BY ci1.song ORDER BY function('RAND')",
    countQuery = "SELECT count(1) FROM ConcertItem ci1 LEFT JOIN ConcertItem ci2 ON ci1.song=ci2.song AND ci2.createTime < :fromDate WHERE ci2.id IS NULL GROUP BY ci1.song")
    Page<SongProj> findRecentSongs(Pageable page, Date fromDate);
}