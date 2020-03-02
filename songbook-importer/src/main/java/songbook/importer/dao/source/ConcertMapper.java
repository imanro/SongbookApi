package songbook.importer.dao.source;

import org.springframework.jdbc.core.RowMapper;
import songbook.importer.model.source.Concert;
import songbook.importer.model.source.SongContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ConcertMapper extends AbstractMapper implements RowMapper<Concert>{

    @Override
    public Concert mapRow(ResultSet rs, int rowNum) throws SQLException {
        Concert concert = new Concert();
        concert.setId(rs.getInt("id"));

        Date createdAt = this.parseDateString(rs.getString("create_time"));
        concert.setCreateTime(createdAt);

        Date time = this.parseDateString(rs.getString("time"));
        concert.setTime(time);

        return concert;
    }
}
