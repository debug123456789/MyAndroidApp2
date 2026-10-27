package com.nexshizuku.ipc;

import com.nexshizuku.ipc.ServiceInfo;

/**
 * Callback interface for service events
 */
interface IServiceCallback {
    void onServiceConnected(in ServiceInfo info);
    void onServiceDisconnected();
    void onServiceError(String error);
    void onCapabilitiesChanged(in ServiceCapabilities capabilities);
}
