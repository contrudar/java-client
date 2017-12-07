/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.hubs;

import com.bluelinelabs.logansquare.LoganSquare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.InvalidStateException;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;

/**
 * Represents a SignalRConnection that implements the Hubs protocol
 */
public class HubConnection extends Connection {

    private Map<String, Action<HubResult>> mCallbacks = Collections.synchronizedMap(new HashMap<String, Action<HubResult>>());
    private Map<String, HubProxy> mHubs = Collections.synchronizedMap(new HashMap<String, HubProxy>());
    private Integer mCallbackId = 0;

    /**
     * Initializes the connection
     *
     * @param url           The connection URL
     * @param queryString   The connection query string
     * @param useDefaultUrl indicates if the default SignalR URL should be used
     * @param logger        The connection logger
     */
    public HubConnection(String url, String queryString, boolean useDefaultUrl, Logger logger, boolean isSsl) {
        super(getUrl(url, useDefaultUrl), queryString, logger, isSsl);
    }

    /**
     * Initializes the connection
     *
     * @param url           The connection URL
     * @param useDefaultUrl indicates if the default SignalR URL should be used
     * @param logger        The connection logger
     */
    public HubConnection(String url, boolean useDefaultUrl, Logger logger, boolean isSsl) {
        super(getUrl(url, useDefaultUrl), logger, isSsl);
    }

    /**
     * Initialized the connection
     *
     * @param url The connection URL
     */
    public HubConnection(String url, boolean isSsl) {
        super(getUrl(url, true), isSsl);
    }

    public HubConnection(String url, Logger logger, boolean isSsl) {
        super(url, logger, isSsl);
    }

    /**
     * Initializes the connection
     *
     * @param url           The connection URL
     * @param useDefaultUrl indicates if the default SignalR URL should be used
     */
    public HubConnection(String url, boolean useDefaultUrl, boolean isSsl) {
        super(getUrl(url, useDefaultUrl), isSsl);
    }

    @Override
    public void onReceived(JSONObject message) {
        super.onReceived(message);

        log("Processing message", LogLevel.Information);
        if (getState() == ConnectionState.Connected) {
            try {
                if (message.has("I")) {
                    log("Getting HubResult from message", LogLevel.Verbose);
                    HubResult result = LoganSquare.parse(message.toString(), HubResult.class);

                    String id = result.getId().toLowerCase(Locale.getDefault());
                    log("Result Id: " + id, LogLevel.Verbose);
                    log("Result Data: " + result.getResult(), LogLevel.Verbose);

                    if (mCallbacks.containsKey(id)) {
                        log("Get and remove callback with id: " + id, LogLevel.Verbose);
                        Action<HubResult> callback = mCallbacks.remove(id);

                        try {
                            log("Execute callback for message", LogLevel.Verbose);
                            callback.run(result);
                        } catch (Exception e) {
                            onError(e, false);
                        }
                    }
                } else {
                    HubInvocation invocation = LoganSquare.parse(message.toString(), HubInvocation.class);

                    log("Getting HubInvocation from message", LogLevel.Verbose);

                    String hubName = invocation.getHub().toLowerCase(Locale.getDefault());
                    log("Message for: " + hubName, LogLevel.Verbose);

                    if (mHubs.containsKey(hubName)) {
                        HubProxy hubProxy = mHubs.get(hubName);
                        if (invocation.getState() != null) {
                            for (String key : invocation.getState().keySet()) {
                                String value = invocation.getState().get(key);
                                log("Setting state for hub: " + key + " -> " + value, LogLevel.Verbose);
                                hubProxy.setState(key, value);
                            }
                        }

                        String eventName = invocation.getMethod().toLowerCase(Locale.getDefault());
                        log("Invoking event: " + eventName + " with arguments " + invocation.getArgs(), LogLevel.Verbose);

                        try {
                            JSONArray args = message.getJSONArray("A");
                            String[] argsResult = new String[args.length()];
                            for (int i = 0; i < args.length(); i++) {
                                argsResult[i] = args.getJSONObject(i).toString();
                            }
                            hubProxy.invokeEvent(eventName, argsResult);
                        } catch (Exception e) {
                            onError(e, false);
                        }
                    }
                }
            } catch (Exception ex) {
                getLogger().log(ex.getMessage(), LogLevel.Critical);
            }
        }
    }

    @Override
    public String getConnectionData() {
        JSONArray connectionData = new JSONArray();
        for (String hubName : mHubs.keySet()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", hubName);
            } catch (JSONException e) {
                getLogger().log(e.toString(), LogLevel.Critical);
            }
            connectionData.put(jsonObject);
        }

        log("Getting connection data: " + connectionData, LogLevel.Verbose);
        return connectionData.toString();
    }

    @Override
    protected void onClosed() {
        clearInvocationCallbacks("Connection closed");
        super.onClosed();
    }

    private void clearInvocationCallbacks(String error) {
        log("Clearing invocation callbacks: " + error, LogLevel.Verbose);
        HubResult result = new HubResult();
        result.setError(error);

        for (String key : mCallbacks.keySet()) {
            try {
                log("Invoking callback with empty result: " + key, LogLevel.Verbose);
                mCallbacks.get(key).run(result);
            } catch (Exception e) {
                getLogger().log(e.toString(), LogLevel.Critical);
            }
        }

        mCallbacks.clear();
    }

    @Override
    protected void onReconnecting() {
        clearInvocationCallbacks("Reconnecting");
        super.onReconnecting();
    }

    /**
     * Creates a proxy for a hub
     *
     * @param hubName The hub name
     * @return The proxy for the hub
     * @throws InvalidStateException If called when not disconnected, the method will throw an
     *                               exception
     */
    public HubProxy createHubProxy(String hubName) {
        if (mState != ConnectionState.Disconnected) {
            throw new InvalidStateException(mState);
        }

        if (hubName == null) {
            throw new IllegalArgumentException("hubName cannot be null");
        }

        String hubNameLower = hubName.toLowerCase(Locale.getDefault());

        log("Creating hub proxy: " + hubNameLower, LogLevel.Information);

        HubProxy proxy = null;
        if (mHubs.containsKey(hubNameLower)) {
            proxy = mHubs.get(hubNameLower);
        } else {
            proxy = new HubProxy(this, hubName, getLogger());
            mHubs.put(hubNameLower, proxy);
        }

        return proxy;
    }

    /**
     * Registers a callback
     *
     * @param callback The callback to register
     * @return The callback Id
     */
    String registerCallback(Action<HubResult> callback) {
        String id = mCallbackId.toString().toLowerCase(Locale.getDefault());
        log("Registering callback: " + id, LogLevel.Verbose);
        mCallbacks.put(id, callback);
        mCallbackId++;
        return id;
    }

    /**
     * Removes a callback
     *
     * @param callbackId Id for the callback to remove
     */
    void removeCallback(String callbackId) {
        log("Removing callback: " + callbackId, LogLevel.Verbose);
        mCallbacks.remove(callbackId.toLowerCase(Locale.getDefault()));
    }

    /**
     * Generates a standarized URL
     *
     * @param url           The base URL
     * @param useDefaultUrl Indicates if the default SignalR suffix should be appended
     * @return The connection URL
     */
    private static String getUrl(String url, boolean useDefaultUrl) {
        if (!url.endsWith("/")) {
            url += "/";
        }

        if (useDefaultUrl) {
            return url + "signalr";
        }

        return url;
    }

    @Override
    protected String getSourceNameForLog() {
        return "HubConnection";
    }
}
