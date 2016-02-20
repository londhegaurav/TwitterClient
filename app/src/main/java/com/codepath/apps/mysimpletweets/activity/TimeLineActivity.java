package com.codepath.apps.mysimpletweets.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.DB.PostsDatabaseHelper;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapter.TweetsRecycleAdapter;
import com.codepath.apps.mysimpletweets.application.TwitterApplication;
import com.codepath.apps.mysimpletweets.application.TwitterClient;
import com.codepath.apps.mysimpletweets.filter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.fragment.ComposeTweetDailog;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimeLineActivity extends AppCompatActivity implements ComposeTweetDailog.OnFiltersSaveListener, ObservableScrollViewCallbacks {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private ArrayList<Tweet> tw;
    private TweetsRecycleAdapter aTweets;
    private RecyclerView rvTweets;
    private String tweetText;
    private JSONObject jsonTweet;
    private SwipeRefreshLayout swipeContainer;
    private ActionBar actionBar;
    private PostsDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);

        ObservableRecyclerView rvTweets = (ObservableRecyclerView) findViewById(R.id.rvTimelineGrid);
        rvTweets.setScrollViewCallbacks(this);

        databaseHelper = PostsDatabaseHelper.getInstance(this);

        //find the recycle view
        //rvTweets = (RecyclerView) findViewById(R.id.rvTimelineGrid);
        //create arraylist
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        //construct the adapter from data source
        if (!isNetworkAvailable()) {
            Toast.makeText(TimeLineActivity.this, "Please check your Internet connectivity.", Toast.LENGTH_LONG).show();
            aTweets = new TweetsRecycleAdapter(this, tweets, true);
        } else aTweets = new TweetsRecycleAdapter(this, tweets, false);
        //connect adapter to recyvle view
        rvTweets.setAdapter(aTweets);

        actionBar = getSupportActionBar();
        setActionBar();

        //First param is number of columns and second param is orientation i.e Vertical or Horizontal
        //StaggeredGridLayoutManager gridLayoutManager =
        //new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        //Attach the layout manager to the recycler view

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(TimeLineActivity.this, "Please check your Internet connectivity.", Toast.LENGTH_LONG).show();
                } else {
                    actionBar.hide();
                    Log.d("DEBUG", "Loading more API calls");
                    populateTimeline();
                    actionBar.show();
                }
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    Toast.makeText(TimeLineActivity.this, "Please check your Internet connectivity.", Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                } else {
                    actionBar.hide();
                    Log.d("DEBUG", "Calling Refresh");
                    fetchTimelineAsync();
                    actionBar.show();
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
    }

    private void setActionBar() {

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.mipmap.twitterimg);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setElevation(0);
        actionBar.setTitle("TwitterClient");
    }

    @Override
    public void onFiltersSave(String tweetText) {
        postStatus(tweetText);
        Log.i("DEBUG", "beforefromjson");

        //Toast.makeText(this, "new tweet" + this.tweetText, Toast.LENGTH_LONG).show();
    }


    public void callComposeTweet(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDailog editNameDialog = ComposeTweetDailog.newInstance("Some Title");
        editNameDialog.show(fm, "fragment_edit_name");

    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        if (scrollState == ScrollState.UP) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!actionBar.isShowing()) {
                actionBar.hide();
            }
        }
    }

    private void postStatus(String status) {

        client.postTweet(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getClass().toString(), response.toString());
                Tweet tweet = Tweet.fromJson(response);
                tweets.add(0, tweet);
                databaseHelper.addPost(Tweet.fromJson(response));
                //aTweets.clearData();
                aTweets.notifyItemInserted(aTweets.getItemCount() - 1);
                aTweets.notifyDataSetChanged();
                ArrayList<Tweet> t = databaseHelper.getAllPosts();

                //     for (int i = 0; i < t.size(); i++){
                Tweet ttweet = t.get(0);
                Log.d("test", String.valueOf(ttweet.getUid()));
                Log.d("test", String.valueOf(ttweet.getUser().getUid()));
                Log.d("test", ttweet.getUser().getProfileIamgeURL());
                Log.d("test", ttweet.getUser().getName());
                Log.d("test", ttweet.getBody());
                Log.d("test", ttweet.getUser().getScreenName());
                Log.d("test", ttweet.getCreatedAt());
                //   }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("DEBUG", "IN POST failure");
                Log.e(getClass().toString(), responseString);
            }
        }, status);
    }

    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                //aTweets.clearData();
                // ...the data has come back, add new items to your adapter...
                tweets.addAll(Tweet.fromJsonArray(json));
                //databaseHelper.deleteAllPostsAndUsers();
                //databaseHelper.deleteAllPostsAndUsers();
                databaseHelper.addAllPosts(Tweet.fromJsonArray(json));
                int cnt = databaseHelper.getProfilesCount();
                Log.d("DEBUG", "cnt" + String.valueOf(cnt));
                aTweets.notifyItemInserted(cnt - 1);
                aTweets.notifyDataSetChanged();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
            }
        });
    }

    // send an API request to get the timeline json
    //Fill the timeline by creating the tweet object from the json
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
                                   final Context context = TimeLineActivity.this;

                                   @Override
                                   public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                                       Log.d(getClass().toString(), json.toString());
                                       //json here
                                       // desrialize json
                                       // create models and add them to adapter
                                       // load the model data into listview
                                       //databaseHelper.deleteAllPostsAndUsers();
                                       databaseHelper.addAllPosts(Tweet.fromJsonArray(json));
                                       //tw = new ArrayList<Tweet>(databaseHelper.getAllPosts());
                                       //tweets = tw;
                                       tweets.addAll(Tweet.fromJsonArray(json));
                                       Log.d("DEBUG_TWeetSize", String.valueOf(tweets.size()));
                                       //int cnt = databaseHelper.getProfilesCount();
                                       //tweets.addAll(Tweet.fromJsonArray(json));
                                       //aTweets.clearData();
                                       aTweets.notifyItemInserted(aTweets.getItemCount() - 1);
                                       //aTweets.notifyDataSetChanged();


//               swipeContainer.setRefreshing(false);
                                   }

                                   @Override
                                   public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                       Log.d("DEBUG", errorResponse.toString());
                                       //Toast.makeText(this,"Rate limit Exceeded",Toast.LENGTH_LONG).show();

                                   }

                                   @Override
                                   public void onUserException(Throwable error) {
                                       super.onUserException(error);
                                       Log.d("DEBUG", error.toString());

                                       Toast.makeText(context, "Rate limit Exceeded", Toast.LENGTH_LONG).show();
                                   }
                               }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        if (!isNetworkAvailable())
            Toast.makeText(TimeLineActivity.this, "Please check your Internet connectivity.", Toast.LENGTH_LONG).show();
        else populateTimeline();
        setActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.componseTweet) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
