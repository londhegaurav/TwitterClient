package com.codepath.apps.mysimpletweets.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.DB.PostsDatabaseHelper;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activity.DetailActivity;
import com.codepath.apps.mysimpletweets.fragment.ComposeTweetDailog;
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
    private final FragmentManager fm;
    public ArrayList<Tweet> mTweet;
    private String relativeDate;
    private PostsDatabaseHelper databaseHelper;
    private TextView tvUsername;
    private Tweet tweet = null;
    private Boolean flag = false;

    public TweetsRecycleAdapter(Context context, ArrayList<Tweet> tweets, Boolean flag, FragmentManager fm) {
        this.context = context;
        databaseHelper = PostsDatabaseHelper.getInstance(this.context);
        if (flag.equals(true))
            mTweet = databaseHelper.getAllPosts();
        else
            mTweet = tweets;

        this.fm = fm;
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

        final ViewHolder viewHolder1 = null;

        tweet = null;
        tweet = mTweet.get(position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                //you can pass on the Pojo with PARCELABLE
                intent.putExtra("tweet", tweet);
                v.getContext().startActivity(intent);
            }
        });

        TextView tvTweet = viewHolder.tvTweet;
        tvUsername = viewHolder.tvUsername;
        ImageView ivProfileImg = viewHolder.ivProfileImg;

        tvTweet.setText(tweet.body);
        tvTweet.setTextColor(Color.parseColor("#000000"));
        tvTweet.setTextSize(12);
        tvTweet.setLinksClickable(true);
        Pattern httpPattern = Pattern.compile("[a-z]+:\\/\\/[^ \\n]*");
        Linkify.addLinks(tvTweet, httpPattern, "");

        Log.d("cScreenName", tweet.user.screenName);
        Log.d("cScreenName", tweet.user.profileIamgeURL);
        Log.d("cScreenName", tweet.body);

        tvUsername = viewHolder.tvUsername;
        String date = tweet.createdAt;
        relativeDate = getRelativeTimeAgo(date);
        tvUsername.setText(tweet.user.name + " " + "@" + tweet.user.screenName + " * " + relativeDate);
        tvUsername.setTextSize(14);
        tvUsername.setLinksClickable(true);
        Pattern httpPattern2 = Pattern.compile("@[^ \\n]*");
        Linkify.addLinks(tvUsername, httpPattern2, "");

        final TextView retweet = viewHolder.tvReTweet;
        final TextView tvStar = viewHolder.tvStar;
        Log.d("retweet", String.valueOf(tweet.reTweetCnt));
        retweet.setText(String.valueOf(tweet.reTweetCnt));
        tvStar.setText("0");

        final ImageView ivPostTweet = viewHolder.ivPostTweet;

        ivPostTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("screenName", tweet.user.screenName );
                ComposeTweetDailog editNameDialog = ComposeTweetDailog.newInstance("Some Title");
                editNameDialog.setArguments(bundle);
                editNameDialog.show(fm, "fragment_edit_name");
            }
        });

        final ImageButton ivretweet = viewHolder.ivReTweet;
        final ImageButton ivstar = viewHolder.ivStar;

        ivretweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t;
                if (!ivretweet.isSelected()) {
                    ivretweet.setImageResource(R.drawable.retweetorange);
                    t = Integer.valueOf(String.valueOf(retweet.getText())) + 1;
                    retweet.setText(String.valueOf(t));

                } else {
                    ivretweet.setImageResource(R.drawable.retweetgray);
                    t = Integer.valueOf(String.valueOf(retweet.getText())) - 1;
                    retweet.setText(String.valueOf(t));
                }
            }
        });

        ivstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t;
                if (!ivstar.isSelected()) {
                    ivstar.setImageResource(R.drawable.starorange);
                    t = Integer.valueOf(String.valueOf(tvStar.getText())) + 1;
                    tvStar.setText(String.valueOf(t));

                } else {
                    ivstar.setImageResource(R.drawable.starnocolor);
                    t = Integer.valueOf(String.valueOf(tvStar.getText())) - 1;
                    tvStar.setText(String.valueOf(t));
                }
            }
        });

        Glide.with(context).load(tweet.user.profileIamgeURL.replace("normal", "bigger")).into(ivProfileImg);

    }

    public static String getRelativeTimeAgo(String rawJsonDate) {

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

        relativeDate = relativeDate.replace("hours", "h");
        relativeDate = relativeDate.replace("minutes", "m");
        relativeDate = relativeDate.replace("minute", "m");
        relativeDate = relativeDate.replace("seconds", "s");
        relativeDate = relativeDate.replace("ago", "");
        relativeDate = relativeDate.replace("in", "");
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
        ImageView ivMedia;
        TextView tvReTweet;
        TextView tvStar;
        ImageButton ivReTweet;
        ImageButton ivStar;
        ImageView ivPostTweet;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImg = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvTweet = (TextView) itemView.findViewById(R.id.tvTweet);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
            tvReTweet = (TextView) itemView.findViewById(R.id.tvreTweet);
            tvStar = (TextView) itemView.findViewById(R.id.tvStar);
            ivReTweet = (ImageButton) itemView.findViewById(R.id.ivretweet);
            ivStar = (ImageButton) itemView.findViewById(R.id.ivstar);
            ivPostTweet = (ImageView) itemView.findViewById(R.id.ivPostTweet);

        }

    }
}