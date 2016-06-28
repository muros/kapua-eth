package org.eclipse.kapua.app.console.server.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.kapua.util.JpaUtils;

public class ConsoleListener implements ServletContextListener
{
    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        try {
            JpaUtils.cleanUpDataSource();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0)
    {
        // TODO Auto-generated method stub
    }
}