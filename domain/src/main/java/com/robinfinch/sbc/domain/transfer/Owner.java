package com.robinfinch.sbc.domain.transfer;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.client.TransactorClient;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.Entry;
import com.robinfinch.sbc.core.ledger.LedgerStore;
import com.robinfinch.sbc.core.network.Network;

import java.util.List;

public class Owner extends TransactorClient {

    public Owner(UserStore userStore, LedgerStore ledgerStore, Network network) {
        super(userStore, ledgerStore, network);
    }

    public boolean registerNew(String asset, String reference) throws ConfigurationException {

        User user = userStore.load();

        Transfer transfer = new Transfer.Builder()
                .withFrom(user.getId())
                .withTo(user.getId())
                .withAsset(asset)
                .withReference(reference)
                .build();

        return publish(transfer);
    }

    public boolean registerTransfer(String to, String asset, String reference, int confirmationLevel) throws ConfigurationException {

        User user = userStore.load();

        List<Entry> sourceEntries = getWallet(confirmationLevel);

        Hash source = null;

        for (Entry sourceEntry : sourceEntries) {
            Transfer sourceTransfer = (Transfer) sourceEntry.getTransaction();
            if (sourceTransfer.getAsset().equals(asset)) {
                source = sourceEntry.getHash();
                break;
            }
        }

        if (source == null) {
            return false;
        } else {
            Transfer transfer = new Transfer.Builder()
                    .withFrom(user.getId())
                    .withTo(to)
                    .withAsset(asset)
                    .withReference(reference)
                    .withSource(source)
                    .build();

            return publish(transfer);
        }
    }
}
