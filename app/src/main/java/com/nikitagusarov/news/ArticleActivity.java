package com.nikitagusarov.news;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Iterator;

public class ArticleActivity extends AppCompatActivity {

    TextView titleView;
    TextView textView;

    View contentView;
    View loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        titleView = (TextView) findViewById(R.id.articleTitle);
        textView = (TextView) findViewById(R.id.articleText);
        contentView = findViewById(R.id.contentLayout);
        loadingView = findViewById(R.id.loadingLayout);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        if (url != null) {
            showLoading();
            ArticleObtainer obtainer = new ArticleObtainer();
            obtainer.execute(url);
        }
    }

    private void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    private void showLoading() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    private class ArticleObtainer extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                return MercuryAPI.getJSON(params[0]);
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response != null) {
                    String title = response.getString("title");
                    String text = response.getString("content");

                    titleView.setText(title);
                    textView.setText(Html.fromHtml(text));
                }

                showContent();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
}
