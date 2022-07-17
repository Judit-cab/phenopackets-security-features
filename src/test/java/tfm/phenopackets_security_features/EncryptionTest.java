package tfm.phenopackets_security_features;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import phenopackets.securityFeatures.SymmetricEncryption;

public class EncryptionTest {
    
    @Test
    void givenString_whenEncrypt_thenSuccess() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException { 
        String input = "baeldung";
        SecretKey key = SymmetricEncryption.generateKey();
        IvParameterSpec ivParameterSpec = SymmetricEncryption.generateIv();
        
        String cipherText = SymmetricEncryption.encrypt( input, key, ivParameterSpec);
        String plainText = SymmetricEncryption.decrypt(cipherText, key, ivParameterSpec);
    
        Assertions.assertEquals(input, plainText);
    }
}
