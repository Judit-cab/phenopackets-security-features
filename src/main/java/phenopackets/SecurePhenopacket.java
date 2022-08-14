package phenopackets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;


import com.google.gson.stream.JsonReader;
import com.google.protobuf.util.JsonFormat;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.securityMechanisms.DigitalSignature;
import phenopackets.securityMechanisms.ExternalResources;
import phenopackets.securityMechanisms.HybridEncryption;

import org.phenopackets.secure.schema.Phenopacket;

public class SecurePhenopacket {

    private static final String SIGNATURES_FILE = "signatures.json";

    static ExternalResources externalResource = new ExternalResources();
    public static JSONObject jsonObj = new JSONObject();


    public SecurePhenopacket() {
    }

    /**
     * Method to create a Universally Unique Identifier (UUID)
     * @return the new UUID
     */
    public static String generatePhenopacketId() {
        return String.valueOf(UUID.randomUUID());
    }
    
    /**
     * Method to create a Phenopacket element
     * @param subject recommended - an individual (patient)
     * @param phenotypicFeatures recommended - List of phenotypics
     * @param metaData required - information about ontologies 
     * @param diseases optional - List of diseases
     * @param medicalActions optional - List of medical actions
     * @return New Phenopacket
     */
    public static Phenopacket createPhenopacket (String id, Individual subject, List<PhenotypicFeature> phenotypicFeatures, MetaData metaData, List<Disease> diseases, List<MedicalAction> medicalActions){

        return Phenopacket.newBuilder()
            .setId(id)
            .setSubject(subject)
            .setMetaData(metaData)
            .addAllPhenotypicFeatures(phenotypicFeatures)
            .addAllDiseases(diseases)
            .addAllMedicalActions(medicalActions)
            .build();
    }
    
    public static Phenopacket createPhenopacketToHybrydEncryption(String id, Individual subject, MetaData metaData){

        return Phenopacket.newBuilder()
            .setId(id)
            .setSubject(subject)
            .setMetaData(metaData)
            .build();
    }


    public static void protectMetaData(Phenopacket phenopacket) throws URISyntaxException, IOException, GeneralSecurityException, ParseException{
        MetaData metaData = phenopacket.getMetaData();
        String phenopacketId = phenopacket.getId();
        
        phenopacket = Phenopacket.newBuilder(phenopacket).clearMetaData().build();
        System.out.println("Phenopacket is:" + phenopacket);

        byte[] cipherMetadata = MainElements.protectedMetaData(metaData, phenopacketId.getBytes());
        byte[] phenopacketBytes = phenopacket.toByteArray();

        HybridEncryption.saveInFile(cipherMetadata, "Metadata", phenopacketId);
        HybridEncryption.saveInFile(phenopacketBytes, "Phenopacket", phenopacketId);
        
        System.out.println("The elements have been saved successfully");
    }

    /**
     * Method to apply signature in the phenopacket
     * @param phenopacket required - Phenopacket element
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public static void signPhenopacket(Phenopacket phenopacket) throws IOException, URISyntaxException, ParseException{
        
        // Parse to byte array 
        byte[] phenopacketBytes = phenopacket.toByteArray();
        
        // Get the id as identifier 
        String phenopacketId = phenopacket.getId();

        // Sign the element
        DigitalSignature.protectWithDS("sign", phenopacketBytes, phenopacketId);

    }

    /**
     * Method to verify the signature
     * @param phenopacket required - phenopacket element
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    public static  void verifyPhenopacket(Phenopacket phenopacket) throws IOException, URISyntaxException, ParseException{
        
        // Parse to byte array 
        byte[] phenopacketBytes = phenopacket.toByteArray();
        
        // Get the id as identifier 
        String phenopacketId = phenopacket.getId();

        // Verify the element
        DigitalSignature.protectWithDS("verify", phenopacketBytes, phenopacketId);
    }

    public static byte[] getPhenopacketFromFile(String elementID) throws URISyntaxException, IOException{

        byte[] phenopacketBytes = null;

        // Get the file with the signature
        File signaturesFile = externalResource.getFileFromResource(SIGNATURES_FILE);

        try (FileReader reader = new FileReader(signaturesFile)){
            JsonReader js =  new JsonReader(reader);
            js.beginObject();
            // Search for a specific item by its ID 
            while (js.hasNext()) {
                String field = js.nextName();
                
                if (field.equals(elementID)) {
                    String ptPhenopacket = js.nextString();
                    // Get the signature Bytes from the file
                    phenopacketBytes = Base64.getDecoder().decode(ptPhenopacket);
                   
                }else {
                    js.skipValue();
                }
            }
            js.endObject();
            js.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        System.out.println(phenopacketBytes);
        return phenopacketBytes;
    }

    /*
     * Export Phenopacket to JSON
     */
    public static void exportPhenopacket(Phenopacket phenopacket) throws URISyntaxException{
       
        try{
            String jsonString = JsonFormat.printer().includingDefaultValueFields().print(phenopacket);
            System.out.println(jsonString);
            String path = externalResource.getNewPath(phenopacket.getId(),".json" );
            File phenopacketJson = new File(path);

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(phenopacketJson));     
            fileWriter.write(jsonString);
            fileWriter.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /*
     * Import Phenopacket from JSON
     */
    public static Phenopacket importPhenopacket(String path) throws URISyntaxException, ParseException, IOException{
        File jsonFile = new File(path);
        String js = Files.readString(jsonFile.toPath());

        Phenopacket phenopacket = null;

        try{
            Phenopacket.Builder phenopacketBuilder = Phenopacket.newBuilder();
            JsonFormat.parser().merge(js, phenopacketBuilder);
            phenopacket = phenopacketBuilder.build();
       
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return phenopacket;
    }
    
}
