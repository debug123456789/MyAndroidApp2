package com.nexshizuku.ipc;

import com.nexshizuku.ipc.CommandResult;

/**
 * Callback interface for async command execution
 */
interface ICommandCallback {
    void onResult(in CommandResult result);
    void onError(String error);
}
