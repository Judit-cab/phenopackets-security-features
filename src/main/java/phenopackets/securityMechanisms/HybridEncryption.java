package phenopackets.securityMechanisms;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.hybrid.HybridConfig;

import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class HybridEncryption {
    private static final String SK_FILE = "sk_hybridEnc.json"; 
    private static final String PK_FILE = "pk_hybridEnc.json"; 
    private static final String ALGORITHM = "ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM";
    private static final String FILE_FORMAT =".json";

    static ExternalResources externalResource = new ExternalResources();
    static JSONObject jsonObj = new JSONObject();

    /**
     * Method to create the assymetric keyset : private and public key
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    private static void createKeySet() throws IOException, GeneralSecurityException, URISyntaxException {

        // Generate  new private key
        KeysetHandle privateKey = KeysetHandle.generateNew(KeyTemplates.get(ALGORITHM));
        CleartextKeysetHandle.write(privateKey, JsonKeysetWriter.withPath(externalResource.createNewFile(SK_FILE).getAbsolutePath()));

        // Obtain the public key 
        KeysetHandle publicKey = privateKey.getPublicKeysetHandle();
        CleartextKeysetHandle.write(publicKey, JsonKeysetWriter.withPath(externalResource.createNewFile(PK_FILE).getAbsolutePath()));
    }

    /**
     * Private method to encrypt the data
     * @param element required - the element or field to encrypt
     * @param contextInfo required - context related with the data
     * @return the encrypted data
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    private static byte[] hybridEncryption(byte[] element, byte[] contextInfo) throws GeneralSecurityException, URISyntaxException{
        
        // Input validation
        if (element == null || element.length == 0){
            throw new NullPointerException();
        }
        if (contextInfo == null || contextInfo.length == 0){
            throw new NullPointerException();
        }

        // Read the keyset into a KeysetHandle
        KeysetHandle handle = null;
        try {
            handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(externalResource.getFileFromResource(PK_FILE)));
        } catch (GeneralSecurityException | IOException ex) {
            System.err.println("Process error: " + ex);
        }
        
        // Get primitive related to the encryption
        HybridEncrypt encryptor = null;
        try {
            encryptor = handle.getPrimitive(HybridEncrypt.class);
        } catch (GeneralSecurityException ex) {
            System.err.println("Process error: " + ex);
        }

        // Encrypt and return the ciphertext
        byte[] ciphertext = encryptor.encrypt(element, contextInfo);
        return ciphertext;
    }

    /**
     * Private method to decrypt the data
     * @param cipher required - the element or field to decryt
     * @param contextInfo required - context related with the data
     * @return the plaintext
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    private static byte[] hybridDecryption(byte[] cipher, byte[] contextInfo) throws GeneralSecurityException, URISyntaxException{
        
        // Input validation
        if (cipher == null || cipher.length == 0){
            throw new NullPointerException();
        }
        if (contextInfo == null || contextInfo.length == 0){
            throw new NullPointerException();
        }

        // Read the keyset into a KeysetHandle
        KeysetHandle handle = null;
        try {
            handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(externalResource.getFileFromResource(SK_FILE)));
        } catch (GeneralSecurityException | IOException ex) {
            System.err.println("Process error: " + ex);
        }

        // Get primitive related to the decryption
        HybridDecrypt decryptor = null;
        try {
          decryptor = handle.getPrimitive(HybridDecrypt.class);
        } catch (GeneralSecurityException ex) {
          System.err.println("Process error: " + ex);
        }

        // Decrypt and return the plaintext
        byte[] plaintext = decryptor.decrypt(cipher, contextInfo);
        return plaintext;
      }
    
    /**
     * Main method to encrypt and decrypt with an hybrid encryption any element or data
     * @param mode required - only two methods allowed: encrypt or decrypt
     * @param element required - the element or field to encrypt/decryt
     * @param context required - context related with the data
     * @return the corresponding bytes to encryption/decryption
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static byte[] hybridEncryption(String mode, byte[] element, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        
        byte[] result;
        // Initialize the hybrid configuration
        HybridConfig.register();
        // Check if exist the keyset
        File hybridFile = externalResource.getFileFromResource(SK_FILE);
        // If not, create the keyset for the process
        if (hybridFile.length()==0) {
            createKeySet();
        }

        // Check the mode is correct
        if (!mode.equals("encrypt") && !mode.equals("decrypt")) {
          System.err.println("Incorrect mode.");
        }

        // If the mode is encrypt then call function hybridEncryption, otherwise call hybridDecryption
        if (mode.equals("encrypt")) {
            result = hybridEncryption(element,context);
            return result;
        }else{
            result = hybridDecryption(element, context);
            return result;
        } 
    }


    /**
     * Method to save the corresponding encrypted bytes into a file
     * @param elementBytes required - the cipherBytes
     * @param elementName required - element that was encrypted
     * @param fileName required - file where will be stored
     * @throws URISyntaxException
     * @throws ParseException
     */
    public static void saveInFile(byte[] elementBytes,String elementName, String fileName) throws URISyntaxException, ParseException{
    
        // Input validation
        if (elementBytes == null || elementBytes.length == 0){
            throw new NullPointerException();
        }
        if (elementName == null || elementName.length()==0){
            throw new NullPointerException();
        }
        if (fileName == null || fileName.length()==0){
            throw new NullPointerException();
        }
        
        // Encoding bytes
        String ptBytes = new String(Base64.getEncoder().encode(elementBytes), StandardCharsets.UTF_8);
        // Create new JSON file
        externalResource.createJSONFile(fileName+FILE_FORMAT, ptBytes, elementName);
    }

    /**
     * Method to retrieve the stored cypherbytes
     * @param elementName required - element that was encrypted
     * @param fileName required - file where will be stored
     * @return the cipherbytes
     * @throws URISyntaxException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static byte[] getCipherBytes(String elementName, String fileName) throws URISyntaxException, IOException, GeneralSecurityException{
        
         // Input validation
        if (elementName == null || elementName.length()==0){
            throw new NullPointerException();
        }
        if (fileName == null || fileName.length()==0){
            throw new NullPointerException();
        }
        
        byte[] cipherBytes = null;
        // Get the file 
        File hybridFile = externalResource.getFileFromResource(fileName+FILE_FORMAT);
        
        try (FileReader reader = new FileReader(hybridFile)){
            JsonReader js =  new JsonReader(reader);
            js.beginObject();
            
            // Search for a specific item by its ID 
            while (js.hasNext()) {
                String field = js.nextName();
                
                if (field.equals(elementName)) {
                    String ptBytes = js.nextString();
                    // Get the element Bytes from the file
                    cipherBytes = Base64.getDecoder().decode(ptBytes);
                }else {
                    js.skipValue();
                }
            }
            js.endObject();
            js.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return cipherBytes;
    }
    
}
