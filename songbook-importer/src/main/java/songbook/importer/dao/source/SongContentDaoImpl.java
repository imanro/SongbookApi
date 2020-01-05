package songbook.importer.dao.source;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import songbook.importer.model.source.Song;
import songbook.importer.model.source.SongContent;

import java.util.List;

@Transactional
@Repository
public class SongContentDaoImpl extends AbstractDao {
    public List<SongContent> findAll() {
        String sql = "select * from content";
        JdbcTemplate srcTemplate = this.getSrcTemplate();
        List<SongContent> list = srcTemplate.query(sql, new SongContentMapper());
        return list;
    }
}
