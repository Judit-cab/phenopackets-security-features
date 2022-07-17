package tfm.phenopackets_security_features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;

import phenopackets.securityFeatures.Keccak256Hashing;


public class HashingTest {
    private Phenopacket originalValue;
    private static String hashedValue = "719accc61a9cc126830e5906f9d672d06eab6f8597287095a2c55a8b775e7016";


    @Test public void testHashWithBouncyCastle() {
        final String currentHashedValue = Keccak256Hashing.computeHash(originalValue);
        assertEquals(hashedValue, currentHashedValue);
    }

}
    