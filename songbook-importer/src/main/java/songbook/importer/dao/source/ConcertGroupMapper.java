package songbook.importer.dao.source;

import org.springframework.jdbc.core.RowMapper;
import songbook.importer.model.source.ConcertGroup;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConcertGroupMapper extends AbstractMapper implements RowMapper<ConcertGroup> {

    @Override
    public ConcertGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConcertGroup concertGroup = new ConcertGroup();

        concertGroup.setId(rs.getInt("id"));

        concertGroup.setName(rs.getString("name"));

        concertGroup.setConcertId(rs.getInt("concert_id"));

        return concertGroup;
    }
}
