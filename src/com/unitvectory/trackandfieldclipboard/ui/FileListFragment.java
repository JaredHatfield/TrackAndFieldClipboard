/*
 * Track and Field Clipboard
 * Copyright 2011 Jared Hatfield.  All rights reserved.
 */
package com.unitvectory.trackandfieldclipboard.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.util.OpenClipboardTask;

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
    public void displayFiles() {
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
        String filename = this.directoryEntries.get(position);
        OpenClipboardTask openClipboardTask = new OpenClipboardTask(
                this.getActivity());
        openClipboardTask.execute(filename);
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
        final String filename = this.directoryEntries.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_file)
                .setCancelable(true)
                .setPositiveButton(R.string.delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete the file
                                getActivity().deleteFile(filename);
                                displayFiles();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }
}
