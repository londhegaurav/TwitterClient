package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by glondhe on 2/17/16.
 */
public class User {

    String name;
    long uid;
    String screenName;
    String profileIamgeURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileIamgeURL() {
        return profileIamgeURL;
    }

    public void setProfileIamgeURL(String profileIamgeURL) {
        this.profileIamgeURL = profileIamgeURL;
    }

    public static User fromJSON(JSONObject json){
        User u = new User();

        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileIamgeURL = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return u;
    }
}
