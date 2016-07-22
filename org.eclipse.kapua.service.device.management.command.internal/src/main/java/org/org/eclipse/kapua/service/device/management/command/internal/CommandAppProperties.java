package org.org.eclipse.kapua.service.device.management.command.internal;

import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public enum CommandAppProperties implements KapuaAppProperties
{
    APP_NAME("COMMAND"),
    APP_VERSION("1.0.0"),

    // Request
    APP_PROPERTY_CMD("kapua.cmd.command"),
    APP_PROPERTY_ARG("kapua.cmd.argument"),
    APP_PROPERTY_ENVP("kapua.cmd.environment.pair"),
    APP_PROPERTY_DIR("kapua.cmd.working.directory"),
    APP_PROPERTY_STDIN("kapua.cmd.stdin"),
    APP_PROPERTY_TOUT("kapua.cmd.timeout"),
    APP_PROPERTY_ASYNC("kapua.cmd.run.async"),
    APP_PROPERTY_PASSWORD("kapua.cmd.password"),

    // Response
    APP_PROPERTY_STDERR("kapua.cmd.stderr"),
    APP_PROPERTY_STDOUT("kapua.cmd.stdout"),
    APP_PROPERTY_EXIT_CODE("kapua.cmd.exit.code"),
    APP_PROPERTY_TIMED_OUT("kapua.cmd.timed.out"),
    ;

    private String value;

    CommandAppProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }
}
