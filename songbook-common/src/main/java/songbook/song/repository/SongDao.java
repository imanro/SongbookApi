package songbook.song.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import songbook.song.entity.Song;
import songbook.user.entity.User;

import java.util.List;
import java.util.Optional;


/**
 * A DAO for the entity User is simply created by extending the CrudRepository
 * interface provided by spring. The following methods are some of the ones
 * available from such interface: save, delete, deleteAll, findOne and findAll.
 * The magic is that such methods must not be implemented, and moreover it is
 * possible create new query methods working only by defining their signature!
 *
 * @author netgloo
 */
@Transactional
@Repository
public interface SongDao extends JpaRepository<Song, Long>, SongDaoCustom {

    // headers should be always connected to certain user. when adding song into account, define procedure to copy headers from another user accounts to this
    @Query("SELECT e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE e.id=:id AND h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user")
    Optional<Song> findByIdWithHeaders(
            @Param("id") long songID,
            @Param("user") User user
    );

    @Query("SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user")
    List<Song> findAllWithHeaders(
            @Param("user") User user
    );

    @Query(
            value="SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user",
            countQuery="SELECT distinct e FROM Song e ")
    Page<Song> findAllWithHeaders(
            @Param("user") User user,
            Pageable pageable
    );

    @Query("SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user AND h.content LIKE '%' || :searchString || '%'")
    List<Song> findAllByHeaderWithHeaders(
            @Param("searchString") String searchString,
            @Param("user") User user
    );

    @Query(value="SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user AND h.content LIKE '%' || :searchString || '%'",
    countQuery="SELECT count(e) FROM Song e LEFT JOIN e.headers h WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user AND h.content LIKE '%' || :searchString || '%'")
    Page<Song> findAllByHeaderWithHeaders(
            @Param("searchString") String searchString,
            @Param("user") User user,
            Pageable pageable
    );
    Song findBySbV1Id(int sbV1Id);

    /*
    @Query("SELECT e FROM Song e " +
            "INNER JOIN FETCH e.header h " +
            "WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user AND h.content LIKE %:string% ORDER BY h.isFavorite")
    List<Song> findByHeaderWithHeaders(
            @Param("string") String string,
            @Param("user") User user
    );
    */

    /*
    SELECT
   song.`title`,
   song.`id`,
   song_content.content,
   song_content.song_id
FROM `song`
LEFT JOIN `song_content`
ON song_content.id = (
  SELECT id FROM `song_content` WHERE type='HEADER' AND song_id = song.id ORDER BY is_favorite DESC LIMIT 1
)
     */
}
