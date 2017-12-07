/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.hubs;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Map;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.ANNOTATIONS_ONLY)
public class HubInvocation {

    @JsonField(name = "I")
    private String mCallbackId;
    @JsonField(name = "H")
    private String mHub;
    @JsonField(name = "M")
    private String mMethod;
    // this field is handled in Connection and HubConnection class due to the fact that LoganSquare can't handle Object[]
    private String mArgs;
    @JsonField(name = "S")
    private Map<String, String> mState;

    public String getCallbackId() {
        return mCallbackId;
    }

    public void setCallbackId(String callbackId) {
        mCallbackId = callbackId;
    }

    public String getHub() {
        return mHub;
    }

    public void setHub(String hub) {
        mHub = hub;
    }

    public String getMethod() {
        return mMethod;
    }

    public void setMethod(String method) {
        mMethod = method;
    }

    public String getArgs() {
        return mArgs;
    }

    public void setArgs(String args) {
        mArgs = args;
    }

    public Map<String, String> getState() {
        return mState;
    }

    public void setState(Map<String, String> state) {
        mState = state;
    }
}
