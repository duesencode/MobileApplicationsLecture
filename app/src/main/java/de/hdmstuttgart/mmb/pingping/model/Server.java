package de.hdmstuttgart.mmb.pingping.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdmstuttgart.mmb.pingping.util.Availability;

public final class Server {
    private String alias;
    private String url;
    private float goodRequests;
    private float badRequests;
    private float score;
    private String lastUpdate;
    private Availability availability;
    private boolean isVisible;
    private boolean isOnline;

    public Server(String p_alias, String p_url) {
        super();
        this.alias = p_alias;
        this.url = p_url;
        this.goodRequests = 0;
        this.badRequests = 0;
        this.score = -1.f;
        this.lastUpdate = null;
        this.isOnline = false;
        this.availability = Availability.NO_SCORE;
        this.isVisible = true;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setLastUpdate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss",
                Locale.getDefault());
        this.lastUpdate = sdf.format(date);
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public float getScore() {
        return this.score;
    }

    public void setAvailability() {
        if (this.score == -1f) {
            this.availability = Availability.NO_SCORE;
        } else if (this.score < 0.01f && this.score >= 0) {
            this.availability = Availability.HIGH_AVAILABILITY;
        } else if (this.score >= 0.01f && this.score < 0.3f) {
            this.availability = Availability.AVAILABLE;
        } else if (this.score >= 0.3f) {
            this.availability = Availability.LOW_AVAILABILITY;
        }
    }

    public Availability getAvailability() {
        return availability;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void calculateScore() {
        if ((this.badRequests + this.goodRequests) > 0)
            this.score = this.badRequests
                    / (this.badRequests + this.goodRequests);
        else
            this.score = -1f;
        setAvailability();
    }

    public void incrementGoodRequests() {
        this.goodRequests++;
        setOnline(true);
        setLastUpdate(new Date());
    }

    public void incrementBadRequests() {
        this.badRequests++;
        setOnline(false);
        setLastUpdate(new Date());
    }
}
