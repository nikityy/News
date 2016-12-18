package com.nikitagusarov.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by mac on 17/12/2016.
 */
public class FeedItemsAdapter extends ArrayAdapter<FeedItem> {

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM HH:mm");

    public FeedItemsAdapter(Context context, ArrayList<FeedItem> feedItems) {
        super(context, 0, feedItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem feedItem = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.feedItemTitle);
            holder.pubDate = (TextView) convertView.findViewById(R.id.feedItemPubDate);
            holder.imageView = (ImageView) convertView.findViewById(R.id.feedItemImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.title.setText(feedItem.title);
            holder.pubDate.setText(dateFormat.format(feedItem.pubDate));
            holder.imageView.setTag(position);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        if (feedItem.imageURL != null) {
            new LoadImageTask(new ImageLoadController(position, holder)).execute(feedItem.imageURL);
        }

        return convertView;
    }

    private class ImageLoadController implements ImageLoadListener {

        int position;
        ViewHolder holder;

        ImageLoadController(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
            this.holder.imageView.setImageBitmap(null);
        }

        public void onImageLoaded(Bitmap bitmap) {
            if (bitmap != null && holder != null && holder.imageView != null && this.position == (int) holder.imageView.getTag()) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageBitmap(null);
            }
        };

        public void onError() {

        };
    }

    static class ViewHolder {
        TextView title;
        TextView pubDate;
        ImageView imageView;
    }
}