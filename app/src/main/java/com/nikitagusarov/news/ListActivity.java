package com.nikitagusarov.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {


    FeedList feedList;
    Feed currentFeed;

    NavigationView navigationView;
    FeedItemsAdapter feedItemsAdapter;
    SwipeRefreshLayout swipeContainer;

    SharedPreferences preferences;

    final int ADD_SUBSCRIPTION_ID = 1000;

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

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serializedFeedList = preferences.getString("subscriptions", "");
        feedList = FeedList.parse(serializedFeedList);

        if (!feedList.getList().isEmpty()) {
            Intent intent = getIntent();
            int id = intent.getIntExtra("subscriptionId", -1);

            System.out.println(id);

            if (id != -1) {
                currentFeed = feedList.getFeedById(id);
            } else {
                currentFeed = feedList.getList().get(0);
            }
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
            menuItem.setChecked(currentFeed == feed);
        }

        menu.add(1, ADD_SUBSCRIPTION_ID, ADD_SUBSCRIPTION_ID, "Add Subscription");
    }

    private void initFeedItemsList() {
        feedItemsAdapter = new FeedItemsAdapter(this, new ArrayList<FeedItem>());
        ListView feedItemsList = (ListView) findViewById(R.id.feedItemsList);
        feedItemsList.setAdapter(feedItemsAdapter);
        feedItemsList.setOnItemClickListener(this);

        if (!feedList.getList().isEmpty()) {
            // Create a progress bar to display while the list loads
            ProgressBar progressBar = new ProgressBar(feedItemsList.getContext());
            progressBar.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT,
                    DrawerLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            progressBar.setIndeterminate(true);
            feedItemsList.setEmptyView(progressBar);

            // Must add the progress bar to the root of the layout
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            root.addView(progressBar);
        }

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

        if (feedList.getList().isEmpty()) {
            menu.findItem(R.id.action_remove).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove) {
            int index = feedList.getList().indexOf(currentFeed);
            feedList.getList().remove(index);
            preferences.edit().putString("subscriptions", feedList.toString()).commit();

            Intent intent = new Intent(this, ListActivity.class);
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == ADD_SUBSCRIPTION_ID) {
            Intent intent = new Intent(this, NewSubscriptionActivity.class);
            startActivity(intent);
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

    public void onItemClick(AdapterView adapterView, View itemView, int id, long offset) {
        try {
            TextView title = (TextView) itemView.findViewById(R.id.feedItemTitle);
            String url = (String) title.getTag();

            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
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
            try {
                Iterator<FeedItem> iterator = feed.getMessages().iterator();
                FeedItem item;

                feedItemsAdapter.clear();

                while(iterator.hasNext()) {
                    item = iterator.next();
                    feedItemsAdapter.add(item);
                }

                swipeContainer.setRefreshing(false);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

}
