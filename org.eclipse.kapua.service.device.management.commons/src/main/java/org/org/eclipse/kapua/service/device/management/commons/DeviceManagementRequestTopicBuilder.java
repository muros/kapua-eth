package org.org.eclipse.kapua.service.device.management.commons;

public class DeviceManagementRequestTopicBuilder
{
    private String   m_accountName;
    private String   m_assetId;
    private String   m_appId;
    private String   m_method;
    private String[] m_resources;

    public DeviceManagementRequestTopicBuilder withAccountName(String accountName)
    {
        m_accountName = accountName;
        return this;
    }

    public DeviceManagementRequestTopicBuilder withAssetId(String assetId)
    {
        m_assetId = assetId;
        return this;
    }

    public DeviceManagementRequestTopicBuilder withAppId(String appId)
    {
        m_appId = appId;
        return this;
    }

    public DeviceManagementRequestTopicBuilder withMethod(String method)
    {
        m_method = method;
        return this;
    }

    public DeviceManagementRequestTopicBuilder withResources(String[] resources)
    {
        m_resources = resources;
        return this;
    }

    // public EdcAppRequestTopic buildAppRequestTopic()
    // throws Exception
    // {
    // if (m_method == null) {
    // throw new Exception("null method");
    // }
    //
    // if (!(m_method.equals(EdcAppRequestTopic.GET) ||
    // m_method.equals(EdcAppRequestTopic.PUT) ||
    // m_method.equals(EdcAppRequestTopic.POST) ||
    // m_method.equals(EdcAppRequestTopic.DEL) ||
    // m_method.equals(EdcAppRequestTopic.EXEC))) {
    // throw new Exception("Invalid method: " + m_method);
    // }
    //
    // EdcAppRequestTopic reqTopic = new EdcAppRequestTopic();
    // reqTopic.setMethod(m_method);
    // reqTopic.setResources(m_resources);
    //
    // return reqTopic;
    // }
    //
    // public EdcAppRequestTopic buildSemanticTopic()
    // throws Exception
    // {
    // if (m_appId == null) {
    // throw new Exception("null app ID");
    // }
    //
    // EdcAppRequestTopic reqTopic = buildAppRequestTopic();
    // reqTopic.setAppId(m_appId);
    // return reqTopic;
    // }
    //
    // public EdcAppRequestTopic buildFullTopic()
    // throws Exception
    // {
    // if (m_accountName == null) {
    // throw new Exception("null account name");
    // }
    //
    // if (m_assetId == null) {
    // throw new Exception("null asset ID");
    // }
    //
    // EdcAppRequestTopic reqTopic = buildSemanticTopic();
    // reqTopic.setAccountName(m_accountName);
    // reqTopic.setAssetId(m_assetId);
    //
    // return reqTopic;
    // }
    //
    // public String buildAppRequestTopicString()
    // throws Exception
    // {
    // EdcAppRequestTopic reqTopic = buildAppRequestTopic();
    // return reqTopic.toApplicationTopicString();
    // }
    //
    // public String buildSemanticTopicString()
    // throws Exception
    // {
    // EdcAppRequestTopic reqTopic = buildSemanticTopic();
    // return reqTopic.toSemanticTopicString();
    // }
    //
    // public String buildFullTopicString()
    // throws Exception
    // {
    // EdcAppRequestTopic reqTopic = buildFullTopic();
    // return reqTopic.toFullTopicString();
    // }
}
