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
package com.box.androidlib.ResponseListeners;

/**
 * Interface definition for a callback to be invoked when Box.rename() is
 * called.
 */
public interface RenameListener extends ResponseListener {

    /** Operation was successful. */
    String STATUS_S_RENAME_NODE = "s_rename_node";

    /** A file of the same name already exists in the same folder. */
    String STATUS_E_FILENAME_IN_USE = "e_filename_in_use";

    /**
     * For all other errors. Verify that your target is 'file' or 'folder', that
     * your target_id is a valid item id in the user's account, and that the new
     * name contains valid characters.
     */
    String STATUS_E_RENAME_NODE = "e_rename_node";

    /**
     * Called when the API request has completed.
     * 
     * @param status
     *            Status code from Box API
     */
    void onComplete(String status);
}
