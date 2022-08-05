package phenopackets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.securityFeatures.DigitalSignature;
import phenopackets.securityFeatures.HybridEncryption;

import org.phenopackets.secure.schema.Phenopacket;

public class SecurePhenopacket {

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
        String phenopacketID = phenopacket.getId();
        
        phenopacket = Phenopacket.newBuilder().clearMetaData().build();

        byte[] cipherMetadata = MainElements.protectedMetaData(metaData, phenopacketID.getBytes());
        byte[] phenopacketBytes = phenopacket.toByteArray();

        HybridEncryption.saveInFile(cipherMetadata, "Metadata", phenopacketID);
        HybridEncryption.saveInFile(phenopacketBytes, "Phenopacket", phenopacketID);
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
        String phenopacketID = phenopacket.getId();

        // Sign the element
        DigitalSignature.protectWithDS("sign", phenopacketBytes, phenopacketID);

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
        String phenopacketID = phenopacket.getId();

        // Verify the element
        DigitalSignature.protectWithDS("verify", phenopacketBytes, phenopacketID);
    }
    
}
