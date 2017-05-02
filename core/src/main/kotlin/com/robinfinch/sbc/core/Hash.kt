package com.robinfinch.sbc.core

class Hash(val value : ByteArray) {

    companion object {
        const val HASH_ALGORITHM = "SHA-256"
    }

    fun startsWithZeros(n : Int) : Boolean {
        var zeros = 0
        for (v in value) {
            if (v.equals(0)) {
                zeros++
            } else {
                break
            }
        }
        return zeros >= n
    }
}