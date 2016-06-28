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
package org.eclipse.kapua.message;

/**
 * A container class for the values associated with an MQtt subscription.
 *
 * @author eurotech
 *
 */
public class KapuaSubscription {
    private int      m_qos;
    private KapuaTopic m_edcTopic;
    private String   m_semanticTopic;


    /**
     * The default constructor.
     */
    public KapuaSubscription() {
        m_qos           = 0;
        m_semanticTopic = null;
    }

    /**
     * A constructor method specifying the semantic topic as a String and QoS for the subscription.
     *
     * @param topic  A String object representing the semantic topic.
     * @param qos  An integer indicating the Quality of Service.
     */
    public KapuaSubscription(KapuaTopic edcTopic, int qos) {
        m_qos           = qos;
        m_edcTopic      = edcTopic;
        m_semanticTopic = m_edcTopic.getSemanticTopic();
    }

    /**
     * A constructor method specifying the semantic topic as a String and QoS for the subscription.
     *
     * @param topic  A String object representing the semantic topic.
     * @param qos  An integer indicating the Quality of Service.
     */
    public KapuaSubscription(String semanticTopic, int qos) {
        m_qos           = qos;
        m_semanticTopic = semanticTopic;
    }

    /**
     * Returns the EdcTopic.
     *
     * @return EdcTopic.
     */
    public KapuaTopic getEdcTopic() {
        return m_edcTopic;
    }

    /**
     * Returns the full topic.
     *
     * @return A String object containing the full topic.
     */
    public String getFullTopic() {
        if (m_edcTopic != null) {
            return m_edcTopic.getFullTopic();
        }
        return null;
    }

    /**
     * Returns the subscription topic.
     *
     * @return A String object containing the semantic topic.
     */
    public String getSemanticTopic() {
        return m_semanticTopic;
    }

    /**
     * Returns the Quality of Service on the semantic topic.
     *
     * @return An integer indicating the Quality of Service.
     */
    public int getQos() {
        return m_qos;
    }

    public boolean exactlyEquals(KapuaSubscription sub) {
        if(m_semanticTopic.compareTo(sub.getSemanticTopic()) == 0 && this.m_qos==sub.getQos()) {
            return true;
        }
        return false;
    }
}
