package songbook.song.repository;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import songbook.song.entity.Song;
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
            "WHERE e.id=:id")
    Optional<Song> findByIdWithHeaders(
            @Param("id") long songID
    );

    @Query("SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "LEFT JOIN FETCH e.tags t " +
            "ORDER BY e.createTime DESC"
            )
    List<Song> findAllWithHeadersAndTags();

    @Query(
            value="SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "LEFT JOIN FETCH e.tags t " +
            "ORDER BY e.createTime DESC ",
            countQuery="SELECT count(e) FROM Song e ")
    Page<Song> findAllWithHeadersAndTags(
            Pageable pageable
    );

    @Query("SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "LEFT JOIN FETCH e.tags t " +
            "WHERE h.content LIKE '%' || :searchString || '%' " +
            "ORDER BY e.createTime DESC ")
    List<Song> findAllByHeaderWithHeadersAndTags(
            @Param("searchString") String searchString
    );

    @Query(value="SELECT distinct e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "LEFT JOIN FETCH e.tags t " +
            "WHERE h.content LIKE '%' || :searchString || '%' " +
            "ORDER BY e.createTime DESC ",
    countQuery="SELECT count(e) FROM Song e LEFT JOIN e.headers h WHERE h.content LIKE '%' || :searchString || '%'")
    Page<Song> findAllByHeaderWithHeadersAndTags(
            @Param("searchString") String searchString,
            Pageable pageable
    );

    Song findByCode(String code);

    // ManyToMany search!!!
    @Query(value="SELECT e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "LEFT JOIN FETCH e.tags t " +
            "LEFT JOIN e.tags ts " + // twise join because otherwise we'll only get the tag we sarch, in collection
            "WHERE ts.id IN :ids " +
            "ORDER BY e.createTime DESC ",
            countQuery = "SELECT COUNT(e) FROM Song e " +
                    "JOIN e.tags t WHERE t.id IN :ids")
    Page<Song> findAllByTags(@Param("ids") List<Long> ids, Pageable pageable);

    @Query(value="SELECT e FROM Song e " +
            "LEFT JOIN FETCH e.headers h " +
            "LEFT JOIN FETCH e.tags t " +
            "LEFT JOIN e.tags ts " + // twise join because otherwise we'll only get the tag we sarch, in collection
            "WHERE ts.id IN :ids AND h.content LIKE '%' || :searchString || '%' " +
            "ORDER BY e.createTime DESC ",
            countQuery = "SELECT COUNT(e) FROM Song e " +
                    "LEFT JOIN e.tags t " +
                    "LEFT JOIN e.headers h " +
                    "WHERE t.id IN :ids AND h.content LIKE '%' || :searchString || '%'")
    Page<Song> findAllByTagsAndContent(@Param("ids") List<Long> ids, @Param("searchString") String searchString, Pageable pageable);
}
