package org.eclipse.kapua.service.device.call.kura;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.client.KapuaClient;
import org.eclipse.kapua.service.device.call.KapuaDeviceCall;
import org.eclipse.kapua.service.device.call.kura.exception.KapuaDeviceCallErrorCodes;
import org.eclipse.kapua.service.device.call.kura.exception.KapuaDeviceCallException;
import org.eclipse.kapua.service.device.call.message.kura.KuraRequestDestination;
import org.eclipse.kapua.service.device.call.message.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseDestination;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraResponsePayload;
import org.org.eclipse.kapua.client.pool.KapuaClientPool;

public class KuraDeviceCallImpl implements KapuaDeviceCall<KuraResponseDestination, KuraResponsePayload, KuraResponseMessage, KuraDeviceCallHandler>
{
    private KuraRequestDestination  requestDestination;
    private KuraRequestPayload      requestPayload;
    private KuraResponseDestination responseDestination;
    private Long                    timeout;

    public KuraDeviceCallImpl(KuraRequestDestination requestDestination, KuraRequestPayload requestPayload)
    {
        this(requestDestination, requestPayload, null);
    }

    public KuraDeviceCallImpl(KuraRequestDestination requestDestination, KuraRequestPayload requestPayload, Long timeout)
    {
        this(requestDestination, requestPayload, null, timeout);
    }

    public KuraDeviceCallImpl(KuraRequestDestination requestDestination, KuraRequestPayload requestPayload, KuraResponseDestination responseDestination, Long timeout)
    {
        this.requestDestination = requestDestination;
        this.requestPayload = requestPayload;
        this.responseDestination = responseDestination;
        this.timeout = timeout;
    }

    @Override
    public KuraResponseMessage send()
        throws KapuaDeviceCallException
    {
        List<KuraResponseMessage> responses = new ArrayList<KuraResponseMessage>();
        KuraDeviceCallHandler kuraDeviceCallHandler = new KuraDeviceCallHandler(responses);

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

        if (responses.isEmpty() && responseDestination != null) {
            throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CALL_TIMEOUT, null, (Object[]) null);
        }

        return responses.get(0);
    }

    @Override
    public KuraResponseDestination sendAsync()
        throws KapuaDeviceCallException
    {
        return sendInternal(null);
    }

    @Override
    public KuraResponseDestination sendAsync(KuraDeviceCallHandler kuraDeviceCallHandler)
        throws KapuaDeviceCallException
    {
        return sendInternal(kuraDeviceCallHandler);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private KuraResponseDestination sendInternal(KuraDeviceCallHandler kuraDeviceCallHandler)
        throws KapuaDeviceCallException
    {
        // Borrow a KapuaClient
        KapuaClient kapuaClient = null;
        try {
            kapuaClient = (KapuaClient) KapuaClientPool.getInstance().borrowObject();
        }
        catch (Exception e) {
            if (kapuaClient != null) {
                try {
                    KapuaClientPool.getInstance().returnObject(kapuaClient);
                }
                catch (Exception e1) {
                    throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CLIENT_RETURN_ERROR, e, (Object[]) null);
                }
            }

            throw new KapuaDeviceCallException(KapuaDeviceCallErrorCodes.CLIENT_BORROW_ERROR, e, (Object[]) null);
        }

        try {
            if (timeout != null) {
                if (responseDestination == null) {

                    // FIXME: create an utilty class to use the same synchronized random instance to avoid duplicates
                    Random r = new Random();
                    String requestId = String.valueOf(r.nextLong());
                    //

                    responseDestination = new KuraResponseDestination();

                    responseDestination.setControlDestinationPrefix(requestDestination.getControlDestinationPrefix());
                    responseDestination.setScopeNamespace(requestDestination.getScopeNamespace());
                    responseDestination.setClientId(kapuaClient.getClientId());
                    responseDestination.setAppId(requestDestination.getAppId());
                    // REPLY
                    responseDestination.setRequestId(requestId);

                    requestPayload.setRequestId(requestId);
                }

                requestPayload.setRequesterId(kapuaClient.getClientId());
            }

            //
            // Subscribe to the response topic
            if (kuraDeviceCallHandler != null && responseDestination != null) {
                kapuaClient.setCallback(new KuraClientCallback(kuraDeviceCallHandler));
                kapuaClient.subscribe(responseDestination);
            }

            //
            // Do publish
            try {
                kapuaClient.publish(requestDestination, requestPayload);
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

        return responseDestination;
    }
}
