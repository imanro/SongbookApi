package songbook.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SongTest {

    Song song;

    @BeforeEach
    void init() {
        song = new Song();
    }

    @Test
    void idAssigned() {
        song.setId(111);

        assertEquals(111, song.getId(), "The assigned id is not match to that expected");
    }
}