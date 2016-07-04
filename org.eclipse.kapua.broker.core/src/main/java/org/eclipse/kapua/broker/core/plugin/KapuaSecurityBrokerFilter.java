package org.eclipse.kapua.broker.core.plugin;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.CredentialException;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.security.AuthorizationEntry;
import org.apache.activemq.security.DefaultAuthorizationMap;
import org.apache.activemq.security.SecurityContext;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.util.ThreadState;
import org.eclipse.kapua.KapuaErrorCode;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.broker.core.metrics.MetricsService;
import org.eclipse.kapua.broker.core.ratelimit.KapuaConnectionRateLimitExceededException;
import org.eclipse.kapua.commons.config.KapuaEnvironmentConfig;
import org.eclipse.kapua.commons.config.KapuaEnvironmentConfigKeys;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.AccessToken;
import org.eclipse.kapua.service.authentication.AuthenticationCredentials;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.authentication.shiro.KapuaAuthenticationErrorCodes;
import org.eclipse.kapua.service.authentication.shiro.KapuaAuthenticationException;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionCreator;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.google.common.util.concurrent.RateLimiter;

/**
 * A-MQ broker filter plugin implementation (security filter)
 * 
 * Filter allow unfiltered connection/disconnection and publishing/subscribe action for pass through connection (embedded broker and filter also). This connection type is used by broker assistant
 * bundle.
 * Otherwise perform all kapua authorization/check action
 * 
 * This filter is added inside amq filter chain plugin by {@link KapuaBrokerSecurityPlugin}
 * 
 * Patched version.
 * Switch ACLs from org.apache.activemq.security.SimpleAuthorizationMap to org.apache.activemq.security.AuthorizationEntry and org.apache.activemq.security.DefaultAuthorizationMap as suggested by
 * Dejan (RedHat engineer)
 */
public class KapuaSecurityBrokerFilter extends BrokerFilter
{
    private static Logger                          logger                            = LoggerFactory.getLogger(KapuaSecurityBrokerFilter.class);
    
    public static final String VT_TOPIC_PREFIX_TEMPLATE    = "VirtualTopic.{0}";
    public static final String VT_TOPIC_PREFIX             = "VirtualTopic.";
    public static final String HEADER_KAPUA_CONNECTION_ID  = "KAPUA_CONNECTION_ID";
    
    /**
	 * VM connector
	 */
	private final static String CONNECTOR_NAME_VM = "vm://amq";
	private final static String VT_CONSUMER_PREFIX          = "Consumer";
	private final static String ACL_HASH                         = "VirtualTopic.>";
	private final static String ACL_AMQ_ADVISORY                 = "VirtualTopic.ActiveMQ.Advisory.>";
	private final static String ACL_CTRL_ACC_REPLY               = "VirtualTopic.$EDC.{0}.*.*.REPLY.>";
	private final static String ACL_CTRL_ACC_CLI_MQTT_LIFE_CYCLE = "VirtualTopic.$EDC.{0}.{1}.MQTT.>";
	private final static String ACL_CTRL_ACC                     = "VirtualTopic.$EDC.{0}.>";
	private final static String ACL_CTRL_ACC_CLI                 = "VirtualTopic.$EDC.{0}.{1}.>";
	private final static String ACL_DATA_ACC                     = "VirtualTopic.{0}.>";
	private final static String ACL_DATA_ACC_CLI                 = "VirtualTopic.{0}.{1}.>";
	private final static String ACL_CTRL_ACC_NOTIFY              = "VirtualTopic.$EDC.{0}.*.*.NOTIFY.{1}.>";
	
	private final static String[] ACL_VT_DURABLE_PREFIX          = new String[]{
    											"Consumer.{0}:AT_LEAST_ONCE.{1}",
    											"Consumer.{0}:EXACTLY_ONCE.{1}"};

	// full client id, with account prepended
	public final static String MULTI_ACCOUNT_CLIENT_ID = "{0}:{1}";
    
    //rate limiter section
    //TODO read these parameters from the configuration
    private final static long                      UPDATER_RELOAD_PARAMETER_FREQUENCY  = 5 * 60 * 1000;
    private static double                          connectionRateLimit                 = 10;
    private static long                            connectionWaitForToken              = 15000;
    private final static RateLimiter               CONNECTION_RATE_LIMITER             = RateLimiter.create(connectionRateLimit);
    private Runnable                               configurationUpdaterTask;
    
    private final static String                    PERMISSION_LOG                      = "{0}/{1}/{2} - {3}";

    private static final int                       BROKER_CONNECT_IDX                  = 0;
    private static final int                       DEVICE_MANAGE_IDX                   = 1;
    private static final int                       DATA_VIEW_IDX                       = 2;
    private static final int                       DATA_MANAGE_IDX                     = 3;

    private final static Map<String, ConnectionId> connectionMap                       = new ConcurrentHashMap<String, ConnectionId>();

    //metrics section
    private MetricsService                         metricsService;
    //login
    private Counter                                metricLoginSuccess;
    private Counter                                metricLoginRateLimited;
    private Counter                                metricLoginFailure;
    private Counter                                metricLoginInvalidUserPassword;
    private Counter                                metricLoginInvalidClientId;
    private Counter                                metricLoginKapuasysTokenAttempt;
    private Counter                                metricLoginNormalUserAttempt;
    private Counter                                metricLoginStealingLinkConnect;
    private Counter                                metricLoginStealingLinkDisconnect;
    private Timer                                  metricLoginAddConnectionTime;
    private Timer                                  metricLoginNormalUserTime;
    private Timer                                  metricLoginPreCheckTime;
    private Timer                                  metricLoginShiroLoginTime;
    private Timer                                  metricLoginCheckAccessTime;
    private Timer                                  metricLoginFindClientIdTime;
    private Timer                                  metricLoginFindDevTime;
    private Timer                                  metricLoginShiroLogoutTime;
    private Timer                                  metricLoginSendLoginUpdateMsgTime;
    private Timer                                  metricLoginRemoveConnectionTime;
    //publish/subscibe
    private Counter                                metricPublishAllowedMessages;
    private Counter                                metricPublishNotAllowedMessages;
    private Timer                                  metricPublishTime;
    private Counter                                subscribeAllowedMessages;
    private Counter                                subscribeNotAllowedMessages;
    private Timer                                  subscribeTime;
    //clients
    private Counter                                metricClientConnectedClient;
    private Counter                                metricClientConnectedKapuasys;
    private Counter                                metricClientDisconnectionClient;
    private Counter                                metricClientDisconnectionKapuasys;
    //mesage size
    private Histogram                              metricPublishMessageSizeAllowed;
    private Histogram                              metricPublishMessageSizeNotAllowed;

    public KapuaSecurityBrokerFilter(Broker next) throws KapuaException
    {
        super(next);

    	// Initialize the metrics
        metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
        //login
        metricLoginSuccess              = metricsService.getCounter("security", "login", "success", "count");
        metricLoginRateLimited          = metricsService.getCounter("security", "login", "rate_limit", "count");
        metricLoginFailure              = metricsService.getCounter("security", "login", "failure", "count");
        metricLoginInvalidUserPassword  = metricsService.getCounter("security", "login", "failure_password", "count");
        metricLoginInvalidClientId      = metricsService.getCounter("security", "login", "failure_client_id", "count");
        metricLoginKapuasysTokenAttempt   = metricsService.getCounter("security", "login", "kapuasys", "count");
        metricLoginNormalUserAttempt    = metricsService.getCounter("security", "login", "normal", "count");
        metricLoginStealingLinkConnect  = metricsService.getCounter("security", "login", "stealing_link", "connect", "count");
        metricLoginStealingLinkDisconnect = metricsService.getCounter("security", "login", "stealing_link", "disconnect", "count");
        //login time
        metricLoginAddConnectionTime    = metricsService.getTimer("security", "login", "add_connection", "time", "s");
        metricLoginNormalUserTime       = metricsService.getTimer("security", "login", "user", "time", "s");
        metricLoginPreCheckTime         = metricsService.getTimer("security", "login", "pre_check", "time", "s");
        metricLoginShiroLoginTime       = metricsService.getTimer("security", "login", "shiro", "login", "time", "s");
        metricLoginCheckAccessTime      = metricsService.getTimer("security", "login", "check_access", "time", "s");
        metricLoginFindClientIdTime     = metricsService.getTimer("security", "login", "find_client_id", "time", "s");
        metricLoginFindDevTime          = metricsService.getTimer("security", "login", "find_device", "time", "s");
        metricLoginShiroLogoutTime      = metricsService.getTimer("security", "login", "shiro", "logout", "time", "s");
        metricLoginSendLoginUpdateMsgTime = metricsService.getTimer("security", "login", "send_login_update", "time", "s");
        metricLoginRemoveConnectionTime = metricsService.getTimer("security", "login", "remove_connection", "time", "s");
        //publish/subscribe
        metricPublishAllowedMessages      = metricsService.getCounter("security", "publish", "allowed", "count");
        metricPublishNotAllowedMessages   = metricsService.getCounter("security", "publish", "not_allowed", "count");
        metricPublishTime                 = metricsService.getTimer("security", "publish", "time", "s");
        subscribeAllowedMessages          = metricsService.getCounter("security", "subscribe", "allowed", "count");
        subscribeNotAllowedMessages       = metricsService.getCounter("security", "subscribe", "not_allowed", "count");
        subscribeTime                     = metricsService.getTimer("security", "subscribe", "time", "s");
        //clients connected
        metricClientConnectedClient       = metricsService.getCounter("security", "login", "clients", "connected", "count");
        metricClientConnectedKapuasys       = metricsService.getCounter("security", "login", "kapuasys", "connected", "count");
        metricClientDisconnectionClient   = metricsService.getCounter("security", "login", "clients", "disconnected", "count");
        metricClientDisconnectionKapuasys   = metricsService.getCounter("security", "login", "kapuasys", "disconnected", "count");
        //message size
        metricPublishMessageSizeAllowed   = metricsService.getHistogram("security", "publish", "messages", "allowed", "size", "bytes");
        metricPublishMessageSizeNotAllowed = metricsService.getHistogram("security", "publish", "messages", "not_allowed", "size", "bytes");
    
        configurationUpdaterTask = new Runnable() {
			@Override
			public void run() {
			connectionRateLimit = 10;
			connectionWaitForToken = 15000;
			
			logger.info("Configuration updater task: values loaded: ");
			logger.info(" connection rate {} - wait for token {}", new Object[]{connectionRateLimit, connectionWaitForToken});
			}
		};
		this.getBrokerService().getScheduler().executePeriodically(configurationUpdaterTask, UPDATER_RELOAD_PARAMETER_FREQUENCY);

    }

    @Override
    public void start()
        throws Exception
    {
        super.start();
    }

    @Override
    public void stop()
        throws Exception
    {
    	logger.info(">>> Security broker filter: calling stop...");
    	if (configurationUpdaterTask != null) {
    		logger.info(">>> Security broker filter: stopping configuration updater...");
    		try {
    			this.getBrokerService().getScheduler().cancel(configurationUpdaterTask);
    		}
    		catch (Exception letsNotStopStop) {
    			logger.warn(">>> Security broker filter: Failed to cancel configuration updater task", letsNotStopStop);
    		}
    	}
        super.stop();
    }

    // ------------------------------------------------------------------
    // Connections
    // ------------------------------------------------------------------

    /**
     * Check if the connection is pass through.
     * Pass through connection is a connection with null connector (Advisory topic connection) or embedded broker connection (connection string starts by vm://)
     * 
     * @param context
     * @return
     */
    private boolean isPassThroughConnection(ConnectionContext context)
    {
        if (context != null) {
            if (context.getConnector() == null || CONNECTOR_NAME_VM.equals(((TransportConnector) context.getConnector()).getName())) {
                return true;
            }

            // network connector
            if (context.getConnection().isNetworkConnection()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if security context is broker context
     * Return true if security context is a broker context or if is a pass through connection
     * False if connection context is null or if security context is null and the connection context is not a pass through connection
     * Return true if
     * 
     * @param context
     * @return
     */
    private boolean isBrokerContext(ConnectionContext context)
    {
        if (context == null)
        {
            return false;
        }
        else if (context.getSecurityContext() != null)
        {
            return context.getSecurityContext().isBrokerContext();
        }
        else
        {
            return isPassThroughConnection(context);
        }
    }

    /**
     * Add connection.
     * If connection is not a pass through connection check username/password credential and device limits and then register the connection into kapua environment
     * 
     * Return error code is compliant to fix ENTMQ-731
     * Extract of MQTTProtocolConverter.java
     * 
     * if (exception instanceof InvalidClientIDException) {
     * ack.code(CONNACK.Code.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
     * }
     * else if (exception instanceof SecurityException) {
     * ack.code(CONNACK.Code.CONNECTION_REFUSED_NOT_AUTHORIZED);
     * }
     * else if (exception instanceof CredentialException) {
     * ack.code(CONNACK.Code.CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD);
     * }
     * else {
     * ack.code(CONNACK.Code.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
     * }
     * 
     */
    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info)
        throws Exception
    {
    	if (!isPassThroughConnection(context)) {
    		if (CONNECTION_RATE_LIMITER.tryAcquire(connectionWaitForToken, TimeUnit.MILLISECONDS)) {
    			addExternalConnection(context, info);
    			metricLoginSuccess.inc();
    		}
    		else {
    			metricLoginRateLimited.inc();
    			throw new KapuaConnectionRateLimitExceededException(info.getClientId(), info.getUserName(), connectionRateLimit);
    		}
        }
        super.addConnection(context, info);
    }

    public void addExternalConnection(ConnectionContext context, ConnectionInfo info)
        throws Exception
    {
    	Context loginTotalContext = metricLoginAddConnectionTime.time();
    	
        Subject currentSubject = (new Subject.Builder()).buildSubject();
        ThreadState threadState = new SubjectThreadState(currentSubject);
        threadState.bind();

        String username = info.getUserName();
        String password = info.getPassword();
        String clientId = info.getClientId();
        String clientIp = info.getClientIp();
        ConnectionId connectionId = info.getConnectionId();

        AuthorizationService authorizationService = KapuaLocator.getInstance().getService(AuthorizationService.class);
        AuthenticationService authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);

        List<String> authDestinations = null;
        if (logger.isDebugEnabled()) {
            authDestinations = new ArrayList<String>();
        }
        try {
            // Build KapuaUsername
//            User username = User.parse(username);//KapuaUserName

        	AccountService accountService = KapuaLocator.getInstance().getService(AccountService.class);
        	UserService userService = KapuaLocator.getInstance().getService(UserService.class);

            logger.info("User name {} - client id {}", new Object[]{username, clientId});

            Context loginPreCheckTimeContext = metricLoginPreCheckTime.time();
            // 1) validate client id
            // Check the device Mqtt ClientId
            
            //TODO move to deviceservice
//            MqttUtils.checkDeviceClientId(clientId);
            loginPreCheckTimeContext.stop();

            Context loginShiroLoginTimeContext = metricLoginShiroLoginTime.time();
            UsernamePasswordTokenFactory credentialsFactory = KapuaLocator.getInstance().getFactory(UsernamePasswordTokenFactory.class);
            AuthenticationCredentials credentials = credentialsFactory.newInstance(username, password.toCharArray());
            AccessToken accessToken = authenticationService.login(credentials);

            KapuaId scopeId = new KapuaEid(new BigInteger("1"));//accessToken.getScopeId();
            KapuaId userId = new KapuaEid(new BigInteger("2"));//accessToken.getId();
            Account account = accountService.find(scopeId);
			
            String accountName = account.getName();
//            KapuaSession kapuaSession = authSrv.getCurrentSession();
//            User currentUser = kapuaSession.getUser();
            loginShiroLoginTimeContext.stop();

            // if a user acts as a child MOVED INSIDE KapuaAuthorizingRealm otherwise through REST API and console this @accountName won't work

            // get account id and name from kapua session methods that check for the run as
            //
//            String accountName = kapuaSession.getSessionAccountName();
//            long accountId = kapuaSession.getSessionAccountId();

            // multiple account stealing link fix
            String fullClientId = MessageFormat.format(MULTI_ACCOUNT_CLIENT_ID, scopeId.getId().longValue(), clientId);

            KapuaPrincipal principal = new KapuaPrincipal(username,
										            scopeId.getId().longValue(),
                                                    clientId,
                                                    clientIp,
                                                    connectionId);
            DeviceConnection deviceConnection = null;
            // 3) check authorization
            DefaultAuthorizationMap authMap = null;
            if (isAdminUser(username)) {
            	metricLoginKapuasysTokenAttempt.inc();
                // 3-1) admin authMap
                authMap = buildEurotechAdminAuthMap(authDestinations, principal, fullClientId);
                metricClientConnectedKapuasys.inc();
            }
            else {
                	Context loginNormalUserTimeContext = metricLoginNormalUserTime.time();
                	metricLoginNormalUserAttempt.inc();
                    // 3-3) check permissions
                	PermissionFactory permissionFactory = KapuaLocator.getInstance().getFactory(PermissionFactory.class);
                	
                    Context loginCheckAccessTimeContext = metricLoginCheckAccessTime.time();
                    boolean[] hasPermissions = new boolean[]{
                    		authorizationService.isPermitted(permissionFactory.newInstance("broker", "connect", scopeId)),
                    		authorizationService.isPermitted(permissionFactory.newInstance("device", "manage", scopeId)),
                    		authorizationService.isPermitted(permissionFactory.newInstance("data", "read", scopeId)),
                    		authorizationService.isPermitted(permissionFactory.newInstance("data", "create", scopeId))
                    };
                    if (!hasPermissions[BROKER_CONNECT_IDX]) {
                        throw new KapuaIllegalAccessException(permissionFactory.newInstance("broker", "connect", scopeId).toString());
                    }
                    loginCheckAccessTimeContext.stop();

                    // 3-4) build authMap
                    authMap = buildAuthMap(authDestinations, principal, hasPermissions, accountName, clientId, fullClientId);

                    // 4) find device
                    Context loginFindClientIdTimeContext = metricLoginFindClientIdTime.time();
                    DeviceConnectionService deviceConnectionService = KapuaLocator.getInstance().getService(DeviceConnectionService.class);
                    deviceConnection = deviceConnectionService.findByClientId(scopeId, clientId);
                    loginFindClientIdTimeContext.stop();

                	Context loginFindDevTimeContext = metricLoginFindDevTime.time();
                	
                	// send connect message
                    ConnectionId previousConnectionId = connectionMap.get(fullClientId);
                    boolean stealingLinkDetected = (previousConnectionId != null);
                    // Update map for stealing link detection on disconnect
                    connectionMap.put(fullClientId, info.getConnectionId());
                    if (deviceConnection==null) {
                    	DeviceConnectionFactory deviceConnectionFactory = KapuaLocator.getInstance().getFactory(DeviceConnectionFactory.class);
                    	DeviceConnectionCreator deviceConnectionCreator = deviceConnectionFactory.newCreator(scopeId);
                    	deviceConnectionCreator.setClientId(clientId);
                    	deviceConnectionCreator.setClientIp(clientIp);
                    	deviceConnectionCreator.setProtocol("MQTT");
                    	deviceConnectionCreator.setServerIp(null);//TODO to be filled with the proper value
                    	deviceConnectionCreator.setUserId(userId);
                    	deviceConnection = deviceConnectionService.create(deviceConnectionCreator);
                    }
                    else {
                    	deviceConnection.setClientIp(clientIp);
                    	deviceConnection.setProtocol("MQTT");
                    	deviceConnection.setServerIp(null);//TODO to be filled with the proper value
                    	deviceConnection.setUserId(userId);
                    	deviceConnection.setStatus(DeviceConnectionStatus.CONNECTED);
                    	deviceConnectionService.update(deviceConnection);
                    	//TODO implement the banned status
//                    	if (DeviceStatus.DISABLED.equals(device.getStatus())) {
//                    		throw new KapuaIllegalAccessException("clientId - This client ID is disabled and cannot connect");
//                    	}
                    	//TODO manage the stealing link event (may be a good idea to use different connect status (connect -stealing)?
                    	if (stealingLinkDetected) {
                        	metricLoginStealingLinkConnect.inc();
                        	
                        	// stealing link detected, skip info
                        	logger.warn("Detected Stealing link for cliend id {} - account - last connection id was {} - current connection id is {} - IP: {} - No connection status changes!",
                        			new Object[] { clientId, accountName, previousConnectionId, info.getConnectionId(), info.getClientIp() });
                        }
                    }
                    loginFindDevTimeContext.stop();
                    
                    //TODO should be removed the connect grant for the admin user?
//                    // if user admin send alert
//                    if (currentUser.hasAdministratorRole()) {
//                        // send asynch alert
//                        JmsAssistantProducerPool pool = JmsAssistantProducerPool.getIOnstance(DESTINATIONS.EDC_SERVICE);
//                        JmsAssistantProducerWrapper producer = null;
//                        try {
//                            // to do refresh change user on connect value
//                            producer = pool.borrowObject();
//                            producer.sendAlertMessage(accountName, 
//                            		username, 
//                            		clientId, 
//                            		AlertCategories.SECURITY.name(), 
//                            		Severity.WARNING,
//                                    MessageFormat.format(AlertMessage.ADMIN_USER_DEVICE_ACCESS, new Object[] { clientId, username, kapuaSession.getSessionAccountName() }));
//                        }
//                        finally {
//                            pool.returnObject(producer);
//                        }
//                    }
                    loginNormalUserTimeContext.stop();
//                }
                // 6) update device info (also for to provision devices)
                Context loginSendLogingUpdateMsgTimeContex = metricLoginSendLoginUpdateMsgTime.time();

                loginSendLogingUpdateMsgTimeContex.stop();
                metricClientConnectedClient.inc();
            }
            logAuthDestinationToLog(authDestinations);

            KapuaSecurityContext securityCtx = new KapuaSecurityContext(principal, authMap, (deviceConnection!=null ? deviceConnection.getId() : null));
            context.setSecurityContext(securityCtx);

            // multiple account stealing link fix
            info.setClientId(fullClientId);
            context.setClientId(fullClientId);
        }
        catch (Exception e) {
        	metricLoginFailure.inc();
        	
        	
        	// fix ENTMQ-731
            if (e instanceof KapuaAuthenticationException) {
            	KapuaAuthenticationException kapuaException = (KapuaAuthenticationException) e;
            	KapuaErrorCode errorCode = kapuaException.getCode();
            	if (errorCode.equals(KapuaAuthenticationErrorCodes.INVALID_USERNAME) ||
            			errorCode.equals(KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS) ||
            			errorCode.equals(KapuaAuthenticationErrorCodes.INVALID_CREDENTIALS_TOKEN_PROVIDED)) {
            		logger.warn("Invalid username or password for user {} ({})", username, e.getMessage());
                    // A-MQ will map CredentialException into a CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD message (see javadoc on top of this method)
                    CredentialException ce = new CredentialException("Invalid username and/or password or disabled or expired account!");
                    ce.setStackTrace(e.getStackTrace());
                    metricLoginInvalidUserPassword.inc();
                    throw ce;
            	}
//            	else if (errorCode.equals(KapuaAuthenticationErrorCodes. ||
//            			errorCode.equals(KapuaAuthenticationErrorCodes. ||
//    					errorCode.equals(KapuaAuthenticationErrorCodes.) {
//    				logger.warn("Invalid client id for user {} - client id {} ({})", new Object[] { username, clientId, e.getMessage() });
//                    // A-MQ will map InvalidClientIDException into a CONNECTION_REFUSED_IDENTIFIER_REJECTED message (see javadoc on top of this method)
//                    InvalidClientIDException icid = new InvalidClientIDException("Connection refused or identifier rejected!");
//                    icid.setStackTrace(e.getStackTrace());
//                    metricLoginInvalidClientId.inc();
//                    throw icid;
//            	}
            	else if (errorCode.equals(KapuaAuthenticationErrorCodes.LOCKED_USERNAME) ||
            			errorCode.equals(KapuaAuthenticationErrorCodes.DISABLED_USERNAME) ||
    					errorCode.equals(KapuaAuthenticationErrorCodes.EXPIRED_CREDENTIALS)) {
					logger.warn("User {} not authorized ({})", username, e.getMessage());
	                // A-MQ will map SecurityException into a CONNECTION_REFUSED_NOT_AUTHORIZED message (see javadoc on top of this method)
	                SecurityException se = new SecurityException("User not authorized!");
	                se.setStackTrace(e.getStackTrace());
	                throw se;
            	}
            	
            }
            // Excluded CredentialException, InvalidClientIDException, SecurityException all others exceptions will be mapped by A-MQ to a CONNECTION_REFUSED_SERVER_UNAVAILABLE message (see
            // javadoc on top of this method)
            // Not trapped exception now:
            // KapuaException
            logger.info("@@ error", e);
            throw e;

            
//            // fix ENTMQ-731
//            if (e instanceof KapuaAuthenticationException) {
//                logger.warn("Invalid username or password for user {} ({})", username, e.getMessage());
//                // A-MQ will map CredentialException into a CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD message (see javadoc on top of this method)
//                CredentialException ce = new CredentialException("Invalid username and/or password or disabled or expired account!");
//                ce.setStackTrace(e.getStackTrace());
//                metricLoginInvalidUserPassword.inc();
//                throw ce;
//            }
//            else if (e instanceof KapuaInvalidDeviceClientIdException) {
//                logger.warn("Invalid client id for user {} - client id {} ({})", new Object[] { username, clientId, e.getMessage() });
//                // A-MQ will map InvalidClientIDException into a CONNECTION_REFUSED_IDENTIFIER_REJECTED message (see javadoc on top of this method)
//                InvalidClientIDException icid = new InvalidClientIDException("Connection refused or identifier rejected!");
//                icid.setStackTrace(e.getStackTrace());
//                metricLoginInvalidClientId.inc();
//                throw icid;
//            }
//            else if (e instanceof KapuaIllegalAccessException ||
//                     e instanceof KapuaLockedUserException ||
//                     e instanceof KapuaIllegalNullArgumentException ||
//                     e instanceof SecurityException ||
//                     e instanceof KapuaEntityNotFoundException ||
//                     e instanceof KapuaServicePlanLimitException) {
//                logger.warn("User {} not authorized ({})", username, e.getMessage());
//                // A-MQ will map SecurityException into a CONNECTION_REFUSED_NOT_AUTHORIZED message (see javadoc on top of this method)
//                SecurityException se = new SecurityException("User not authorized!");
//                se.setStackTrace(e.getStackTrace());
//                throw se;
//            }
//            else {
//                // Excluded CredentialException, InvalidClientIDException, SecurityException all others exceptions will be mapped by A-MQ to a CONNECTION_REFUSED_SERVER_UNAVAILABLE message (see
//                // javadoc on top of this method)
//                // Not trapped exception now:
//                // KapuaException
//                logger.info("@@ error", e);
//
//                throw e;
//            }
        }
        finally {
            // 7) logout
        	Context loginShiroLogoutTimeContext = metricLoginShiroLogoutTime.time();

        	authenticationService.logout();
        	// DON'T remove that. Fix shiro exceptions due to the reuse of subject on a heavy stress on amq broker
            ThreadContext.unbindSubject();
            
            loginShiroLogoutTimeContext.stop();
            loginTotalContext.stop();
        }
    }

    @Override
    public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error)
        throws Exception
    {
        if (!isPassThroughConnection(context)) {
        	Context loginRemoveConnectionTimeContext = metricLoginRemoveConnectionTime.time();
        	try {
        		
        		Subject currentSubject = (new Subject.Builder()).buildSubject();
                ThreadState threadState = new SubjectThreadState(currentSubject);
                threadState.bind();
                
                
        		KapuaSecurityContext kapuaSecurityContext = getKapuaSecurityContext(context);
        		
        		KapuaPrincipal kapuaPrincipal = ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal());
        		String clientId = kapuaPrincipal.getClientId();
        		long accountId = kapuaPrincipal.getAccountId();
        		String username = kapuaSecurityContext.getUserName();
        		String remoteAddress = (context.getConnection() != null) ? context.getConnection().getRemoteAddress() : "";

        		AuthenticationService authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);
        		UsernamePasswordTokenFactory credentialsFactory = KapuaLocator.getInstance().getFactory(UsernamePasswordTokenFactory.class);
        		AuthenticationCredentials credentials = credentialsFactory.newInstance("kapua-broker", "We!come12345".toCharArray());
        		AccessToken accessToken = authenticationService.login(credentials);
        		KapuaId scopeId = new KapuaEid(new BigInteger("1"));//accessToken.getScopeId();
                KapuaId userId = new KapuaEid(new BigInteger("2"));//accessToken.getId();
        		
        		// multiple account stealing link fix
        		String fullClientId = MessageFormat.format(MULTI_ACCOUNT_CLIENT_ID, accountId, clientId);
        		
        		if (!isAdminUser(username)) {
        			// Stealing link check
        			ConnectionId connectionId = connectionMap.get(fullClientId);
        			
        			boolean stealingLinkDetected = false;
        			if (connectionId != null) {
        				stealingLinkDetected = !connectionId.equals(info.getConnectionId());
        			}
        			else {
        				logger.error("Cannot find connection id for client id {} on connection map. Currect connection id is {} - IP: {}",
        						new Object[] { clientId, info.getConnectionId(), info.getClientIp() });
        			}
        			if (stealingLinkDetected) {
        				metricLoginStealingLinkDisconnect.inc();
        				
        				// stealing link detected, skip info
        				logger.warn("Detected Stealing link for cliend id {} - account id {} - last connection id was {} - current connection id is {} - IP: {} - No disconnection info will be added!",
        						new Object[] { clientId, accountId, connectionId, info.getConnectionId(), info.getClientIp() });
        			}
        			else {
        				KapuaId deviceConnectionId = kapuaSecurityContext.getConnectionId();
        				DeviceConnectionService deviceConnectionService = KapuaLocator.getInstance().getService(DeviceConnectionService.class);
        				DeviceConnection deviceConnection = deviceConnectionService.findByClientId(scopeId, clientId);
        				//the device connection must be not null
        				
        				// cleanup stealing link detection map
        				connectionMap.remove(fullClientId);
						if (error == null) {
							// update device connection
							deviceConnection.setStatus(DeviceConnectionStatus.DISCONNECTED);
	        				deviceConnectionService.update(deviceConnection);
						}
						else {
							// send missing message
							// update device connection
							deviceConnection.setStatus(DeviceConnectionStatus.MISSING);
	        				deviceConnectionService.update(deviceConnection);
						}
        			}
        			metricClientDisconnectionClient.inc();
        		}
        		else {
        			metricClientDisconnectionKapuasys.inc();
        		}
        		// multiple account stealing link fix
        		info.setClientId(fullClientId);
        		context.setClientId(fullClientId);
        	}
        	finally {
        		loginRemoveConnectionTimeContext.stop();
        		
        		AuthenticationService authenticationService = KapuaLocator.getInstance().getService(AuthenticationService.class);
        		authenticationService.logout();
            	// DON'T remove that. Fix shiro exceptions due to the reuse of subject on a heavy stress on amq broker
                ThreadContext.unbindSubject();
                
        	}
        }
        super.removeConnection(context, info, error);
        context.setSecurityContext(null);
    }

    // ------------------------------------------------------------------
    //
    // Destinations
    //
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    //
    // Producer
    //
    // ------------------------------------------------------------------

    @Override
    public void send(ProducerBrokerExchange producerExchange, Message messageSend)
        throws Exception
    {
		Context sendTimeContext = metricPublishTime.time();
		try {
			_send(producerExchange, messageSend);
		}
		finally {
			sendTimeContext.stop();
		}
    }
    
    public void _send(ProducerBrokerExchange producerExchange, Message messageSend)
        throws Exception
    {
        if (!isBrokerContext(producerExchange.getConnectionContext()))
        {
            KapuaSecurityContext kapuaSecurityContext = getKapuaSecurityContext(producerExchange.getConnectionContext());
            // if (!kapuaSecurityContext.getAuthorizedWriteDests().contains(messageSend.getDestination()))
            // {
            if (!messageSend.getDestination().isTemporary())
            {
                Set<?> allowedACLs = kapuaSecurityContext.getAuthorizationMap().getWriteACLs(messageSend.getDestination());
                if (allowedACLs != null && !kapuaSecurityContext.isInOneOf(allowedACLs)) {
                    String message = MessageFormat.format("User {0} ({1} - {2} - conn id {3}) is not authorized to write to: {4}",
                                                          kapuaSecurityContext.getUserName(),
                                                          ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal()).getClientId(),
                                                          ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal()).getClientIp(),
                                                          ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal()).getConnectionId(),
                                                          messageSend.getDestination());
                    logger.warn(message);
                    metricPublishMessageSizeNotAllowed.update(messageSend.getSize());
                    metricPublishNotAllowedMessages.inc();
                    // IMPORTANT
                    // restored the throw exception because otherwise we got acl's issues (Dejan said that in his opinion we can just return (without super call) in order to block the send operation
                    // but from my test is not true
                    // so we maintain the throw exception code
                    // to prevent ACLs bug on retained message send 01376647
                    // (retained messages sent to not allowed ACLs are received by a client if it subscribes that topic after message send operation)
                    throw new SecurityException(message);
                }
                // kapuaSecurityContext.getAuthorizedWriteDests().put(messageSend.getDestination(), messageSend.getDestination());
            }
            // }
            messageSend.setProperty(HEADER_KAPUA_CONNECTION_ID, kapuaSecurityContext.getConnectionId());
        }
        if (messageSend.getContent()!=null) {
        	metricPublishMessageSizeAllowed.update(messageSend.getContent().length);
        }
        else {
        	logger.warn("Cannot update message size metric. Incoming message for topic {} has null content!", messageSend.getDestination());
        }
        metricPublishAllowedMessages.inc();
        super.send(producerExchange, messageSend);
    }

    // ------------------------------------------------------------------
    //
    // Consumer
    //
    // ------------------------------------------------------------------
    
    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info)
        throws Exception
    {
		Context subscribeTimeContext = subscribeTime.time();
		try {
			return _addConsumer(context, info);
		}
		finally {
			subscribeTimeContext.stop();
		}
    }

    public Subscription _addConsumer(ConnectionContext context, ConsumerInfo info)
        throws Exception
    {
        info.setClientId(context.getClientId());
        if (!isBrokerContext(context))
        {
            String[] destinationsPath = info.getDestination().getDestinationPaths();
            String destination = info.getDestination().getPhysicalName();
            KapuaSecurityContext kapuaSecurityContext = getKapuaSecurityContext(context);
            if (destinationsPath != null && destinationsPath.length >= 2 && destinationsPath[0].equals(VT_CONSUMER_PREFIX)) {
                StringBuilder sb = new StringBuilder();
                sb.append(destination.substring(0, destinationsPath[0].length() + 1));
                sb.append(context.getClientId());
                if (destinationsPath[1].endsWith(":EXACTLY_ONCE")) {
                    sb.append(":EXACTLY_ONCE");
                }
                else if (destinationsPath[1].endsWith(":AT_LEAST_ONCE")) {
                    sb.append(":AT_LEAST_ONCE");
                }
                else {
                    throw new SecurityException(
                                                MessageFormat.format("Wrong suscription path attempts for client {0} - destination {1}", context.getClientId(), info.getDestination().getPhysicalName()));
                }
                sb.append(destination.substring(destinationsPath[0].length() + destinationsPath[1].length() + 1));
                destination = sb.toString();
            }
            info.getDestination().setPhysicalName(destination);
            // if (!kapuaSecurityContext.getAuthorizedReadDests().contains(info.getDestination()))
            // {
            if (!info.getDestination().isTemporary())
            {
                Set<?> allowedACLs = null;
                allowedACLs = kapuaSecurityContext.getAuthorizationMap().getReadACLs(info.getDestination());
                if (allowedACLs != null && !kapuaSecurityContext.isInOneOf(allowedACLs)) {
                    String message = MessageFormat.format("User {0} ({1} - {2} - conn id {3}) is not authorized to read from: {4}",
                                                          kapuaSecurityContext.getUserName(),
                                                          ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal()).getClientId(),
                                                          ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal()).getClientIp(),
                                                          ((KapuaPrincipal) kapuaSecurityContext.getMainPrincipal()).getConnectionId(),
                                                          info.getDestination());
                    logger.warn(message);
                    subscribeNotAllowedMessages.inc();
                    // IMPORTANT
                    // restored the throw exception because otherwise we got acl's issues (Dejan said that in his opinion we can just return (without super call) in order to block the send operation
                    // but from my test is not true
                    // so we maintain the throw exception code
                    // to prevent ACLs bug on # subscribe allowed regardless ACLs 01374321
                    throw new SecurityException(message);
                }
                // kapuaSecurityContext.getAuthorizedReadDests().put(info.getDestination(), info.getDestination());
            }
            // }
        }
        subscribeAllowedMessages.inc();
        return super.addConsumer(context, info);
    }

    // ------------------------------------------------------------------
    //
    // Protected
    //
    // ------------------------------------------------------------------

    protected KapuaSecurityContext getKapuaSecurityContext(ConnectionContext context)
        throws SecurityException
    {
        SecurityContext securityContext = context.getSecurityContext();
        if (securityContext == null || !(securityContext instanceof KapuaSecurityContext)) {
            throw new SecurityException("Invalid SecurityContext.");
        }

        return (KapuaSecurityContext) securityContext;
    }

    // ------------------------------------------------------------------
    //
    // Private
    //
    // ------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    private DefaultAuthorizationMap buildEurotechAdminAuthMap(List<String> authDestinations, KapuaPrincipal principal, String fullClientId)
    {
        ArrayList<DestinationMapEntry> dme = new ArrayList<DestinationMapEntry>();
        String clientId = principal.getClientId();
        dme.addAll(createAuthorizationEntries(authDestinations, ACL_HASH,
                                              principal, clientId, fullClientId, true, true, true));// (topic, principal, read, write, admin)
        dme.addAll(createAuthorizationEntries(authDestinations, ACL_AMQ_ADVISORY,
                                              principal, clientId, fullClientId, false, true, true));// (topic, principal, read, write, admin)
        return new DefaultAuthorizationMap(dme);
    }

    @SuppressWarnings("rawtypes")
    private DefaultAuthorizationMap buildProvisioningAuthMap(List<String> authDestinations, KapuaPrincipal principal, String accountName, String fullClientId)
    {
        ArrayList<DestinationMapEntry> dme = new ArrayList<DestinationMapEntry>();

        String clientId = principal.getClientId();
        // Write reply to any client Id and any application
        dme.addAll(createAuthorizationEntries(authDestinations, MessageFormat.format(ACL_CTRL_ACC_REPLY, accountName),
                                              principal, clientId, fullClientId, false, true, false));// (topic, principal, read, write, admin)

        // Publish only on MQTT/# for life-cycle messages and message that will trigger the provision service
        dme.addAll(createAuthorizationEntries(authDestinations, MessageFormat.format(ACL_CTRL_ACC_CLI_MQTT_LIFE_CYCLE, accountName, clientId),
                                              principal, clientId, fullClientId, false, true, false));// (topic, principal, read, write, admin)

        // Read any control topic on its client id to be able to receive configurations and bundles
        dme.addAll(createAuthorizationEntries(authDestinations, MessageFormat.format(ACL_CTRL_ACC_CLI, accountName, clientId),
                                              principal, clientId, fullClientId, true, false, false));// (topic, principal, read, write, admin)

        // FIXME: check if is correct "$EDC.{0}.>" instead of ">"
        dme.addAll(createAuthorizationEntries(authDestinations, MessageFormat.format(ACL_CTRL_ACC, accountName),
                                              principal, clientId, fullClientId, false, false, true));// (topic, principal, read, write, admin)

        dme.addAll(createAuthorizationEntries(authDestinations, ACL_AMQ_ADVISORY,
                                              principal, clientId, fullClientId, false, true, true));// (topic, principal, read, write, admin)

        return new DefaultAuthorizationMap(dme);
    }

    @SuppressWarnings("rawtypes")
    private DefaultAuthorizationMap buildAuthMap(List<String> authDestinations, KapuaPrincipal principal,
                                                 boolean[] hasPermissions,
                                                 String accountName,
                                                 String clientId,
                                                 String fullClientId)
    {
        ArrayList<DestinationMapEntry> dme = new ArrayList<DestinationMapEntry>();

        dme.addAll(createAuthorizationEntries(authDestinations, ACL_AMQ_ADVISORY,
                                              principal, clientId, fullClientId, false, true, true));// (topic, principal, read, write, admin)

        // addConnection checks BROKER_CONNECT_IDX permission before call this method
        // then here user has BROKER_CONNECT_IDX permission and if check isn't needed
        // if (hasPermissions[BROKER_CONNECT_IDX]) {
        if (hasPermissions[DEVICE_MANAGE_IDX]) {
            dme.addAll(createAuthorizationEntries(authDestinations,
                                                  MessageFormat.format(ACL_CTRL_ACC, accountName),
                                                  principal,
                                                  clientId,
                                                  fullClientId,
                                                  true,
                                                  true,
                                                  true)); // (topic, principal, read, write, admin)
        }
        else {
            dme.addAll(createAuthorizationEntries(authDestinations,
                                                  MessageFormat.format(ACL_CTRL_ACC_CLI, accountName, clientId),
                                                  principal,
                                                  clientId,
                                                  fullClientId,
                                                  true,
                                                  true,
                                                  true)); // (topic, principal, read, write, admin)
        }

        if (hasPermissions[DATA_MANAGE_IDX]) {
            dme.addAll(createAuthorizationEntries(authDestinations,
                                                  MessageFormat.format(ACL_DATA_ACC, accountName),
                                                  principal,
                                                  clientId,
                                                  fullClientId,
                                                  true,
                                                  true,
                                                  true)); // (topic, principal, read, write, admin)
        }
        else if (hasPermissions[DATA_VIEW_IDX]) {
            dme.addAll(createAuthorizationEntries(authDestinations,
                                                  MessageFormat.format(ACL_DATA_ACC, accountName),
                                                  principal,
                                                  clientId,
                                                  fullClientId,
                                                  true,
                                                  false,
                                                  true)); // (topic, principal, read, write, admin)

            dme.addAll(createAuthorizationEntries(authDestinations,
                                                  MessageFormat.format(ACL_DATA_ACC_CLI, accountName, clientId),
                                                  principal,
                                                  clientId,
                                                  fullClientId,
                                                  false,
                                                  true,
                                                  false)); // (topic, principal, read, write, admin)
        }
        else {
            dme.addAll(createAuthorizationEntries(authDestinations,
                                                  MessageFormat.format(ACL_DATA_ACC_CLI, accountName, clientId),
                                                  principal,
                                                  clientId,
                                                  fullClientId,
                                                  true,
                                                  true,
                                                  true)); // (topic, principal, read, write, admin)
        }

        dme.addAll(createAuthorizationEntries(authDestinations,
                                              MessageFormat.format(ACL_CTRL_ACC_REPLY, accountName),
                                              principal,
                                              clientId,
                                              fullClientId,
                                              false,
                                              true,
                                              true)); // (topic, principal, read, write, admin)
        
        // Write notify to any client Id and any application and operation
        dme.addAll(createAuthorizationEntries(authDestinations,
                                              MessageFormat.format(ACL_CTRL_ACC_NOTIFY, accountName, clientId),
                                              principal,
                                              clientId,
                                              fullClientId,
                                              false,
                                              true,
                                              false));// (topic, principal, read, write, admin)

        return new DefaultAuthorizationMap(dme);
    }

    protected List<AuthorizationEntry> createAuthorizationEntries(List<String> authDestinations, String topic, KapuaPrincipal principal, String clientId, String fullClientId, boolean read,
                                                                  boolean write, boolean admin)
    {
        List<AuthorizationEntry> entries = new ArrayList<AuthorizationEntry>();
        entries.add(createAuthorizationEntry(authDestinations, topic, principal, read, write, admin));
        // added to support the vt topic name space for durable subscriptions
        if (read) {
            // logger.info("pattern {} - clientid {} - topic {} - evaluated {}", new Object[]{JmsConstants.ACL_VT_DURABLE_PREFIX[0], clientId, topic,
            // MessageFormat.format(JmsConstants.ACL_VT_DURABLE_PREFIX[0], fullClientId, topic)});
            entries.add(createAuthorizationEntry(authDestinations, MessageFormat.format(ACL_VT_DURABLE_PREFIX[0], fullClientId, topic), principal));
            // logger.info("pattern {} - clientid {} - topic {} - evaluated {}", new Object[]{JmsConstants.ACL_VT_DURABLE_PREFIX[1], clientId, topic,
            // MessageFormat.format(JmsConstants.ACL_VT_DURABLE_PREFIX[1], fullClientId, topic)});
            entries.add(createAuthorizationEntry(authDestinations, MessageFormat.format(ACL_VT_DURABLE_PREFIX[1], fullClientId, topic), principal));
        }
        return entries;
    }

    protected AuthorizationEntry createAuthorizationEntry(List<String> authDestinations, String topic, KapuaPrincipal principal, boolean read, boolean write, boolean admin)
    {
        AuthorizationEntry entry = new AuthorizationEntry();
        entry.setDestination(ActiveMQDestination.createDestination(topic, ActiveMQDestination.TOPIC_TYPE));
        Set<Object> writeACLs = new HashSet<Object>();
        Set<Object> readACLs = new HashSet<Object>();
        Set<Object> adminACLs = new HashSet<Object>();
        if (read) {
            readACLs.add(principal);
        }
        if (write) {
            writeACLs.add(principal);
        }
        if (admin) {
            adminACLs.add(principal);
        }
        entry.setWriteACLs(writeACLs);
        entry.setReadACLs(readACLs);
        entry.setAdminACLs(adminACLs);
        addAuthDestinationToLog(authDestinations, MessageFormat.format(PERMISSION_LOG,
                                                                       read ? "r" : "_",
                                                                       write ? "w" : "_",
                                                                       admin ? "a" : "_",
                                                                       topic));
        return entry;

    }

    protected AuthorizationEntry createAuthorizationEntry(List<String> authDestinations, String topic, KapuaPrincipal principal)
    {
        AuthorizationEntry entry = new AuthorizationEntry();
        entry.setDestination(ActiveMQDestination.createDestination(topic, ActiveMQDestination.QUEUE_TYPE));
        Set<Object> writeACLs = new HashSet<Object>();
        Set<Object> readACLs = new HashSet<Object>();
        Set<Object> adminACLs = new HashSet<Object>();
        readACLs.add(principal);
        entry.setWriteACLs(writeACLs);
        entry.setReadACLs(readACLs);
        entry.setAdminACLs(adminACLs);
        addAuthDestinationToLog(authDestinations, MessageFormat.format(PERMISSION_LOG, "r", "_", "_", topic));
        return entry;

    }

    private void addAuthDestinationToLog(List<String> authDestinations, String message)
    {
        if (logger.isDebugEnabled()) {
            authDestinations.add(message);
        }
    }

    private void logAuthDestinationToLog(List<String> authDestinations)
    {
        if (authDestinations != null) {
            logger.debug("Authorization map:");
            for (String str : authDestinations) {
                logger.debug(str);
            }
        }
    }
    
    private boolean isAdminUser(String user) {
    	String adminAccount = KapuaEnvironmentConfig.getInstance().getString(KapuaEnvironmentConfigKeys.SYS_ADMIN_ACCOUNT);
    	return user.equals(adminAccount);
    }

//    private void checkCrossClusterLogin(long clientAccountId, String accountName)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNegative(clientAccountId, "clientAccountId");
//
//        //
//        // Check if cross broker login
//        AccountService accountService = KapuaLocator.getInstance().getService(AccountService.class);
//
//        AccountDeploymentPlan deploymentPlan = accountService.getAccountDeploymentPlanTrusted(clientAccountId);
//        if (deploymentPlan == null) {
//            throw new KapuaException(KapuaErrorCode.INTERNAL_ERROR, null, "Cannot find deployment plan for account with id: " + clientAccountId);
//        }
//
//        // If all data is retrieved, then check
//        if (s_clusterId != deploymentPlan.getBrokerCluster().getId()) {
//            if (clientAccountId != s_eurotechAccount.getId()) {
//                logger.error("Cross cluster login check FAILED for account {}", accountName);
//                throw new KapuaOperationNotAllowedException("Account not authorized for this cluster.");
//            }
//        }
//
//        logger.debug("Cross cluster login check PASSED for account {}", accountName);
//    }

}