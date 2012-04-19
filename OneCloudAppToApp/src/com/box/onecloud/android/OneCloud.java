package com.box.onecloud.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/**
 * Represents a OneCloud transaction. Data can be read and written to through this, and can be uploaded back to Box. This class implements Parcelable which
 * means you can pass it around your activities and other contexts through intent extras.
 */
public class OneCloud implements Parcelable {

    /** Binder back to Box through AIDL. */
    private OneCloudInterface mBinder;

    /** Whether or not a handshake has taken place to verify the identity on the other side of the binder. */
    private boolean mHandshaken = false;

    /**
     * Default constructor.
     * 
     * @param binder
     *            OneCloudInterface.
     */
    public OneCloud(OneCloudInterface binder) {
        mBinder = binder;
    }

    /**
     * Constructor used when the object is being recreated from a Parcel. This should not be called manually.
     * 
     * @param in
     *            Parcel.
     */
    public OneCloud(Parcel in) {
        readFromParcel(in);
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
            public long skip(long byteCount) throws IOException {
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
            public void write(byte[] buffer, int offset, int count) throws IOException {
                try {
                    mBinder.oWrite(buffer, offset, count);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public void write(byte[] buffer) throws IOException {
                try {
                    mBinder.oWriteAll(buffer);
                }
                catch (RemoteException e) {
                    throw new IOException();
                }
            }

            @Override
            public void write(int oneByte) throws IOException {
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
            public void onProgress(long bytesTransferred, long totalBytes) throws RemoteException {
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
            public void onProgress(long bytesTransferred, long totalBytes) throws RemoteException {
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
     * @param newFileName
     *            The new file name that the file will take on in Box.
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
            public void onProgress(long bytesTransferred, long totalBytes) throws RemoteException {
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
    public static interface UploadListener {

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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(mBinder.asBinder());
        dest.writeByte((byte) (mHandshaken ? 1 : 0));
    }

    /**
     * Read back one self from a parcel.
     * 
     * @param in
     *            Parcel.
     */
    private void readFromParcel(Parcel in) {
        mBinder = OneCloudInterface.Stub.asInterface(in.readStrongBinder());
        mHandshaken = in.readByte() == 1;
    }

    /**
     * Parcel CREATOR.
     */
    public static final Parcelable.Creator<OneCloud> CREATOR = new Parcelable.Creator<OneCloud>() {

        @Override
        public OneCloud createFromParcel(Parcel in) {
            return new OneCloud(in);
        }

        @Override
        public OneCloud[] newArray(int size) {
            return new OneCloud[size];
        }
    };

    /**
     * Trigger a handshake between us and the Box app.
     * 
     * @param context
     *            Context.
     */
    public void sendHandshake(final Context context) {
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
     * Check whether the binder interface back to Box is valid.
     * 
     * @return True if valid, false otherwise.
     */
    private boolean isBinderValid() {
        return mHandshaken && mBinder != null && mBinder.asBinder().isBinderAlive();
    }
}
