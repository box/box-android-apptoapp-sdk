package com.box.onecloud.android.sample;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.box.onecloud.android.OneCloudData;
import com.box.onecloud.android.OneCloudData.UploadListener;

public class Main extends Activity {

    public static final String EXTRA_ONE_CLOUD = "one_cloud";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (getIntent().getExtras() == null) {
            return;
        }
        final Handler handler = new Handler();

        // The OneCloud object is what we want to keep track of in our app. It is parcelable so it is easily transferred between activities, receivers, etc.
        final OneCloudData oneCloudData = (OneCloudData) getIntent().getParcelableExtra(EXTRA_ONE_CLOUD);

        // In this example, we are being asked to edit a file, so Box has sent us the file name through the OneCloud object.
        final String fileName = oneCloudData.getFileName();

        // We can also get an output stream to which we can write updated data.
        final OutputStream boxOutputStream = oneCloudData.getOutputStream();

        // This input stream is where we can read the Box file data from.
        final InputStream boxInputStream = oneCloudData.getInputStream();

        new Thread() {

            @Override
            public void run() {
                try {

                    // In this example, we take a file from Box and zip it up.
                    ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(boxOutputStream)); // Notice how we are ultimately writing to the output
                                                                                                          // stream from the OneCloud object.
                    ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    int len = 0;
                    byte[] buffer = new byte[40960];
                    while ((len = boxInputStream.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();

                    // You MUST close the OutputStream before attempting to upload back to Box. One way or another, the boxOutputStream must have its close()
                    // method called. In this case, calling close() on the ZipOutputStream will cascade down to the boxOutputStream.
                    zos.close();

                    // Set up a new file name that we want this file to have in Box.
                    final String newFileName = fileName + " " + SystemClock.uptimeMillis() + ".zip";

                    // Set up an upload listener that we can use to monitor the upload. We don't necessarily need to have one if we don't care about upload
                    // progress.
                    UploadListener listener = new UploadListener() {

                        @Override
                        public void onProgress(long bytesTransferred, long totalBytes) {
                            Log.d("debug", "Upload progress: " + bytesTransferred + " / " + totalBytes);
                        }

                        @Override
                        public void onComplete() {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Uploaded zip file to Box: " + newFileName, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Failed to upload zip file to Box: " + newFileName, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    };

                    // Upload this zipped data as a new file on box.
                    oneCloudData.uploadNewFile(newFileName, listener);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}