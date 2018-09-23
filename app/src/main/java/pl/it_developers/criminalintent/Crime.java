package pl.it_developers.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean solved;
    private boolean requiredPolice;

    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isRequiredPolice() {
        return requiredPolice;
    }

    public void setRequiredPolice(boolean requiredPolice) {
        this.requiredPolice = requiredPolice;
    }
}
