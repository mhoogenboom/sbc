package com.robinfinch.sbc.core.ledger;

public interface LedgerStore {

    Ledger load();

    void store(Ledger ledger);
}
