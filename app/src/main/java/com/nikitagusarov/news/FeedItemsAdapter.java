package com.nikitagusarov.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mac on 17/12/2016.
 */
public class FeedItemsAdapter extends ArrayAdapter<FeedItem> {

    public FeedItemsAdapter(Context context, ArrayList<FeedItem> feedItems) {
        super(context, 0, feedItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem feedItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
        }

        TextView feedItemTitle = (TextView) convertView.findViewById(R.id.feedItemTitle);

        feedItemTitle.setText(feedItem.title);

        return convertView;
    }
}