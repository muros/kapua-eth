package org.eclipse.kapua.app.console.shared.model;

import java.util.Date;

import org.eclipse.kapua.app.console.client.util.DateUtils;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GwtUser extends EdcBaseModel implements IsSerializable
{
    private static final long serialVersionUID = -3731370307878410611L;

    public enum GwtUserStatus implements IsSerializable
    {
        ENABLED,
        DISABLED;
        GwtUserStatus()
        {
        };
    }

    public GwtUser()
    {
    }

    public GwtUser(String username, String password)
    {
        this.setUsername(username);
        this.setPassword(password);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <X> X get(String property)
    {
        if ("statusEnum".equals(property)) {
            return (X) (GwtUserStatus.valueOf(getStatus()));
        }
        else if ("loginAttemptsResetOnFormatted".equals(property)) {
            return (X) (getLoginAttemptsResetOnFormatted());
        }
        else if ("lockedOnFormatted".equals(property)) {
            return (X) (getLockedOnFormatted());
        }
        else if ("unlockOnFormatted".equals(property)) {
            return (X) (getUnlockOnFormatted());
        }
        else {
            return super.get(property);
        }
    }

    public String getId()
    {
        return (String) get("id");
    }

    public void setId(String id)
    {
        set("id", id);
    }

    public String getScopeId()
    {
        return (String) get("scopeId");
    }

    public void setScopeId(String accountId)
    {
        set("scopeId", accountId);
    }

    public String getUsername()
    {
        return (String) get("username");
    }

    public String getUnescapedUsername()
    {
        return (String) getUnescaped("username");
    }

    public void setUsername(String username)
    {
        set("username", username);
    }

    public Date getCreatedOn()
    {
        return (Date) get("createdOn");
    }

    public void setCreatedOn(Date createdOn)
    {
        set("createdOn", createdOn);
    }

    public String getCreatedBy()
    {
        return (String) get("createdBy");
    }

    public void setCreatedBy(String createdBy)
    {
        set("createdBy", createdBy);
    }

    public Date getModifiedOn()
    {
        return (Date) get("modifiedOn");
    }

    public void setModifiedOn(Date modifiedOn)
    {
        set("modifiedOn", modifiedOn);
    }

    public String getModifiedBy()
    {
        return (String) get("modifiedBy");
    }

    public void setModifiedBy(String modifiedBy)
    {
        set("modifiedBy", modifiedBy);
    }

    public String getPassword()
    {
        return (String) get("password");
    }

    public String getUnescapedPassword()
    {
        return (String) getUnescaped("password");
    }

    public void setPassword(String password)
    {
        set("password", password);
    }

    public String getDisplayName()
    {
        return (String) get("displayName");
    }

    public String getUnescapedDisplayName()
    {
        return (String) getUnescaped("displayName");
    }

    public void setDisplayName(String displayName)
    {
        set("displayName", displayName);
    }

    public String getEmail()
    {
        return (String) get("email");
    }

    public String getUnescapedEmail()
    {
        return (String) getUnescaped("email");
    }

    public void setEmail(String email)
    {
        set("email", email);
    }

    public String getPhoneNumber()
    {
        return (String) get("phoneNumber");
    }

    public String getUnescapedPhoneNumber()
    {
        return (String) getUnescaped("phoneNumber");
    }

    public void setPhoneNumber(String phoneNumber)
    {
        set("phoneNumber", phoneNumber);
    }

    public String getStatus()
    {
        return (String) get("status");
    }

    public GwtUserStatus getStatusEnum()
    {
        return (GwtUserStatus) get("statusEnum");
    }

    public void setStatus(String status)
    {
        set("status", status);
    }

    public Date getLoginOn()
    {
        return (Date) get("loginOn");
    }

    public String getLoginOnFormatted()
    {
        return DateUtils.formatDateTime((Date) get("loginOn"));
    }

    public void setLoginOn(Date loginOn)
    {
        set("loginOn", loginOn);
    }

    public int getLoginAttempts()
    {
        return (Integer) get("loginAttempts");
    }

    public void setLoginAttempts(int loginAttempts)
    {
        set("loginAttempts", loginAttempts);
    }

    public Date getLoginAttemptsResetOn()
    {
        return (Date) get("loginAttemptsResetOn");
    }

    public String getLoginAttemptsResetOnFormatted()
    {
        return DateUtils.formatDateTime((Date) get("loginAttemptsResetOn"));
    }

    public void setLoginAttemptsResetOn(Date loginAttemptsResetOn)
    {
        set("loginAttemptsResetOn", loginAttemptsResetOn);
    }

    public boolean isLocked()
    {
        return (Boolean) get("isLocked");
    }

    public void setIsLocked(boolean isLocked)
    {
        set("isLocked", isLocked);
    }

    public Date getLockedOn()
    {
        return (Date) get("lockedOn");
    }

    public String getLockedOnFormatted()
    {
        return DateUtils.formatDateTime((Date) get("lockedOn"));
    }

    public void setLockedOn(Date lockedOn)
    {
        set("lockedOn", lockedOn);
    }

    public Date getUnlockOn()
    {
        return (Date) get("unlockOn");
    }

    public String getUnlockOnFormatted()
    {
        return DateUtils.formatDateTime((Date) get("unlockOn"));
    }

    public void setUnlockOn(Date unlockOn)
    {
        set("unlockOn", unlockOn);
    }

    public String getPermissions()
    {
        return (String) get("permissions");
    }

    public void setPermissions(String permissions)
    {
        set("permissions", permissions);
    }

    public String getSortedAccountPermissions()
    {
        return (String) get("sortedAccountPermissions");
    }

    public void setSortedAccountPermissions(String permissions)
    {
        set("sortedAccountPermissions", permissions);
    }

    public boolean isAdministrator()
    {
        return (Boolean) get("administrator");
    }

    public void setAdministrator(boolean administrator)
    {
        set("administrator", administrator);
    }

    public String toString()
    {
        return (String) get("username");
    }

    public void setAuthenticationKey(String s)
    {
        set("authenticationKey", s);
    }

    public void setOptlock(int optlock)
    {
        set("optlock", optlock);
    }

    public int getOptlock()
    {
        return (Integer) get("optlock");
    }
}
