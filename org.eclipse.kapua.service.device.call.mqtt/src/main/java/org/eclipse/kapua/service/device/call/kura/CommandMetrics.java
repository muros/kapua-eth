package org.eclipse.kapua.service.device.call.kura;

public enum CommandMetrics
{
    APP_ID("COMMAND"),
    APP_VERSION("1.0.0"),

    APP_PROPERTY_CMD("kapua.cmd.command"),
    APP_PROPERTY_ARG("kapua.cmd.argument"),
    APP_PROPERTY_ENVP("kapua.cmd.environment.pair"),
    APP_PROPERTY_DIR("kapua.cmd.working.directory"),
    APP_PROPERTY_STDIN("kapua.cmd.stdin"),
    APP_PROPERTY_TOUT("kapua.cmd.timeout"),
    APP_PROPERTY_ASYNC("kapua.cmd.run.async"),
    APP_PROPERTY_PASSWORD("kapua.cmd.password"),
    ;

    private String value;

    CommandProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
