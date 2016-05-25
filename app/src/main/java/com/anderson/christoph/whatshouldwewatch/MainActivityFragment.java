package com.anderson.christoph.whatshouldwewatch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> movieAdapter;

    public MainActivityFragment() {
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

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_movie);
        listView.setAdapter(movieAdapter);

        return rootView;
    }
}