package com.tomgehrke.theblist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.tomgehrke.theblist.MainActivity.LOG_TAG;

public final class BookQueryUtils {

    // Class not intended to be instantiated
    private BookQueryUtils() {
    }

    // Create ArrayList of books
    public static ArrayList<Book> fetchBookData(String stringUrl) {
        URL requestUrl = createUrlFromString(stringUrl);

        // Attempt to create JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpsRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request.", e);
        }

        // Return ArrayList
        return extractFeatureFromJson(jsonResponse);
    }

    //Extract an ArrayList of Book objects from the JSON response
    @Nullable
    private static ArrayList<Book> extractFeatureFromJson(String jsonResponse) {

        // Is there something to parse?
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Prepare an empty ArrayList
        ArrayList<Book> books = new ArrayList<>();

        // Attempt to parse the JSON response
        try {
            JSONObject googleBooksObject = new JSONObject(jsonResponse);

            if (googleBooksObject.getInt("totalItems") > 0) {
                JSONArray items = googleBooksObject.getJSONArray("items");

                // Iterate through items array
                for (int i = 0; i < items.length(); i++) {

                    // Get the current book item
                    JSONObject currentBook = items.getJSONObject(i);

                    // Each Google Books item contain a volumeInfo object which contains the information
                    // we need
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");

                    // Get book authors
                    StringBuilder authorStringBuilder = new StringBuilder();

                    if (volumeInfo.has("authors")) {
                        JSONArray authors = volumeInfo.getJSONArray("authors");

                        authorStringBuilder.append(authors.getString(0));

                        for (int j = 1; j < authors.length(); j++) {
                            authorStringBuilder.append(", ").append(authors.getString(j));
                        }
                    }

                    // Get the cover image
                    Bitmap cover = null;

                    if (volumeInfo.has("imageLinks")) {
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String thumbnailAddress = imageLinks.getString("smallThumbnail");

                        URL coverUrl = createUrlFromString(thumbnailAddress);

                        try {
                            cover = BitmapFactory.decodeStream(coverUrl.openConnection().getInputStream());
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Problem downloading cover image.", e);
                        }
                    }

                    // Create new Book object and add it to the ArrayList
                    Book newBook = new Book(title, authorStringBuilder.toString(), cover);
                    books.add(newBook);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON response for book information.", e);
        }

        return books;
    }

    // Create URL object from String URL
    private static URL createUrlFromString(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem converting string into a URL.", e);
        }

        return url;
    }

    // Make the HTTP request in order to get the JSON response
    private static String makeHttpsRequest(URL url)
            throws IOException {

        String jsonResponse = "";

        // Make sure we've got a URL
        if (url != null) {
            HttpsURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream and parse if the request was successful
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = createStringFromInputStream(inputStream);
                } else {
                    // If the request was not successful log the response code
                    Log.e(LOG_TAG, "HTTP request for JSON returned the following code: " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving JSON response.", e);

            } finally {
                // Do some cleanup
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }

        return jsonResponse;
    }

    // Convert input stream JSON response into a string
    @NonNull
    private static String createStringFromInputStream(InputStream inputStream)
            throws IOException {

        StringBuilder responseStringBuilder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                responseStringBuilder.append(line);
                line = reader.readLine();
            }
        }

        return responseStringBuilder.toString();
    }
}
