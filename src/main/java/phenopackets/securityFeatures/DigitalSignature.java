package phenopackets.securityFeatures;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeyTemplates;
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
    private static final String SIGNATURES_FILE = "signaturesElements.json";
    public static JSONObject jsonObj = new JSONObject();
    public static JSONParser jsonParser;
    static ExternalResources resourceFile = new ExternalResources();

    /**
     *  Stores the private keyset in the projects resources/keysets directory if it does not exist yet.
     * @throws IOException              Failure during saving
     * @throws GeneralSecurityException Failure during keyset generation
     * */
    private void createKeySet() throws IOException, GeneralSecurityException {
        File keysetFile = new File(PK_FILE);
        if (!keysetFile.exists()) {
            KeysetHandle keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("ECDSA"));
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(keysetFile));
        }
    }

    private static byte[] signElement(byte[] element) throws GeneralSecurityException, IOException, URISyntaxException{
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(resourceFile.getFileFromResource(SK_FILE)));
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

    private static void verifyElement(byte[] element, byte[]signature) throws GeneralSecurityException, IOException, URISyntaxException{
      
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(resourceFile.getFileFromResource(PK_FILE)));

        PublicKeyVerify verifier = null;

        try{
            verifier = handle.getPrimitive(PublicKeyVerify.class);
        } catch (GeneralSecurityException ex) {
            System.err.println("Cannot create primitive: " + ex);
        }

        try{
            verifier.verify(signature, element);
            System.out.println("Verified!");
        }catch (GeneralSecurityException ex) {
            System.err.println("Signature verification failed.");
        }
    }
    
    public static JSONObject protectWithDS(String mode, byte[] elementBytes, String elementName) throws IOException, URISyntaxException, ParseException{
        
        try {
            SignatureConfig.register();
            
            if(mode.equals("sign")){
                byte[] signatureBytes = signElement(elementBytes);
                //save the signature in a jsonObj
                String ptSignature = new String(Base64.getEncoder().encode(signatureBytes), StandardCharsets.UTF_8);
                jsonObj.appendField(elementName, ptSignature);
                resourceFile.createJsonFile(SIGNATURES_FILE, jsonObj);

            }else if(mode.equals("verify")){
                File signatures = resourceFile.getFileFromResource(SIGNATURES_FILE);

                try (FileReader reader = new FileReader(signatures)){
                    JsonReader js =  new JsonReader(reader);
                    js.beginObject();
                    while (js.hasNext()) {
                        String field = js.nextName();
                        if (field.equals(elementName)) {
                            String ptSignature = js.nextString();
                            byte[] signatureBytes = Base64.getDecoder().decode(ptSignature);
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
        }catch (java.security.GeneralSecurityException e){
            System.out.println("Error protecting with DS");
        }
        return jsonObj;
    }

    private DigitalSignature() {}
}
