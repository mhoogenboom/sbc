package com.robinfinch.sbc.simulation;

import com.robinfinch.sbc.core.identity.User;
import com.robinfinch.sbc.core.identity.UserStore;

public class MemoryUserStore implements UserStore {

    private User user;

    @Override
    public User load() {
        return user;
    }

    @Override
    public void store(User user) {
        this.user = user;
    }
}
