/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client;

import org.json.JSONObject;

/**
 * Interface to define a handler for a "Message received" event
 */
public interface MessageReceivedHandler {
    /**
     * Handles an incoming message
     * 
     * @param json
     *            The received message
     */
    void onMessageReceived(JSONObject json);
}
