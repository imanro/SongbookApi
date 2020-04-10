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
            "WHERE e.id=:id")
    Optional<Song> findByIdWithHeaders(
            @Param("id") long songID
    );

    @Query("SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user")
    List<Song> findAllWithHeaders();

    @Query(
            value="SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h ",
            countQuery="SELECT distinct e FROM Song e ")
    Page<Song> findAllWithHeaders(
            Pageable pageable
    );

    @Query("SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.content LIKE '%' || :searchString || '%'")
    List<Song> findAllByHeaderWithHeaders(
            @Param("searchString") String searchString
    );

    @Query(value="SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "WHERE h.content LIKE '%' || :searchString || '%'",
    countQuery="SELECT count(e) FROM Song e LEFT JOIN e.headers h WHERE h.content LIKE '%' || :searchString || '%'")
    Page<Song> findAllByHeaderWithHeaders(
            @Param("searchString") String searchString,
            Pageable pageable
    );
    Song findBySbV1Id(int sbV1Id);
}
