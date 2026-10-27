package com.nexshizuku.ipc;

/**
 * Binder connection status parcelable
 */
parcelable BinderStatus {
    boolean isConnected;
    int servicePid;
    int serviceUid;
    int apiVersion;
    long lastPingMs;
    String error;
}
