package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.LocalServicePlan;

class AccountInfo 
{
    private Account account;
    private LocalServicePlan servicePlan;
    
    public AccountInfo(Account account, LocalServicePlan servicePlan) {
        this.account = account;
        this.servicePlan = servicePlan;
    }
    
    public Account getAccount() {
        return this.account;
    }
    
    public LocalServicePlan getServicePlan() {
        return this.servicePlan;
    }
}
