package com.mvwsolutions.common.dao.hibernate;


/**
 * 
 * @author smineyev
 * 
 */
public abstract class UberTransactionSynchronization {

    private UberTransactionManager uberTransactionManager;

    public void init() {
        uberTransactionManager.registerTxSynchronization(this);
    }

    public UberTransactionManager getUberTransactionManager() {
        return uberTransactionManager;
    }

    public void setUberTransactionManager(
            UberTransactionManager uberTransactionManager) {
        this.uberTransactionManager = uberTransactionManager;
    }
    
    public abstract void afterTxStart();
    public abstract void afterTxCompletion(int txStatus);

}
