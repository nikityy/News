package com.nikitagusarov.news;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mac on 17/12/2016.
 */
public class FeedList {

    private ArrayList<Feed> feedList = new ArrayList<>();

    public void add(Feed feed) {
        feedList.add(feed);
    }

    public ArrayList<Feed> getList() {
        return feedList;
    }

    public Feed getFeedById(int id) {
        Iterator<Feed> iterator = getList().iterator();
        Feed feed;

        while(iterator.hasNext()) {
            feed = iterator.next();
            if (feed.id == id) {
                return feed;
            }
        }

        return null;
    }

}
