package com.box.onecloud.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Abstract BroadcastReceiver for handling braodcasts sent by the Box app and other OneCloud partners. Also provides methods for communicating back to Box.
 * 
 */
public abstract class BoxOneCloudReceiver extends BroadcastReceiver {

    /** Intent action for editing a file. */
    public static final String ACTION_BOX_EDIT_FILE = "com.box.android.EDIT_FILE";

    /** Intent action for creating a file. */
    public static final String ACTION_BOX_CREATE_FILE = "com.box.android.CREATE_FILE";

    /** Intent action for viewing a file. */
    public static final String ACTION_BOX_VIEW_FILE = "com.box.android.VIEW_FILE";

    /** Intent action for requesting app launch. */
    public static final String ACTION_BOX_LAUNCH = "com.box.android.LAUNCH";

    /** Extras key for a OneCloud transaction object. */
    public static final String EXTRA_ONE_CLOUD = "com.box.android.ONE_CLOUD";

    /** Box package name. */
    public static final String BOX_PACKAGE_NAME = "com.box.android";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        OneCloud oneCloud = null;
        if (intent.getParcelableExtra(EXTRA_ONE_CLOUD) != null) {
            oneCloud = (OneCloud) intent.getParcelableExtra(EXTRA_ONE_CLOUD);
            oneCloud.sendHandshake(context);
        }

        if (intent.getAction().equals(ACTION_BOX_EDIT_FILE)) {
            onEditFileRequested(context, oneCloud);
        }
        else if (intent.getAction().equals(ACTION_BOX_CREATE_FILE)) {
            onCreateFileRequested(context, oneCloud);
        }
        else if (intent.getAction().equals(ACTION_BOX_VIEW_FILE)) {
            onViewFileRequested(context, oneCloud);
        }
        else if (intent.getAction().equals(ACTION_BOX_LAUNCH)) {
            onLaunchRequested(context, oneCloud);
        }
    }

    /**
     * Box has requested that you modify an existing Box file. You should load UI for the user to modify the file and save it back to Box by calling
     * uploadNewVersion().
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloud
     *            OneCloud transaction object.
     */
    public abstract void onEditFileRequested(final Context context, final OneCloud oneCloud);

    /**
     * Box has requested that you create a new file that should be uploaded to Box by calling uploadNewFile(). You should load UI for the user to create a new
     * file of the given type.
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloud
     *            OneCloud transaction object.
     */
    public abstract void onCreateFileRequested(final Context context, final OneCloud oneCloud);

    /**
     * Box has requested that you open and show the contents of a file to the user. You should load a read-only UI for the user to view the file.
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloud
     *            OneCloud transaction object.
     */
    public abstract void onViewFileRequested(final Context context, final OneCloud oneCloud);

    /**
     * Box has requested that you launch your app in no particular mode. You should load general UI that is most appropriate as a starting screen.
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param boxToken
     *            A token that must be passed back to Box. You will need this when trying to send data back to Box.
     */
    public abstract void onLaunchRequested(final Context context, final OneCloud oneCloud);
}
