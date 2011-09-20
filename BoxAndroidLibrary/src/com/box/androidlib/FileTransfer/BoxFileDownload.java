/*******************************************************************************
 * Copyright 2011 Box.net.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.box.androidlib.FileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.os.Handler;

import com.box.androidlib.ResponseListeners.FileDownloadListener;
import com.box.androidlib.ResponseParsers.DefaultResponseParser;
import com.box.androidlib.Utils.BoxConfig;

/**
 * Contains logic for downloading a user's file from Box API and reporting
 * errors that may have occurred. You should not call this directly, and instead
 * use
 * {@link com.box.androidlib.Box#download(String, long, File, Long, FileDownloadListener)}
 * or
 * {@link com.box.androidlib.BoxSynchronous#download(String, long, File, Long, FileDownloadListener)}
 * to download.
 *
 * @author developers@box.net
 */
public class BoxFileDownload {

    /**
     * auth token from Box.
     */
    private final String mAuthToken;
    /**
     * FileDownloadListener that can notify you of download progress.
     */
    private FileDownloadListener mListener;
    /**
     * Handler to execute onProgress callbacks.
     */
    private Handler mHandler;
    /**
     * Runnable for FileDownloadListener.onProgress.
     */
    private Runnable mOnProgressRunnable;    
    /**
     * Used to track how many bytes have been transferred so far.
     */
    private long mBytesTransferred;

    /**
     * size of buffer used when reading from download input stream.
     */
    private static final int DOWNLOAD_BUFFER_SIZE = 4096;
    /**
     * if downloaded file is less than this size (in bytes), then inspect it for
     * an error message from Box API.
     */
    private static final int FILE_ERROR_SIZE = 100;

    /**
     * Instantiate a new BoxFileDownload.
     *
     * @param authToken
     *            Auth token from Box
     */
    public BoxFileDownload(final String authToken) {
        mAuthToken = authToken;
    }

    /**
     * Set a download listener which allows you to monitor download progress and
     * see the response status.
     *
     * @param listener
     *            A file download listener. You will likely be interested in
     *            callbacks
     *            {@link com.box.androidlib.ResponseListeners.FileDownloadListener#onProgress(long)}
     *            and
     *            {@link com.box.androidlib.ResponseListeners.FileDownloadListener#onComplete(String)}
     * @param handler
     *            The handler through which FileDownloadListener.onProgress will be invoked.
     */
    public void setListener(final FileDownloadListener listener, final Handler handler) {
        mListener = listener;
        mHandler = handler;
        mOnProgressRunnable = new Runnable() {
            @Override
            public void run() {
                mListener.onProgress(mBytesTransferred);
            }
        };
    }

    /**
     * Execute a file download.
     *
     * @param fileId
     *            The file_id of the file to be downloaded
     * @param destinationFile
     *            A java.io.File resource to which the downloaded file will be
     *            written. Ensure that this points to a valid file-path that can
     *            be written to.
     * @param versionId
     *            The version_id of the version of the file to download. Set to
     *            null to download the latest version of the file.
     * @return a response handler
     * @throws IOException
     *             Can be thrown if there was a connection error, or if
     *             destination file could not be written.
     */
    public DefaultResponseParser execute(final long fileId, final File destinationFile,
        final Long versionId) throws IOException {
        
        final DefaultResponseParser handler = new DefaultResponseParser();

        final Uri.Builder builder = new Uri.Builder();
        builder.scheme(BoxConfig.getInstance().getDownloadUrlScheme());
        builder.authority(BoxConfig.getInstance().getDownloadUrlAuthority());
        builder.path(BoxConfig.getInstance().getDownloadUrlPath());
        builder.appendPath(mAuthToken);
        builder.appendPath(String.valueOf(fileId));
        if (versionId != null) {
            builder.appendPath(String.valueOf(versionId));
        }

        final HttpURLConnection conn = (HttpURLConnection) (new URL(builder.build().toString()))
            .openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.connect();
        final int responseCode = conn.getResponseCode();
        final InputStream is = conn.getInputStream();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            final FileOutputStream fos = new FileOutputStream(destinationFile);
            final byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
            int bufferLength = 0;
            mBytesTransferred = 0;
            while ((bufferLength = is.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                fos.write(buffer, 0, bufferLength);
                mBytesTransferred += bufferLength;
                if (mListener != null && mHandler != null) {
                    mHandler.post(mOnProgressRunnable);
                }
            }
            fos.close();
            handler.setStatus(FileDownloadListener.STATUS_DOWNLOAD_OK);

            // If download thread was interrupted, set to STATUS_DOWNLOAD_CANCELED
            if (Thread.currentThread().isInterrupted()) {
                handler.setStatus(FileDownloadListener.STATUS_DOWNLOAD_CANCELLED);
            }
            // Even if download completed, Box API may have put an error message
            // in the file itself. Refer to
            // http://developers.box.net/w/page/12923951/ApiFunction_Upload-and-Download
            else if (destinationFile.length() < FILE_ERROR_SIZE) {
                final byte[] buff = new byte[(int) destinationFile.length()];
                final FileInputStream fis = new FileInputStream(destinationFile);
                fis.read(buffer);
                final String str = new String(buff).trim();
                if (str.equals(FileDownloadListener.STATUS_DOWNLOAD_WRONG_AUTH_TOKEN)) {
                    handler.setStatus(FileDownloadListener.STATUS_DOWNLOAD_WRONG_AUTH_TOKEN);
                } else if (str.equals(FileDownloadListener.STATUS_DOWNLOAD_RESTRICTED)) {
                    handler.setStatus(FileDownloadListener.STATUS_DOWNLOAD_RESTRICTED);
                }
            }
        } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            handler.setStatus(FileDownloadListener.STATUS_DOWNLOAD_PERMISSIONS_ERROR);
        } else {
            handler.setStatus(FileDownloadListener.STATUS_DOWNLOAD_FAIL);
        }

        is.close();
        conn.disconnect();

        return handler;
    }

}
