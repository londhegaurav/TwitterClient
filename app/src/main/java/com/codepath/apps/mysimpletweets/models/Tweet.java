package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by glondhe on 2/17/16.
 */

/*
   "text": "just another test",
    "contributors": null,
    "id": 240558470661799936,
    "retweet_count": 0,
    "in_reply_to_status_id_str": null,
    "geo": null,
    "retweeted": false,
    "in_reply_to_user_id": null,
    "place": null,
    "source": "<a href="//realitytechnicians.com%5C%22" rel="\"nofollow\"">OAuth Dancer Reborn</a>",
    "user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      "follow_request_sent": false,
      "id_str": "119476949",
      "is_translator": false,
      "profile_link_color": "0084B4",
      "entities": {
 */
   //Parse the JSON + store the data, encapsulate state logic or display logic.
public class Tweet implements Parcelable{
    //list out the attribute

    public String body;
    public long uid;
    public String createdAt;
    public User user;
    public ArrayList<Media> medias;
    public int reTweetCnt;
    public int favCnt;

    public Tweet(){

    }


    public static Tweet fromJson(JSONObject jsonObject) {

        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

            if(String.valueOf(jsonObject.getInt("retweet_count")).length() > 0) {
                tweet.reTweetCnt = jsonObject.getInt("retweet_count");
            }
            else{
                tweet.reTweetCnt = 0;
            }

//            if(String.valueOf(jsonObject.getInt("favourites_count")).length() > 0) {
//                tweet.favCnt = jsonObject.getInt("favourites_count");
//            }
//            else{
//                tweet.favCnt = 0;
//            }

            JSONObject entities = jsonObject.optJSONObject("entities");
            if(entities != null) {
                JSONArray medias = entities.optJSONArray("media");
                if (medias != null) {
                    tweet.medias = Media.fromJSONArray(medias);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    //Tweet.fromJsonArray
    public static ArrayList<Tweet> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i=0; i < jsonArray.length(); i++){
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(tweetJson);
                if (tweet != null){
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
            dest.writeLong(this.uid);
            dest.writeString(this.createdAt);
            dest.writeParcelable(this.user, 0);
            dest.writeTypedList(medias);
            dest.writeInt(this.reTweetCnt);
        dest.writeInt(this.favCnt);
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
            this.uid = in.readLong();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.medias = in.createTypedArrayList(Media.CREATOR);
        this.favCnt = in.readInt();
        this.reTweetCnt = in.readInt();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}


