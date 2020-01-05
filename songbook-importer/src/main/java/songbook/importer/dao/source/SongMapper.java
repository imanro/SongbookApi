package songbook.importer.dao.source;

import org.springframework.jdbc.core.RowMapper;
import songbook.importer.model.source.Song;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

class SongMapper extends AbstractMapper implements RowMapper<Song> {

    @Override
    public Song mapRow(ResultSet rs, int rowNum) throws SQLException {
        Song song = new Song();
        song.setId(rs.getInt("id"));
        song.setTitle(rs.getString("title"));
        song.setAuthor(rs.getString("author"));
        song.setCopyright(rs.getString("copyright"));

        Date createdAt = this.parseDateString(rs.getString("create_time"));
        song.setCreateTime(createdAt);

        return song;
    }
}
