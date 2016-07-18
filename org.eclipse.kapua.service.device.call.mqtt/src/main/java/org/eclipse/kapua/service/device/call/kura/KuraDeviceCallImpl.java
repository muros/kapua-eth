package org.eclipse.kapua.service.device.call.kura;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.kura.exception.KuraMqttDeviceCallErrorCodes;
import org.eclipse.kapua.service.device.call.kura.exception.KuraMqttDeviceCallException;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.org.eclipse.kapua.transport.pooling.TransportClientPool;

public class KuraDeviceCallImpl implements DeviceCall<KuraResponseChannel, KuraResponsePayload, KuraResponseMessage, KuraDeviceResponseContainer>
{
    private KuraRequestChannel  requestDestination;
    private KuraRequestPayload  requestPayload;
    private KuraResponseChannel responseDestination;
    private Long                timeout;

    public KuraDeviceCallImpl(KuraRequestChannel requestDestination, KuraRequestPayload requestPayload)
    {
        this(requestDestination, requestPayload, null);
    }

    public KuraDeviceCallImpl(KuraRequestChannel requestDestination, KuraRequestPayload requestPayload, Long timeout)
    {
        this(requestDestination, requestPayload, null, timeout);
    }

    public KuraDeviceCallImpl(KuraRequestChannel requestDestination, KuraRequestPayload requestPayload, KuraResponseChannel responseDestination, Long timeout)
    {
        this.requestDestination = requestDestination;
        this.requestPayload = requestPayload;
        this.responseDestination = responseDestination;
        this.timeout = timeout;
    }

    @Override
    public KuraResponseMessage send()
        throws KuraMqttDeviceCallException
    {

        KuraDeviceResponseContainer kuraDeviceResponseContainer = new KuraDeviceResponseContainer();

        synchronized (kuraDeviceResponseContainer) {
            sendInternal(kuraDeviceResponseContainer);
            try {
                kuraDeviceResponseContainer.wait();
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CALL_ERROR, e, (Object[]) null);
            }
        }

        if (kuraDeviceResponseContainer.isEmpty() && responseDestination != null) {
            throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CALL_TIMEOUT, null, (Object[]) null);
        }

        return kuraDeviceResponseContainer.get(0);
    }

    @Override
    public KuraResponseChannel sendAsync()
        throws KuraMqttDeviceCallException
    {
        return sendInternal(null);
    }

    @Override
    public KuraResponseChannel sendAsync(KuraDeviceResponseContainer kuraDeviceCallHandler)
        throws KuraMqttDeviceCallException
    {
        return sendInternal(kuraDeviceCallHandler);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private KuraResponseChannel sendInternal(KuraDeviceResponseContainer kuraDeviceResponseContainer)
        throws KuraMqttDeviceCallException
    {
        // Borrow a KapuaClient
        TransportClient transportClient = null;
        try {
            // TODO:
            // Device device = findByClientId(requestDest.getClientid();
            // DeviceConnection deviceConnection = findById(scopeId, device.getConnectionId());
            // kapuaClient = (KapuaClient) KapuaClientPool.getInstance(deviceConnection.getProtocol()).borrowObject();

            transportClient = (TransportClient) TransportClientPool.getInstance(/* MqttClient.class */).borrowObject();
        }
        catch (Exception e) {
            if (transportClient != null) {
                try {
                    TransportClientPool.getInstance().returnObject(transportClient);
                }
                catch (Exception e1) {
                    throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CLIENT_RETURN_ERROR,
                                                          e,
                                                          (Object[]) null);
                }
            }

            throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CLIENT_BORROW_ERROR,
                                                  e,
                                                  (Object[]) null);
        }

        Translator translator;
        try {
            translator = Translator.getTranslatorFor(KuraMessage.class, transportClient.getMessageClass());
        }
        catch (KapuaException e) {
            throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CALL_ERROR,
                                                  e,
                                                  (Object[]) null);
        }

        try {
            if (timeout != null) {
                if (responseDestination == null) {

                    // FIXME: create an utilty class to use the same synchronized random instance to avoid duplicates
                    Random r = new Random();
                    String requestId = String.valueOf(r.nextLong());
                    //

                    responseDestination = new KuraResponseChannel(requestDestination.getMessageClassification(),
                                                                  requestDestination.getScope(),
                                                                  transportClient.getClientId());

                    responseDestination.setAppId(requestDestination.getAppId());
                    responseDestination.setReplyPart(DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_REPLY_PART));
                    responseDestination.setRequestId(requestId);

                    requestPayload.setRequestId(requestId);
                }

                requestPayload.setRequesterClientId(transportClient.getClientId());
            }

            //
            // Subscribe to the response topic
            if (kuraDeviceResponseContainer != null && responseDestination != null) {

                transportClient.newCallback(kuraDeviceResponseContainer, KuraResponseMessage.class);
                transportClient.subscribe((TransportChannel) translator.translate(responseDestination));
            }

            //
            // Do publish
            try {

                // Mqtt topic
                List<String> topicParts = new ArrayList<String>();
                topicParts.add(requestDestination.getMessageClassification());
                topicParts.add(requestDestination.getScope());
                topicParts.add(requestDestination.getClientId());
                topicParts.add(requestDestination.getAppId());
                topicParts.add(requestDestination.getMethod());
                for (String r : requestDestination.getResources()) {
                    topicParts.add(r);
                }

                KuraMessage kuraMessage = new KuraMessage(requestDestination, new Date(), requestPayload);
                transportClient.publish((TransportMessage) translator.translate(kuraMessage));
            }
            catch (KapuaException e) {
                throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CLIENT_SEND_ERROR, e, (Object[]) null);
            }

            //
            // If timeout and handler are specified
            if (timeout != null &&
                kuraDeviceResponseContainer != null) {
                Timer timeoutTimer = new Timer("timeoutTimer", true);

                timeoutTimer.schedule(new TimerTask() {

                    @Override
                    public void run()
                    {
                        if (kuraDeviceResponseContainer != null) {
                            kuraDeviceResponseContainer.timedOut();
                        }
                    }
                }, timeout);

            }
        }
        catch (KapuaException ke) {

        }

        return responseDestination;
    }
}
