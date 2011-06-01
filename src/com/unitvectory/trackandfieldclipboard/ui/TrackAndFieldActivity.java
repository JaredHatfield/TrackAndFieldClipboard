package com.unitvectory.trackandfieldclipboard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.unitvectory.trackandfieldclipboard.R;
import com.unitvectory.trackandfieldclipboard.model.FieldEvent;

public class TrackAndFieldActivity extends Activity {

    static final int NEW_EVENT_REQUEST = 639;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_and_field);
    }

    public void onDialogTestClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Title");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                value = "test";
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

        alert.show();
    }

    public void onTestEventClick(View v) {
        // Launch the new event activity
        FieldEvent event = FieldEvent.example();
        Intent intent = new Intent(this, DistanceClipboardActivity.class);
        intent.putExtra("event", event);
        this.startActivity(intent);
    }

    public void onNewEventClick(View v) {
        // Launch the new event activity
        Intent intent = new Intent(this, NewEventActivity.class);
        this.startActivityForResult(intent, NEW_EVENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_EVENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                FieldEvent event = (FieldEvent) data.getExtras().getSerializable("event");
                Intent intent = new Intent(this,
                        DistanceClipboardActivity.class);
                intent.putExtra("event", event);
                this.startActivity(intent);
            }
        }
    }
}