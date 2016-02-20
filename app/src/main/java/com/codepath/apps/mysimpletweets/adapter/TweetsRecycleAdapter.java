package com.codepath.apps.mysimpletweets.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.DB.PostsDatabaseHelper;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by glondhe on 2/17/16.
 */

//taking the tweets objects and turing then into view displayed in the recycle view
public class TweetsRecycleAdapter extends RecyclerView.Adapter<TweetsRecycleAdapter.ViewHolder> {

    private final Context context;
    public ArrayList<Tweet> mTweet;
    private String relativeDate;
    private PostsDatabaseHelper databaseHelper;

    public TweetsRecycleAdapter(Context context, ArrayList<Tweet> tweets, Boolean flag) {
        this.context = context;
        databaseHelper = PostsDatabaseHelper.getInstance(this.context);
        if (flag.equals(true))
            mTweet = databaseHelper.getAllPosts();
        else
            mTweet = tweets;
    }

    @Override
    public TweetsRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsRecycleAdapter.ViewHolder viewHolder, int position) {

        Tweet tweet = mTweet.get(position);
        TextView tvTweet = viewHolder.tvTweet;
        tvTweet.setText(tweet.getBody());
        tvTweet.setTextColor(Color.parseColor("#000000"));
        tvTweet.setTextSize(12);
        tvTweet.setLinksClickable(true);
        Pattern httpPattern = Pattern.compile("[a-z]+:\\/\\/[^ \\n]*");
        Linkify.addLinks(tvTweet, httpPattern, "");

        TextView tvUsername = viewHolder.tvUsername;
        String date = tweet.getCreatedAt();
        relativeDate = getRelativeTimeAgo(date);
        tvUsername.setText(tweet.getUser().getName() + " " + "@" + tweet.getUser().getScreenName() + " * " + relativeDate);
        tvUsername.setTextSize(14);
        tvUsername.setLinksClickable(true);
        Pattern httpPattern2 = Pattern.compile("@[^ \\n]*");
        Linkify.addLinks(tvUsername, httpPattern2, "");

        ImageView ivProfileImg = viewHolder.ivProfileImg;
        tweet.getUser().getProfileIamgeURL();
        Glide.with(context).load(tweet.getUser().getProfileIamgeURL().replace("normal", "bigger")).into(ivProfileImg);
    }

    public String getRelativeTimeAgo(String rawJsonDate) {

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        relativeDate = relativeDate.substring(2);
        relativeDate = relativeDate.replace("hours", "h");

        return relativeDate;
    }

    public void clearData() {
        int size = this.mTweet.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mTweet.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mTweet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImg;
        TextView tvUsername;
        TextView tvTweet;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImg = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvTweet = (TextView) itemView.findViewById(R.id.tvTweet);
        }
    }
}