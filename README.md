Box Android Library
-------------------

You can use the Android library to create applications based on the Box Rest API. You can find documentation for the Rest API at [http://developers.box.net/](http://developers.box.net/).

### Javadoc ###

Javadocs for the library can be found in the gh-pages branch, or by going to http://boxplatform.github.com/box-android/javadoc

### API Key ###

You must first obtain an API key by [registering your Box.net application](http://www.box.net/developers/services).

### Sample App ###

A sample app has been included to demonstrate usage of the library. In order for the sample application to work, you must set your OpenBox app's API key into BoxAndroidLibrarySample/src/com/box/androidlib/sample/Constants.java.

### Dependencies ###

The Box Android library requires mime4j and httpmime.  If you do not already have those included in your project, you will need to include them.

mime4j:
[http://james.apache.org/mime4j/index.html](http://james.apache.org/mime4j/index.html)

httpmime:
[http://hc.apache.org/httpcomponents-client-ga/httpmime/index.html](http://hc.apache.org/httpcomponents-client-ga/httpmime/index.html)
