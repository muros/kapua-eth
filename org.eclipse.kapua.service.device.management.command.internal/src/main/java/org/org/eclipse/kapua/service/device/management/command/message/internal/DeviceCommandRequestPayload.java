package org.org.eclipse.kapua.service.device.management.command.message.internal;

import java.util.Vector;

import org.eclipse.kapua.message.KapuaPayload;

public class DeviceCommandRequestPayload extends KapuaPayload
{
    public static final String METRIC_CMD      = "command.command";
    public static final String METRIC_ARG      = "command.argument";
    public static final String METRIC_ENVP     = "command.environment.pair";
    public static final String METRIC_DIR      = "command.working.directory";
    public static final String METRIC_STDIN    = "command.stdin";
    public static final String METRIC_TOUT     = "command.timeout";
    public static final String METRIC_ASYNC    = "command.run.async";
    public static final String METRIC_PASSWORD = "command.password";

    public String[] getArguments()
    {
        Vector<String> v = new Vector<String>();

        for (int i = 0;; i++) {
            String value = (String) getMetric(METRIC_ARG + i);
            if (value != null) {
                v.add(value);
            }
            else {
                break;
            }
        }

        if (v.size() == 0) {
            return null;
        }
        else {
            return v.toArray(new String[v.size()]);
        }
    }

    public void setArguments(String[] arguments)
    {
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                addMetric(METRIC_ARG + i, arguments[i]);
            }
        }
    }

    public String[] getEnvironmentPairs()
    {
        Vector<String> v = new Vector<String>();

        for (int i = 0;; i++) {
            String value = (String) getMetric(METRIC_ENVP + i);
            if (value != null) {
                v.add(value);
            }
            else {
                break;
            }
        }

        if (v.size() == 0) {
            return null;
        }
        else {
            return v.toArray(new String[v.size()]);
        }
    }

    public void setEnvironmentPairs(String[] environmentPairs)
    {
        if (environmentPairs != null) {
            for (int i = 0; i < environmentPairs.length; i++) {
                addMetric(METRIC_ENVP + i, environmentPairs[i]);
            }
        }
    }

    public String getWorkingDir()
    {
        return (String) getMetric(METRIC_DIR);
    }

    public void setWorkingDir(String workingDir)
    {
        if (workingDir != null) {
            addMetric(METRIC_DIR, workingDir);
        }
    }

    public String getStdin()
    {
        return (String) getMetric(METRIC_STDIN);
    }

    public void setStdin(String stdin)
    {
        if (stdin != null) {
            addMetric(METRIC_STDIN, stdin);
        }
    }

    public Integer getTimeout()
    {
        return (Integer) getMetric(METRIC_TOUT);
    }

    public void setTimeout(int timeout)
    {
        addMetric(METRIC_TOUT, Integer.valueOf(timeout));
    }

    public Boolean isRunAsync()
    {
        return (Boolean) getMetric(METRIC_ASYNC);
    }

    public void setRunAsync(boolean runAsync)
    {
        addMetric(METRIC_ASYNC, Boolean.valueOf(runAsync));
    }

    public String getCommand()
    {
        return (String) getMetric(METRIC_CMD);
    }

    public byte[] getZipBytes()
    {
        return getBody();
    }

    public void setZipBytes(byte[] zipBytes)
    {
        if (zipBytes != null) {
            setBody(zipBytes);
        }
    }

    public void setPassword(String password)
    {
        if (password != null && !password.isEmpty())
            addMetric(METRIC_PASSWORD, password);
    }
}
