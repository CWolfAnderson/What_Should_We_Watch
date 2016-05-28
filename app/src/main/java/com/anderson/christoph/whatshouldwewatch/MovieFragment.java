package com.anderson.christoph.whatshouldwewatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieFragment extends Fragment {

    ArrayAdapter<String> movieAdapter;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks.
        int id = item.getItemId();

        // when the refresh button is clicked
        if (id == R.id.action_refresh) {
            FetchMovieTask movieTask = new FetchMovieTask();
            // movieTask.execute("eminem");
            movieTask.execute("2016-05-01");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] data = {
                "Jaws",
                "The Avengers",
                "Some other movie",
                "Something else",
                "Making a Murderer",
                "Who cares",
                "Whatever",
                "Enough?"
        };

        List<String> movies = new ArrayList<>(Arrays.asList(data));

        movieAdapter = new ArrayAdapter<String>(
                getActivity(), // the current context (this activity)
                R.layout.list_item_movie, // the name of the layout ID
                R.id.list_item_movie_textview, // the ID of the textview to populate
                movies); // movie data

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // get a reference to the ListView, and attach this adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_movie);
        listView.setAdapter(movieAdapter);

        // display a toast when a movie is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String movie = movieAdapter.getItem(position);
                Toast.makeText(getActivity(), movie, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;

    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        /**
         * Take the String representing the complete movie in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String TMDB_LIST = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_DESCRIPTION = "overview";

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(TMDB_LIST);

            int numMovies = 20;

            String[] resultStrs = new String[numMovies];

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieJson = movieArray.getJSONObject(i);

                String title = movieJson.getString(TMDB_TITLE);

                Log.d("title", "Title: " + title);

                String description = movieJson.getString(TMDB_DESCRIPTION);

                Log.d("description", "Description: " + description);

                resultStrs[i] = title;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            // if there's no movie, there's nothing to look up
            if (params.length ==0) {
                return null;
            }

            // declared outside the try/catch so they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // contains the raw JSON response as a string.
            String movieJsonStr = null;

            try {

                // construct the URL for the TheMovieDatabase query
                // http://api.themoviedb.org/3/search/movie?query=eminem/&api_key=f80d264860c34f8a56de05455c80846f
                // http://api.themoviedb.org/3/discover/movie?primary_release_date.gte=2016-01-01&primary_release_date.lte=2016-05-01/&api_key=f80d264860c34f8a56de05455c80846f
                // final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/search/movie?";
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?primary_release_date.gte=2016-01-01&";
                final String QUERY_PARAM = "primary_release_date.lte";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                        .build();

                // String apiKey = "&api_key=" + BuildConfig.THE_MOVIE_DATABASE_API_KEY;
                URL url = new URL(builtUri.toString());

                Log.d("uri", "Built URI" + builtUri.toString());

                // create the request to TheMovieDatabase, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // but it does make debugging a *lot* easier if you print out the completed buffer for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // stream empty ~ no point in parsing
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie JSON String: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // if the code didn't successfully get the movie data, there's no point in attempting to parse it
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch(JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        // this is what actually displays the titles
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                movieAdapter.clear();
                for (String movieStr : result) {
                    movieAdapter.add(movieStr);
                }
                // new data is back from the server
            }
        }

    }
}