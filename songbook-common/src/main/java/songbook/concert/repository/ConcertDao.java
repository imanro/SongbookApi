package songbook.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import songbook.concert.entity.Concert;

import java.util.List;

public interface ConcertDao extends JpaRepository<Concert, Long> {

//  This way (see entity's @NamedQuery annotation) to implement default sorting in findAll method, without of customRepository

    @Override
    @Query
    public List<Concert> findAll();

    @Override
    @Query
    public Page<Concert> findAll(Pageable var);
}
