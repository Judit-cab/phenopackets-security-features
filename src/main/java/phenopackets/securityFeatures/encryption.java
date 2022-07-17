package phenopackets.securityFeatures;

import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeysetHandle;

import java.security.GeneralSecurityException;

public class encryption {

    String plaintext;
    
    // Register all hybrid encryption key types with the Tink runtime.
    //HybridConfig.register();

    // Read the keyset into a KeysetHandle
    KeysetHandle handle = null;

    public byte[] encryptString(String field) throws GeneralSecurityException{
        HybridEncrypt encryptor = null;
        byte [] input = field.getBytes();
        byte [] contextInfo = new byte[0];

        try {
          encryptor = handle.getPrimitive(HybridEncrypt.class);
          
        } catch (GeneralSecurityException ex) {
            System.err.println("Cannot create primitive, got error: " + ex);
            System.exit(1);
        }
        // Use the primitive to encrypt data.
        byte [] ciphertext = encryptor.encrypt(input, contextInfo);
        return ciphertext;
    }

    public byte[] decryptString(String ciphertext) throws GeneralSecurityException{
        // Get the primitive.
        HybridDecrypt decryptor = null;

        byte [] input = ciphertext.getBytes();
        byte [] contextInfo = new byte[0];

        try {
            decryptor = handle.getPrimitive(HybridDecrypt.class);

        } catch (GeneralSecurityException ex) {
            System.err.println("Cannot create primitive, got error: " + ex);
            System.exit(1);
        }
        // Use the primitive to decrypt data.
        byte[] field = decryptor.decrypt(input, contextInfo);

        return field;
    }


}
