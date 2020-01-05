package songbook.importer.dao.source;

import org.springframework.jdbc.core.RowMapper;
import songbook.importer.model.source.SongContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

class SongContentMapper extends AbstractMapper implements RowMapper<SongContent> {

    @Override
    public SongContent mapRow(ResultSet rs, int rowNum) throws SQLException {
        SongContent content = new SongContent();
        content.setId(rs.getInt("id"));
        content.setSongId(rs.getInt("song_id"));
        content.setType(rs.getString("type"));
        content.setUrl(rs.getString("url"));
        content.setContent(rs.getString("content"));
        content.setUserId(rs.getInt("user_id"));
        content.setIsFavorite(rs.getBoolean("is_favorite"));
        content.setFileName(rs.getString("file_name"));
        content.setMimeType(rs.getString("mime_type"));

        Date createdAt = this.parseDateString(rs.getString("create_time"));
        content.setCreateTime(createdAt);

        return content;
    }
}
