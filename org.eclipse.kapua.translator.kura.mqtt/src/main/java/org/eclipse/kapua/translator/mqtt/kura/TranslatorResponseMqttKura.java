package org.eclipse.kapua.translator.mqtt.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

public class TranslatorResponseMqttKura implements Translator<MqttMessage, KuraResponseMessage>
{
    private static final String CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);

    @Override
    public KuraResponseMessage translate(MqttMessage mqttMessage)
        throws KapuaException
    {
        //
        // Kura topic
        KuraResponseChannel kuraChannel = translate(mqttMessage.getRequestTopic());

        //
        // Kura payload
        KuraResponsePayload kuraPayload = translate(mqttMessage.getPayload());

        //
        // Kura message
        KuraResponseMessage kuraMessage = new KuraResponseMessage(kuraChannel,
                                                                  mqttMessage.getTimestamp(),
                                                                  kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    private KuraResponseChannel translate(MqttTopic mqttTopic)
        throws KapuaException
    {
        String[] mqttTopicTokens = mqttTopic.getSplittedTopic();

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(mqttTopicTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          mqttTopicTokens[0]);
        }

        KuraResponseChannel kuraResponseChannel = new KuraResponseChannel(mqttTopicTokens[0],
                                                                          mqttTopicTokens[1],
                                                                          mqttTopicTokens[2]);

        kuraResponseChannel.setAppId(mqttTopicTokens[3]);
        kuraResponseChannel.setReplyPart(mqttTopicTokens[4]);
        kuraResponseChannel.setRequestId(mqttTopicTokens[5]);

        return kuraResponseChannel;
    }

    private KuraResponsePayload translate(MqttPayload mqttPayload)
        throws KapuaException
    {
        byte[] mqttBody = mqttPayload.getBody();

        KuraResponsePayload kuraResponsePayload = new KuraResponsePayload();
        kuraResponsePayload.readFromByteArray(mqttBody);

        return kuraResponsePayload;
    }

    @Override
    public Class<MqttMessage> getClassFrom()
    {
        return MqttMessage.class;
    }

    @Override
    public Class<KuraResponseMessage> getClassTo()
    {
        return KuraResponseMessage.class;
    }
}
