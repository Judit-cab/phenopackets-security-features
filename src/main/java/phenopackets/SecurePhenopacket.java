package phenopackets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.securityFeatures.DigitalSignature;

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
        String id = phenopacket.getId();

        // Sign the element
        DigitalSignature.protectWithDS("sign", phenopacketBytes, id);

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
        String id = phenopacket.getId();

        // Verify the element
        DigitalSignature.protectWithDS("verify", phenopacketBytes, id);
    }
    
}
