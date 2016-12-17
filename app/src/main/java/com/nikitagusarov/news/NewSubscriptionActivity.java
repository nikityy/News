package com.nikitagusarov.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewSubscriptionActivity extends AppCompatActivity {

    EditText subscriptionTitle;
    EditText subscriptionURL;

    FeedList feedList;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serializedFeedList = preferences.getString("subscriptions", "");
        feedList = FeedList.parse(serializedFeedList);

        preferences.edit().putString("subscriptions", "").commit();

        subscriptionTitle = (EditText) findViewById(R.id.newSubscriptionTitle);
        subscriptionURL = (EditText) findViewById(R.id.newSubscriptionURL);

        subscriptionTitle.setText("Onliner");
        subscriptionURL.setText("https://tech.onliner.by/feed");
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
}
