package songbook.importer.dao.source;

import org.springframework.jdbc.core.RowMapper;
import songbook.importer.model.source.ConcertItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ConcertItemMapper extends AbstractMapper implements RowMapper<ConcertItem>{

    @Override
    public ConcertItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConcertItem concertItem = new ConcertItem();

        concertItem.setId(rs.getInt("id"));

        concertItem.setSongId(rs.getInt("song_id"));

        concertItem.setConcertId(rs.getInt("concert_id"));

        concertItem.setConcertGroupId(rs.getInt("concert_group_id"));

        concertItem.setOrder(rs.getInt("order"));

        Date createdAt = this.parseDateString(rs.getString("create_time"));
        concertItem.setCreateTime(createdAt);

        return concertItem;
    }
}
