package com.robinfinch.sbc.testdata;

import com.robinfinch.sbc.core.Hash;
import com.robinfinch.sbc.core.ledger.Transaction;
import com.robinfinch.sbc.p2p.config.UniqueAsset;
import com.robinfinch.sbc.p2p.config.ValueAsset;

public class Tests {

    public static final Hash T1_HASH = new Hash(new byte[]{40, -116, -87, -84, -110, 50, -117, -48, 72, -113, 47,
            -81, 113, 90, -43, -104, 68, 50, -16, 90, 122, 9, 31, 65, 39, 8, -20, 95, -7, 67, -26, -99});

    public Transaction createTransaction1() throws Exception {

        return new Transaction.Builder()
                .withTo("dave")
                .withAsset(new ValueAsset(1, 0))
                .withFee(1)
                .withTimestamp(1L)
                .build();
    }

    public static final Hash T2_HASH = new Hash(new byte[]{48, -6, 89, 107, 70, 11, 51, -7, -103, 108, 54, -3, -16,
            72, -42, 64, -107, -71, 80, -58, 11, -85, -22, 79, -25, 27, 109, -24, -90, -6, 53, -102});

    public Transaction createTransaction2() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withFee(1)
                .withTimestamp(2L)
                .build();
    }

    public static final Hash T3_HASH = new Hash(new byte[]{-48, -54, -42, 63, 45, -22, 85, 109, -91, 31, -101, -54,
            -81, -95, 12, -23, -105, 79, 103, -82, -12, 35, -107, -93, 43, 97, -127, 119, 91, -83, -53, 121});

    public Transaction createTransaction3() throws Exception {

        return new Transaction.Builder()
                .withTo("dave")
                .withAsset(new ValueAsset(1, 0))
                .withTimestamp(3L)
                .build();
    }

    public static final Hash T4_HASH = new Hash(new byte[]{-56, 108, -44, 85, -3, 110, 95, 100, 84, -97, 16, -6, 63,
            -43, 123, -12, 90, -62, 120, -18, -47, 88, 117, 63, -96, -88, -38, -5, 62, 72, 53, -107});

    public Transaction createTransaction4() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withTimestamp(4L)
                .build();
    }

    public static final Hash T5_HASH = new Hash(new byte[]{-91, -16, 6, -78, 10, 30, -120, -99, -94, 10, 53, -94, 46,
            -87, 76, 92, -5, -65, -31, -70, -112, -83, -58, 19, 95, 82, -77, 3, -112, -35, -64, 122});

    public Transaction createTransaction5() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withFee(1)
                .withTimestamp(7L)
                .build();
    }

    public static final byte[] T6_SIGNATURE = {48, -62, -76, -126, -11, 51, -62, -69, -33, -125, -21, -39, -32, -120,
            -35, -10, -92, 38, 95, 25, -58, 89, 14, 28, 16, 91, -113, 48, -84, -103, -78, -104, 93, 90, -100, -88,
            -127, 86, -101, 37, 1, -105, 48, -99, -50, 31, 97, 19, -75, -39, -66, -29, -51, -66, 125, 46, 43, -72,
            -12, -68, 118, -10, -124, -26, -103, -66, 100, 37, 26, -56, 54, -104, -11, 93, 7, 7, -94, 108, 29, 104,
            -112, 15, 77, -61, 44, 122, -41, -21, -28, 23, -119, -41, -106, 93, 120, -87, -98, 42, -77, 29, -119, 45,
            50, -39, -87, -79, -10, -37, -1, -26, -53, 54, 9, -121, -101, -109, 25, 33, -60, -68, -47, 81, 5, -54, 115,
            100, -36, 3};

    public static final Hash T6_HASH = new Hash(new byte[]{-125, 77, -98, 74, 84, 51, -127, 127, 84, 17, 86, -68, 111,
            -107, -4, 31, 84, 84, -59, 40, -108, 46, 127, -92, 62, -106, -57, 112, -71, 87, 19, 26});

    public Transaction createTransaction6() throws Exception {

        Transaction transaction = new Transaction.Builder()
                .withFrom("chris")
                .withTo("alice")
                .withAsset(new ValueAsset(1, 0))
                .withReference("chris-001")
                .withSource(T2_HASH)
                .withTimestamp(8L)
                .build();

        return new Transaction(transaction, T6_SIGNATURE);
    }

    public static final Hash T7_HASH = new Hash(new byte[]{30, -30, 105, -14, 0, -75, -10, -37, -32, 56, -43, 42,
            73, 107, 7, -107, 92, 21, 55, 101, -11, -15, 46, 6, -15, -94, -119, 112, 72, 77, -9, -86});

    public Transaction createTransaction7() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withTimestamp(9L)
                .build();
    }

    public static final Hash T8_HASH = new Hash(new byte[]{-107, 25, -122, 109, -99, -89, -56, 6, -62, -112, -73, -16,
            -3, 10, 12, -57, 19, 74, -116, -39, -99, 56, -57, -105, -68, -43, -95, -34, 34, -90, -44, 37});

    public Transaction createTransaction8() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withFee(1)
                .withTimestamp(11L)
                .build();
    }

    public static final byte[] T9_SIGNATURE = {70, 6, 10, -100, -122, 111, -113, 105, 64, 108, 103, -63, -84, -5, 9,
            -43, -85, 100, -118, -60, -99, 101, -39, -17, 56, 84, 79, 15, -89, -1, 31, -57, 95, -112, 46, 63, -2, 46,
            115, 71, 43, -79, 56, -25, 110, -98, 87, -89, -21, -76, 101, -119, -104, 4, 68, 10, 32, -94, -68, 120, -98,
            28, -51, 56, -53, 30, 17, 114, -52, 41, -20, -122, -13, -84, 36, -3, 120, -82, 94, -116, 113, 47, -8, -115,
            -34, -16, -62, 80, 118, -128, 65, 101, 46, -72, -81, 57, -64, 0, -65, -74, 126, -107, 93, -2, -84, 52, -19,
            112, 73, -12, 72, -71, 91, 16, -57, 72, -18, -70, 85, 100, 111, 69, -11, 15, -32, -41, 106, 31};

    public static final Hash T9_HASH = new Hash(new byte[]{60, 117, 115, 41, 85, -116, -27, 119, -103, 127, 72, -27,
            12, 90, 109, 72, -51, 57, 78, 1, -110, 107, 125, 5, -55, 65, 127, -7, 52, 127, 28, 114});

    public Transaction createTransaction9() throws Exception {

        Transaction transaction = new Transaction.Builder()
                .withFrom("chris")
                .withTo("bob")
                .withAsset(new ValueAsset(3, 0))
                .withReference("chris-002")
                .withSource(T4_HASH)
                .withSource(T5_HASH)
                .withSource(T7_HASH)
                .withTimestamp(12L)
                .build();

        return new Transaction(transaction, T9_SIGNATURE);
    }

    public static final Hash T10_HASH = new Hash(new byte[]{-20, -70, 18, -25, 123, -126, 116, 6, -5, 74, -107, -19,
            -101, 90, -117, -7, 86, 39, -72, 56, -101, 21, -37, 27, -77, -41, 104, 8, -8, 60, -98, -64});

    public Transaction createTransaction10() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withTimestamp(13L)
                .build();
    }

    public static final Hash T11_HASH = new Hash(new byte[]{32, 51, 27, -59, 104, -92, -69, 111, -13, -3, 53, -34,
            62, 44, 51, 45, 31, -63, -40, 102, 29, -39, 78, -1, 75, 86, 45, -34, 54, 64, -121, -65});

    public Transaction createTransaction11() throws Exception {

        return new Transaction.Builder()
                .withTo("dave")
                .withAsset(new ValueAsset(1, 0))
                .withFee(1)
                .withTimestamp(15L)
                .build();
    }

    public static final Hash T12_HASH = new Hash(new byte[]{-60, -107, -80, -55, 110, 27, -94, 5, -67, 89, -115,
            24, -53, -50, 47, 10, 30, 58, -97, -12, -98, -76, 60, 5, 6, 88, -43, -16, -81, 53, -40, -95});

    public Transaction createTransaction12() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(1, 0))
                .withFee(1)
                .withTimestamp(16L)
                .build();
    }

    public static final byte[] T13_SIGNATURE = {24, -101, -75, 87, 52, 113, 124, 80, 2, 16, 41, 69, 75, -31, -107, 67,
            -31, 86, 34, 74, -46, -30, -102, -43, 19, 41, 76, 127, -3, 118, -56, 124, 92, -31, -88, -32, -123, -18, -82,
            -4, 116, -111, 20, -17, 11, -3, 5, 125, 125, 27, -54, 85, 80, -60, 106, -88, 113, 43, -113, -1, 75, 83, -48,
            -114, -124, -94, 81, -83, 47, -47, 105, -35, 117, -125, 6, -85, 39, 117, 82, 87, 41, 32, 48, 59, -23, 97,
            41, -17, -111, 51, 74, 67, -24, -100, 65, -34, -44, -96, 84, -66, 52, -71, 92, -7, -123, -24, -28, 33, -35,
            -124, -15, 0, 56, -93, 119, 40, -81, 76, 85, 41, -113, 121, 80, -117, 31, 97, 18, 37};

    public static final Hash T13_HASH = new Hash(new byte[]{-100, 78, 2, 57, -89, 39, -5, -100, -2, 93, -79, -33, 85,
            -109, 94, 120, -46, 120, 53, 5, -40, -52, 26, -101, 127, 70, 50, -52, 101, 14, 95, -109});

    public Transaction createTransaction13() throws Exception {

        Transaction transaction = new Transaction.Builder()
                .withFrom("bob")
                .withTo("alice")
                .withAsset(new ValueAsset(1, 1))
                .withFee(1)
                .withReference("bob-001")
                .withSource(T9_HASH)
                .withTimestamp(17L)
                .build();

        return new Transaction(transaction, T13_SIGNATURE);
    }

    public static final Hash T14_HASH = new Hash(new byte[]{-74, -116, 125, 71, 28, 93, 77, -64, 97, 112, 50, 74, -30,
            71, -91, -86, 2, -92, -58, 100, 83, -47, -88, -68, 104, -90, 66, -112, 93, 34, -10, -80});

    public Transaction createTransaction14() throws Exception {

        return new Transaction.Builder()
                .withTo("chris")
                .withAsset(new ValueAsset(2, 0))
                .withTimestamp(18L)
                .build();
    }

    public static final byte[] T15_SIGNATURE = {-102, 114, -94, 86, 25, -101, 126, -63, 73, -33, -33, 103, 50, -79,
            -13, -104, 122, -81, -83, -91, -22, 41, -70, 97, 18, -35, 106, -30, 3, 82, -96, -127, 88, 54, 29, 124, 89,
            20, -127, -50, -69, -96, 56, 62, 103, -57, 93, -70, -13, 38, -90, 91, 62, -46, 24, -48, -108, -32, -77, 99,
            -94, -93, 40, 122, -12, -71, -61, -43, 97, -111, 100, 89, -85, 13, -94, 64, 126, -89, 82, 13, 43, -126, -48,
            -83, -102, -91, -67, 57, 18, 91, 46, -86, 81, -41, 90, -27, 95, -99, -49, 113, -18, 50, -18, -98, -113, 24,
            -34, -119, -47, 7, -91, -38, 94, 76, -104, 54, 47, 0, 113, -42, -39, 97, -36, 74, -9, 10, 10, -55};

    public static final Hash T15_HASH = new Hash(new byte[]{91, -18, 100, -52, -83, 76, 40, -49, -43, 53, 0, -6, 32,
            94, 118, -128, 54, 27, -120, 92, -108, 5, 70, -6, 58, -8, 15, 14, 98, 13, -47, 0});

    public Transaction createTransaction15() throws Exception {

        Transaction transaction = new Transaction.Builder()
                .withFrom("chris")
                .withTo("bob")
                .withAsset(new ValueAsset(2, 0))
                .withReference("chris-003")
                .withSource(T8_HASH)
                .withSource(T10_HASH)
                .withTimestamp(19L)
                .build();

        return new Transaction(transaction, T15_SIGNATURE);
    }

    public static final Hash T16_HASH = new Hash(new byte[]{56, 20, 21, 37, -69, -18, -96, -8, -37, -54, 111, 28, 57,
            -84, 41, -98, -62, -29, -114, -20, -26, -50, -82, 98, -75, -28, 38, -110, -95, -21, 86, -106});

    public Transaction createTransaction16() throws Exception {

        return new Transaction.Builder()
                .withTo("dave")
                .withAsset(new ValueAsset(2, 0))
                .withTimestamp(20L)
                .build();
    }

    public static final byte[] T17_SIGNATURE = {93, -93, 50, -41, -109, 78, 114, 65, 35, 85, 65, -78, 115, -6, -26, 26,
            -4, 118, 36, -1, -83, 52, 8, -20, 63, 123, 43, -57, 115, 14, -118, 34, 79, 40, -73, 74, 19, 63, -3, -8, 68,
            30, -94, -5, 42, 111, 14, 26, 44, 47, -31, -24, -95, -11, -81, -29, -120, 10, 33, -19, -13, -68, -91, -16,
            -126, -27, 52, -24, -37, -40, -70, 44, -102, -124, -9, -72, 42, -102, -49, -87, 65, -55, 67, -46, -91, -61,
            19, 42, 109, 16, -120, -25, -78, -104, 31, -49, -10, 30, -28, -28, -45, 28, -107, 68, -21, 71, -37, 91, 76,
            44, 61, 55, -46, 115, -110, -72, 19, 92, -7, -79, 10, 109, 14, 33, 8, 105, 90, -16};

    public static final Hash T17_HASH = new Hash(new byte[]{-39, 116, -83, 21, -82, 100, 96, 33, 17, 52, 83, -58, -10,
            -12, -24, -123, -112, 47, -111, 78, 82, -116, 107, 103, 90, -108, 19, -72, -99, -57, -29, 18});

    public Transaction createTransaction17() throws Exception {

        Transaction transaction = new Transaction.Builder()
                .withFrom("bob")
                .withTo("alice")
                .withAsset(new ValueAsset(1, 0))
                .withFee(2)
                .withReference("bob-002")
                .withSource(T13_HASH)
                .withSource(T15_HASH)
                .withTimestamp(23L)
                .build();

        return new Transaction(transaction, T17_SIGNATURE);
    }

    public static final Hash T18_HASH = new Hash(new byte[]{91, 113, -25, 47, 12, 13, 83, -56, -36, 19, 20, 22, 94, 61,
            97, -60, -103, -86, 36, -113, 34, -32, -93, 108, 121, -107, -36, -69, 101, -97, 120, -71});

    public Transaction createTransaction18() throws Exception {

        return new Transaction.Builder()
                .withTo("dave")
                .withAsset(new ValueAsset(2, 0))
                .withTimestamp(24L)
                .build();
    }

    public static final Hash T19_HASH = new Hash(new byte[]{48, -37, -45, 6, 93, -92, -68, 21, -49, -59, -51, 90, 36,
            126, -73, -103, -109, -108, 100, -66, -63, -74, -110, -20, 59, -92, -63, 89, 87, 50, -97, 45});

    public Transaction createTransaction19() throws Exception {

        return new Transaction.Builder()
                .withFrom("alice")
                .withTo("bob")
                .withAsset(new UniqueAsset("BD12 PXZ"))
                .withReference("alice-car-001")
                .withTimestamp(25L)
                .build();
    }
}
