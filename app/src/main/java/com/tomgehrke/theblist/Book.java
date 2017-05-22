package com.tomgehrke.theblist;

import android.graphics.Bitmap;
import android.media.Image;

public class Book {

    // Class Attributes
    private String mTitle;
    private String mAuthor;
    private Bitmap mCover;

    public Book(String title, String author) {
        this.mTitle = title;
        this.mAuthor = author;
    }

    public Book(String title, String author, Bitmap cover) {
        this.mTitle = title;
        this.mAuthor = author;
        this.mCover = cover;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Bitmap getCover() {
        return mCover;
    }

}
