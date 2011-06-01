/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;

/**
 * The fragment that lists all of the files.
 * 
 * @author Jared Hatfield
 * 
 */
public class FileListFragment extends ListFragment {

    /**
     * The list of file names.
     */
    private List<String> directoryEntries = new ArrayList<String>();

    /**
     * Called when the activity is first created.
     * 
     * @param savedState
     *            The saved state.
     */
    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
                    long id) {
                return onLongListItemClick(v, pos, id);
            }
        });

        this.displayFiles();
    }

    /**
     * Refreshes the list of the files that is displayed.
     */
    private void displayFiles() {
        this.directoryEntries.clear();

        File[] files = getActivity().getFilesDir().listFiles();

        for (File file : files) {
            this.directoryEntries.add(file.getName());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(
                getActivity(), R.layout.file_row, this.directoryEntries);

        this.setListAdapter(directoryList);
    }

    /**
     * Handles the item click event.
     * 
     * @param l
     *            The list view.
     * @param v
     *            The view that was clicked.
     * @param position
     *            The position that was clicked.
     * @param id
     *            The id that was clicked.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            // TODO: This should be performed on a background thread.
            String filename = this.directoryEntries.get(position);
            FileInputStream input = this.getActivity().openFileInput(filename);
            Serializer serializer = new Persister();
            FieldEvent event = serializer.read(FieldEvent.class, input);
            Intent intent = new Intent(getActivity(),
                    DistanceClipboardActivity.class);
            intent.putExtra("event", event);
            this.startActivity(intent);
        } catch (Exception e) {
            Context context = getActivity().getApplicationContext();
            Toast toast = Toast.makeText(context, R.string.failed_open,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Handles the long click event.
     * 
     * @param v
     *            The view that was clicked.
     * @param position
     *            The position that was clicked.
     * @param id
     *            The id that was clicked.
     * @return True on success;
     */
    protected boolean onLongListItemClick(View v, int position, long id) {
        // TODO: Add a confirmation dialog
        String filename = this.directoryEntries.get(position);
        this.getActivity().deleteFile(filename);
        this.displayFiles();
        return true;
    }
}
