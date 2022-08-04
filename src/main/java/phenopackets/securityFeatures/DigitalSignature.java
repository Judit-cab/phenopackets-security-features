package phenopackets.securityFeatures;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.signature.SignatureConfig;
import com.google.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;


public class DigitalSignature {

    private static final String PK_FILE = "pk_verify.json"; 
    private static final String SK_FILE = "pk_sign.json"; 
    private static final String SIGNATURES_FILE = "signatures";
    public static JSONObject jsonObj = new JSONObject();
    public static JSONParser jsonParser;
    static ExternalResources resourceFile = new ExternalResources();


    /**
     * Primate method to sign the Phenopacket element
     * @param element required - the Phenopacket element 
     * @return the signature bytes 
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static byte[] signElement(byte[] element) throws GeneralSecurityException, IOException, URISyntaxException{
        
        // Read and store the private key to sign
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(resourceFile.getFileFromResource(SK_FILE)));
        
        // Create the signer instance and get the associated primitive
        PublicKeySign signer = null;

        try{
            signer = handle.getPrimitive(PublicKeySign.class);
        }catch(GeneralSecurityException ex){
            System.err.println("Process error: " + ex);
            System.exit(1);
        }

        // Sign and return the signature bytes 
        byte[] signature = signer.sign(element);
        return signature;
    }

    /**
     * Private method to verify the above signature
     * @param element required - the Phenopacket element 
     * @param signature required - the signature 
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static void verifyElement(byte[] element, byte[]signature) throws GeneralSecurityException, IOException, URISyntaxException{

        // Read and store the public key to verify
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(resourceFile.getFileFromResource(PK_FILE)));

         // Create the verifier instance and get the associated primitive
        PublicKeyVerify verifier = null;

        try{
            verifier = handle.getPrimitive(PublicKeyVerify.class);
        } catch (GeneralSecurityException ex) {
            System.err.println("Process error: " + ex);
        }

        // Check if the signature is correct
        try{
            verifier.verify(signature, element);
            System.out.println("Verified!");
        }catch (GeneralSecurityException ex) {
            System.err.println("Verification failed.");
        }
    }
    
    /**
     * Main process to sign or verify the element
     * @param mode required - two actions are allowed: sign or verify
     * @param elementBytes required - the phenopacket element
     * @param elementID required - the Phenopacket Id
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public static void protectWithDS(String mode, byte[] elementBytes, String elementID) throws IOException, URISyntaxException, ParseException{
        
        try {
            // Set the Digital Signature configuration 
            SignatureConfig.register();
            
            // Check the mode is correct
            if (!mode.equals("sign") && !mode.equals("verify")) {
                System.err.println("Incorrect mode.");
            }
            
            if(mode.equals("sign")){
                byte[] signatureBytes = signElement(elementBytes);
                // Store the signature in a jsonObj and create a file with the signature
                String ptSignature = new String(Base64.getEncoder().encode(signatureBytes), StandardCharsets.UTF_8);
                resourceFile.createJSONFile(SIGNATURES_FILE, ptSignature, elementID);

            }else if(mode.equals("verify")){
                searchSignatureAndVerify(elementBytes, elementID);
            }
        }catch (java.security.GeneralSecurityException e){
            System.out.println("Error protecting with DS");
        }
    }

    /**
     * Method to search a signature in a json file and check if is correct
     * @param elementBytes required - the element bytes to verify the signature
     * @param elementID required - the Phenopacket ID
     * @throws URISyntaxException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static void searchSignatureAndVerify(byte[] elementBytes, String elementID) throws URISyntaxException, IOException, GeneralSecurityException{
        
        // Get the file with the signature
        File signaturesFile = resourceFile.getFileFromResource(SIGNATURES_FILE);

        try (FileReader reader = new FileReader(signaturesFile)){
            JsonReader js =  new JsonReader(reader);
            js.beginObject();
            // Search for a specific item by its ID 
            while (js.hasNext()) {
                String field = js.nextName();
                
                if (field.equals(elementID)) {
                    String ptSignature = js.nextString();
                    // Get the signature Bytes from the file
                    byte[] signatureBytes = Base64.getDecoder().decode(ptSignature);
                    // Proceeds to verify
                    verifyElement(elementBytes, signatureBytes);
                }else {
                    js.skipValue();
                }
            }
            js.endObject();
            js.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private DigitalSignature() {}
}
