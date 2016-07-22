package org.eclipse.kapua.service.device.call.kura;

public enum CommandMetrics
{
    APP_ID("CMD"),
    APP_VERSION("V1"),

    // Request
    APP_METRIC_CMD("command.command"),
    APP_METRIC_ARG("command.argument"),
    APP_METRIC_ENVP("command.environment.pair"),
    APP_METRIC_DIR("command.working.directory"),
    APP_METRIC_STDIN("command.stdin"),
    APP_METRIC_TOUT("command.timeout"),
    APP_METRIC_ASYNC("command.run.async"),
    APP_METRIC_PASSWORD("command.password"),

    // Response
    APP_METRIC_STDERR("command.stderr"),
    APP_METRIC_STDOUT("command.stdout"),
    APP_METRIC_EXIT_CODE("command.exit.code"),
    APP_METRIC_TIMED_OUT("command.timedout"),

    ;

    private String value;

    CommandMetrics(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
