package com.codepath.apps.mysimpletweets.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapter.TweetsRecycleAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 2/20/16.
 */
public class DetailActivity  extends AppCompatActivity {

    @Bind(R.id.tvTweetDetail) TextView tvTweetDetail;
    @Bind(R.id.tvUsernameDetail) TextView tvUsernameDetail;
    @Bind(R.id.ivProfileImageDetail) ImageView ivProfileImageDetail;
    private String relativeDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        Tweet tweet = bundle.getParcelable("tweet");
        tvTweetDetail.setText(tweet.body);
        tvTweetDetail.setTextColor(Color.parseColor("#000000"));
        tvTweetDetail.setTextSize(12);
        tvTweetDetail.setLinksClickable(true);
        Pattern httpPattern = Pattern.compile("[a-z]+:\\/\\/[^ \\n]*");
        Linkify.addLinks(tvTweetDetail, httpPattern, "");

        relativeDate = TweetsRecycleAdapter.getRelativeTimeAgo(tweet.createdAt);
        tvUsernameDetail.setText(tweet.user.name + " " + "@" + tweet.user.screenName + " * " + relativeDate);
        tvUsernameDetail.setTextSize(14);
        tvUsernameDetail.setLinksClickable(true);
        Pattern httpPattern2 = Pattern.compile("@[^ \\n]*");
        Linkify.addLinks(tvUsernameDetail, httpPattern2, "");

        Glide.with(this).load(tweet.user.profileIamgeURL.replace("normal", "bigger")).into(ivProfileImageDetail);

        Log.d("DetailActivity", tweet.user.screenName);
        Log.d("DetailActivity", tweet.user.name);
        Log.d("DetailActivity", tweet.body);
        Log.d("DetailActivity", tweet.createdAt);
        Log.d("DetailActivity", tweet.user.profileIamgeURL);

    }
}
