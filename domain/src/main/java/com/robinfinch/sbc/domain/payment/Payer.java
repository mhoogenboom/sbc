package com.robinfinch.sbc.domain.payment;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.client.TransactorClient;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.ledger.LedgerStore;
import com.robinfinch.sbc.core.network.Network;
import com.robinfinch.sbc.domain.transfer.Transfer;

import java.util.ArrayList;
import java.util.List;

public class Payer extends TransactorClient {

    public Payer(UserStore userStore, LedgerStore ledgerStore, Network network) {
        super(userStore, ledgerStore, network);
    }

    public boolean makePayment(String to, int value, int fee, String reference, int confirmationLevel) throws ConfigurationException {

        User user = userStore.load();

        List<Entry> sourceEntries = getWallet(confirmationLevel);

        Payment.Builder builder = new Payment.Builder()
                .withFrom(user.getId())
                .withTo(to);

        int change = -(value + fee);

        for (Entry sourceEntry : sourceEntries) {
            Payment sourcePayment = (Payment) sourceEntry.getTransaction();

            builder.withSource(sourceEntry.getHash());

            if (sourcePayment.getTo().equals(user.getId())) {
                change += sourcePayment.getValue();
            } else {
                change += sourcePayment.getChange();
            }

            if (change >= 0) {
                break;
            }
        }

        if (change < 0) {
            return false;
        } else {
            Payment payment = builder
                    .withValue(value)
                    .withChange(change)
                    .withReference(reference)
                    .withFee(fee)
                    .build();

            return publish(payment);
        }
    }
}
