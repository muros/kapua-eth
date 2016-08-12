package org.eclipse.kapua.service.device.management.command;

import java.util.Date;
import java.util.Random;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.SystemUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.credential.CredentialType;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionCreator;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionFactory;
import org.eclipse.kapua.service.authorization.user.permission.UserPermissionService;
import org.eclipse.kapua.service.device.call.kura.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.app.CommandMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.lifecycle.DeviceLifecycleDomain;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.transport.message.mqtt.MqttMessage;
import org.eclipse.kapua.transport.message.mqtt.MqttPayload;
import org.eclipse.kapua.transport.message.mqtt.MqttTopic;
import org.eclipse.kapua.transport.utils.ClientIdGenerator;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DeviceCommandManagementServiceTest extends Assert
{
    protected static Random       random  = new Random();
    protected static KapuaLocator locator = KapuaLocator.getInstance();

    protected static KapuaId      adminUserId;
    protected static KapuaId      adminScopeId;

    protected static Account      account;
    protected static User         user;

    @BeforeClass
    public static void setUpClass()
    {
        try {
            //
            // Login
            String username = "kapua-sys";
            String password = "We!come12345";

            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
            UsernamePasswordTokenFactory credentialsFactory = locator.getFactory(UsernamePasswordTokenFactory.class);
            authenticationService.login(credentialsFactory.newInstance(username, password.toCharArray()));

            //
            // Get current user Id
            adminUserId = KapuaSecurityUtils.getSession().getUserId();
            adminScopeId = KapuaSecurityUtils.getSession().getScopeId();

            //
            // Create test account
            KapuaLocator locator = KapuaLocator.getInstance();

            //
            // Account creation
            AccountService accountService = locator.getService(AccountService.class);
            AccountFactory accountFactory = locator.getFactory(AccountFactory.class);
            AccountCreator accountCreator = accountFactory.newAccountCreator(adminScopeId, DeviceCommandManagementServiceTest.class.getName());
            account = accountService.create(accountCreator);
        }
        catch (KapuaException exc) {
            exc.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownClass()
    {
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);

            authenticationService.logout();
        }
        catch (KapuaException exc) {
            exc.printStackTrace();
        }
    }

    @Before
    public void setUpTest()
        throws Exception
    {
        //
        // User creation
        UserService userService = locator.getService(UserService.class);
        UserFactory userFactory = locator.getFactory(UserFactory.class);
        UserCreator userCreator = userFactory.newCreator(account.getId(), "testUser");
        user = userService.create(userCreator);

        //
        // User permission creation
        UserPermissionService userPermissionService = locator.getService(UserPermissionService.class);
        UserPermissionFactory userPermissionFactory = locator.getFactory(UserPermissionFactory.class);
        UserPermissionCreator userPermissionCreator = userPermissionFactory.newCreator(account.getId());

        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);

        userPermissionCreator.setPermission(permissionFactory.newPermission(DeviceLifecycleDomain.DEVICE_LIFECYCLE,
                                                                            Actions.connect,
                                                                            account.getId()));

        userPermissionService.create(userPermissionCreator);

        //
        // User credentials creation
        CredentialService credentialService = locator.getService(CredentialService.class);
        CredentialFactory credentialFactory = locator.getFactory(CredentialFactory.class);
        CredentialCreator credentialCreator = credentialFactory.newCreator(account.getId(),
                                                                           user.getId(),
                                                                           CredentialType.PASSWORD,
                                                                           "We!come12345");

        credentialService.create(credentialCreator);

    }

    @Test
    public void testCommandExecution()
        throws Exception
    {

        //
        // Setup client and callback
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(user.getName());
        mqttConnectOptions.setPassword("We!come12345".toCharArray());

        MqttClient mqttClient = new MqttClient(SystemUtils.getBrokerURI().toString(),
                                               ClientIdGenerator.next("testCommandExecution"));

        mqttClient.connect(mqttConnectOptions);

        assertTrue("mqttClient.connected", mqttClient.isConnected());

        MqttCallback mqttCallback = new MqttClientCommandCallback(mqttClient);
        mqttClient.setCallback(mqttCallback);

        //
        // Send command
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

        Device targetDevice = deviceRegistryService.findByClientId(account.getId(),
                                                                   mqttClient.getClientId());

        DeviceCommandManagementService deviceCommandManagementService = locator.getService(DeviceCommandManagementService.class);
        DeviceCommandFactory deviceCommandFactory = locator.getFactory(DeviceCommandFactory.class);

        DeviceCommandInput commandInput = deviceCommandFactory.newCommandInput();

        DeviceCommandOutput commandOutput = deviceCommandManagementService.exec(account.getId(),
                                                                                targetDevice.getId(),
                                                                                commandInput,
                                                                                15000L);

        //
        // Verify output
        assertNotNull("output", commandOutput);
        assertEquals("output.stdout", commandOutput.getStdout());

    }

    private class MqttClientCommandCallback implements MqttCallback
    {
        private MqttClient mqttClient;

        public MqttClientCommandCallback(MqttClient mqttClient)
        {
            this.mqttClient = mqttClient;
        }

        @Override
        public void connectionLost(Throwable cause)
        {

        }

        @Override
        public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage message)
            throws Exception
        {
            //
            // Mqtt --> Kura
            Translator<MqttMessage, KuraRequestMessage> tMqttReqKura = Translator.getTranslatorFor(MqttMessage.class,
                                                                                                   KuraRequestMessage.class);
            KuraRequestMessage kuraRequestMessage = tMqttReqKura.translate(new MqttMessage(new MqttTopic(topic),
                                                                                           new Date(),
                                                                                           new MqttPayload(message.getPayload())));

            //
            // Kura --> Mqtt
            KuraRequestChannel kuraRequestChannel = kuraRequestMessage.getChannel();
            KuraRequestPayload kuraRequestPayload = kuraRequestMessage.getPayload();

            KuraResponseChannel kuraResponseChannel = new KuraResponseChannel(kuraRequestChannel.getMessageClassification(),
                                                                              kuraRequestChannel.getScope(),
                                                                              kuraRequestPayload.getRequesterClientId());
            kuraResponseChannel.setAppId(kuraRequestChannel.getAppId());
            kuraResponseChannel.setReplyPart("REPLY");
            kuraResponseChannel.setRequestId(kuraRequestPayload.getRequestId());

            KuraResponsePayload kuraResponsePayload = new KuraResponsePayload();
            kuraResponsePayload.metrics().put(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue(), 200);
            kuraResponsePayload.metrics().put(CommandMetrics.APP_METRIC_STDOUT.getValue(), "ok");

            KuraResponseMessage kuraResponseMessage = new KuraResponseMessage(kuraResponseChannel,
                                                                              new Date(),
                                                                              kuraResponsePayload);

            //
            // Send back answer
            Translator<KuraResponseMessage, MqttMessage> tKuraResMqtt = Translator.getTranslatorFor(KuraResponseMessage.class,
                                                                                                    MqttMessage.class);
            MqttMessage mqttMessage = tKuraResMqtt.translate(kuraResponseMessage);
            mqttClient.publish(mqttMessage.getRequestTopic().getTopic(),
                               new org.eclipse.paho.client.mqttv3.MqttMessage(mqttMessage.getPayload().getBody()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token)
        {

        }

    }
}
