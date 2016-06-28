package org.eclipse.kapua.app.console.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.eurotech.cloud.message.EdcMessage;

public abstract class DataExporter {

    protected HttpServletResponse m_response;


    protected DataExporter(HttpServletResponse response) {
        m_response = response;
    }

    public abstract void init(String account,
                              String topic,
                              String asset,
                              Date startDate,
                              Date endDate,
                              String[] headers)
    throws ServletException, IOException;


    public abstract void append(List<EdcMessage> messages) throws ServletException, IOException;


    public abstract void close() throws ServletException, IOException;
}
