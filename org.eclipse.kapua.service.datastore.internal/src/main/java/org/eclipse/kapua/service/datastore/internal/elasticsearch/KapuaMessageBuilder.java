/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.client.message.KapuaInvalidTopicException;
import org.eclipse.kapua.client.message.KapuaMessage;
import org.eclipse.kapua.client.message.KapuaPayload;
import org.eclipse.kapua.client.message.KapuaPosition;
import org.eclipse.kapua.client.message.KapuaTopic;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

public class KapuaMessageBuilder
{

    private KapuaMessage kapuaMessage;

    public KapuaMessageBuilder build(SearchHit searchHit, MessageFetchStyle fetchStyle)
        throws KapuaInvalidTopicException, ParseException
    {

        String account = searchHit.getFields().get(EsSchema.MESSAGE_ACCOUNT).getValue();
        String asset = searchHit.getFields().get(EsSchema.MESSAGE_AS_NAME).getValue();
        String topic = searchHit.getFields().get(EsSchema.MESSAGE_SEM_TOPIC).getValue();
        KapuaTopic kapuaTopic = new KapuaTopic(account, asset, topic);

        KapuaMessage tmpKapuaMessage = new KapuaMessage();
        tmpKapuaMessage.setTopic(kapuaTopic.getFullTopic());

        SearchHitField timestampObj = searchHit.getFields().get(EsSchema.MESSAGE_TIMESTAMP);
        tmpKapuaMessage.setTimestamp((Date) (timestampObj == null ? null : EsUtils.convertToEdcObject("date", (String) timestampObj.getValue())));
        tmpKapuaMessage.setUuid(searchHit.getId());

        if (fetchStyle.equals(MessageFetchStyle.METADATA)) {
            this.kapuaMessage = tmpKapuaMessage;
            return this;
        }

        Map<String, Object> source = searchHit.getSource();

        KapuaPayload kapuaPayload = new KapuaPayload();
        KapuaPosition kapuaPosition = null;
        if (source.get(EsSchema.MESSAGE_POS) != null) {

            kapuaPosition = new KapuaPosition();
            Map<String, Object> position = (Map<String, Object>) source.get(EsSchema.MESSAGE_POS);

            Map<String, Object> location = (Map<String, Object>) position.get(EsSchema.MESSAGE_POS_LOCATION);
            if (location != null && location.get("lat") != null)
                kapuaPosition.setLatitude((double) location.get("lat"));

            if (location != null && location.get("lon") != null)
                kapuaPosition.setLatitude((double) location.get("lon"));

            Object obj = position.get(EsSchema.MESSAGE_POS_ALT);
            if (obj != null)
                kapuaPosition.setAltitude((double) obj);

            obj = position.get(EsSchema.MESSAGE_POS_HEADING);
            if (obj != null)
                kapuaPosition.setHeading((double) obj);

            obj = position.get(EsSchema.MESSAGE_POS_PRECISION);
            if (obj != null)
                kapuaPosition.setPrecision((double) obj);

            obj = position.get(EsSchema.MESSAGE_POS_SATELLITES);
            if (obj != null)
                kapuaPosition.setSatellites((int) obj);

            obj = position.get(EsSchema.MESSAGE_POS_SPEED);
            if (obj != null)
                kapuaPosition.setSpeed((double) obj);

            obj = position.get(EsSchema.MESSAGE_POS_STATUS);
            if (obj != null)
                kapuaPosition.setStatus((int) obj);

            obj = position.get(EsSchema.MESSAGE_POS_TIMESTAMP);
            if (obj != null)
                kapuaPosition.setTimestamp((Date) EsUtils.convertToEdcObject("date", (String) obj));
        }

        Object collectedOnFld = source.get(EsSchema.MESSAGE_COLLECTED_ON);
        if (collectedOnFld != null) {
            kapuaPayload.setTimestamp((Date) (collectedOnFld == null ? null : EsUtils.convertToEdcObject("date", (String) collectedOnFld)));
        }

        if (source.get(EsSchema.MESSAGE_MTR) != null) {

            Map<String, Object> metrics = (Map<String, Object>) source.get(EsSchema.MESSAGE_MTR);

            String[] metricNames = metrics.keySet().toArray(new String[] {});
            for (String metricsName : metricNames) {
                Map<String, Object> metricValue = (Map<String, Object>) metrics.get(metricsName);
                if (metricValue.size() > 0) {
                    String[] valueTypes = metricValue.keySet().toArray(new String[] {});
                    Object value = metricValue.get(valueTypes[0]);
                    if (value != null && value instanceof Integer)
                        kapuaPayload.addMetric(EsUtils.restoreMetricName(metricsName), value);
                }
            }
        }

        if (fetchStyle.equals(MessageFetchStyle.METADATA_HEADERS)) {
            this.kapuaMessage = tmpKapuaMessage;
        }

        if (source.get(EsSchema.MESSAGE_BODY) != null) {
            byte[] body = ((String) source.get(EsSchema.MESSAGE_BODY)).getBytes();
            kapuaPayload.setBody(body);
        }

        if (kapuaPayload != null)
            tmpKapuaMessage.setKapuaPayload(kapuaPayload);

        this.kapuaMessage = tmpKapuaMessage;
        return this;
    }

    public KapuaMessage getMessage()
    {
        return kapuaMessage;
    }
}
