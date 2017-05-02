package com.robinfinch.sbc.p2p.identity;

import com.robinfinch.sbc.core.identity.Identity;

import java.util.Objects;

public class IdentityTo {

    private String userId;

    private String publicKeyData;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPublicKeyData() {
        return publicKeyData;
    }

    public void setPublicKeyData(String publicKeyData) {
        this.publicKeyData = publicKeyData;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId)
                + 3 * Objects.hashCode(publicKeyData);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IdentityTo) {
            IdentityTo that = (IdentityTo) o;
            return Objects.equals(this.userId, that.userId)
                    && Objects.equals(this.publicKeyData, that.publicKeyData);
        } else {
            return false;
        }
    }
}
