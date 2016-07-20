package org.eclipse.kapua.translator.kura.mqtt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

@SuppressWarnings("rawtypes")
public class TranslatorImpl implements Translator<KuraChannel, MqttTopic, KuraPayload, MqttPayload, KuraMessage, MqttMessage>
{

    @Override
    public MqttMessage translate(KuraMessage message)
        throws KapuaException
    {
        //
        // Mqtt request topic
        MqttTopic mqttTopic = translate(message.getChannel());

        //
        // Mqtt response topic

        MqttTopic mqttResponseTopic = generateResponseTopic((KuraRequestChannel) message.getChannel());

        //
        // Mqtt payload
        MqttPayload mqttPayload = translate(message.getPayload());

        //
        // Mqtt message
        MqttMessage mqttMessage = new MqttMessage(mqttTopic,
                                                  mqttResponseTopic,
                                                  mqttPayload);

        //
        // Return result
        return mqttMessage;
    }

    @Override
    public MqttTopic translate(KuraChannel channel)
        throws KapuaException
    {
        List<String> topicTokens = new ArrayList<String>();

        if (channel.getMessageClassification() != null) {
            topicTokens.add(channel.getMessageClassification());
        }

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getClientId());

        if (channel.getSemanticChannelParts() != null &&
            !channel.getSemanticChannelParts().isEmpty()) {
            topicTokens.addAll(channel.getSemanticChannelParts());
        }

        return new MqttTopic(topicTokens.toArray(new String[0]));
    }

    private MqttTopic generateResponseTopic(KuraRequestChannel channel)
    {
        String replyPart = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_REPLY_PART);

        List<String> topicTokens = new ArrayList<String>();

        if (channel.getMessageClassification() != null) {
            topicTokens.add(channel.getMessageClassification());
        }

        topicTokens.add(channel.getScope());
        topicTokens.add(channel.getClientId());
        topicTokens.add(channel.getAppId());
        topicTokens.add(replyPart);
        topicTokens.add(channel.getRequestId());

        return new MqttTopic(topicTokens.toArray(new String[0]));
    }

    @Override
    public MqttPayload translate(KuraPayload kuraPayload)
        throws KapuaException
    {
        return new MqttPayload(kuraPayload.toByteArray());
    }

    @Override
    public Class<?> getClassFrom()
    {
        return KuraMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return MqttMessage.class;
    }
}
