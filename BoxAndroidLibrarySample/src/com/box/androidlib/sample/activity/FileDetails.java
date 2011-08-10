//  Copyright 2011 Box.net.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package com.box.androidlib.sample.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.BoxFile;
import com.box.androidlib.ResponseListeners.GetFileInfoListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class FileDetails extends Activity {

    private String authToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT)
                .show();
            finish();
            return;
        }

        setContentView(R.layout.file_details);

        final Bundle extras = getIntent().getExtras();
        final long file_id = extras.getLong("file_id");

        final Box box = Box.getInstance(Constants.API_KEY);
        box.getFileInfo(authToken, file_id, new GetFileInfoListener() {

            @Override
            public void onComplete(final BoxFile boxFile, final String status) {
                if (status.equals(GetFileInfoListener.STATUS_S_GET_FILE_INFO)) {
                    final TextView detailsText = (TextView) findViewById(R.id.detailsText);
                    StringBuffer sb = new StringBuffer();
                    sb.append("File name: ").append(boxFile.getFileName()).append("\n");
                    sb.append("Size: ").append(boxFile.getSize()).append("\n");
                    sb.append("Created: ").append(boxFile.getCreated()).append("\n");
                    sb.append("Updated: ").append(boxFile.getUpdated()).append("\n");
                    detailsText.setText(sb.toString());
                }
            }

            @Override
            public void onIOException(IOException e) {
            }
        });
    }
}
