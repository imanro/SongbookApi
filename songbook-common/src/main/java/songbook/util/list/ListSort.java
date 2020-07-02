package songbook.util.list;

import org.springframework.stereotype.Component;
import songbook.common.entity.BaseEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class ListSort<T extends BaseEntity> {
    public void sortListEntitiesByIds(List<T> listEntities, List<Long> listIds) {
        Collections.sort(listEntities, new Comparator<T>() {
            public int compare(T left, T right) {
                return Long.compare(listIds.indexOf(left.getId()), listIds.indexOf(right.getId()));
            }
        });
    }
}
