/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.device.call.message.kura;

import java.util.Iterator;

import org.eclipse.kapua.service.device.call.message.DevicePayload;

public class KuraBirthPayload extends KuraPayload implements DevicePayload
{
    private final static String UPTIME                 = "uptime";
    private final static String DISPLAY_NAME           = "display_name";
    private final static String MODEL_NAME             = "model_name";
    private final static String MODEL_ID               = "model_id";
    private final static String PART_NUMBER            = "part_number";
    private final static String SERIAL_NUMBER          = "serial_number";
    private final static String AVAILABLE_PROCESSORS   = "available_processors";
    private final static String TOTAL_MEMORY           = "total_memory";
    private final static String FIRMWARE_VERSION       = "firmware_version";
    private final static String BIOS_VERSION           = "bios_version";
    private final static String OS                     = "os";
    private final static String OS_VERSION             = "os_version";
    private final static String OS_ARCH                = "os_arch";
    private final static String JVM_NAME               = "jvm_name";
    private final static String JVM_VERSION            = "jvm_version";
    private final static String JVM_PROFILE            = "jvm_profile";
    private final static String ESF_VERSION            = "esf_version";
    private final static String KURA_VERSION           = "kura_version";
    private final static String ESF_PREFIX             = "ESF_";
    private final static String ESFKURA_VERSION        = "esf_version";
    private final static String OSGI_FRAMEWORK         = "osgi_framework";
    private final static String OSGI_FRAMEWORK_VERSION = "osgi_framework_version";
    private final static String CONNECTION_INTERFACE   = "connection_interface";
    private final static String CONNECTION_IP          = "connection_ip";
    private final static String ACCEPT_ENCODING        = "accept_encoding";
    private final static String APPLICATION_IDS        = "application_ids";
    private final static String MODEM_IMEI             = "modem_imei";
    private final static String MODEM_IMSI             = "modem_imsi";
    private final static String MODEM_ICCID            = "modem_iccid";

    public KuraBirthPayload(String uptime,
                            String displayName,
                            String modelName,
                            String modelId,
                            String partNumber,
                            String serialNumber,
                            String firmwareVersion,
                            String biosVersion,
                            String os,
                            String osVersion,
                            String jvmName,
                            String jvmVersion,
                            String jvmProfile,
                            String esfkuraVersion,
                            String connectionInterface,
                            String connectionIp,
                            String acceptEncoding,
                            String applicationIdentifiers,
                            String availableProcessors,
                            String totalMemory,
                            String osArch,
                            String osgiFramework,
                            String osgiFrameworkVersion,
                            String modemImei,
                            String modemImsi,
                            String modemIccid)
    {
        super();

        if (uptime != null) {
            metrics().put(UPTIME, uptime);
        }
        if (displayName != null) {
            metrics().put(DISPLAY_NAME, displayName);
        }
        if (modelName != null) {
            metrics().put(MODEL_NAME, modelName);
        }
        if (modelId != null) {
            metrics().put(MODEL_ID, modelId);
        }
        if (partNumber != null) {
            metrics().put(PART_NUMBER, partNumber);
        }
        if (serialNumber != null) {
            metrics().put(SERIAL_NUMBER, serialNumber);
        }
        if (firmwareVersion != null) {
            metrics().put(FIRMWARE_VERSION, firmwareVersion);
        }
        if (biosVersion != null) {
            metrics().put(BIOS_VERSION, biosVersion);
        }
        if (os != null) {
            metrics().put(OS, os);
        }
        if (osVersion != null) {
            metrics().put(OS_VERSION, osVersion);
        }
        if (jvmName != null) {
            metrics().put(JVM_NAME, jvmName);
        }
        if (jvmVersion != null) {
            metrics().put(JVM_VERSION, jvmVersion);
        }
        if (jvmProfile != null) {
            metrics().put(JVM_PROFILE, jvmProfile);
        }
        if (esfkuraVersion != null) {
            metrics().put(ESFKURA_VERSION, esfkuraVersion);
        }
        if (connectionInterface != null) {
            metrics().put(CONNECTION_INTERFACE, connectionInterface);
        }
        if (connectionIp != null) {
            metrics().put(CONNECTION_IP, connectionIp);
        }
        if (acceptEncoding != null) {
            metrics().put(ACCEPT_ENCODING, acceptEncoding);
        }
        if (applicationIdentifiers != null) {
            metrics().put(APPLICATION_IDS, applicationIdentifiers);
        }
        if (availableProcessors != null) {
            metrics().put(AVAILABLE_PROCESSORS, availableProcessors);
        }
        if (totalMemory != null) {
            metrics().put(TOTAL_MEMORY, totalMemory);
        }
        if (osArch != null) {
            metrics().put(OS_ARCH, osArch);
        }
        if (osgiFramework != null) {
            metrics().put(OSGI_FRAMEWORK, osgiFramework);
        }
        if (osgiFrameworkVersion != null) {
            metrics().put(OSGI_FRAMEWORK_VERSION, osgiFrameworkVersion);
        }
        if (modemImei != null) {
            metrics().put(MODEM_IMEI, modemImei);
        }
        if (modemImsi != null) {
            metrics().put(MODEM_IMSI, modemImsi);
        }
        if (modemIccid != null) {
            metrics().put(MODEM_ICCID, modemIccid);
        }
    }

    public <P extends KuraPayload> KuraBirthPayload(P kuraPayload)
    {
        Iterator<String> hdrIterator = kuraPayload.metrics().keySet().iterator();
        while (hdrIterator.hasNext()) {
            String hdrName = hdrIterator.next();
            String hdrVal = (String) kuraPayload.metrics().get(hdrName);

            // FIXME: Is this fine??
            if (hdrName.compareTo(ESF_VERSION) == 0) {
                metrics().put(ESFKURA_VERSION, ESF_PREFIX + hdrVal);
            }
            else if (hdrName.compareTo(KURA_VERSION) == 0) {
                metrics().put(ESFKURA_VERSION, hdrVal);
            }
            else {
                metrics().put(hdrName, hdrVal);
            }
        }
        setTimestamp(kuraPayload.getTimestamp());
        setBody(kuraPayload.getBody());
        setPosition(kuraPayload.getPosition());
    }

    public String getUptime()
    {
        return (String) metrics().get(UPTIME);
    }

    public String getDisplayName()
    {
        return (String) metrics().get(DISPLAY_NAME);
    }

    public String getModelName()
    {
        return (String) metrics().get(MODEL_NAME);
    }

    public String getModelId()
    {
        return (String) metrics().get(MODEL_ID);
    }

    public String getPartNumber()
    {
        return (String) metrics().get(PART_NUMBER);
    }

    public String getSerialNumber()
    {
        return (String) metrics().get(SERIAL_NUMBER);
    }

    public String getFirmware()
    {
        return (String) metrics().get(FIRMWARE_VERSION);
    }

    public String getFirmwareVersion()
    {
        return (String) metrics().get(FIRMWARE_VERSION);
    }

    public String getBios()
    {
        return (String) metrics().get(BIOS_VERSION);
    }

    public String getBiosVersion()
    {
        return (String) metrics().get(BIOS_VERSION);
    }

    public String getOs()
    {
        return (String) metrics().get(OS);
    }

    public String getOsVersion()
    {
        return (String) metrics().get(OS_VERSION);
    }

    public String getJvm()
    {
        return (String) metrics().get(JVM_NAME);
    }

    public String getJvmVersion()
    {
        return (String) metrics().get(JVM_VERSION);
    }

    public String getJvmProfile()
    {
        return (String) metrics().get(JVM_PROFILE);
    }

    public String getContainerFramework()
    {
        return (String) metrics().get(OSGI_FRAMEWORK);
    }

    public String getContainerFrameworkVersion()
    {
        return (String) metrics().get(OSGI_FRAMEWORK_VERSION);
    }

    public String getApplicationFramework()
    {
        return (String) metrics().get(ESFKURA_VERSION);
    }

    public String getApplicationFrameworkVersion()
    {
        return (String) metrics().get(ESFKURA_VERSION);
    }

    public String getConnectionInterface()
    {
        return (String) metrics().get(CONNECTION_INTERFACE);
    }

    public String getConnectionIp()
    {
        return (String) metrics().get(CONNECTION_IP);
    }

    public String getAcceptEncoding()
    {
        return (String) metrics().get(ACCEPT_ENCODING);
    }

    public String getApplicationIdentifiers()
    {
        return (String) metrics().get(APPLICATION_IDS);
    }

    public String getAvailableProcessors()
    {
        return (String) metrics().get(AVAILABLE_PROCESSORS);
    }

    public String getTotalMemory()
    {
        return (String) metrics().get(TOTAL_MEMORY);
    }

    public String getOsArch()
    {
        return (String) metrics().get(OS_ARCH);
    }

    public String getModemImei()
    {
        return (String) metrics().get(MODEM_IMEI);
    }

    public String getModemImsi()
    {
        return (String) metrics().get(MODEM_IMSI);
    }

    public String getModemIccid()
    {
        return (String) metrics().get(MODEM_ICCID);
    }

    public String toDisplayString()
    {
        return new StringBuilder().append("[ getUptime()=").append(getUptime())
                                  .append(", getDisplayName()=").append(getDisplayName())
                                  .append(", getModelName()=").append(getModelName())
                                  .append(", getModelId()=").append(getModelId())
                                  .append(", getPartNumber()=").append(getPartNumber())
                                  .append(", getSerialNumber()=").append(getSerialNumber())
                                  .append(", getFirmwareVersion()=").append(getFirmwareVersion())
                                  .append(", getBiosVersion()=").append(getBiosVersion())
                                  .append(", getOs()=").append(getOs())
                                  .append(", getOsVersion()=").append(getOsVersion())
                                  .append(", getJvmName()=").append(getJvm())
                                  .append(", getJvmVersion()=").append(getJvmVersion())
                                  .append(", getJvmProfile()=").append(getJvmProfile())
                                  .append(", getOsgiFramework()=").append(getContainerFramework())
                                  .append(", getOsgiFrameworkVersion()=").append(getContainerFrameworkVersion())
                                  .append(", getEsfKuraVersion()=").append(getApplicationFrameworkVersion())
                                  .append(", getConnectionInterface()=").append(getConnectionInterface())
                                  .append(", getConnectionIp()=").append(getConnectionIp())
                                  .append(", getAcceptEncoding()=").append(getAcceptEncoding())
                                  .append(", getApplicationIdentifiers()=").append(getApplicationIdentifiers())
                                  .append(", getAvailableProcessors()=").append(getAvailableProcessors())
                                  .append(", getTotalMemory()=").append(getTotalMemory())
                                  .append(", getOsArch()=").append(getOsArch())
                                  .append(", getModemImei()=").append(getModemImei())
                                  .append(", getModemImsi()=").append(getModemImsi())
                                  .append(", getModemIccid()=").append(getModemIccid())
                                  .append("]")
                                  .toString();
    }
}