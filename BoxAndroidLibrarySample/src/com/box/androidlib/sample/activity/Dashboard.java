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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.ResponseListeners.LogoutListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class Dashboard extends Activity {

    private String authToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT)
                .show();
            finish();
            return;
        }

        final Button logoutButton = (Button) findViewById(R.id.logoutButton);
        final Button userInfoButton = (Button) findViewById(R.id.userInfoButton);
        final Button treeButton = (Button) findViewById(R.id.treeButton);

        userInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, UserInfo.class);
                startActivity(i);
            }
        });

        treeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, Browse.class);
                startActivity(i);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Box.getInstance(Constants.API_KEY).logout(authToken, new LogoutListener() {

                    @Override
                    public void onIOException(IOException e) {
                        Toast.makeText(getApplicationContext(),
                            "Logout failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete(String status) {
                        if (status.equals(LogoutListener.STATUS_LOGOUT_OK)) {
                            // Delete stored auth token and send user back to
                            // splash page
                            final SharedPreferences prefs = getSharedPreferences(
                                Constants.PREFS_FILE_NAME, 0);
                            final SharedPreferences.Editor editor = prefs.edit();
                            editor.remove(Constants.PREFS_KEY_AUTH_TOKEN);
                            editor.commit();
                            Toast
                                .makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG)
                                .show();
                            Intent i = new Intent(Dashboard.this, Splash.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Logout failed - " + status,
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
