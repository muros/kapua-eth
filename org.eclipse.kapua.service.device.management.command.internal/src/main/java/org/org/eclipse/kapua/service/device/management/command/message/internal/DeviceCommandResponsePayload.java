package org.org.eclipse.kapua.service.device.management.command.message.internal;

import org.eclipse.kapua.service.device.call.message.KapuaResponsePayload;

public class DeviceCommandResponsePayload extends KapuaResponsePayload
{
    public static final String METRIC_STDERR    = "command.stderr";
    public static final String METRIC_STDOUT    = "command.stdout";
    public static final String METRIC_EXIT_CODE = "command.exit.code";
    public static final String METRIC_TIMEDOUT  = "command.timedout";

    public DeviceCommandResponsePayload(KapuaResponsePayload kapuaResponsePayload)
    {
        super(kapuaResponsePayload);
    }

    public String getStderr()
    {
        return (String) getMetric(METRIC_STDERR);
    }

    public void setStderr(String stderr)
    {
        if (stderr != null) {
            addMetric(METRIC_STDERR, stderr);
        }
    }

    public String getStdout()
    {
        return (String) getMetric(METRIC_STDOUT);
    }

    public void setStdout(String stdout)
    {
        if (stdout != null) {
            addMetric(METRIC_STDOUT, stdout);
        }
    }

    public Integer getExitCode()
    {
        return (Integer) getMetric(METRIC_EXIT_CODE);
    }

    public void setExitCode(Integer exitCode)
    {
        if (exitCode != null) {
            addMetric(METRIC_EXIT_CODE, exitCode);
        }
    }

    public Boolean hasTimedout()
    {
        return (Boolean) getMetric(METRIC_TIMEDOUT);
    }

    public void setTimedout(boolean timedout)
    {
        addMetric(METRIC_TIMEDOUT, timedout);
    }

}
