package songbook.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import songbook.user.entity.User;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User getOne(Long id);

}
