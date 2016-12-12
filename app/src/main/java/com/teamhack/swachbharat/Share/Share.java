package com.teamhack.swachbharat.Share;

import com.teamhack.swachbharat.Profile.User;

/**
 * Created by Rishi on 11-12-2016.
 */

public class Share {

    String latitude;
    String longitude;
    String category;
    String time;
    User user;

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCategory() {
        return category;
    }

    public String getTime() {
        return time;
    }

    public User getUser() {
        return user;
    }
}