package org.eclipse.kapua.service.device.management.packages.message.internal;

import org.eclipse.kapua.service.device.management.KapuaAppProperties;

public enum PackageAppProperties implements KapuaAppProperties
{
    APP_NAME("DEPLOY"),
    APP_VERSION("1.0.0"),

    // Commons exec properties
    APP_PROPERTY_PACKAGE_OPERATION_ID("kapua.package.operation.id"),
    APP_PROPERTY_PACKAGE_REBOOT("kapua.package.reboot"),
    APP_PROPERTY_PACKAGE_REBOOT_DELAY("kapua.package.reboot.delay"),

    // Request exec download
    APP_PROPERTY_PACKAGE_DOWNLOAD_PACKAGE_URI("kapua.package.download.uri"),
    APP_PROPERTY_PACKAGE_DOWNLOAD_PACKAGE_NAME("kapua.package.download.name"),
    APP_PROPERTY_PACKAGE_DOWNLOAD_PACKAGE_VERSION("kapua.package.download.version"),
    APP_PROPERTY_PACKAGE_DOWNLOAD_PACKAGE_INSTALL("kapua.package.download.install"),

    // Request exec install
    APP_PROPERTY_PACKAGE_INSTALL_PACKAGE_NAME("kapua.package.install.name"),
    APP_PROPERTY_PACKAGE_INSTALL_PACKAGE_VERSION("kapua.package.install.version"),

    // Request exec uninstall
    APP_PROPERTY_PACKAGE_UNINSTALL_PACKAGE_NAME("kapua.package.uninstall.name"),
    APP_PROPERTY_PACKAGE_UNINSTALL_PACKAGE_VERSION("kapua.package.uninstall.version"),

    // Response get download

    ;

    private String value;

    PackageAppProperties(String value)
    {
        this.value = value;
    }

    @Override
    public String getValue()
    {
        return value;
    }

}
