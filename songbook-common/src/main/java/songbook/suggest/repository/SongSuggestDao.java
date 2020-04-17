package songbook.suggest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import songbook.concert.entity.ConcertItem;
import songbook.song.entity.Song;
import songbook.suggest.entity.SongCountProj;
import songbook.suggest.entity.SongProj;
import songbook.suggest.entity.PopularSongProj;
import songbook.user.entity.User;

import java.util.Date;


public interface SongSuggestDao extends JpaRepository<ConcertItem, Long> {

    // If you'll have similar problems, google for the topic: Projections (open, closed) or DTO
    // I use open projection here, and I managed to access to method of TupletMap.get() to obtain my entity along with total
    // You can use @Formula for counts as well but I thought that count(1) is not related to the concert item

    @Query(value="SELECT count(1) as total, e as entity, e.song as song, max(e.concert) as lastConcert from ConcertItem e JOIN Concert c ON e.concert=c WHERE c.user=:user " +
            "GROUP BY e.song ORDER BY count(1) DESC",
            countQuery = "SELECT count(1) from ConcertItem e JOIN Concert c ON e.concert=c WHERE c.user=:user " +
                    "GROUP BY e.song ")
    Page<PopularSongProj> findPopularConcertItems(Pageable page, @Param("user") User user);

    @Query(value="SELECT ci1.song as song FROM ConcertItem ci1 JOIN Concert c ON ci1.concert=c LEFT JOIN ConcertItem ci2 ON ci1.song=ci2.song AND ci2.createTime < :fromDate WHERE c.user=:user AND ci2.id IS NULL GROUP BY ci1.song ORDER BY function('RAND')",
            countQuery = "SELECT count(1) FROM ConcertItem ci1 JOIN Concert c ON ci1.concert=c LEFT JOIN ConcertItem ci2 ON ci1.song=ci2.song AND ci2.createTime < :fromDate WHERE c.user=:user AND ci2.id IS NULL GROUP BY ci1.song")
    Page<SongProj> findRecentSongs(Pageable page, @Param("user") User user, @Param("fromDate") Date fromDate);

    @Query(value="SELECT count(1) as total, ci1.song as song FROM ConcertItem ci1 JOIN Concert c ON c=ci1.concert LEFT JOIN ConcertItem ci2 ON ci1.song=ci2.song AND ci2.createTime > :fromDate WHERE c.user=:user AND ci2.id IS NULL GROUP BY ci1.song HAVING count(1) >= :performancesAmount ORDER BY function('RAND') ",
            countQuery = "SELECT count(1) FROM ConcertItem ci1 JOIN Concert c ON c=ci1.concert LEFT JOIN ConcertItem ci2 ON ci1.song=ci2.song AND ci2.createTime > :fromDate WHERE c.user=:user AND ci2.id IS NULL GROUP BY ci1.song HAVING count(1) >= :performancesAmount")
    Page<SongCountProj> findAbandonedSongs(Pageable page, @Param("user") User user, @Param("fromDate") Date fromDate, @Param("performancesAmount") long performancesAmount);

    @Query(value="SELECT ci2.song as song from ConcertItem ci1 JOIN Concert c ON c=ci1.concert LEFT JOIN ConcertItem ci2 ON ci2.concert=ci1.concert AND ci2.orderValue = ci1.orderValue - 1 WHERE c.user=:user AND ci1.song=:sampleSong AND ci2.song IS NOT NULL GROUP BY ci2.song ORDER BY function('RAND')",
            countQuery = "SELECT count(1) from ConcertItem ci1 JOIN Concert c ON c=ci1.concert LEFT JOIN ConcertItem ci2 ON ci2.concert=ci1.concert AND ci2.orderValue = ci1.orderValue - 1 WHERE c.user=:user AND ci1.song=:sampleSong AND ci2.song IS NOT NULL GROUP BY ci2.song")
    Page<SongProj> findSongsBefore(Pageable page, @Param("user") User user, @Param("sampleSong") Song sampleSong);

    @Query(value="SELECT ci2.song as song from ConcertItem ci1 JOIN Concert c ON c=ci1.concert LEFT JOIN ConcertItem ci2 ON ci2.concert=ci1.concert AND ci2.orderValue = ci1.orderValue + 1 WHERE c.user=:user AND ci1.song=:sampleSong AND ci2.song IS NOT NULL GROUP BY ci2.song ORDER BY function('RAND')",
            countQuery = "SELECT count(1) from ConcertItem ci1 JOIN Concert c ON c=ci1.concert LEFT JOIN ConcertItem ci2 ON ci2.concert=ci1.concert AND ci2.orderValue = ci1.orderValue + 1 WHERE c.user=:user AND ci1.song=:sampleSong AND ci2.song IS NOT NULL GROUP BY ci2.song")
    Page<SongProj> findSongsAfter(Pageable page, @Param("user") User user, @Param("sampleSong") Song sampleSong);

}