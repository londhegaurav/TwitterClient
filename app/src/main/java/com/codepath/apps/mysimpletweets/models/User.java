package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by glondhe on 2/17/16.
 */
public class User implements Parcelable{

    public String name;
    public long uid;
    public String screenName;
    public String profileIamgeURL;
    public String tagLine;
    public int followersCount;
    public int followingsCount;
    public String profileBannerUrl;
    public String tweetCount;

    public User() {

    }

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
            u.tagLine = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingsCount = json.getInt("friends_count");
            u.profileBannerUrl = json.optString("profile_banner_url");
            u.tweetCount = json.getString("statuses_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileIamgeURL);
        dest.writeString(this.tagLine);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.followingsCount);
        dest.writeString(this.profileBannerUrl);
        dest.writeString(this.tweetCount);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileIamgeURL = in.readString();
        this.tagLine = in.readString();
        this.followersCount = in.readInt();
        this.followingsCount = in.readInt();
        this.profileBannerUrl = in.readString();
        this.tweetCount = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
