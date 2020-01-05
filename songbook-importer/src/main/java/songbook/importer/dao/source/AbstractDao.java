package songbook.importer.dao.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractDao {

    @Autowired
    @Qualifier("srcTemplate")
    private  JdbcTemplate srcTemplate;

    @Autowired
    @Qualifier("trgTemplate")
    private JdbcTemplate trgTemplate;

    protected JdbcTemplate getSrcTemplate() {
        return this.srcTemplate;
    }

    protected JdbcTemplate getTrgTemplate() {
        return this.trgTemplate;
    }
}
