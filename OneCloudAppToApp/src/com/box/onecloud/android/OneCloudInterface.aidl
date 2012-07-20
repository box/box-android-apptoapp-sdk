package com.box.onecloud.android;

import com.box.onecloud.android.HandshakeCallback;
import com.box.onecloud.android.FileUploadCallbacks;

interface OneCloudInterface {

	//////////
    // 1.8.0
    //////////

    // InputStream
    int iAvailable();
    void iClose();
    void iMark(int readLimit);
    boolean iMarkSupported();
    int iReadAll(out byte[] buffer);
    int iReadOne();
    int iRead(out byte[] buffer, int offset, int length);
    void iReset();
    long iSkip(long byteCount);
    
    // OutputStream
    void oClose();
    void oFlush();
    void oWrite(in byte[] buffer, int offset, int count);
    void oWriteAll(in byte[] buffer);
    void oWriteOne(in int oneByte);
    
    // Handshake
    void sendHandshake(HandshakeCallback handshakeCallback);
    
    // Upload methods
    void uploadNewVersion(FileUploadCallbacks uploadCallbacks);
    void uploadNewVersionWithNewName(String newFileName, FileUploadCallbacks uploadCallbacks);
    void uploadNewFile(String suggestedFileName, FileUploadCallbacks uploadCallbacks);
    
    // Launch methods
    void launch();
    
    // Info getters
    String getFileName();
    String getMimeType();
    long getFileSize();
    
    //////////
    // 1.9.0
    //////////
    
    // Additional info getters
    long getToken();    
    long getFileId();
    long getFolderId();
    String getFolderPath();
    String getUsername();
    
    // Notify Box that data has changed
    void notifyDataChanged();
}