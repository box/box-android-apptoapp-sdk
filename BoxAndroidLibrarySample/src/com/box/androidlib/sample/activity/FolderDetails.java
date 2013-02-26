// Copyright 2011 Box.net.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.box.androidlib.sample.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.BoxFolder;
import com.box.androidlib.ResponseListeners.GetAccountTreeListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class FolderDetails extends Activity {

    private String authToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.folder_details);

        final Bundle extras = getIntent().getExtras();
        final long folder_id = extras.getLong("folder_id");

        final Box box = Box.getInstance(Constants.API_KEY);
        box.getAccountTree(authToken, folder_id, new String[] {Box.PARAM_NOFILES, Box.PARAM_ONELEVEL, Box.PARAM_SHOW_PATH_IDS, Box.PARAM_SHOW_PATH_NAMES},
            new GetAccountTreeListener() {

                @Override
                public void onComplete(final BoxFolder boxFolder, final String status) {
                    if (status.toString().equals(GetAccountTreeListener.STATUS_LISTING_OK)) {
                        final TextView detailsText = (TextView) findViewById(R.id.detailsText);
                        StringBuffer sb = new StringBuffer("FOLDER:\n").append(boxFolder.toStringDebug());
                        detailsText.setText(sb.toString());
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Failed to get tree - " + status, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onIOException(final IOException e) {
                    Toast.makeText(getApplicationContext(), "Failed to get tree - " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
    }
}
