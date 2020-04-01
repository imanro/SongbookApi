package songbook.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import songbook.concert.entity.Concert;
import songbook.user.entity.User;

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
            "WHERE e.id=:id AND h.type=songbook.song.entity.SongContentTypeEnum.HEADER AND h.user=:user"
    )
    Optional<Concert> findByIdWithHeaders(Long id, User user);

    @Override
    @Query
    public List<Concert> findAll();

    @Override
    @Query
    public Page<Concert> findAll(Pageable var);
}
