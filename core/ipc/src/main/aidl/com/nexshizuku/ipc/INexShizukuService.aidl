package com.nexshizuku.ipc;

import com.nexshizuku.ipc.ServiceInfo;
import com.nexshizuku.ipc.CommandRequest;
import com.nexshizuku.ipc.CommandResult;
import com.nexshizuku.ipc.ServiceCapabilities;

/**
 * Main IPC interface for NexShizuku service communication.
 * Provides secure, privileged command execution and service management.
 */
interface INexShizukuService {
    
    /**
     * Get the API version of the service
     * @return API version integer
     */
    int getApiVersion();
    
    /**
     * Get service information
     * @return ServiceInfo object with version, capabilities, and status
     */
    ServiceInfo getServiceInfo();
    
    /**
     * Get service capabilities
     * @return ServiceCapabilities object
     */
    ServiceCapabilities getCapabilities();
    
    /**
     * Check if the service is ready to accept commands
     * @return true if service is connected and ready
     */
    boolean isReady();
    
    /**
     * Execute a command request
     * @param request CommandRequest with command, args, and options
     * @return CommandResult with exit code, stdout, stderr
     */
    CommandResult executeCommand(in CommandRequest request);
    
    /**
     * Execute a command asynchronously with callback
     * @param request CommandRequest
     * @param callback Callback to receive result
     */
    void executeCommandAsync(in CommandRequest request, in ICommandCallback callback);
    
    /**
     * Ping the service to check connectivity
     * @return current timestamp in milliseconds
     */
    long ping();
    
    /**
     * Register a callback for service events
     * @param callback Event callback
     */
    void registerCallback(in IServiceCallback callback);
    
    /**
     * Unregister a callback
     * @param callback Event callback
     */
    void unregisterCallback(in IServiceCallback callback);
    
    /**
     * Get current binder status
     * @return BinderStatus object
     */
    com.nexshizuku.ipc.BinderStatus getBinderStatus();
    
    /**
     * Request service restart (requires appropriate permissions)
     * @return true if restart initiated
     */
    boolean requestRestart();
    
    /**
     * Shutdown the service gracefully
     */
    void shutdown();
}
