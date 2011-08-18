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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.BoxFile;
import com.box.androidlib.DAO.BoxFolder;
import com.box.androidlib.ResponseListeners.CreateFolderListener;
import com.box.androidlib.ResponseListeners.DeleteListener;
import com.box.androidlib.ResponseListeners.FileDownloadListener;
import com.box.androidlib.ResponseListeners.FileUploadListener;
import com.box.androidlib.ResponseListeners.GetAccountTreeListener;
import com.box.androidlib.ResponseListeners.RenameListener;
import com.box.androidlib.sample.Constants;
import com.box.androidlib.sample.R;

public class Browse extends ListActivity {

    private MyArrayAdapter adapter;
    private TreeListItem[] items;
    private String authToken;
    private long folderId;

    // Menu button options
    private static final int MENU_ID_UPLOAD = 1;
    private static final int MENU_ID_CREATE_FOLDER = 2;

    // Options shown when you click on a file/folder
    private static final String OPTION_FOLDER_DETAILS = "Folder details";
    private static final String OPTION_FOLDER_CONTENTS = "Folder contents";
    private static final String OPTION_FILE_DETAILS = "File details";
    private static final String OPTION_FILE_DOWNLOAD = "Download file";
    private static final String OPTION_SHARE = "Share";
    private static final String OPTION_DELETE = "Delete";
    private static final String OPTION_RENAME = "Rename";

    // Activity request codes
    private static final int REQUEST_CODE_FILE_PICKER = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree);

        // Check if we have an Auth Token stored.
        final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, null);
        if (authToken == null) {
            Toast.makeText(getApplicationContext(), "You are not logged in.", Toast.LENGTH_SHORT)
                .show();
            finish();
            return;
        }

        // View your root folder by default (folder_id = 0l), or this activity
        // can also be launched to view subfolders
        folderId = 0l;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("folder_id")) {
            folderId = extras.getLong("folder_id");
        }

        // Initialize list items and set adapter
        items = new TreeListItem[0];
        adapter = new MyArrayAdapter(this, 0, items);
        setListAdapter(adapter);

        // Go get the account tree
        refresh();
    }

    /**
     * Refresh the tree.
     */
    private void refresh() {
        final Box box = Box.getInstance(Constants.API_KEY);
        box.getAccountTree(authToken, folderId,
            new String[] { Box.PARAM_ONELEVEL }, new GetAccountTreeListener() {
                @Override
                public void onComplete(BoxFolder boxFolder, String status) {
                    if (!status.equals(GetAccountTreeListener.STATUS_LISTING_OK)) {
                        Toast.makeText(getApplicationContext(), "There was an error.",
                            Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    /**
                     * Box.getAccountTree() was successful. boxFolder contains a
                     * list of subfolders and files. Shove those into an array
                     * so that our list adapter displays them.
                     */

                    items = new TreeListItem[boxFolder.getFoldersInFolder().size()
                        + boxFolder.getFilesInFolder().size()];

                    int i = 0;

                    Iterator<BoxFolder> foldersIterator = boxFolder.getFoldersInFolder().iterator();
                    while (foldersIterator.hasNext()) {
                        BoxFolder subfolder = foldersIterator.next();
                        TreeListItem item = new TreeListItem();
                        item.id = subfolder.getId();
                        item.name = subfolder.getFolderName();
                        item.type = TreeListItem.TYPE_FOLDER;
                        item.folder = subfolder;
                        items[i] = item;
                        i++;
                    }

                    Iterator<BoxFile> filesIterator = boxFolder.getFilesInFolder().iterator();
                    while (filesIterator.hasNext()) {
                        BoxFile boxFile = filesIterator.next();
                        TreeListItem item = new TreeListItem();
                        item.id = boxFile.getId();
                        item.name = boxFile.getFileName();
                        item.type = TreeListItem.TYPE_FILE;
                        item.file = boxFile;
                        items[i] = item;
                        i++;
                    }

                    adapter.notifyDataSetChanged();
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onIOException(final IOException e) {
                    Toast.makeText(getApplicationContext(),
                        "Failed to get tree - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {

        /**
         * Demonstrates some of the actions you can perform on files and folders
         */

        final CharSequence[] options = items[position].type == TreeListItem.TYPE_FOLDER ? new String[] {
            OPTION_FOLDER_DETAILS, OPTION_FOLDER_CONTENTS, OPTION_SHARE, OPTION_DELETE,
            OPTION_RENAME }
            : new String[] { OPTION_FILE_DETAILS, OPTION_SHARE, OPTION_FILE_DOWNLOAD,
                OPTION_DELETE, OPTION_RENAME };

        AlertDialog.Builder builder = new AlertDialog.Builder(Browse.this);
        builder.setTitle(items[position].name);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (options[selected].equals(OPTION_SHARE)) {
                    Intent i = new Intent(Browse.this, Share.class);
                    if (items[position].type == TreeListItem.TYPE_FOLDER) {
                        i.putExtra("itemType", Box.TYPE_FOLDER);
                    } else {
                        i.putExtra("itemType", Box.TYPE_FILE);
                    }
                    i.putExtra("itemId", items[position].id);
                    i.putExtra("itemName", items[position].name);
                    startActivity(i);
                } else if (options[selected].equals(OPTION_FILE_DETAILS)) {
                    Intent i = new Intent(Browse.this, FileDetails.class);
                    i.putExtra("file_id", items[position].id);
                    startActivity(i);
                } else if (options[selected].equals(OPTION_FOLDER_DETAILS)) {
                    Intent i = new Intent(Browse.this, FolderDetails.class);
                    i.putExtra("folder_id", items[position].id);
                    startActivity(i);
                } else if (options[selected].equals(OPTION_FOLDER_CONTENTS)) {
                    Intent i = new Intent(Browse.this, Browse.class);
                    i.putExtra("folder_id", items[position].id);
                    startActivity(i);
                } else if (options[selected].equals(OPTION_FILE_DOWNLOAD)) {

                    /**
                     * Download a file and put it into the SD card. In your app,
                     * you can put the file wherever you have access to.
                     */
                    final Box box = Box.getInstance(Constants.API_KEY);
                    final java.io.File destinationFile = new java.io.File(Environment
                        .getExternalStorageDirectory()
                        + "/"
                        + URLEncoder.encode(items[position].name));

                    final ProgressDialog downloadDialog = new ProgressDialog(Browse.this);
                    downloadDialog.setMessage("Downloading " + items[position].name);
                    downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    downloadDialog.setMax((int) items[position].file.getSize());
                    downloadDialog.show();

                    box.download(authToken, items[position].id, destinationFile, null,
                        new FileDownloadListener() {
                            @Override
                            public void onComplete(final String status) {
                                downloadDialog.dismiss();
                                if (status.equals(FileDownloadListener.STATUS_DOWNLOAD_OK)) {
                                    Toast.makeText(getApplicationContext(),
                                        "File downloaded to " + destinationFile.getAbsolutePath(),
                                        Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onIOException(final IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),
                                    "Download failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(final long bytesDownloaded) {
                                downloadDialog.setProgress((int) bytesDownloaded);
                            }
                        });
                } else if (options[selected].equals(OPTION_DELETE)) {
                    final Box box = Box.getInstance(Constants.API_KEY);
                    String target;
                    if (items[position].type == TreeListItem.TYPE_FOLDER) {
                        target = Box.TYPE_FOLDER;
                    } else {
                        target = Box.TYPE_FILE;
                    }
                    box.delete(authToken, target, items[position].id, new DeleteListener() {

                        @Override
                        public void onComplete(final String status) {
                            if (status.equals(DeleteListener.STATUS_S_DELETE_NODE)) {
                                Toast.makeText(getApplicationContext(),
                                    "Successfully deleted " + items[position].name,
                                    Toast.LENGTH_SHORT).show();
                                refresh();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                    "Delete failed - " + status, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onIOException(final IOException e) {
                            Toast.makeText(getApplicationContext(),
                                "Delete failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (options[selected].equals(OPTION_RENAME)) {
                    /**
                     * In this example, we simply demonstrate renaming a file to
                     * "renamed [ORIGINAL_FILENAME]"
                     */
                    final Box box = Box.getInstance(Constants.API_KEY);
                    String target;
                    if (items[position].type == TreeListItem.TYPE_FOLDER) {
                        target = Box.TYPE_FOLDER;
                    } else {
                        target = Box.TYPE_FILE;
                    }
                    box.rename(authToken, target, items[position].id, "renamed "
                        + items[position].name, new RenameListener() {

                        @Override
                        public void onComplete(final String status) {
                            if (status.equals(RenameListener.STATUS_S_RENAME_NODE)) {
                                Toast.makeText(getApplicationContext(),
                                    "Successfully renamed " + items[position].name,
                                    Toast.LENGTH_SHORT).show();
                                refresh();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                    "Rename failed - " + status, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onIOException(final IOException e) {
                            Toast.makeText(getApplicationContext(),
                                "Rename failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).show();
    }

    /**
     * Just a utility class to store BoxFile and BoxFolder objects, which can be
     * passed as the source data of our list adapter.
     */
    private class TreeListItem {
        public static final int TYPE_FILE = 1;
        public static final int TYPE_FOLDER = 2;
        public int type;
        public long id;
        public String name;
        public BoxFile file;
        @SuppressWarnings("unused")
        public BoxFolder folder;
    }

    private class MyArrayAdapter extends ArrayAdapter<TreeListItem> {

        private final Context context;

        public MyArrayAdapter(Context contextt, int textViewResourceId, TreeListItem[] objects) {
            super(contextt, textViewResourceId, objects);
            context = contextt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(context);
            if (items[position].type == TreeListItem.TYPE_FOLDER) {
                tv.append("FOLDER: ");
            } else if (items[position].type == TreeListItem.TYPE_FILE) {
                tv.append("FILE: ");
            }
            tv.append(items[position].name);
            tv.setPadding(10, 20, 10, 20);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            return tv;
        }

        @Override
        public int getCount() {
            return items.length;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ID_UPLOAD, 0, "Upload");
        menu.add(0, MENU_ID_CREATE_FOLDER, 1, "Create Folder");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case MENU_ID_UPLOAD:
            /**
             * To demonstrate file uploads, make sure you have an app that can
             * handle file choosing. Look at onActivityResult to see the actual
             * file upload. You don't have to use a file picker in your app. All
             * you need is the absolute path of the filename you wish to upload.
             */
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select file picker"), 1);
            Toast
                .makeText(
                    getApplicationContext(),
                    "Install ASTRO FILE MANAGER if you don't already have an app that can handle file choosing",
                    Toast.LENGTH_LONG).show();
            return true;
        case MENU_ID_CREATE_FOLDER:
            /**
             * In this example, we create a folder with the current time as its
             * name
             */
            final Box box = Box.getInstance(Constants.API_KEY);
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD HH.mm.ss");
            String new_folder_name = sdf.format(d);
            box.createFolder(authToken, folderId, new_folder_name, false,
                new CreateFolderListener() {

                    @Override
                    public void onComplete(final BoxFolder boxFolder, final String status) {
                        if (status.equals(CreateFolderListener.STATUS_CREATE_OK)) {
                            Toast
                                .makeText(getApplicationContext(),
                                    "Folder created - " + boxFolder.getFolderName(),
                                    Toast.LENGTH_SHORT).show();
                            refresh();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                "Folder creation failed - " + status, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onIOException(final IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                            "Folder creation failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE_PICKER && data != null) {
            /**
             * A file has been selected for upload
             */
            Uri uri = data.getData();
            final String filepath = uri.getPath();
            final File file = new File(filepath);

            final ProgressDialog uploadDialog = new ProgressDialog(this);
            uploadDialog.setMessage("Uploading " + filepath);
            uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            uploadDialog.setMax((int) file.length());
            uploadDialog.show();

            final Box boxServiceHandler = Box.getInstance(Constants.API_KEY);
            boxServiceHandler.upload(authToken, Box.UPLOAD_ACTION_UPLOAD, file, file.getName(),
                folderId, new FileUploadListener() {
                    @Override
                    public void onComplete(BoxFile file, final String status) {
                        if (status.equals(FileUploadListener.STATUS_UPLOAD_OK)) {
                            Toast.makeText(getApplicationContext(),
                                "Successfully uploaded " + filepath, Toast.LENGTH_SHORT).show();
                            refresh();
                        } else {
                            Toast.makeText(getApplicationContext(), "Upload failed - " + status,
                                Toast.LENGTH_SHORT).show();
                        }
                        uploadDialog.dismiss();
                    }

                    @Override
                    public void onIOException(final IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                            "Upload failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        uploadDialog.dismiss();
                    }

                    @Override
                    public void onMalformedURLException(final MalformedURLException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                            "Upload failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        uploadDialog.dismiss();
                    }

                    @Override
                    public void onFileNotFoundException(final FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                            "Upload failed - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        uploadDialog.dismiss();
                    }

                    @Override
                    public void onProgress(final long bytesUploaded) {
                        uploadDialog.setProgress((int) bytesUploaded);
                        if (bytesUploaded >= file.length()) {
                            uploadDialog.setMessage("Syncing on Box servers. Please Wait...");
                        }
                    }
                });
        }
    }
}
