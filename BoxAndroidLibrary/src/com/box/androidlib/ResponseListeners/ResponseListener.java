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

import java.io.IOException;

/**
 * Interface definition for a callback to be invoked when a Box API request has
 * been executed.
 */
public interface ResponseListener {

    /** Unknown HTTP response code. */
    String STATUS_UNKNOWN_HTTP_RESPONSE_CODE = "unknown_http_response_code";
    /** Invalid API key, or the API key is restricted from calling the API action. */
    String STATUS_APPLICATION_RESTRICTED = "application_restricted";
    /** User is not logged in. Auth token is not valid. */
    String STATUS_NOT_LOGGED_IN = "not_logged_in";

    /**
     * Called when an IOException has been thrown while executing the API
     * request.
     *
     * @param e
     *            The IOException that was thrown
     */
    void onIOException(IOException e);
}
