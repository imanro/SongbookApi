package songbook.importer.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan("songbook")
public class SpringJdbcConfig {
    @Bean(name = "srcDataSource")
    @ConfigurationProperties("db.src")
    public DataSource SrcDataSource() {
        return DataSourceBuilder.create().build();
    }

    // this datasource will be used by default by common project...
    @Bean(name = "trgDataSource")
    @ConfigurationProperties("db.trg")
    @Primary
    public DataSource TrgDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "srcTemplate")
    public JdbcTemplate srcJdbcTemplate(@Qualifier("srcDataSource") DataSource src) {
        return new JdbcTemplate(src);
    }

    @Bean(name = "trgTemplate")
    public JdbcTemplate trgJdbcTemplate(@Qualifier("trgDataSource") DataSource trg) {
        return new JdbcTemplate(trg);
    }
}
