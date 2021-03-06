package com.nikitagusarov.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class NewSubscriptionActivity extends AppCompatActivity {

    EditText subscriptionTitle;
    EditText subscriptionURL;

    FeedList feedList;
    SharedPreferences preferences;

    final int predefinedItemsCount = 2;
    String[] predefinedTitles = {
            "Onliner",
            "TUT.BY"
    };
    String[] predefinedURLs = {
            "https://www.onliner.by/feed",
            "https://news.tut.by/rss/index.rss"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serializedFeedList = preferences.getString("subscriptions", "");
        feedList = FeedList.parse(serializedFeedList);

        subscriptionTitle = (EditText) findViewById(R.id.newSubscriptionTitle);
        subscriptionURL = (EditText) findViewById(R.id.newSubscriptionURL);

        setPredefinedRandomFeed();
    }

    public void addNewSubscription(View view) {
        String title = subscriptionTitle.getText().toString();
        String url = subscriptionURL.getText().toString();

        try {
            Feed feed = new Feed(title, url);
            feedList.add(feed);

            preferences.edit().putString("subscriptions", feedList.toString()).commit();

            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("subscriptionId", feed.id);
            startActivity(intent);
        }
        catch(Exception e) {
            Toast.makeText(this, "Invalid URL.", Toast.LENGTH_LONG).show();
        }
    }

    private void setPredefinedRandomFeed() {
        int randomIndex = new Random().nextInt(predefinedItemsCount);
        subscriptionTitle.setText(predefinedTitles[randomIndex]);
        subscriptionURL.setText(predefinedURLs[randomIndex]);
    }
}
