package songbook.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import songbook.domain.user.entity.User;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User getOne(Long id);

    Optional<User> findByEmail(
            @Param("email") String email
    );

}
