package com.robinfinch.sbc.core.identity;

import com.robinfinch.sbc.testdata.TestData;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UserTests extends TestData {

    @Test
    public void bobSignsEntry() throws Exception {

//        System.out.println("entry 1 signature = " + Arrays.toString(alice.sign(entry1).getSignature()));
//        System.out.println("entry 2 signature = " + Arrays.toString(bob.sign(entry2).getSignature()));
//        System.out.println("entry 4 signature = " + Arrays.toString(alice.sign(entry4).getSignature()));
//        System.out.println("entry 5 signature = " + Arrays.toString(bob.sign(entry5).getSignature()));

        assertEquals("bob", bob.getId());

        assertArrayEquals(ENTRY2_SIGNATURE, bob.sign(entry2).getSignature());
    }
}
