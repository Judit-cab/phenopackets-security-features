package phenopackets.securityMechanisms;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.signature.SignatureConfig;


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


public class DigitalSignature {

    private static final String PK_FILE = "pk_verify.json"; 
    private static final String SK_FILE = "pk_sign.json"; 
    private static final String SIGNATURES_FILE = "signatures.json";
  
    static ExternalResources externalResource = new ExternalResources();
    public static JSONObject jsonObj = new JSONObject();

    /**
     * Private method to sign the Phenopacket element
     * @param element required - the Phenopacket element 
     * @return the signature bytes 
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static byte[] signElement(byte[] element) throws GeneralSecurityException, IOException, URISyntaxException{
         // Input validation
         if (element == null || element.length == 0){
            throw new NullPointerException();
        }
        // Read and store the private key to sign
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(externalResource.getFileFromResource(SK_FILE)));
        
        // Create the signer instance and get the associated primitive
        PublicKeySign signer = null;

        try{
            signer = handle.getPrimitive(PublicKeySign.class);
        }catch(GeneralSecurityException ex){
            System.err.println("Process error: " + ex);
        }

        // Sign and return the signature bytes 
        byte[] signature = signer.sign(element);
        return signature;
    }

    /**
     * Private method to verify the above signature
     * @param element required - the Phenopacket element 
     * @param signature required - the signature
     * @return boolean value. If the signatures was verified then returns true, otherwise false 
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static Boolean verifyElement(byte[] element, byte[]signature) throws GeneralSecurityException, IOException, URISyntaxException{
         // Input validation
         if (element == null || element.length == 0){
            throw new NullPointerException();
        }
        if (signature == null || signature.length == 0){
            throw new NullPointerException();
        }

        //Set Boolean variable 
        Boolean isVerified = false; 
        // Read and store the public key to verify
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(externalResource.getFileFromResource(PK_FILE)));

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
            isVerified = true;
        }catch (GeneralSecurityException ex) {
            System.err.println("Verification failed." + ex);
        }
        return isVerified;
    }
    
    /**
     * Main process to sign or verify the element
     * @param mode required - two actions are allowed: sign or verify
     * @param elementBytes required - the phenopacket element
     * @param phenopacketId required - the Phenopacket Id
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public static void protectWithDS(String mode, byte[] elementBytes, String phenopacketId) throws IOException, URISyntaxException, ParseException{
        
        // Input validation
        if ( phenopacketId == null || phenopacketId.length() == 0){
            throw new NullPointerException();
        }

        //Set variable 
        Boolean isVerified = false; 
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
                String ptPhenopacket = new String(Base64.getEncoder().encode(elementBytes), StandardCharsets.UTF_8);
                externalResource.createJSONFile(SIGNATURES_FILE, ptPhenopacket, phenopacketId);
                externalResource.createJSONFile(SIGNATURES_FILE, ptSignature, phenopacketId+"-Signature");
                
            }else if(mode.equals("verify")){
                isVerified = searchSignatureAndVerify(elementBytes, phenopacketId);
                System.out.println("Verified:" + isVerified);
            }
        }catch (java.security.GeneralSecurityException e){
            System.out.println("Error protecting with DS");
        }
    }

    /**
     * Method to search a signature in a json file and check if is correct
     * @param elementBytes required - the element bytes to verify the signature
     * @param phenopacketId required - the Phenopacket ID
     * @return boolean value. If the signatures was verified then returns true, otherwise false 
     * @throws URISyntaxException
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static Boolean searchSignatureAndVerify(byte[] elementBytes, String phenopacketId) throws URISyntaxException, IOException, GeneralSecurityException{
        
        // Input validation
        if (elementBytes == null || elementBytes.length == 0){
            throw new NullPointerException();
        }

        // Set variable
        Boolean isVerified = false;

        // Set the Digital Signature configuration 
        SignatureConfig.register();

        // Get the file with the signature
        File signaturesFile = externalResource.getFileFromResource(SIGNATURES_FILE);

        try (FileReader reader = new FileReader(signaturesFile)){
            JsonReader js =  new JsonReader(reader);
            js.beginObject();
            // Search for a specific item by its ID 
            while (js.hasNext()) {
                String field = js.nextName();
                
                if (field.equals(phenopacketId+"-Signature")) {
                    String ptSignature = js.nextString();
                    // Get the signature Bytes from the file
                    byte[] signatureBytes = Base64.getDecoder().decode(ptSignature);
                    // Proceeds to verify
                    isVerified = verifyElement(elementBytes, signatureBytes);
                }else {
                    js.skipValue();
                }
            }
            js.endObject();
            js.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return isVerified;
    }

    private DigitalSignature() {}
}
