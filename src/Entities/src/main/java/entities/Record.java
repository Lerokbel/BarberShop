package entities;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
    private int id;
    private Purpose purpose;
    private User user;
    private Date date;

    public Record() {
        date = new Date();
    }

    public Record(int id, Purpose purpose, User user, Date date) {
        this.id = id;
        this.purpose = purpose;
        this.user = user;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public User getClient() {
        return user;
    }

    public void setClient(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date.setTime(date.getTime());
    }
}
