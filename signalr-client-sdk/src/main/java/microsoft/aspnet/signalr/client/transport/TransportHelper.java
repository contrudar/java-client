/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.transport;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ConnectionBase;
import microsoft.aspnet.signalr.client.Constants;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.MessageResult;

public class TransportHelper {

    public static MessageResult processReceivedData(String data, ConnectionBase connection) {
        Logger logger = connection.getLogger();
        MessageResult result = new MessageResult();

        if (data == null) {
            return result;
        }

        data = data.trim();

        if ("".equals(data) || "{}".equals(data)) {
            return result;
        }

        JSONObject json;

        try {
            json = new JSONObject(data);
        } catch (Exception e) {
            connection.onError(e, false);
            return result;
        }

        if (json.names().length() == 0) {
            return result;
        }

        try {
            if (json.has("I")) {
                logger.log("Invoking message received with: " + data, LogLevel.Verbose);
                connection.onReceived(new JSONObject(data));
            } else {
                // disconnected
                if (json.has("D")) {
                    if (json.getInt("D") == 1) {
                        logger.log("Disconnect message received", LogLevel.Verbose);
                        result.setDisconnect(true);
                        return result;
                    }
                }

                // should reconnect
                if (json.has("T")) {
                    if (json.getInt("T") == 1) {
                        logger.log("Reconnect message received", LogLevel.Verbose);
                        result.setReconnect(true);
                    }
                }

                if (json.has("G")) {
                    String groupsToken = json.getString("G");
                    logger.log("Group token received: " + groupsToken, LogLevel.Verbose);
                    connection.setGroupsToken(groupsToken);
                }

                if (json.has("M")) {
                    Object messages = json.get("M");
                    Object messagesObject = new JSONTokener(messages.toString()).nextValue();
                    if (messagesObject instanceof JSONArray) {
                        if (json.has("C")) {
                            String messageId = json.getString("C");
                            logger.log("MessageId received: " + messageId, LogLevel.Verbose);
                            connection.setMessageId(messageId);
                        }

                        JSONArray messagesArray = (JSONArray) messagesObject;
                        int size = messagesArray.length();

                        for (int i = 0; i < size; i++) {
                            JSONObject message = messagesArray.getJSONObject(i);

                            logger.log("Invoking OnReceived with: " + message.toString(), LogLevel.Verbose);
                            connection.onReceived(message);
                        }
                    }
                }

                if (json.has("S")) {
                    if (json.getInt("S") == 1) {
                        logger.log("Initialization message received", LogLevel.Information);
                        result.setInitialize(true);
                    }
                }
            }
        } catch (Exception ex) {
            connection.onError(ex, false);
        }

        return result;
    }

    /**
     * Creates the query string used on receive
     *
     * @param transport  Transport to use
     * @param connection Current connection
     * @return The querystring
     */
    public static String getReceiveQueryString(ClientTransport transport, ConnectionBase connection) {
        StringBuilder qsBuilder = new StringBuilder();

        qsBuilder.append("?transport=").append(transport.getName()).append("&connectionToken=").append(urlEncode(connection.getConnectionToken()));

        qsBuilder.append("&connectionId=").append(urlEncode(connection.getConnectionId()));

        if (connection.getMessageId() != null) {
            qsBuilder.append("&messageId=").append(urlEncode(connection.getMessageId()));
        }

        if (connection.getGroupsToken() != null) {
            qsBuilder.append("&groupsToken=").append(urlEncode(connection.getGroupsToken()));
        }

        String connectionData = connection.getConnectionData();
        if (connectionData != null) {
            qsBuilder.append("&connectionData=").append(urlEncode(connectionData));
        }

        String customQuery = connection.getQueryString();

        if (customQuery != null) {
            qsBuilder.append("&").append(customQuery);
        }

        return qsBuilder.toString();
    }

    /**
     * Creates the query string used on sending
     *
     * @param connection current connection
     * @return The querystring
     */
    public static String getNegotiateQueryString(ConnectionBase connection) {
        StringBuilder qsBuilder = new StringBuilder();
        qsBuilder.append("?clientProtocol=").append(urlEncode(Connection.PROTOCOL_VERSION.toString()));

        if (connection.getConnectionData() != null) {
            qsBuilder.append("&").append("connectionData=").append(urlEncode(connection.getConnectionData()));
        }

        if (connection.getQueryString() != null) {
            qsBuilder.append("&").append(connection.getQueryString());
        }

        return qsBuilder.toString();
    }

    /**
     * Creates the query string used on sending
     *
     * @param transport  the transport to use
     * @param connection current connection
     * @return The querystring
     */
    public static String getSendQueryString(ClientTransport transport, ConnectionBase connection) throws Exception {
        StringBuilder qsBuilder = new StringBuilder();
        qsBuilder.append("?transport=").append(TransportHelper.urlEncode(transport.getName()));

        qsBuilder.append("&connectionToken=").append(TransportHelper.urlEncode(connection.getConnectionToken()));

        qsBuilder.append("&connectionId=").append(TransportHelper.urlEncode(connection.getConnectionId()));

        if (connection.getConnectionData() != null) {
            qsBuilder.append("&connectionData=").append(TransportHelper.urlEncode(connection.getConnectionData()));
        }

        if (connection.getQueryString() != null) {
            qsBuilder.append("&").append(connection.getQueryString());
        }

        return qsBuilder.toString();
    }

    public static String urlEncode(String s) {
        if (s == null) {
            return "";
        }

        String encoded = null;
        try {
            encoded = URLEncoder.encode(s, Constants.UTF8_NAME);
        } catch (UnsupportedEncodingException e) {
        }

        return encoded;
    }
}
