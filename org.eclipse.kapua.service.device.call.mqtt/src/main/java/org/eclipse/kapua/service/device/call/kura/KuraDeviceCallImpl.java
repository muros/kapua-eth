package org.eclipse.kapua.service.device.call.kura;

import java.util.Date;
import java.util.Random;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.kura.exception.KuraMqttDeviceCallErrorCodes;
import org.eclipse.kapua.service.device.call.kura.exception.KuraMqttDeviceCallException;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.TransportClient;
import org.eclipse.kapua.transport.message.TransportMessage;
import org.org.eclipse.kapua.transport.pooling.TransportClientPool;

@SuppressWarnings("rawtypes")
public class KuraDeviceCallImpl implements DeviceCall<KuraRequestMessage, KuraResponseMessage>
{
    public KuraDeviceCallImpl()
    {
    }

    @Override
    public KuraResponseMessage create(KuraRequestMessage requestMessage, Long timeout)
        throws KapuaException
    {
        requestMessage.getChannel().setMethod("POST");
        return send(requestMessage, timeout);
    }

    @Override
    public KuraResponseMessage read(KuraRequestMessage requestMessage, Long timeout)
        throws KapuaException
    {
        requestMessage.getChannel().setMethod("GET");
        return send(requestMessage, timeout);
    }

    @Override
    public KuraResponseMessage options(KuraRequestMessage requestMessage, Long timeout)
        throws KapuaException
    {
        requestMessage.getChannel().setMethod("GET");
        return send(requestMessage, timeout);
    }

    @Override
    public KuraResponseMessage delete(KuraRequestMessage requestMessage, Long timeout)
        throws KapuaException
    {
        requestMessage.getChannel().setMethod("DEL");
        return send(requestMessage, timeout);
    }

    @Override
    public KuraResponseMessage execute(KuraRequestMessage requestMessage, Long timeout)
        throws KapuaException
    {
        requestMessage.getChannel().setMethod("EXEC");
        return send(requestMessage, timeout);
    }

    @Override
    public KuraResponseMessage write(KuraRequestMessage requestMessage, Long timeout)
        throws KapuaException
    {
        requestMessage.getChannel().setMethod("PUT");
        return send(requestMessage, timeout);
    }

    @SuppressWarnings({ "unchecked" })
    private KuraResponseMessage send(KuraRequestMessage requestMessage, Long timeout)
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

        Translator translatorKuraTransport;
        try {
            translatorKuraTransport = Translator.getTranslatorFor(KuraMessage.class, transportClient.getMessageClass());
        }
        catch (KapuaException e) {
            throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CALL_ERROR,
                                                  e,
                                                  (Object[]) null);
        }

        Translator translatorTransportKura;
        try {
            translatorTransportKura = Translator.getTranslatorFor(transportClient.getMessageClass(), KuraMessage.class);
        }
        catch (KapuaException e) {
            throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CALL_ERROR,
                                                  e,
                                                  (Object[]) null);
        }

        KuraResponseMessage response = null;
        try {
            KuraRequestChannel requestChannel = requestMessage.getChannel();
            KuraRequestPayload requestPayload = requestMessage.getPayload();

            if (timeout != null) {
                // FIXME: create an utilty class to use the same synchronized random instance to avoid duplicates
                Random r = new Random();
                String requestId = String.valueOf(r.nextLong());
                //

                requestChannel.setRequestId(requestId);
                requestChannel.setRequesterClientId(transportClient.getClientId());

                requestPayload.setRequestId(requestId);
                requestPayload.setRequesterClientId(transportClient.getClientId());
            }

            //
            // Do send
            try {
                KuraMessage kuraMessage = new KuraMessage(requestChannel, new Date(), requestPayload);
                TransportMessage transportResponseMessage = transportClient.send((TransportMessage) translatorKuraTransport.translate(kuraMessage),
                                                                                 timeout);
                response = (KuraResponseMessage) translatorTransportKura.translate(transportResponseMessage);
            }
            catch (KapuaException e) {
                throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CLIENT_SEND_ERROR,
                                                      e,
                                                      (Object[]) null);
            }

        }
        catch (KapuaException ke) {
            throw new KuraMqttDeviceCallException(KuraMqttDeviceCallErrorCodes.CALL_ERROR,
                                                  ke,
                                                  (Object[]) null);
        }

        return response;
    }

    @Override
    public Class<KuraResponseMessage> getBaseMessageClass()
    {
        return KuraResponseMessage.class;
    }
}
