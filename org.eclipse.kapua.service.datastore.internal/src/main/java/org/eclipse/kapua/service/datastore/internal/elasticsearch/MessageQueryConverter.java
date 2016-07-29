package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import org.eclipse.kapua.service.datastore.internal.model.query.AbstractStorableQueryConverter;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;

public class MessageQueryConverter extends AbstractStorableQueryConverter<Message, MessageQuery>
{
    @Override
    protected String[] getIncludes(MessageFetchStyle fetchStyle)
    {

        // Fetch mode
        String[] includeSource = null;

        switch (fetchStyle) {
            case METADATA:
                includeSource = new String[] { "" };
                break;
            case METADATA_HEADERS:
                includeSource = new String[] { EsSchema.MESSAGE_COLLECTED_ON, EsSchema.MESSAGE_POS + ".*", EsSchema.MESSAGE_MTR + ".*" };
                break;
            case METADATA_HEADERS_PAYLOAD:
                includeSource = new String[] { "*" };
        }

        return includeSource;
    }

    @Override
    protected String[] getExcludes(MessageFetchStyle fetchStyle)
    {

        // Fetch mode
        String[] excludeSource = null;

        switch (fetchStyle) {
            case METADATA:
                excludeSource = new String[] { "*" };
                break;
            case METADATA_HEADERS:
                excludeSource = new String[] { EsSchema.MESSAGE_BODY };
                break;
            case METADATA_HEADERS_PAYLOAD:
                excludeSource = new String[] { "" };
        }

        return excludeSource;
    }

    @Override
    protected String[] getFields()
    {
        return new String[] {EsMessageField.ACCOUNT.toString(),
                             EsMessageField.ASSET.toString(),
                             EsMessageField.SEMANTIC_TOPIC.toString(),
                             EsMessageField.TIMESTAMP.toString()};
    }
}
