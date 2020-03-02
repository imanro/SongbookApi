package songbook.importer.dao.source;

import org.springframework.jdbc.core.JdbcTemplate;
import songbook.importer.model.source.ConcertGroup;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
@Repository
public class ConcertGroupSrcDao extends AbstractDao {

    public List<ConcertGroup> findAllByConcertId(long concertId) {
        String sql = "select * from concert_group where concert_id=?";
        JdbcTemplate srcTemplate = this.getSrcTemplate();

        List<ConcertGroup> list = srcTemplate.query(sql, new ConcertGroupMapper(), concertId);
        return list;
    }

    public static List<ConcertGroup> filterConcertGroupsByConcertId(List<ConcertGroup> list, long concertId) {
        // http://zetcode.com/java/filterlist/
        Predicate<ConcertGroup> byConcertId = concertGroup -> concertGroup.getConcertId() == concertId;

        return list.stream().filter(byConcertId)
                .collect(Collectors.toList());
    }

    public static ConcertGroup findConcertGroupsById(List<ConcertGroup> list, long id) {
        // http://zetcode.com/java/filterlist/
        Predicate<ConcertGroup> byConcertId = concertGroup -> concertGroup.getId() == id;

        List<ConcertGroup> filtered = list.stream().filter(byConcertId)
                .collect(Collectors.toList());

        if(filtered.size() > 0) {
            return filtered.get(0);
        } else {
            return null;
        }
    }
}
