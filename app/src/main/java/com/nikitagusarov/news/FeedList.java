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

    public String toString() {
        Iterator<Feed> iterator = getList().iterator();
        String string = "";

        Feed feed;
        while(iterator.hasNext()) {
            feed = iterator.next();
            string += feed.toString() + ",";
        }

        return string;
    }

    public static FeedList parse(String rawString) {
        String[] strings = rawString.split(",");
        FeedList feedList = new FeedList();
        ArrayList<Feed> feeds = feedList.getList();
        Feed feed;

        for (int i = 0; i < strings.length; i++) {
            try {
                feed = Feed.parse(strings[i]);

                if (feed != null) {
                    feeds.add(feed);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return feedList;
    }

}
