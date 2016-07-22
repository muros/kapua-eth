package org.eclipse.kapua.translator.mqtt.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;

@SuppressWarnings("rawtypes")
public class TranslatorResponseMqttKura implements Translator<MqttTopic, KuraResponseChannel, MqttPayload, KuraPayload, MqttMessage, KuraMessage>
{
    private final static String controlMessageClassifier = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_CONTROL_PREFIX);

    @Override
    public KuraMessage translate(MqttMessage mqttMessage)
        throws KapuaException
    {
        //
        // Kura topic
        KuraResponseChannel kuraChannel = translate(mqttMessage.getRequestTopic());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(mqttMessage.getPayload());

        //
        // Kura message
        @SuppressWarnings("unchecked")
        KuraMessage kuraMessage = new KuraMessage(kuraChannel,
                                                  mqttMessage.getTimestamp(),
                                                  kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    @Override
    public KuraResponseChannel translate(MqttTopic mqttTopic)
        throws KapuaException
    {
        String[] mqttTopicTokens = mqttTopic.getSplittedTopic();

        if (!controlMessageClassifier.equals(mqttTopicTokens[0])) {
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

    @Override
    public KuraPayload translate(MqttPayload mqttPayload)
        throws KapuaException
    {
        byte[] mqttBody = mqttPayload.getBody();

        KuraPayload kuraResponsePayload = new KuraPayload();
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
        return KuraResponseMessage.class;
    }

}
