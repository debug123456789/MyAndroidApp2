package com.nexshizuku.ipc;

/**
 * Service capabilities parcelable
 */
parcelable ServiceCapabilities {
    int apiVersion;
    boolean supportsShell;
    boolean supportsFileOps;
    boolean supportsProcessManagement;
    boolean supportsBinderCalls;
    int maxCommandLength;
    int maxOutputLength;
    String[] supportedAbis;
    String[] features;
}
