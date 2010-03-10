package com.mvwsolutions.common.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.TransactionException;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * 
 * @author smineyev
 * 
 */
public class UberTransactionManager extends HibernateTransactionManager {
    private static final long serialVersionUID = -2110416203349893509L;

    private List<UberTransactionSynchronization> txSynchronizations = new ArrayList<UberTransactionSynchronization>();

    public void registerTxSynchronization(
            UberTransactionSynchronization txSynchronization) {
        txSynchronizations.add(txSynchronization);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);
        for (UberTransactionSynchronization txSynchronization : txSynchronizations) {
            txSynchronization.afterTxStart();
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus defaulttransactionstatus)
            throws TransactionException {
        try {
            super.doCommit(defaulttransactionstatus);
        } catch (Exception e) {
            triggerAfterCompletion(defaulttransactionstatus,
                    TransactionSynchronization.STATUS_ROLLED_BACK);
            throw new RuntimeException(e);
        }
        triggerAfterCompletion(defaulttransactionstatus,
                TransactionSynchronization.STATUS_COMMITTED);
    }

    protected void doRollback(DefaultTransactionStatus defaulttransactionstatus) {
        try {
            super.doRollback(defaulttransactionstatus);
        } finally {
            triggerAfterCompletion(defaulttransactionstatus,
                    TransactionSynchronization.STATUS_ROLLED_BACK);
        }
    }

    private void triggerAfterCompletion(DefaultTransactionStatus status,
            int completionStatus) {
        if (status.isNewSynchronization()) {
            for (UberTransactionSynchronization txSynchronization : txSynchronizations) {
                txSynchronization.afterTxCompletion(completionStatus);
            }
        }
    }

}
