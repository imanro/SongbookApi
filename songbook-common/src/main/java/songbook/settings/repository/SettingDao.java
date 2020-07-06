package songbook.settings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import songbook.settings.entity.Setting;
import songbook.domain.user.entity.User;

import java.util.List;

@Repository
@Transactional
public interface SettingDao extends JpaRepository<Setting, Long> {

    List<Setting> findAllByUser(User user);
}
