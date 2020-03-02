package songbook.importer.dao.source;

import org.springframework.jdbc.core.JdbcTemplate;
import songbook.importer.model.source.Concert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
@Repository
public class ConcertSrcDao extends AbstractDao {
    public List<Concert> findAll() {
        String sql = "select * from concert order by id";
        JdbcTemplate srcTemplate = this.getSrcTemplate();
        List<Concert> list = srcTemplate.query(sql, new ConcertMapper());
        return list;
    }
}
