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
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.User;
import com.box.androidlib.ResponseListeners.GetAccountInfoListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class Splash extends Activity {

    private TextView statusText;
    private Button homeButton;
    private Button authenticateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPORTANT:
        // You must set the API Key of your OpenBox app into Constants.API_KEY
        // This demo app will not work until you do so.
        // If you do not have an API Key, please see https://www.box.net/developers
        // for instructions on how to create an OpenBox app.
        if (Constants.API_KEY == null) {
            Toast.makeText(getApplicationContext(),
                "You must set your API key into Constants.java before you can use this demo app. Register at https://www.box.net/developers.",
                Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.splash);

        statusText = (TextView) findViewById(R.id.statusText);

        authenticateButton = (Button) findViewById(R.id.authenticateButton);
        authenticateButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash.this, Authentication.class);
                startActivity(intent);
            }
        });

        homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        statusText.setText(getResources().getString(R.string.checking_login_status));
        homeButton.setVisibility(View.GONE);
        authenticateButton.setVisibility(View.GONE);

        // Check if we have an auth token stored as shared_prefs
        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        final String authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            onNotLoggedIn();
        } else {
            // We have an auth token. Let's execute getAccountInfo() and put the
            // user's e-mail address up on the screen.
            // This request will also serve as a way for us to verify that the
            // auth token is actually still valid.
            final Box box = Box.getInstance(Constants.API_KEY);
            box.getAccountInfo(authToken, new GetAccountInfoListener() {
                @Override
                public void onComplete(final User boxUser, final String status) {
                    // see http://developers.box.net/w/page/12923928/ApiFunction_get_account_info for possible status codes
                    if (status.equals(GetAccountInfoListener.STATUS_GET_ACCOUNT_INFO_OK) && boxUser != null) {
                        statusText.setText("Logged in as\n" + boxUser.getEmail());
                        homeButton.setVisibility(View.VISIBLE);
                        authenticateButton.setText("Log in as a different user");
                        authenticateButton.setVisibility(View.VISIBLE);
                    } else {
                        // Could not get user info. It's possible the auth token
                        // was no longer valid. Check the status code that was
                        // returned.
                        onNotLoggedIn();
                    }
                }

                @Override
                public void onIOException(IOException e) {
                    // No network connection?
                    e.printStackTrace();
                    onNotLoggedIn();
                }
            });
        }
    }

    private void onNotLoggedIn() {
        statusText.setText("You are not logged in");
        homeButton.setVisibility(View.GONE);
        authenticateButton.setText("Log in");
        authenticateButton.setVisibility(View.VISIBLE);
    }

}
