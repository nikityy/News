package com.nikitagusarov.news;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mac on 17/12/2016.
 */
public class Feed {

    static int idCounter = 0;

    final List<FeedItem> entries = new ArrayList<FeedItem>();
    int id;
    String title;
    URL url;

    Feed(String title, String feedUrl) {
        this.id = idCounter++;
        this.title = title;
        try {
            this.url = new URL(feedUrl);
        }
        catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addItem(String title, String description, Date pubDate, String url, String imageURL) {
        FeedItem item = new FeedItem();
        item.setTitle(title);
        item.setDescription(description);
        item.setPubDate(pubDate);
        item.setURL(url);
        item.setImageURL(imageURL);
        entries.add(item);
    }

    public List<FeedItem> getMessages() {
        return entries;
    }

    public String toString() {
        return String.valueOf(id) + "::" + title + "::" + url;
    }

    public static Feed parse(String rawString) {
        Feed feed = null;
        try {
            String[] tokens = rawString.split("::");
            int id = Integer.valueOf(tokens[0]);
            String title = tokens[1];
            String url = tokens[2];

            feed = new Feed(title, url);
            feed.id = id;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return feed;
    }

}
