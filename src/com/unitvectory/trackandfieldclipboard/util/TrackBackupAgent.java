package com.unitvectory.trackandfieldclipboard.util;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * The BackupAgent responsible for backing up the user's preferences.
 * 
 * @author Jared Hatfield
 * 
 */
public class TrackBackupAgent extends BackupAgentHelper {

    /**
     * A key to uniquely identify the set of backup data.
     */
    static final String PREFS_BACKUP_KEY = "prefs";

    /**
     * Allocate a helper and add it to the backup agent.
     */
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this,
                        TrackAndFieldPreferences.PREFS_NAME);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
