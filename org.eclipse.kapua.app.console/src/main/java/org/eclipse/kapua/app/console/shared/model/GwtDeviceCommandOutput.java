package org.eclipse.kapua.app.console.shared.model;

import java.io.Serializable;

public class GwtDeviceCommandOutput extends EdcBaseModel implements Serializable {

    private static final long serialVersionUID = 922409273611552321L;

    public Integer getExitCode() {
        return get("exitCode");
    }
    public void setExitCode(Integer exitCode) {
        set("exitCode", exitCode);
    }
    public Boolean isTimedout() {
        return get("timedout");
    }
    public void setTimedout(Boolean timedout) {
        set("timedout", timedout);
    }
    public String getStderr() {
        return get("stderr");
    }
    public void setStderr(String stderr) {
        set("stderr", stderr);
    }
    public String getStdout() {
        return get("stdout");
    }
    public void setStdout(String stdout) {
        set("stdout", stdout);
    }
    public String getExceptionMessage() {
        return get("exceptionMessage");
    }
    public void setExceptionMessage(String exceptionMessage) {
        set("exceptionMessage", exceptionMessage);
    }
    public String getExceptionStack() {
        return get("exceptionStack");
    }
    public void setExceptionStack(String exceptionStack) {
        set("exceptionStack", exceptionStack);
    }
}
