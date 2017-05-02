package com.robinfinch.sbc.domain.vote;

import com.robinfinch.sbc.core.ConfigurationException;
import com.robinfinch.sbc.core.client.TransactorClient;
import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;
import com.robinfinch.sbc.core.ledger.LedgerStore;
import com.robinfinch.sbc.core.network.Network;

public class Voter extends TransactorClient {

    public Voter(UserStore userStore, LedgerStore ledgerStore, Network network) {
        super(userStore, ledgerStore, network);
    }

    public boolean vote(String vote) throws ConfigurationException {

        User user = userStore.load();

        Vote transaction = new Vote.Builder()
                .withVoterId(user.getId())
                .withVote(vote)
                .build();

        return publish(transaction);
    }
}
