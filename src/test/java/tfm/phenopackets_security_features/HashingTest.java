package tfm.phenopackets_security_features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.crypto.tink.subtle.Hex;

import phenopackets.securityFeatures.Keccak256Hashing;


public class HashingTest {
    private static String phenopacketId = "123450";
    private static String hashedValue = "0d14ec3174ee68481d2a64252b4e728fedbc9407205c19a3de8e0a899c0ff951";


    @Test public void testIdHash() {
        final byte[] currentHashedValue = Keccak256Hashing.computeHash(phenopacketId.getBytes());
        String hashValue = new String(Hex.encode(currentHashedValue));
        System.out.println(hashValue);

        assertEquals(hashedValue, hashValue);
    }

}
    