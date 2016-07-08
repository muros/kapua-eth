package org.eclipse.kapua.commons.util;

import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;

public class KapuaEnvironmentUtils
{
    private static final String BROKER_URI_FORMAT = "%s://%s:%s";

    public static String getBrokerURI()
    {
        SystemSetting envConfig = SystemSetting.getInstance();
        return String.format(BROKER_URI_FORMAT,
                             envConfig.getString(SystemSettingKey.BROKER_PROTOCOL),
                             envConfig.getString(SystemSettingKey.BROKER_DNS),
                             envConfig.getString(SystemSettingKey.BROKER_PORT));
    }
}
