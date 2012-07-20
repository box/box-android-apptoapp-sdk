package com.box.onecloud.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/**
 * Used to verify identity between the apps participating in the OneCloud transaction.
 * 
 */
public class OneCloudHandshake implements Parcelable {

    /** Binder back to Box through AIDL. */
    private OneCloudHandshakeInterface mBinder;

    /**
     * Parcel CREATOR.
     */
    public static final Parcelable.Creator<OneCloudHandshake> CREATOR = new Parcelable.Creator<OneCloudHandshake>() {

        @Override
        public OneCloudHandshake createFromParcel(final Parcel in) {
            return new OneCloudHandshake(in);
        }

        @Override
        public OneCloudHandshake[] newArray(final int size) {
            return new OneCloudHandshake[size];
        }
    };

    /**
     * Recreate from a parcel.
     * 
     * @param in
     *            Parcel.
     */
    public OneCloudHandshake(final Parcel in) {
        readFromParcel(in);
    }

    /**
     * Default constructor.
     * 
     * @param binder
     *            Binder.
     */
    public OneCloudHandshake(final OneCloudHandshakeInterface binder) {
        mBinder = binder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStrongBinder(mBinder.asBinder());
    }

    /**
     * Read back one self from a parcel.
     * 
     * @param in
     *            Parcel.
     */
    private void readFromParcel(final Parcel in) {
        mBinder = OneCloudHandshakeInterface.Stub.asInterface(in.readStrongBinder());
    }

    /**
     * Send a handshake request. The receiver can call handshakeCallback.onShake().
     * 
     * @param handshakeCallback
     *            Handshake callback through which the receiver can respond with onShake().
     * @throws RemoteException
     *             Thrown if there was a problem with the binder.
     */
    public void sendHandshake(final HandshakeCallback handshakeCallback) throws RemoteException {
        mBinder.sendHandshake(handshakeCallback);
    }

    /**
     * Send a OneCloudData object.
     * 
     * @param oneCloudInterface
     *            OneCloudData object.
     * @throws RemoteException
     *             Thrown if there was a problem with the binder.
     */
    public void sendOneCloudData(final OneCloudInterface oneCloudInterface) throws RemoteException {
        mBinder.sendOneCloudData(oneCloudInterface);
    }

}
