package songbook.importer.model.source;

import java.util.Date;

public class Concert {
    private long id;

    private Date createTime;

    private Date time;

    private int userId;

    public long getId() {
        return id;
    }

    public Concert setId(long id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Concert setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public Concert setTime(Date time) {
        this.time = time;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Concert setUserId(int userId) {
        this.userId = userId;
        return this;
    }
}
