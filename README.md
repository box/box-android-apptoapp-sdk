Box Android Library
-------------------

You can use the Android library to create applications based on the Box Rest API. You can find documentation for the Rest API at [http://developers.box.net/](http://developers.box.net/).

### Javadoc ###

Javadocs for the library can be found in the gh-pages branch, or by going to http://box.github.com/box-android-sdk/javadoc/

### API Key ###

You must first obtain an API key by [registering your Box.net application](http://www.box.net/developers/services).

### Sample App ###

A sample app has been included to demonstrate usage of the library. In order for the sample application to work, you must set your OpenBox app's API key into BoxAndroidLibrarySample/src/com/box/androidlib/sample/Constants.java.

### Dependencies ###

The Box Android library requires mime4j, httpmime and gson.  If you do not already have those included in your project, you will need to include them.

mime4j:
[http://james.apache.org/mime4j/index.html](http://james.apache.org/mime4j/index.html)

httpmime:
[http://hc.apache.org/httpcomponents-client-ga/httpmime/index.html](http://hc.apache.org/httpcomponents-client-ga/httpmime/index.html)

gson:
[http://code.google.com/p/google-gson/](http://code.google.com/p/google-gson/)


## Copyright and License

Copyright 2014 Box, Inc. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
