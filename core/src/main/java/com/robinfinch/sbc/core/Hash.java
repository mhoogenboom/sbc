package com.robinfinch.sbc.core;

import java.util.Arrays;

public class Hash {

    public static final String HASH_ALGORITHM = "SHA-256";

    public final byte[] value;

    public Hash(byte[] value) {
        this.value = value;
    }

    public boolean startsWithZeros(int count) {
        for (int i = 0; i < count; i++) {
            if (value[i] != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Hash) {
            Hash that = (Hash) o;
            return Arrays.equals(this.value, that.value);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }
}
