package com.robinfinch.sbc.core.identity;

public interface UserStore {

    User load();

    void store(User user);
}
