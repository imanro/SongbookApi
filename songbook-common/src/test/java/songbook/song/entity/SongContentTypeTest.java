package songbook.song.entity;

import org.junit.jupiter.api.Test;

public class SongContentTypeTest {

    @Test
    void toStringWorks() {
        String typeHeader = SongContentType.HEADER.name();
        String typeHeaderTitle = SongContentType.HEADER.toString();

        System.out.println(typeHeaderTitle);
    }
}
