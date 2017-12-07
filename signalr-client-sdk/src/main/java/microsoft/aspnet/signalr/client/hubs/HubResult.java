/*
Copyright (c) Microsoft Open Technologies, Inc.
All Rights Reserved
See License.txt in the project root for license information.
*/

package microsoft.aspnet.signalr.client.hubs;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Locale;
import java.util.Map;

/**
 * Represents the result of a hub operation
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.ANNOTATIONS_ONLY)
public class HubResult {

    @JsonField(name = "I")
    private String id;
    @JsonField(name = "R")
    private String result;
    @JsonField(name = "H")
    private boolean hubException;
    @JsonField(name = "E")
    private String error;
    @JsonField(name = "D")
    private Object errorData;
    @JsonField(name = "S")
    private Map<String, String> state;

    public String getId() {
        return id == null ? null : id.toLowerCase(Locale.getDefault());
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isHubException() {
        return hubException;
    }

    public void setHubException(boolean isHubException) {
        this.hubException = isHubException;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getErrorData() {
        return errorData;
    }

    public void setErrorData(Object errorData) {
        this.errorData = errorData;
    }

    public Map<String, String> getState() {
        return state;
    }

    public void setState(Map<String, String> state) {
        this.state = state;
    }
}
