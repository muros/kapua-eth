package org.eclipse.kapua.translator.mqtt.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.message.device.app.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

public class TranslatorImpl implements Translator<MqttTopic, KuraChannel, MqttPayload, KuraPayload, MqttMessage, KuraMessage>
{
    private final static String controlMessageClassifier = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_CONTROL_PREFIX);

    @Override
    public KuraMessage translate(MqttMessage mqttMessage)
        throws KapuaException
    {
        //
        // Kura topic
        KuraChannel kuraChannel = translate(mqttMessage.getTopic());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(mqttMessage.getPayload());

        //
        // Kura message
        KuraMessage kuraMessage = new KuraMessage(kuraChannel,
                                                  mqttMessage.getTimestamp(),
                                                  kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    @Override
    public KuraChannel translate(MqttTopic mqttTopic)
        throws KapuaException
    {
        String[] mqttTopicTokens = mqttTopic.getSplittedTopic();

        KuraResponseChannel kuraResponseChannel;
        if (controlMessageClassifier.equals(mqttTopicTokens[0])) {
            kuraResponseChannel = new KuraResponseChannel(mqttTopicTokens[0],
                                                          mqttTopicTokens[1],
                                                          mqttTopicTokens[2]);

            kuraResponseChannel.setAppId(mqttTopicTokens[3]);
            kuraResponseChannel.setReplyPart(mqttTopicTokens[4]);
            kuraResponseChannel.setRequestId(mqttTopicTokens[5]);
        }
        else {
            kuraResponseChannel = new KuraResponseChannel(mqttTopicTokens[0],
                                                          mqttTopicTokens[1]);

            kuraResponseChannel.setAppId(mqttTopicTokens[2]);
            kuraResponseChannel.setReplyPart(mqttTopicTokens[3]);
            kuraResponseChannel.setRequestId(mqttTopicTokens[4]);
        }

        return kuraResponseChannel;
    }

    @Override
    public KuraPayload translate(MqttPayload mqttPayload)
        throws KapuaException
    {
        byte[] mqttBody = mqttPayload.getBody();

        KuraResponsePayload kuraResponsePayload = new KuraResponsePayload();
        kuraResponsePayload.readFromByteArray(mqttBody);

        return kuraResponsePayload;
    }

    @Override
    public Class<?> getClassFrom()
    {
        return MqttMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return KapuaResponseMessage.class;
    }

}
