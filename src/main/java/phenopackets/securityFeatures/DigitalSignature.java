package phenopackets.securityFeatures;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;

import com.google.crypto.tink.signature.SignatureConfig;

import com.google.gson.JsonObject;

import java.io.File;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;


public class DigitalSignature {

    private static final String PK_FILE = "pk.json"; 
    public static JsonObject jsonObj = new JsonObject();
    
    /**
     *  Stores the private keyset in the projects resources/keysets directory if it does not exist yet.
     * @throws IOException              Failure during saving
     * @throws GeneralSecurityException Failure during keyset generation
     * */
    private static void createKeySet() throws IOException, GeneralSecurityException {
        File keysetFile = new File(PK_FILE);
        if (!keysetFile.exists()) {
            KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECDSA"));
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(keysetFile));
        }
    }

    private static byte[] signElement(byte[] element) throws GeneralSecurityException, IOException{
        createKeySet();
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(PK_FILE)));
        PublicKeySign signer = null;

        try{
            signer = handle.getPrimitive(PublicKeySign.class);
        }catch(GeneralSecurityException ex){
            System.err.println("Cannot create primitive: " + ex);
            System.exit(1);
        }

        // use primitive to sign
        byte[] signature = signer.sign(element);

        return signature;
    }

    private static void verifyElement(byte[] element, byte[]signature) throws GeneralSecurityException, IOException{
        createKeySet();
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(new File(PK_FILE)));

        PublicKeyVerify verifier = null;

        try{
            verifier = handle.getPrimitive(PublicKeyVerify.class);
        } catch (GeneralSecurityException ex) {
            System.err.println("Cannot create primitive: " + ex);
        }

        try{
            verifier.verify(signature, element);
        }catch (GeneralSecurityException ex) {
            System.err.println("Signature verification failed.");
        }
    }
    
    public static void protectWithDS(String mode, byte[] elementBytes, String elementName) throws IOException{
        
        try {
            SignatureConfig.register();
            
            if(mode.equals("sign")){
                byte[] signatureBytes = signElement(elementBytes);
                //save the signature in a jsonObj
                String ptSignature = new String(Base64.getEncoder().encode(signatureBytes), StandardCharsets.UTF_8);
                jsonObj.addProperty(elementName, ptSignature);

            }else if(mode.equals("verify")){
                String ptSignature = jsonObj.getAsJsonArray("elementName").getAsString();
                byte[] signatureBytes = Base64.getDecoder().decode(ptSignature);
                verifyElement(elementBytes, signatureBytes);
            }
        }catch (java.security.GeneralSecurityException e){
            System.out.println("Error protecting with DS");
        }
    }

    private DigitalSignature() {}
}
