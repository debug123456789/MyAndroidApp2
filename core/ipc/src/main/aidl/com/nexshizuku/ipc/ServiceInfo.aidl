package com.nexshizuku.ipc;

/**
 * Service information parcelable
 */
parcelable ServiceInfo {
    String version;
    int apiVersion;
    int pid;
    int uid;
    String processName;
    long startTime;
    ServiceCapabilities capabilities;
    boolean isPrivileged;
    String selinuxContext;
}
