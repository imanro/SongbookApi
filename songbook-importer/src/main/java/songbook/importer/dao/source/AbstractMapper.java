package songbook.importer.dao.source;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AbstractMapper {

    protected Date parseDateString(String dateString) {
        LocalDateTime localDate = LocalDateTime.parse(
                dateString,
                DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss.S" )
        );

        return Date.from( localDate.atZone( ZoneId.systemDefault()).toInstant());
    }
}
