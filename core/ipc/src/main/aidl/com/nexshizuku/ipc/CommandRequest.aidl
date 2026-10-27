package com.nexshizuku.ipc;

/**
 * Command request parcelable
 */
parcelable CommandRequest {
    String command;
    String[] args;
    String workingDirectory;
    String[] envKeys;
    String[] envValues;
    long timeoutMs;
    boolean requireRoot;
    String requestId;
}
