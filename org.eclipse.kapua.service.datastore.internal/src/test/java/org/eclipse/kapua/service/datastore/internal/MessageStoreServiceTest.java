/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordTokenFactory;
import org.eclipse.kapua.service.datastore.DatastoreObjectFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.model.MessageCreator;
import org.eclipse.kapua.service.datastore.model.Payload;
import org.eclipse.kapua.service.datastore.model.Position;
import org.eclipse.kapua.service.datastore.model.StorableId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageStoreServiceTest
{
    @SuppressWarnings("unused")
    private static final Logger s_logger = LoggerFactory.getLogger(MessageStoreServiceTest.class);

    protected static Random       random  = new Random();
    protected static KapuaLocator locator = KapuaLocator.getInstance();

    protected static KapuaId      adminUserId;

    @BeforeClass
    public static void setUp()
    {
//        try {
//            //
//            // Login
//            String username = "kapua-sys";
//            String password = "We!come12345";
//
//            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
//            UsernamePasswordTokenFactory credentialsFactory = locator.getFactory(UsernamePasswordTokenFactory.class);
//            authenticationService.login(credentialsFactory.newInstance(username, password.toCharArray()));
//
//            //
//            // Get current user Id
//            adminUserId = KapuaSecurityUtils.getSession().getUserId();
//
//        }
//        catch (KapuaException exc) {
//            exc.printStackTrace();
//        }
    }

    @AfterClass
    public static void tearDown()
    {
//        try {
//            KapuaLocator locator = KapuaLocator.getInstance();
//            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
//
//            authenticationService.logout();
//        }
//        catch (KapuaException exc) {
//            exc.printStackTrace();
//        }
    }

    @Test
    public void testStore()
        throws Exception
    {
        final MessageStoreServiceTest reallyMe = this;

        KapuaSecurityUtils.doPriviledge(new Callable<Object>() {

            @Override
            public Object call()
                throws Exception
            {
                try {
                    // KapuaPeid accountPeid = KapuaEidGenerator.generate();//
                    KapuaId rootScopeId = new KapuaEid(BigInteger.valueOf(1));

                    KapuaLocator locator = KapuaLocator.getInstance();

                    long accountSerial = (new Date()).getTime();
                    AccountCreator accountCreator = reallyMe.getTestAccountCreator(rootScopeId, accountSerial);

                    AccountService accountService = locator.getService(AccountService.class);
                    Account account = accountService.create(accountCreator);
                    KapuaId scopeId = account.getId();
                    String scopeName = account.getName();

                    MessageStoreService messageStoreService = locator.getService(MessageStoreService.class);
                    DatastoreObjectFactory dsObjectFactory = locator.getFactory(DatastoreObjectFactory.class);

                    MessageCreator messageCreator = dsObjectFactory.newMessageCreator();
                    Payload messagePayload = dsObjectFactory.newPayload();
                    Position messagePosition = dsObjectFactory.newPosition();
                    Map<String, Object> metrics = new HashMap<String, Object>();

                    Date now = new Date();
                    messageCreator.setTimestamp(now);
                    messageCreator.setReceivedOn(now);

                    String topicName = String.format("%s/CLIENT001/APP01", scopeName);
                    messageCreator.setTopic(topicName);

                    metrics.put("metric_long", 1L);
                    metrics.put("metric_string", "pippo");
                    messagePayload.setMetrics(metrics);

                    messagePayload.setCollectedOn(now);

                    messagePosition.setAltitude(1.0);
                    messagePosition.setTimestamp(now);
                    messagePayload.setPosition(messagePosition);

                    messagePayload.setMetrics(metrics);
                    messageCreator.setPayload(messagePayload);

                    StorableId messageId = messageStoreService.store(scopeId, messageCreator);

                    //
                    // Message asserts
                    assertNotNull(messageId);
                    assertTrue(!messageId.toString().isEmpty());
                    return null;
                }
                catch (KapuaException e) {
                    throw new Exception(e);
                }
            }

        });
    }

    private AccountCreator getTestAccountCreator(KapuaId scopeId, long random)
    {
        KapuaLocator locator = KapuaLocator.getInstance();

        long accountSerial = (new Date()).getTime();
        String testAccount = String.format("test-%d", accountSerial);
        AccountFactory accountFactory = locator.getFactory(AccountFactory.class);
        AccountCreator accountCreator = accountFactory.newAccountCreator(scopeId, testAccount);

        accountCreator.setAccountPassword("!aA1234567890");
        accountCreator.setOrganizationName(testAccount);
        accountCreator.setOrganizationEmail(String.format("theuser@%s.com", testAccount));

        return accountCreator;
    }
}
