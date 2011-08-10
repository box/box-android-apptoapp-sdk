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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.User;
import com.box.androidlib.ResponseListeners.GetAccountInfoListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class UserInfo extends Activity {

    private String authToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        final TextView userInfoText = (TextView) findViewById(R.id.userInfoText);

        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            onNotLoggedIn();
            return;
        }

        final Box boxServiceHandler = Box.getInstance(Constants.API_KEY);
        boxServiceHandler.getAccountInfo(authToken, new GetAccountInfoListener() {

            @Override
            public void onComplete(final User boxUser, String status) {
                if (status.equals(GetAccountInfoListener.STATUS_GET_ACCOUNT_INFO_OK) && boxUser != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Login: ").append(boxUser.getLogin()).append("\n");
                    sb.append("E-mail: ").append(boxUser.getEmail()).append("\n");
                    sb.append("Space amount: ").append(boxUser.getSpaceAmount()).append("\n");
                    sb.append("Space used: ").append(boxUser.getSpaceUsed()).append("\n");
                    userInfoText.setText(sb.toString());
                } else if (status.equals(GetAccountInfoListener.STATUS_NOT_LOGGED_IN)) {
                    onNotLoggedIn();
                } else if (status.equals(GetAccountInfoListener.STATUS_APPLICATION_RESTRICTED)) {
                    Toast.makeText(getApplicationContext(), "Please check your API key.",
                        Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onIOException(IOException e) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void onNotLoggedIn() {
        Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT)
            .show();
        Intent intent = new Intent(this, Splash.class);
        startActivity(intent);
        finish();
    }
}
