package com.nikitagusarov.news;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Iterator;

public class ListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {


    FeedList feedList;
    Feed currentFeed;

    NavigationView navigationView;
    FeedItemsAdapter feedItemsAdapter;
    SwipeRefreshLayout swipeContainer;

    final int ADD_SUBSCRIPTION_ID = -42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String serializedFeedList = preferences.getString("subscriptions", "");
        feedList = FeedList.parse(serializedFeedList);


        if (!feedList.getList().isEmpty()) {
            currentFeed = feedList.getList().get(0);
        }

        initNavigationMenu();
        initFeedItemsList();

        updateFeed();
    }

    private void initNavigationMenu() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        Iterator<Feed> iterator = feedList.getList().iterator();
        Feed feed;
        MenuItem menuItem;

        while(iterator.hasNext()) {
            feed = iterator.next();
            menuItem = menu.add(0, feed.id, feed.id, feed.title);
            menuItem.setCheckable(true);
        }

        menu.add(ADD_SUBSCRIPTION_ID, ADD_SUBSCRIPTION_ID, ADD_SUBSCRIPTION_ID, "Add Subscription");
    }

    private void initFeedItemsList() {
        feedItemsAdapter = new FeedItemsAdapter(this, new ArrayList<FeedItem>());
        ListView feedItemsList = (ListView) findViewById(R.id.feedItemsList);
        feedItemsList.setAdapter(feedItemsAdapter);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(feedItemsList.getContext());
        progressBar.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT,
                DrawerLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        feedItemsList.setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // Init swipe container
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == ADD_SUBSCRIPTION_ID) {
            
        } else {
            navigationView.setCheckedItem(id);
            Feed feed = feedList.getFeedById(id);

            if (feed != null) {
                currentFeed = feed;
                feedItemsAdapter.clear();
                updateFeed();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateFeed() {
        if (currentFeed == null) {
            return;
        }

        FeedObtainer obtainer = new FeedObtainer();
        obtainer.execute(currentFeed);
    }

    @Override
    public void onRefresh() {
        updateFeed();
    }

    private class FeedObtainer extends AsyncTask<Feed, Void, Feed> {

        @Override
        protected Feed doInBackground(Feed... params) {
            RSSFeedParser parser = new RSSFeedParser(params[0]);
            Feed feed = parser.readFeed();
            return feed;
        }

        @Override
        protected void onPostExecute(Feed feed) {
            Iterator<FeedItem> iterator = feed.getMessages().iterator();
            FeedItem item;

            feedItemsAdapter.clear();

            while(iterator.hasNext()) {
                item = iterator.next();
                feedItemsAdapter.add(item);
            }

            swipeContainer.setRefreshing(false);
        }

    }

}
