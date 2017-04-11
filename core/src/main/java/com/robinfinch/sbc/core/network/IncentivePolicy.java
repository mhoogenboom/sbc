package com.robinfinch.sbc.core.network;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.ledger.Transaction;

public interface IncentivePolicy {

    boolean canIntroduceAsset(int ledgerSize)
            throws ConfigurationException;

    Transaction introduceAsset(String userId, long now)
            throws ConfigurationException;

    boolean verifyIntroductionTransaction(String userId, Transaction transaction)
            throws ConfigurationException;

    boolean verifyFee(int fee);

    boolean canChargeFees();

    Transaction chargeFees(String userId, int fees, long now)
            throws ConfigurationException;

    boolean verifyFeeTransaction(String userId, int fees, Transaction transaction)
            throws ConfigurationException;

    int getMaxBlockSize();
}
