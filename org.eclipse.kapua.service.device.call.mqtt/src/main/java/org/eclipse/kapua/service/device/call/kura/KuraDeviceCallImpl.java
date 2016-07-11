package org.eclipse.kapua.service.device.call.kura;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.KapuaDeviceCall;
import org.eclipse.kapua.service.device.call.KapuaDeviceCallHandler;
import org.eclipse.kapua.service.device.call.kura.exception.KapuaDeviceCallErrorCodes;
import org.eclipse.kapua.service.device.call.kura.exception.KapuaDeviceCallException;
import org.eclipse.kapua.service.device.client.KapuaClient;
import org.eclipse.kapua.service.device.client.KapuaClientPool;
import org.eclipse.kapua.service.device.client.mqtt.MqttClientCallback;
import org.eclipse.kapua.service.device.message.request.KapuaRequestDestination;
import org.eclipse.kapua.service.device.message.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.message.request.KapuaRequestPayload;
import org.eclipse.kapua.service.device.message.response.KapuaResponseDestination;
import org.eclipse.kapua.service.device.message.response.KapuaResponseMessage;

public class KuraDeviceCallImpl implements KapuaDeviceCall<KapuaRequestMessage, KapuaResponseMessage, KapuaRequestDestination, KapuaResponseDestination, KapuaDeviceCallHandler<RSM>>
{
    private String              requestTopic;
    private String              responseTopic;
    private KapuaRequestPayload requestPayload;
    private Long                timeout;

    public KuraDeviceCallImpl(String requestTopic, KapuaRequestPayload kapuaPayload)
    {
        this(requestTopic, kapuaPayload, null);
    }

    public KuraDeviceCallImpl(String requestTopic, KapuaRequestPayload kapuaPayload, Long timeout)
    {
        this(requestTopic, null, kapuaPayload, timeout);
    }

    public KuraDeviceCallImpl(String requestTopic, String responseTopic, KapuaRequestPayload kapuaPayload, Long timeout)
    {
        this.requestTopic = requestTopic;
        this.responseTopic = responseTopic;
        this.requestPayload = kapuaPayload;
        this.timeout = timeout;
    }

    @Override
    public KapuaResponseMessage send()
        throws KapuaDeviceCallException
    {
        List<KapuaResponseMessage> responses = new ArrayList<KapuaResponseMessage>();
        KuraDeviceCallHandlerImpl kuraDeviceCallHandler = new KuraDeviceCallHandlerImpl(responses);

        synchronized (kuraDeviceCallHandler) {
            sendInternal(kuraDeviceCallHandler);
            try {
                kuraDeviceCallHandler.wait();
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CALL_ERROR, e, (Object[]) null);
            }
        }

        if (responses.isEmpty() && responseTopic != null) {
            throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CALL_TIMEOUT, null, (Object[]) null);
        }

        return responses.get(0);
    }

    @Override
    public KapuaRequestDestination sendAsync()
        throws KapuaDeviceCallException
    {
        return sendInternal(null);
    }

    @Override
    public KapuaRequestDestination sendAsync(KapuaDeviceCallHandler kuraDeviceCallHandler)
        throws KapuaDeviceCallException
    {
        return sendInternal(kuraDeviceCallHandler);
    }

    private KapuaRequestDestination sendInternal(KapuaDeviceCallHandler kuraDeviceCallHandler)
        throws KapuaDeviceCallException
    {
        // Borrow a KapuaClient
        KapuaClient mqttClient = null;
        try {
            mqttClient = KapuaClientPool.getInstance().borrowObject();
        }
        catch (Exception e) {
            if (mqttClient != null) {
                try {
                    KapuaClientPool.getInstance().returnObject(mqttClient);
                }
                catch (Exception e1) {
                    throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CLIENT_RETURN_ERROR, e, (Object[]) null);
                }
            }

            throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CLIENT_BORROW_ERROR, e, (Object[]) null);
        }

        try {
            if (timeout != null) {
                if (responseTopic == null) {
                    Random r = new Random();
                    String requestId = String.valueOf(r.nextLong());

                    String[] requestTopicTokens = requestTopic.split("/");
                    responseTopic = new StringBuilder().append(requestTopicTokens[0])
                                                       .append("/")
                                                       .append(requestTopicTokens[1])
                                                       .append("/")
                                                       .append(mqttClient.getClientId())
                                                       .append("/")
                                                       .append(requestTopicTokens[3])
                                                       .append("/")
                                                       .append("REPLY")
                                                       .append("/")
                                                       .append(requestId).toString();
                    requestPayload.setRequestId(requestId);
                }

                requestPayload.setRequesterId(mqttClient.getClientId());
            }

            //
            // Subscribe to the response topic
            if (kuraDeviceCallHandler != null && responseTopic != null) {
                mqttClient.setCallback(new MqttClientCallback(kuraDeviceCallHandler));
                mqttClient.subscribe(responseTopic, null);
            }

            //
            // Do publish
            try {
                mqttClient.publish(requestTopic, requestPayload);
            }
            catch (KapuaException e) {
                throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CLIENT_SEND_ERROR, e, (Object[]) null);
            }

            //
            // If timeout and handler are specified
            if (timeout != null &&
                kuraDeviceCallHandler != null) {
                Timer timeoutTimer = new Timer("timeoutTimer", true);

                timeoutTimer.schedule(new TimerTask() {

                    @Override
                    public void run()
                    {
                        if (kuraDeviceCallHandler != null) {
                            kuraDeviceCallHandler.timedOut();
                        }
                    }
                }, timeout);

            }
        }
        catch (KapuaException ke) {

        }

        return responseTopic;
    }

}
