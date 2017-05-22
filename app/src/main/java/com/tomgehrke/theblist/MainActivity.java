package com.tomgehrke.theblist;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<ArrayList<Book>> {

    // Log tag to be used across the application
    public static final String LOG_TAG = MainActivity.class.getName();

    // Constants
    private static final int GOOGLE_BOOKS_LOADER_ID = 1;
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=25&q=";

    // Objects to keep in memory
    private BookArrayAdapter mBookArrayAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mLoadingIndicator;
    private String mSearchUrl = "";
    private Boolean mFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the target ListVIew
        ListView bookListView = (ListView) findViewById(R.id.book_listview);

        // Set up the BookArrayAdapter
        mBookArrayAdapter = new BookArrayAdapter(this, new ArrayList<Book>());

        // Set the ListView's adapter
        bookListView.setAdapter(mBookArrayAdapter);

        // Get the TextView that is displayed when there is nothing in the list
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_textview);

        // Set the ListView's empty state view
        bookListView.setEmptyView(mEmptyStateTextView);

        // Get the loading indicator
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_spinner);
        mLoadingIndicator.setVisibility(View.GONE);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookListVIew();
            }
        });

        // Need to update the list view even if this is the first time through in order to
        // initialize the Loader. This allows it to survive configuration (e.g. orientation)
        // changes.
        updateBookListVIew();
    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, mSearchUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {

        if (mSearchUrl.isEmpty()) {
            mEmptyStateTextView.setText(getString(R.string.instructions));
        } else {
            // Change text of the empty state view to reflect that no books were found
            mEmptyStateTextView.setText(getString(R.string.no_books));
        }

        // Clear the adapter
        mBookArrayAdapter.clear();

        // If there are books to be shown, add them to the adapter
        if (books != null && !books.isEmpty()) {
            mBookArrayAdapter.addAll(books);
        }

        // All done so hide the progress bar
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        // Loader was reset so no need to keep data
        mBookArrayAdapter.clear();
    }

    // Execute on Search button click
    private void updateBookListVIew() {

        mBookArrayAdapter.clear();

        // Get contents of search field
        EditText search_text = (EditText) findViewById(R.id.search_text);
        String searchText = search_text.getText().toString();

        if (searchText.isEmpty()) {
            mSearchUrl = "";
        } else {
            mSearchUrl = GOOGLE_BOOKS_API_URL + search_text.getText().toString();
        }

        // Check for network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) { // If there is a connection...

            // Inform user that we're searching and clear the list in preparation
            mEmptyStateTextView.setText(getString(R.string.searching));
            mBookArrayAdapter.clear();

            // Show progress indicator
            mLoadingIndicator.setVisibility(View.VISIBLE);

            // Get the LoaderManager and initialize it
            LoaderManager loaderManager = getLoaderManager();

            if (mFirstTime) {
                loaderManager.initLoader(GOOGLE_BOOKS_LOADER_ID, null, this);
                mFirstTime = false;
            }else {
                loaderManager.restartLoader(GOOGLE_BOOKS_LOADER_ID, null, this);
            }

        } else { // If there is no connection
            // Change text of the empty state view to reflect lack of network connectivity and clear the list
            mEmptyStateTextView.setText(getString(R.string.no_network));
            mBookArrayAdapter.clear();
        }

    }
}
