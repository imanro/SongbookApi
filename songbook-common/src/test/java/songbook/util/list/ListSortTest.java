package songbook.util.list;

import static org.junit.jupiter.api.Assertions.*;

import songbook.common.entity.BaseEntity;
import songbook.domain.song.entity.Song;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DisplayName("ListSort test")
public class ListSortTest {

    @Test
    @DisplayName("Should sort list entities by list of ids")
    void sortListEntitiesByListOfIds() {

        Song song1 = new Song();
        song1.setId(1);

        Song song2 = new Song();
        song2.setId(2);

        Song song3 = new Song();
        song3.setId(3);

        Song song4 = new Song();
        song4.setId(4);

        List<BaseEntity> listEntities = new ArrayList<>(Arrays.asList(song4, song2, song1, song3));
        List<Long> listIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L));

        ListSort sortUtil = new ListSort();
        sortUtil.sortListEntitiesByIds(listEntities, listIds);

        assertEquals(1L, listEntities.get(0).getId(), "The first entity is wrong");
        assertEquals(2L, listEntities.get(1).getId(), "The 2nd entity is wrong");
        assertEquals(3L, listEntities.get(2).getId(), "The 3rd entity is wrong");
        assertEquals(4L, listEntities.get(3).getId(), "The 4th entity is wrong");
    }
}
