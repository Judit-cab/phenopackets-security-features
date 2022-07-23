package phenopackets.securityFeatures;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.hybrid.HybridConfig;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.List;

public class HybridEncryption {
    private static final String SK_FILE = "sk_hybridEnc.json"; 
    private static final String PK_FILE = "pk_hybridEnc.json"; 
    private static final String ALGORITHM = "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM";
    
    static ExternalResources externalResource = new ExternalResources();

    private static void createKeySet() throws IOException, GeneralSecurityException, URISyntaxException {

        // Generate  new private key
        KeysetHandle privateKey = KeysetHandle.generateNew(KeyTemplates.get(ALGORITHM));
        CleartextKeysetHandle.write(privateKey, JsonKeysetWriter.withPath(externalResource.getFileFromResource(SK_FILE).getAbsolutePath()));

        // Obtain the public key 
        KeysetHandle publicKey = privateKey.getPublicKeysetHandle();
        CleartextKeysetHandle.write(publicKey, JsonKeysetWriter.withPath(externalResource.getFileFromResource(PK_FILE).getAbsolutePath()));
    }

    private static byte[] hybridEncryption(byte[] element, byte[] contextInfo) throws GeneralSecurityException, URISyntaxException{
        // Read the keyset into a KeysetHandle.
        KeysetHandle handle = null;
        try {
            handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(externalResource.getFileFromResource(PK_FILE)));
        } catch (GeneralSecurityException | IOException ex) {
            System.err.println("Error: " + ex);
        }

        HybridEncrypt encryptor = null;
        try {
            encryptor = handle.getPrimitive(HybridEncrypt.class);
        } catch (GeneralSecurityException ex) {
            System.err.println("Cannot create primitive, got error: " + ex);
        }
        // Use the primitive to encrypt data.
        byte[] ciphertext = encryptor.encrypt(element, contextInfo);
        return ciphertext;
    }

    private static byte[] hybridDecryption(byte[] cipher, byte[] contextInfo) throws GeneralSecurityException, URISyntaxException{
        KeysetHandle handle = null;
        try {
            handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(externalResource.getFileFromResource(SK_FILE)));
        } catch (GeneralSecurityException | IOException ex) {
            System.err.println("Error: " + ex);
        }

        HybridDecrypt decryptor = null;
        try {
          decryptor = handle.getPrimitive(HybridDecrypt.class);
        } catch (GeneralSecurityException ex) {
          System.err.println("Cannot create primitive, got error: " + ex);
        }
        // Use the primitive to decrypt data.
        byte[] plaintext = decryptor.decrypt(cipher, contextInfo);
        return plaintext;
      }
    
    public static byte[] hybridEncryption(String mode, byte[] element, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        byte[] res;
        HybridConfig.register();
        File hybridFile = externalResource.getFileFromResource(SK_FILE);
        List<String> lines = Files.readAllLines(hybridFile.toPath());
        if (lines.size()==0) {
            createKeySet();
        }

        if (!mode.equals("encrypt") && !mode.equals("decrypt")) {
          System.err.println("Incorrect mode.");
        }

        // Register all hybrid encryption key types with the Tink runtime.
        
        if (mode.equals("encrypt")) {
            res = hybridEncryption(element,context);
            return res;
        }else{
            res = hybridDecryption(element, context);
            return res;
        } 
    }
    
}
