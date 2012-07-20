package com.box.onecloud.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/**
 * Represents a OneCloud transaction. Data can be read and written to through this, and can be uploaded back to Box. This class implements Parcelable which
 * means you can pass it around your activities and other contexts through intent extras.
 */
public class OneCloudData implements Parcelable {

    /** Binder back to Box through AIDL. */
    private OneCloudInterface mBinder;

    /** Whether or not a handshake has taken place to verify the identity on the other side of the binder. */
    private boolean mHandshaken = false;

    /** Box app version. Gets sent during handshake. */
    private int mBoxAppVersionCode = 0;

    /**
     * Default constructor.
     * 
     * @param binder
     *            OneCloudInterface.
     */
    public OneCloudData(final OneCloudInterface binder) {
        mBinder = binder;
    }

    /**
     * Constructor used when the object is being recreated from a Parcel. This should not be called manually.
     * 
     * @param in
     *            Parcel.
     */
    public OneCloudData(final Parcel in) {
        readFromParcel(in);
    }

    /**
     * Get the token for this OneCloud transaction. This method requires the Box app to be at version 1.9.0 or greater.
     * 
     * @return OneCloud token, or -1 if this transaction is no longer valid. Valid tokens can be negative, except for -1 which denotes an invalid transaction.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public long getToken() throws NoSuchMethodException {
        if (mBoxAppVersionCode < 19000) {
            throw new NoSuchMethodException("Requires Box app version 1.9.0 or later. Installed Box app is at " + mBoxAppVersionCode);
        }
        if (!isBinderValid()) {
            return -1;
        }
        try {
            return mBinder.getToken();
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    /**
     * If this transaction relates to a particular file on Box, then this will return the name of that file. Note that this method always returns the file name
     * that is actually on Box. For example, if you are uploading a new file to Box, this method will return null until the upload has actually completed.
     * 
     * @return File name on Box.
     */
    public String getFileName() {
        if (!isBinderValid()) {
            return null;
        }
        try {
            return mBinder.getFileName();
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * If this transaction relates to a particular file on Box, then this will return the size in bytes of that file. Note that this method always returns the
     * file size on Box. For example, if you are uploading a new file to Box, this method will return 0 until the file has actually been uploaded to Box.
     * 
     * @return File size on Box.
     */
    public long getFileSize() {
        if (!isBinderValid()) {
            return 0;
        }
        try {
            return mBinder.getFileSize();
        }
        catch (RemoteException e) {
            return 0;
        }
    }

    /**
     * If this transaction relates to a file, then this will be the mime type of the file. For example, if you are being asked to create a file, then this will
     * be the mime type of the file you are being asked to create.
     * 
     * @return Mime type.
     */
    public String getMimeType() {
        if (!isBinderValid()) {
            return null;
        }
        try {
            return mBinder.getMimeType();
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * If this transaction relates to a file, this is the unique id of the file on Box. This method requires the Box app to be at version 1.9.0 or greater.
     * 
     * @return The file id of the file. Valid if value is greater than or equal to 0.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public long getFileId() throws NoSuchMethodException {
        if (mBoxAppVersionCode < 19000) {
            throw new NoSuchMethodException("Requires Box app version 1.9.0 or later.");
        }
        if (!isBinderValid()) {
            return -1;
        }
        try {
            return mBinder.getFileId();
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    /**
     * If this transaction relates to a file, the parent folder id of the file. This method requires the Box app to be at version 1.9.0 or greater.
     * 
     * @return The folder id of the file. Valid if value is greater than or equal to 0.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public long getFolderId() throws NoSuchMethodException {
        if (mBoxAppVersionCode < 19000) {
            throw new NoSuchMethodException("Requires Box app version 1.9.0 or later.");
        }
        if (!isBinderValid()) {
            return -1;
        }
        try {
            return mBinder.getFolderId();
        }
        catch (RemoteException e) {
            return -1;
        }
    }

    /**
     * This is the folder path to the file on Box separated by "/". This method requires the Box app to be at version 1.9.0 or greater.
     * 
     * @return Folder path on box.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public String getFolderPath() throws NoSuchMethodException {
        if (mBoxAppVersionCode < 19000) {
            throw new NoSuchMethodException("Requires Box app version 1.9.0 or later.");
        }
        if (!isBinderValid()) {
            return null;
        }
        try {
            return mBinder.getFolderPath();
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * This will be the username of the current box user if the current application has the privilege to know the user. Otherwise null. This method requires the
     * Box app to be at version 1.9.0 or greater.
     * 
     * @return User name.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public String getUsername() throws NoSuchMethodException {
        if (mBoxAppVersionCode < 19000) {
            throw new NoSuchMethodException("Requires Box app version 1.9.0 or later.");
        }
        if (!isBinderValid()) {
            return null;
        }
        try {
            return mBinder.getUsername();
        }
        catch (RemoteException e) {
            return null;
        }
    }

    /**
     * Open an input stream to retrieve the Box file data.
     * 
     * @return InputStream from which you can read the Box file data, or null if the input stream could no longer be retrieved.
     */
    public InputStream getInputStream() {
        if (!isBinderValid()) {
            return null;
        }

        InputStream inputStream = new InputStream() {

            @Override
            public int available() {
                try {
                    return mBinder.iAvailable();
                }
                catch (RemoteException e) {
                    return 0;
                }
            }

            @Override
            public void close() {
                try {
                    mBinder.iClose();
                }
                catch (RemoteException e) {
                    // e.printStackTrace();
                }
            }

            @Override
            public void mark(final int readlimit) {
                try {
                    mBinder.iMark(readlimit);
                }
                catch (RemoteException e) {
                    return;
                }
            }

            @Override
            public boolean markSupported() {
                try {
                    return mBinder.iMarkSupported();
                }
                catch (RemoteException e) {
                    return false;
                }
            }

            @Override
            public int read(final byte[] buffer) throws IOException {
                try {
                    return mBinder.iReadAll(buffer);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public int read() throws IOException {
                try {
                    return mBinder.iReadOne();
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public int read(final byte[] buffer, final int offset, final int length) throws IOException {
                try {
                    return mBinder.iRead(buffer, offset, length);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public synchronized void reset() {
                try {
                    mBinder.iReset();
                }
                catch (RemoteException e) {
                    return;
                }
            }

            @Override
            public long skip(final long byteCount) throws IOException {
                try {
                    return mBinder.iSkip(byteCount);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }
        };
        return inputStream;
    }

    /**
     * Open an output stream to write Box file data. You MUST close() the OutputStream after writing to it so that the OneCloudFile takes on the new data.
     * 
     * @return An OutputStream to which you can write Box file data, or null if the output stream could not be retrieved.
     */
    public OutputStream getOutputStream() {
        if (!isBinderValid()) {
            return null;
        }

        OutputStream outputStream = new OutputStream() {

            @Override
            public void close() throws IOException {
                try {
                    mBinder.oClose();
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public void flush() throws IOException {
                try {
                    mBinder.oFlush();
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public void write(final byte[] buffer, final int offset, final int count) throws IOException {
                try {
                    mBinder.oWrite(buffer, offset, count);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public void write(final byte[] buffer) throws IOException {
                try {
                    mBinder.oWriteAll(buffer);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public void write(final int oneByte) throws IOException {
                try {
                    mBinder.oWriteOne(oneByte);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }
        };
        return outputStream;
    }

    /**
     * Upload the contents of this OneCloudFile as a new version to Box. This overrides the current version on Box. Presumably, this means that you have written
     * new data to this OneCloudFile by writing to the output stream which you can get from oneCloudFile.getOutputStream().
     * 
     * @param listener
     *            An upload listener which you can use to monitor the upload progress. This can be null if you do not need to monitor the upload.
     * @throws RemoteException
     *             Thrown if there was a problem triggering the upload. Note, this does not get thrown if there was an error *during* and upload.
     */
    public void uploadNewVersion(final UploadListener listener) throws RemoteException {
        if (!isBinderValid()) {
            return;
        }
        mBinder.uploadNewVersion(new FileUploadCallbacks.Stub() {

            @Override
            public void onProgress(final long bytesTransferred, final long totalBytes) throws RemoteException {
                if (listener != null) {
                    listener.onProgress(bytesTransferred, totalBytes);
                }
            }

            @Override
            public void onComplete() throws RemoteException {
                if (listener != null) {
                    listener.onComplete();
                }
            }

            @Override
            public void onError() throws RemoteException {
                if (listener != null) {
                    listener.onError();
                }
            }
        });
    }

    /**
     * Upload the contents of this OneCloudFile as a new version to Box with a new file name. This overrides the current version on Box. Presumably, this means
     * that you have written new data to this OneCloudFile by writing to the output stream which you can get from oneCloudFile.getOutputStream().
     * 
     * @param newFileName
     *            The new file name that the file will take on in Box.
     * @param listener
     *            An upload listener which you can use to monitor the upload progress. This can be null if you do not need to monitor the upload.
     * @throws RemoteException
     *             Thrown if there was a problem triggering the upload. Note, this does not get thrown if there was an error *during* and upload.
     */
    public void uploadNewVersion(final String newFileName, final UploadListener listener) throws RemoteException {
        if (!isBinderValid()) {
            return;
        }
        mBinder.uploadNewVersionWithNewName(newFileName, new FileUploadCallbacks.Stub() {

            @Override
            public void onProgress(final long bytesTransferred, final long totalBytes) throws RemoteException {
                if (listener != null) {
                    listener.onProgress(bytesTransferred, totalBytes);
                }
            }

            @Override
            public void onComplete() throws RemoteException {
                if (listener != null) {
                    listener.onComplete();
                }
            }

            @Override
            public void onError() throws RemoteException {
                if (listener != null) {
                    listener.onError();
                }
            }
        });
    }

    /**
     * Upload the contents of this OneCloudFile as a new file to Box.
     * 
     * @param suggestedFileName
     *            The file name that will be suggested to the user for the new file.
     * @param listener
     *            An upload listener which you can use to monitor the upload progress. This can be null if you do not need to monitor the upload.
     * @throws RemoteException
     *             Thrown if there was a problem triggering the upload. Note, this does not get thrown if there was an error *during* and upload.
     */
    public void uploadNewFile(final String suggestedFileName, final UploadListener listener) throws RemoteException {
        if (!isBinderValid()) {
            return;
        }
        mBinder.uploadNewFile(suggestedFileName, new FileUploadCallbacks.Stub() {

            @Override
            public void onProgress(final long bytesTransferred, final long totalBytes) throws RemoteException {
                if (listener != null) {
                    listener.onProgress(bytesTransferred, totalBytes);
                }
            }

            @Override
            public void onComplete() throws RemoteException {
                if (listener != null) {
                    listener.onComplete();
                }
            }

            @Override
            public void onError() throws RemoteException {
                if (listener != null) {
                    listener.onError();
                }
            }
        });
    }

    /**
     * A listener through which you can monitor file uploads.
     * 
     */
    public interface UploadListener {

        /**
         * Called during file upload. For example you could use this to draw a progress bar.
         * 
         * @param bytesTransferred
         *            The number of bytes transferred so far.
         * @param totalBytes
         *            The total bytes that will have transferred when the upload has completed.
         */
        void onProgress(long bytesTransferred, long totalBytes);

        /**
         * Called when the upload has successfully completed.
         */
        void onComplete();

        /**
         * Called if the upload has failed.
         */
        void onError();
    }

    /**
     * Laucnh the Box app.
     * 
     * @throws RemoteException
     *             Thrown if the connection to Box is no longer active.
     */
    public void launch() throws RemoteException {
        if (!isBinderValid()) {
            return;
        }
        mBinder.launch();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStrongBinder(mBinder.asBinder());
        dest.writeByte((byte) (mHandshaken ? 1 : 0));
        dest.writeInt(mBoxAppVersionCode);
    }

    /**
     * Read back one self from a parcel.
     * 
     * @param in
     *            Parcel.
     */
    private void readFromParcel(final Parcel in) {
        mBinder = OneCloudInterface.Stub.asInterface(in.readStrongBinder());
        mHandshaken = in.readByte() == 1;
        mBoxAppVersionCode = in.readInt();
    }

    /**
     * Parcel CREATOR.
     */
    public static final Parcelable.Creator<OneCloudData> CREATOR = new Parcelable.Creator<OneCloudData>() {

        @Override
        public OneCloudData createFromParcel(final Parcel in) {
            return new OneCloudData(in);
        }

        @Override
        public OneCloudData[] newArray(final int size) {
            return new OneCloudData[size];
        }
    };

    /**
     * Trigger a handshake between us and the Box app.
     * 
     * @param context
     *            Context.
     */
    public void sendHandshake(final Context context) {
        try {
            mBoxAppVersionCode = context.getPackageManager().getPackageInfo(BoxOneCloudReceiver.BOX_PACKAGE_NAME, 0).versionCode;
        }
        catch (NameNotFoundException e1) {
            // e1.printStackTrace();
            return;
        }

        HandshakeCallback handshake = new HandshakeCallback.Stub() {

            @Override
            public void onShake() throws RemoteException {
                String[] packages = context.getPackageManager().getPackagesForUid(Binder.getCallingUid());
                if (packages.length == 1 && packages[0].equals(BoxOneCloudReceiver.BOX_PACKAGE_NAME)) {
                    mHandshaken = true;
                }
            }
        };
        try {
            mBinder.sendHandshake(handshake);
        }
        catch (RemoteException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Notify Box that the underlying data for this OneCloud transaction has changed. You normally do not need to call this except for scenarios where you have
     * modified the data through your own Box platform calls with your own API key.
     * 
     * @throws RemoteException
     *             Thrown if the connection to Box is no longer active.
     */
    public void notifyDataChanged() throws RemoteException {
        if (!isBinderValid()) {
            return;
        }
        mBinder.notifyDataChanged();
    }

    /**
     * Create a new OneCloudData object that will allow you to create a new file in the same directory on Box. This method should not be called on the UI thread
     * since it may take a few seconds to complete. This method requires the Box app to be at version 1.9.0 or greater.
     * 
     * @param context
     *            App context.
     * @return Sibling OneCloudData object, or null if there was an error.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public OneCloudData createNewSibling(final Context context) throws NoSuchMethodException {
        try {
            int boxAppVersionCode = context.getPackageManager().getPackageInfo(BoxOneCloudReceiver.BOX_PACKAGE_NAME, 0).versionCode;
            if (boxAppVersionCode < 19000) {
                throw new NoSuchMethodException("Requires Box app version 1.9.0 or later.");
            }
        }
        catch (NameNotFoundException e1) {
            // e.printStackTrace();
            return null;
        }

        Intent intent = new Intent(BoxOneCloudReceiver.ACTION_BOX_CREATE_SIBLING_ONE_CLOUD_DATA);
        intent.setComponent(new ComponentName(BoxOneCloudReceiver.BOX_PACKAGE_NAME, BoxOneCloudReceiver.BOX_RECEIVER_CLASS_NAME));
        intent.putExtra(BoxOneCloudReceiver.EXTRA_ONE_CLOUD_TOKEN, getToken());

        // Used to wait for the Box app to send us back the OneCloudData object.
        final CustomCountDownLatch countDownLatch = new CustomCountDownLatch(1);

        // Send a handshake along with the broadcast.
        OneCloudHandshakeInterface.Stub handshake = new OneCloudHandshakeInterface.Stub() {

            @Override
            public void sendHandshake(final HandshakeCallback handshakeCallback) throws RemoteException {
                String[] packages = context.getPackageManager().getPackagesForUid(Binder.getCallingUid());
                if (packages.length == 1 && packages[0].equals(BoxOneCloudReceiver.BOX_PACKAGE_NAME)) {
                    handshakeCallback.onShake();
                }
            }

            @Override
            public void sendOneCloudData(final OneCloudInterface oneCloudInterface) throws RemoteException {
                OneCloudData ocd = new OneCloudData(oneCloudInterface);
                ocd.sendHandshake(context);
                countDownLatch.attachOneCloudData(ocd);
                countDownLatch.countDown();
            }
        };
        intent
            .putExtra(BoxOneCloudReceiver.EXTRA_ONE_CLOUD_HANDSHAKE, new OneCloudHandshake(OneCloudHandshakeInterface.Stub.asInterface(handshake.asBinder())));
        context.sendBroadcast(intent);

        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
            return countDownLatch.getOneCloudData();
        }
        catch (InterruptedException e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * CountDownLatch that allows for OneCloudData objects to be attached to it. Useful when waiting for binder callbacks to send us data.
     * 
     */
    private static class CustomCountDownLatch extends CountDownLatch {

        /** OneCloudData object. */
        private OneCloudData mOcd;

        /**
         * Default constructor.
         * 
         * @param count
         *            Latch count.
         */
        public CustomCountDownLatch(int count) {
            super(count);
        }

        /**
         * Attach a OneCloudData object.
         * 
         * @param ocd
         *            OneCloudData object.
         */
        public void attachOneCloudData(OneCloudData ocd) {
            mOcd = ocd;
        }

        /**
         * Get an attached OneCloudData object.
         * 
         * @return OneCloudData object.
         */
        public OneCloudData getOneCloudData() {
            return mOcd;
        }
    }

    /**
     * Check whether the binder interface back to Box is valid.
     * 
     * @return True if valid, false otherwise.
     */
    private boolean isBinderValid() {
        return mHandshaken && mBinder != null && mBinder.asBinder().isBinderAlive();
    }

    /**
     * Restore a OneCloudData object through a token. In general you should not need this since OneCloudData is Parcelable and can be persisted through methods
     * such as Activity.onSavedInstanceState. Use this only if you must persist OneCloud transactions through scenarios where mechanisms such as
     * Activity.onSavedInstanceState do not suffice. This method should not be called on the UI thread since it may take a few seconds to complete. This method
     * requires the Box app to be at version 1.9.0 or greater.
     * 
     * @param context
     *            Context.
     * @param token
     *            OneCloud token. This is not arbitrary. You must have obtained this by getting the token of a OneCloudData object you had in the past.
     * @return OneCloudData object or null of one could not be retrieved.
     * @throws NoSuchMethodException
     *             Thrown if the Box app installed does not yet support this method (you should fail gracefully and/or ask the user to upgrade their Box app).
     */
    public static OneCloudData restoreFromToken(final Context context, final long token) throws NoSuchMethodException {
        try {
            int boxAppVersionCode = context.getPackageManager().getPackageInfo(BoxOneCloudReceiver.BOX_PACKAGE_NAME, 0).versionCode;
            if (boxAppVersionCode < 19000) {
                throw new NoSuchMethodException("Requires Box app version 1.9.0 or later.");
            }
        }
        catch (NameNotFoundException e1) {
            // e.printStackTrace();
            return null;
        }

        Intent intent = new Intent(BoxOneCloudReceiver.ACTION_BOX_RESTORE_ONE_CLOUD_DATA);
        intent.setComponent(new ComponentName(BoxOneCloudReceiver.BOX_PACKAGE_NAME, BoxOneCloudReceiver.BOX_RECEIVER_CLASS_NAME));
        intent.putExtra(BoxOneCloudReceiver.EXTRA_ONE_CLOUD_TOKEN, token);

        // Used to wait for the Box app to send us back the OneCloudData object.
        final CustomCountDownLatch countDownLatch = new CustomCountDownLatch(1);

        // Send a handshake along with the broadcast.
        OneCloudHandshakeInterface.Stub handshake = new OneCloudHandshakeInterface.Stub() {

            @Override
            public void sendHandshake(final HandshakeCallback handshakeCallback) throws RemoteException {
                String[] packages = context.getPackageManager().getPackagesForUid(Binder.getCallingUid());
                if (packages.length == 1 && packages[0].equals(BoxOneCloudReceiver.BOX_PACKAGE_NAME)) {
                    handshakeCallback.onShake();
                }
            }

            @Override
            public void sendOneCloudData(final OneCloudInterface oneCloudInterface) throws RemoteException {
                OneCloudData ocd = new OneCloudData(oneCloudInterface);
                ocd.sendHandshake(context);
                countDownLatch.attachOneCloudData(ocd);
                countDownLatch.countDown();
            }
        };
        intent
            .putExtra(BoxOneCloudReceiver.EXTRA_ONE_CLOUD_HANDSHAKE, new OneCloudHandshake(OneCloudHandshakeInterface.Stub.asInterface(handshake.asBinder())));
        context.sendBroadcast(intent);
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
            return countDownLatch.getOneCloudData();
        }
        catch (InterruptedException e) {
            // e.printStackTrace();
        }
        return null;
    }
}
