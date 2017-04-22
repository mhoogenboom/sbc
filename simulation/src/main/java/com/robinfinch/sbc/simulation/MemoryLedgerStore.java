package com.robinfinch.sbc.simulation;

import com.robinfinch.sbc.core.ledger.Ledger;
import com.robinfinch.sbc.core.ledger.LedgerStore;

public class MemoryLedgerStore implements LedgerStore {

    private Ledger ledger;

    @Override
    public Ledger load() {
        return ledger;
    }

    @Override
    public void store(Ledger ledger) {
        this.ledger = ledger;
    }
}
