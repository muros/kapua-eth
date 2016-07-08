package org.eclipse.kapua.service.device.management.commons;

@SuppressWarnings("unchecked")
public class DeviceManagementTopicBuilder<T>
{
    protected String topicPrefix;
    protected String accountName;
    protected String assetId;
    protected String appId;

    public T withTopicPrefix(String topicPrefix)
    {
        this.topicPrefix = topicPrefix;
        return (T) this;
    }

    public T withAccountName(String accountName)
    {
        this.accountName = accountName;
        return (T) this;
    }

    public T withAssetId(String assetId)
    {
        this.assetId = assetId;
        return (T) this;
    }

    public T withAppId(String appId)
    {
        this.appId = appId;
        return (T) this;
    }

    public static class Request extends DeviceManagementTopicBuilder<Request>
    {
        /**
         * <p>
         * Topic format: <br/>
         * {{@link #topicPrefix}}/{{@link #accountName}}/{{@link #assetId}}/{{@link #appId}}/{{@link #method}}[/{@link #resources}]
         * </p>
         * 
         * <p>
         * Topic example: <br/>
         * $KAPUA/kapua-sys/test-asset/CONF-V1/GET/configurations
         * </p>
         * 
         */
        private static final String requestTopicStringFormat = "%s/%s/%s/%s/%s%s";

        private String              method;
        private String[]            resources;

        public Request withMethod(String method)
        {
            this.method = method;
            return this;
        }

        public Request withResources(String[] resources)
        {
            this.resources = resources;
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
        public String build()
        {
            String resourcesToString = buildResourcesString();

            return String.format(requestTopicStringFormat,
                                 topicPrefix,
                                 accountName,
                                 assetId,
                                 appId,
                                 method,
                                 resourcesToString);

        }

        private String buildResourcesString()
        {
            StringBuilder sb = new StringBuilder();
            if (resources != null) {
                for (String r : resources) {
                    sb.append("/").append(r);
                }
            }
            return sb.toString();
        }
    }

}