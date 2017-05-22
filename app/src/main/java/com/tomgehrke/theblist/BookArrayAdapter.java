package com.tomgehrke.theblist;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookArrayAdapter extends ArrayAdapter<Book> {

    public BookArrayAdapter(@NonNull Context context, @NonNull ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        BookItemViewHolder bookItemViewHolder;

        // Inflate our Book Item layout if there wasn't one already
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);

            // Set up BookItemViewHolder
            bookItemViewHolder = new BookItemViewHolder();

            // Find all the views
            bookItemViewHolder.title = (TextView) convertView.findViewById(R.id.title_textview);
            bookItemViewHolder.author = (TextView) convertView.findViewById(R.id.author_textview);
            bookItemViewHolder.cover = (ImageView) convertView.findViewById(R.id.cover_imageview);

            // Store holder with the view
            convertView.setTag(bookItemViewHolder);
        } else {
            // Just use the saved BookItemViewHolder. No need to do all the findViewById stuff.
            bookItemViewHolder = (BookItemViewHolder) convertView.getTag();
        }

        // Get current book
        final Book currentBook = getItem(position);

        // Find and update layout views (if we've got one)
        if (currentBook != null) {

            bookItemViewHolder.title.setText(currentBook.getTitle());
            bookItemViewHolder.author.setText(currentBook.getAuthor());
            bookItemViewHolder.cover.setImageBitmap(currentBook.getCover());
        }

        return convertView;
    }
}

class BookItemViewHolder {
    TextView title;
    TextView author;
    ImageView cover;
}