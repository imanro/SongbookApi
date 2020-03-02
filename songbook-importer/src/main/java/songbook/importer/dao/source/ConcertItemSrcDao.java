package songbook.importer.dao.source;

import org.springframework.jdbc.core.JdbcTemplate;
import songbook.importer.model.source.ConcertItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Transactional
@Repository
public class ConcertItemSrcDao extends AbstractDao {
    public List<ConcertItem> findAllByConcertId(long concertId) {
        String sql = "select * from concert_item where concert_id=?";
        JdbcTemplate srcTemplate = this.getSrcTemplate();
        List<ConcertItem> list = srcTemplate.query(sql, new ConcertItemMapper(), concertId);
        return list;
    }
}