package org.eclipse.kapua.app.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.kapua.app.api.model.ErrorBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class KapuaResource 
{
    private static final Logger s_logger = LoggerFactory.getLogger(KapuaResource.class);

    /*
    private Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    private EdcSession getEdcSession() {
        return (EdcSession) getSubject().getSession().getAttribute(AuthorizationServiceBean.EDC_SESSION);
    }

    protected long getTargetAccountId() {
        return getEdcSession().getSessionAccountId();
    }

    protected String getTargetAccountName() {
        return getEdcSession().getSessionAccountName();
    }
    */
    
    protected <T> T returnNotNullEntity(T entity) {
        if (entity == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return entity;
    }

    protected void handleException(Throwable t) {

        WebApplicationException wae = null;
/*        
        if (t instanceof EdcUnauthenticatedException ||
                t instanceof EdcIllegalAccessException ||
                t instanceof EdcInvalidUsernamePasswordException) {
            wae = newWebApplicationException(t, Response.Status.FORBIDDEN);
        } else if (t instanceof EdcDuplicateNameException ||
                   t instanceof EdcIllegalArgumentException ||
                   t instanceof EdcIllegalNullArgumentException ||
                   t instanceof EdcLastAdminException ||
                   t instanceof EdcInvalidRuleQueryException ||
                   t instanceof EdcInvalidTopicException ||
                   t instanceof EdcInvalidMetricTypeException ||
                   t instanceof EdcInvalidMessageException) {
            wae = newWebApplicationException(t, Response.Status.BAD_REQUEST);
        } else if (t instanceof EdcEntityNotFoundException ||
                   t instanceof EdcNoResultsFoundException) {
            wae = newWebApplicationException(t, Response.Status.NOT_FOUND);
        } else if (t instanceof EdcOptimisticLockingException) {
            wae = newWebApplicationException(t, Response.Status.CONFLICT);
        } else {
*/
        	s_logger.error("Internal Error", t);
            wae = newWebApplicationException(t, Response.Status.INTERNAL_SERVER_ERROR);
//        }

        s_logger.debug("Error Processing Request", t);
        if (wae != null) {
            throw wae;
        }
    }
  
    protected WebApplicationException newWebApplicationException(Throwable t, Response.Status status) 
    {
        String message = t.getMessage();

//        EdcConfig edcConfig = EdcConfig.getInstance();
//        if (edcConfig.getApiPrintStackTrace()) {
//            StringWriter sw = new StringWriter();
//            t.printStackTrace(new PrintWriter(sw));
//            message = sw.toString();
//        }

        Response response = Response.status(status).entity(new ErrorBean(status, message)).build();
        return new WebApplicationException(response);
    }
    
    protected WebApplicationException newWebApplicationException(Response.Status status, String message) 
    {
        Response response = Response.status(status).entity(new ErrorBean(status, message)).build();
        return new WebApplicationException(response);
    }
}
