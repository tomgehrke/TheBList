package com.tomgehrke.theblist;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {

    private static final String LOG_TAG = BookLoader.class.getName();
    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {
        if (mUrl == null || mUrl.isEmpty()) {
            return null;
        }

        // Make the request, parse the response and pull a list of books
        ArrayList<Book> books = BookQueryUtils.fetchBookData(mUrl);
        return books;
    }
}
