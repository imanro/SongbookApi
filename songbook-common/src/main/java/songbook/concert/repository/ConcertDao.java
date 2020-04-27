package songbook.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import songbook.concert.entity.Concert;

import java.util.List;
import java.util.Optional;

public interface ConcertDao extends JpaRepository<Concert, Long> {

//  This way (see entity's @NamedQuery annotation) to implement default sorting in findAll method, without of customRepository

    // Also CHECK
    // https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#fetching-fetch-annotation
    @Query("SELECT e from Concert e " +
            "LEFT JOIN FETCH e.items i " +
            "LEFT JOIN FETCH i.song s " +
            "LEFT JOIN FETCH s.headers h " +
            "LEFT JOIN FETCH s.tags t " +
            "WHERE e.id=:id"
    )
    Optional<Concert> findByIdWithHeadersAndTags(@Param("id") long id);

    @Query("SELECT e from Concert e " +
            "LEFT JOIN FETCH e.items i " +
            "LEFT JOIN FETCH i.song s " +
            "LEFT JOIN FETCH s.headers h " +
            "LEFT JOIN FETCH s.tags t " +
            "ORDER BY e.createTime DESC"
    )
    List<Concert> findLastConcertWithHeadersAndTags(Pageable page);

    @Override
    @Query("SELECT e FROM Concert e " +
            "ORDER BY e.createTime DESC")
    List<Concert> findAll();

    @Override
    @Query("SELECT e FROM Concert e " +
            "ORDER BY e.createTime DESC")
    Page<Concert> findAll(Pageable page);
}
