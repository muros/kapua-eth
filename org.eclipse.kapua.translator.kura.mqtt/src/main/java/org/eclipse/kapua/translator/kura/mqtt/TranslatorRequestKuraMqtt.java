package org.eclipse.kapua.translator.kura.mqtt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

public class TranslatorRequestKuraMqtt implements Translator<KuraRequestMessage, MqttMessage>
{
    @Override
    public MqttMessage translate(KuraRequestMessage message)
        throws KapuaException
    {
        //
        // Mqtt request topic
        MqttTopic mqttRequestTopic = translate(message.getChannel());

        //
        // Mqtt response topic

        MqttTopic mqttResponseTopic = generateResponseTopic(message.getChannel());

        //
        // Mqtt payload
        MqttPayload mqttPayload = translate(message.getPayload());

        //
        // Mqtt message
        MqttMessage mqttMessage = new MqttMessage(mqttRequestTopic,
                                                  mqttResponseTopic,
                                                  mqttPayload);

        //
        // Return result
        return mqttMessage;
    }

    public MqttTopic translate(KuraRequestChannel channel)
        throws KapuaException
    {
        List<String> topicTokens = new ArrayList<>();

        if (channel.getMessageClassification() != null) {
            topicTokens.add(channel.getMessageClassification());
        }

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getClientId());
        topicTokens.add(channel.getAppId());
        topicTokens.add(channel.getMethod().name());

        for (String s : channel.getResources()) {
            topicTokens.add(s);
        }

        return new MqttTopic(topicTokens.toArray(new String[0]));
    }

    private MqttTopic generateResponseTopic(KuraRequestChannel channel)
    {
        String replyPart = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_REPLY_PART);

        List<String> topicTokens = new ArrayList<>();

        if (channel.getMessageClassification() != null) {
            topicTokens.add(channel.getMessageClassification());
        }

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getRequesterClientId());
        topicTokens.add(channel.getAppId());
        topicTokens.add(replyPart);
        topicTokens.add(channel.getRequestId());

        return new MqttTopic(topicTokens.toArray(new String[0]));
    }

    private MqttPayload translate(KuraPayload kuraPayload)
        throws KapuaException
    {
        return new MqttPayload(kuraPayload.toByteArray());
    }

    @Override
    public Class<KuraRequestMessage> getClassFrom()
    {
        return KuraRequestMessage.class;
    }

    @Override
    public Class<MqttMessage> getClassTo()
    {
        return MqttMessage.class;
    }
}
