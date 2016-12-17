package com.nikitagusarov.news;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    public void addItem(String title, String description) {
        FeedItem item = new FeedItem();
        item.setTitle(title);
        item.setDescription(description);
        entries.add(item);
    }

    public List<FeedItem> getMessages() {
        return entries;
    }

}
