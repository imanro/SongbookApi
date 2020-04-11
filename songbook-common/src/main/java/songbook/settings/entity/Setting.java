package songbook.settings.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import songbook.user.entity.User;

import javax.persistence.*;

@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(unique=true, nullable=false)
    private String name;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user;

    public long getId() {
        return id;
    }

    public Setting setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Setting setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Setting setValue(String value) {
        this.value = value;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Setting setUser(User user) {
        this.user = user;
        return this;
    }
}
