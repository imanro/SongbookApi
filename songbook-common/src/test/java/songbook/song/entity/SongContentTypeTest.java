package songbook.song.entity;

import org.junit.jupiter.api.Test;

class SongContentTypeTest {

    @Test
    void toStringWorks() {
        String typeHeader = SongContentTypeEnum.HEADER.name();
        String typeHeaderTitle = SongContentTypeEnum.HEADER.toString();

        System.out.println(typeHeaderTitle);
    }
}
