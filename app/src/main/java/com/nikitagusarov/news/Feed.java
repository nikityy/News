package com.nikitagusarov.news;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 17/12/2016.
 */
public class Feed {

    final List<FeedItem> entries = new ArrayList<FeedItem>();

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
