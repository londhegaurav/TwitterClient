package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by glondhe on 2/20/16.
 */

public class Media implements Parcelable {

    public String mediaUrl;

    public static Media fromJSON(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.mediaUrl = jsonObject.getString("media_url");
        return media;
    }

    public static ArrayList<Media> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Media> medias = new ArrayList<>();
        for(int i = 0; i< jsonArray.length(); i++) {
            JSONObject mediaJson = null;
            try {
                mediaJson = jsonArray.getJSONObject(i);
                Media media = fromJSON(mediaJson);
                if(media != null) {
                    medias.add(media);
                }
            } catch (JSONException e) {
                Log.e(Tweet.class.getName(), "Error encountered while parsing JSON: " + e);
            }
        }
        return medias;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaUrl);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.mediaUrl = in.readString();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
