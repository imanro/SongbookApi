package songbook.common.entity;

import com.fasterxml.jackson.annotation.JsonView;
import songbook.util.view.Summary;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Summary.class)
    protected long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o != null && o.getClass() == getClass()) {
            return getId() != 0 &&
                    id == ((BaseEntity)o).getId();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName() + getId());
    }

    public long getId() {
        return id;
    }

    public Object setId(long id) {
        this.id = id;
        return this;
    }
}
