package songbook.importer.dao.source;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import songbook.importer.model.source.Song;

import java.util.List;

@Transactional
@Repository
public class SongDaoImpl extends AbstractDao {
    public List<Song> findAll() {
        String sql = "select * from song";
        JdbcTemplate srcTemplate = this.getSrcTemplate();
        List<Song> list = srcTemplate.query(sql, new SongMapper());
        return list;
    }
}
