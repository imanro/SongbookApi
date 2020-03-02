package songbook.concert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import songbook.concert.entity.Concert;

public interface ConcertDao extends JpaRepository<Concert, Long> {
}
