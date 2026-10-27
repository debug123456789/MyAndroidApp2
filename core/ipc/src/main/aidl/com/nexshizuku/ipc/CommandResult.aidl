package com.nexshizuku.ipc;

/**
 * Command result parcelable
 */
parcelable CommandResult {
    String requestId;
    int exitCode;
    String stdout;
    String stderr;
    long executionTimeMs;
    long timestamp;
    boolean isSuccess;
}
