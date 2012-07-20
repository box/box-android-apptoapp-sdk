package com.box.onecloud.android;

import com.box.onecloud.android.HandshakeCallback;

import com.box.onecloud.android.OneCloudInterface;

interface OneCloudHandshakeInterface {

    void sendHandshake(HandshakeCallback handshakeCallback);
    
    void sendOneCloudData(OneCloudInterface oneCloudInterface);

}