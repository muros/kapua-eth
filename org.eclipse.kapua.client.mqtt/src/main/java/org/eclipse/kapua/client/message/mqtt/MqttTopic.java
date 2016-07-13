package org.eclipse.kapua.client.message.mqtt;

import org.eclipse.kapua.message.KapuaDestination;

public class MqttTopic implements KapuaDestination
{

    public static final String MULTI_LEVEL_WCARD  = "#";
    public static final String SINGLE_LEVEL_WCARD = "+";
    public static final String TOPIC_SEPARATOR    = "/";

    public static final String SYS_ACCOUNT        = "$SYS";
    public static final String EDC_ACCOUNT        = "$EDC";
    public static final String ALERT_TOPIC        = "ALERT";

    public boolean isSystemTopic()
    {
        return SYS_ACCOUNT.equals(m_topicParts[0]);
    }

    public boolean isKapuaTopic()
    {
        return EDC_ACCOUNT.equals(m_topicParts[0]);
    }

    public boolean isAlertTopic()
    {
        return ALERT_TOPIC.equals(m_topicParts[2]);
    }

    public boolean isAnyAccount()
    {
        return SINGLE_LEVEL_WCARD.equals(this.getAccount());
    }

    public boolean isAnyAsset()
    {
        return SINGLE_LEVEL_WCARD.equals(this.getAsset());
    }

    public boolean isAnySubtopic()
    {
        final String multilevelAnySubtopic = String.format("%s%s", TOPIC_SEPARATOR, MULTI_LEVEL_WCARD);
        boolean isAnySubtopic = this.getSemanticTopic().endsWith(multilevelAnySubtopic) ||
                                MULTI_LEVEL_WCARD.equals(this.getSemanticTopic());

        return isAnySubtopic;
    }

    public String getLeafName()
    {
        int iLastSlash = m_semanticTopic.lastIndexOf(TOPIC_SEPARATOR);
        return iLastSlash != -1 ? m_semanticTopic.substring(iLastSlash + 1) : m_semanticTopic;
    }

    public String getParentTopic()
    {
        int iLastSlash = m_semanticTopic.lastIndexOf(TOPIC_SEPARATOR);
        return iLastSlash != -1 ? m_semanticTopic.substring(0, iLastSlash) : null;
    }

    public String getGrandParentTopic()
    {
        String parentTopic = getParentTopic();
        if (parentTopic != null) {
            int iLastSlash = parentTopic.lastIndexOf(TOPIC_SEPARATOR);
            return iLastSlash != -1 ? parentTopic.substring(0, iLastSlash) : null;
        }
        else {
            return null;
        }
    }

    @Override
    public String toClientDestination()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void fromClientDestination(String destination)
    {

    }
}
