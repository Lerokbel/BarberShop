package entities;

import java.io.Serializable;

public class Master implements Serializable {
    private int id;
    private String login;
    private String password;
    private int experience;
    private String fullName;
    private Status status;

    private String strstatus;
    private String strexp;
    public Master() {
    }

    public Master(int id, String login, String password, int experience, String fullName, Status status) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.experience = experience;
        this.fullName = fullName;
        this.status = status;

        if(status == Status.NOT_BANNED)
            strstatus = "Активен";
        else
            strstatus = "Заблокирован";

        strexp = String.valueOf(experience);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if(status == Status.NOT_BANNED)
            strstatus = "Активен";
        else
            strstatus = "Заблокирован";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        strexp = String.valueOf(experience);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStrexp() {return strexp;}

    public void setStrexp(String fullName) {}

    public String getStrstatus() {return strstatus;}

    public void setStrstatus(String fullName) {}
}
