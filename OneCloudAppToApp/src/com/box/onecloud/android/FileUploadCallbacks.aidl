package com.box.onecloud.android;

interface FileUploadCallbacks {
    void onProgress(long bytesTransferred, long totalBytes);
    void onComplete();
    void onError();
}