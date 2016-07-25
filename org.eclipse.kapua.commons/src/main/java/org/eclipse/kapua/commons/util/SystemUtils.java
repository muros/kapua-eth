package org.eclipse.kapua.commons.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;

public class SystemUtils
{
    public static URI getBrokerURI()
        throws URISyntaxException
    {
        SystemSetting envConfig = SystemSetting.getInstance();
        return new URI(envConfig.getString(SystemSettingKey.BROKER_SCHEME),
                       null,
                       envConfig.getString(SystemSettingKey.BROKER_HOST),
                       envConfig.getInt(SystemSettingKey.BROKER_PORT),
                       null,
                       null,
                       null);
    }
}
