/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.transport;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Represents the negotiation response sent by the server in the handshake
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.ANNOTATIONS_ONLY)
public class NegotiationResponse {
    public static final double INVALID_KEEP_ALIVE_TIMEOUT = -1;

    @JsonField(name = "ConnectionId")
    private String connectionId;
    @JsonField(name = "ConnectionToken")
    private String connectionToken;
    @JsonField(name = "Url")
    private String url;
    @JsonField(name = "ProtocolVersion")
    private String protocolVersion;
    @JsonField(name = "DisconnectTimeout")
    private double disconnectTimeout;
    @JsonField(name = "TryWebSockets")
    private boolean tryWebSockets;
    @JsonField(name = "KeepAliveTimeout")
    private double keepAliveTimeout;

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getConnectionToken() {
        return connectionToken;
    }

    public void setConnectionToken(String connectionToken) {
        this.connectionToken = connectionToken;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public double getDisconnectTimeout() {
        return disconnectTimeout;
    }

    public void setDisconnectTimeout(double disconnectTimeout) {
        this.disconnectTimeout = disconnectTimeout;
    }

    public boolean isTryWebSockets() {
        return tryWebSockets;
    }

    public void setTryWebSockets(boolean tryWebSockets) {
        this.tryWebSockets = tryWebSockets;
    }

    public double getKeepAliveTimeout() {
        return keepAliveTimeout;
//        if (keepAliveTimeout != null) {
//            return keepAliveTimeout;
//        } else {
//            return INVALID_KEEP_ALIVE_TIMEOUT;
//        }
    }

    public void setKeepAliveTimeout(double keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
}
