package com.box.onecloud.android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
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

    /** Intent action for registering an installation that came through the OneCloud program. */
    public static final String ACTION_BOX_INSTALL_REFERRED = "com.box.android.INSTALL_REFERRER";

    /** Intent action for restoring a OneCloudData object. */
    public static final String ACTION_BOX_RESTORE_ONE_CLOUD_DATA = "com.box.android.RESTORE_ONE_CLOUD_DATA";

    /** Intent action for creating a sibling OneCloudData. */
    public static final String ACTION_BOX_CREATE_SIBLING_ONE_CLOUD_DATA = "com.box.android.CREATE_SIBLING_ONE_CLOUD_DATA";

    /** Extras key for a OneCloudData handshake. */
    public static final String EXTRA_ONE_CLOUD_HANDSHAKE = "com.box.android.ONE_CLOUD_HANDSHAKE";

    /** Extras key for a OneCloudData token. */
    public static final String EXTRA_ONE_CLOUD_TOKEN = "com.box.android.ONE_CLOUD_TOKEN";

    /** Extras key for a OneCloudData transaction object. */
    public static final String EXTRA_ONE_CLOUD = "com.box.android.ONE_CLOUD";

    /** Extras key for package name. */
    public static final String EXTRA_PACKAGE_NAME = "com.box.android.PACKAGE_NAME";

    /** Extras key for package name. */
    public static final String EXTRA_REFERRER = "com.box.android.REFERRER";

    /** Box package name. */
    public static final String BOX_PACKAGE_NAME = "com.box.android";

    /** OneCloud receiver on Box side. */
    public static final String BOX_RECEIVER_CLASS_NAME = "com.box.android.onecloud.OneCloudReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        OneCloudData oneCloudData = null;
        if (intent.getParcelableExtra(EXTRA_ONE_CLOUD) != null) {
            oneCloudData = (OneCloudData) intent.getParcelableExtra(EXTRA_ONE_CLOUD);
            oneCloudData.sendHandshake(context);
        }

        if (intent.getAction().equals(ACTION_BOX_EDIT_FILE)) {
            onEditFileRequested(context, oneCloudData);
        }
        else if (intent.getAction().equals(ACTION_BOX_CREATE_FILE)) {
            onCreateFileRequested(context, oneCloudData);
        }
        else if (intent.getAction().equals(ACTION_BOX_VIEW_FILE)) {
            onViewFileRequested(context, oneCloudData);
        }
        else if (intent.getAction().equals(ACTION_BOX_LAUNCH)) {
            onLaunchRequested(context, oneCloudData);
        }
        else if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
            final String referrer = intent.getStringExtra("referrer");
            if (referrer == null) {
                return;
            }
            if (!referrer.toLowerCase().contains("box")) {
                // We only care about installations that happened as a result of a Box referral.
                return;
            }

            Intent broadcast = new Intent(ACTION_BOX_INSTALL_REFERRED);
            broadcast.setComponent(new ComponentName(BOX_PACKAGE_NAME, BOX_RECEIVER_CLASS_NAME));
            broadcast.putExtra(EXTRA_REFERRER, referrer);
            broadcast.putExtra(EXTRA_PACKAGE_NAME, context.getPackageName());
            context.sendBroadcast(broadcast);
        }
    }

    /**
     * Box has requested that you modify an existing Box file. You should load UI for the user to modify the file and save it back to Box by calling
     * uploadNewVersion().
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloudData
     *            OneCloudData transaction object.
     */
    public abstract void onEditFileRequested(final Context context, final OneCloudData oneCloudData);

    /**
     * Box has requested that you create a new file that should be uploaded to Box by calling uploadNewFile(). You should load UI for the user to create a new
     * file of the given type.
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloudData
     *            OneCloudData transaction object.
     */
    public abstract void onCreateFileRequested(final Context context, final OneCloudData oneCloudData);

    /**
     * Box has requested that you open and show the contents of a file to the user. You should load a read-only UI for the user to view the file.
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloudData
     *            OneCloudData transaction object.
     */
    public abstract void onViewFileRequested(final Context context, final OneCloudData oneCloudData);

    /**
     * Box has requested that you launch your app in no particular mode. You should load general UI that is most appropriate as a starting screen.
     * 
     * @param context
     *            The Context in which the receiver is running.
     * @param oneCloudData
     *            OneCloudData transaction object.
     */
    public abstract void onLaunchRequested(final Context context, final OneCloudData oneCloudData);
}
