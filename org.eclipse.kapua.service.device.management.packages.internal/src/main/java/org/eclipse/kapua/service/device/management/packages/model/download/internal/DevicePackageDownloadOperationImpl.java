package org.eclipse.kapua.service.device.management.packages.model.download.internal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOperation;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadStatus;

@XmlRootElement(name = "downloadOperation")
public class DevicePackageDownloadOperationImpl implements DevicePackageDownloadOperation
{
    @XmlElement(name = "id")
    private KapuaEid id;

    @XmlElement(name = "size")
    private Integer size;

    @XmlElement(name = "percentage")
    private Integer percentage;

    @XmlElement(name = "status")
    private DevicePackageDownloadStatus status;

    public DevicePackageDownloadOperationImpl()
    {}

    @Override
    public KapuaEid getId()
    {
        return id;
    }

    @Override
    public void setId(KapuaId id)
    {
        if (id != null) {
            this.id = new KapuaEid(id.getId());
        }
    }

    @Override
    public Integer getSize()
    {
        return size;
    }

    @Override
    public void setSize(Integer size)
    {
        this.size = size;
    }

    @Override
    public Integer getPercentage()
    {
        return percentage;
    }

    @Override
    public void setPercentage(Integer percentage)
    {
        this.percentage = percentage;
    }

    @Override
    public DevicePackageDownloadStatus getStatus()
    {
        return status;
    }

    @Override
    public void setStatus(DevicePackageDownloadStatus status)
    {
        this.status = status;
    }
}
