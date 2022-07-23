package phenopackets;

import org.phenopackets.secure.schema.core.*;

import com.google.protobuf.Timestamp;

import phenopackets.securityFeatures.HybridEncryption;


import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class MainElements {

    private static final String MODE_ENC = "encrypt"; 
    private static final String MODE_DEC = "decrypt"; 

    public MainElements(){
    }


    /*
     * Function to create a Individual element with all fields
     */

    public static  Individual createSubject (String id, TimeElement timeAtLastEncounter, VitalStatus vitalStatus, int karyotypicSex) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
        
        // Create a secure Individual element
        Individual subject = Individual.newBuilder()
            .setId(id)
            .setTimeAtLastEncounter(timeAtLastEncounter)
            .setVitalStatus(vitalStatus)
            .setKaryotypicSexValue(karyotypicSex)
            .build();

        return subject;
    }

    /*
     * Function to create a Individual element with required and recommended fields
     */

    public static Individual createSubjectWithId (String id, TimeElement timeAtLastEncounter, VitalStatus vitalStatus){
        
        if (!timeAtLastEncounter.hasAge()){
            System.out.println("Incorret TimeElement");
            System.exit(0);
        }

        return Individual.newBuilder()
        .setId(id)
        .setTimeAtLastEncounter(timeAtLastEncounter)
        .setVitalStatus(vitalStatus)
        .build();
    }

    /*
     * Function to create a Phenotypic Feature element
     */

    public static PhenotypicFeature phenotypicFeature (OntologyClass type, OntologyClass severity, List<Evidence> evidence, TimeElement onset, TimeElement resolution){
        
        if (!onset.hasTimestamp() || resolution.hasTimestamp()){
            System.out.println("Incorret TimeElement");
            System.exit(0);
        }
        
        return PhenotypicFeature.newBuilder()
            .setType(type)
            .setSeverity(severity)
            .addAllEvidence(evidence)
            .setOnset(onset)
            .setResolution(resolution)
            .build();
    }

    /*
     * Function to create a Disease element
     */

     public static Disease disease (OntologyClass term, boolean excluded, List<OntologyClass> stages, List<OntologyClass>tnmFindings, OntologyClass primarySite, TimeElement onset){
        
        if (!onset.hasAge()){
            System.out.println("Incorret TimeElement");
            System.exit(1);
        }

        return Disease.newBuilder()
            .setTerm(term)
            .setExcluded(excluded)
            .addAllDiseaseStage(stages)
            .addAllClinicalTnmFinding(tnmFindings)
            .setPrimarySite(primarySite)
            .setOnset(onset)
            .build();
        
    }

    /*
     * Function to create metadata element 
     */

    public static MetaData createMetaData (Timestamp created, String createdBy, String submittedBy, List<Resource> resources, List<Update> updates, String schemaVersion) throws IOException, GeneralSecurityException, URISyntaxException{

        return MetaData.newBuilder()
            .setCreated(created)
            .setCreatedBy(createdBy)
            .setSubmittedBy(submittedBy)
            .addAllResources(resources)
            .addAllUpdates(updates)
            .setPhenopacketSchemaVersion(schemaVersion)
            .build(); 
    }

    public static MetaData protectedMetadataCreator(Timestamp created, String createdBy, String submittedBy, List<Resource> resources, List<Update> updates, String schemaVersion, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        byte[] cipher = HybridEncryption.hybridEncryption(MODE_ENC, createdBy.getBytes(), context);
        String cipherCreatedBy = Base64.getEncoder().encodeToString(cipher);
       
        return MetaData.newBuilder()
            .setCreated(created)
            .setCreatedBy(cipherCreatedBy)
            .setSubmittedBy(submittedBy)
            .addAllResources(resources)
            .addAllUpdates(updates)
            .setPhenopacketSchemaVersion(schemaVersion)
            .build(); 
    }


    /*
     * Function to create protected metadata element 
     */
     public static byte[] protectedMetaData (MetaData metaData, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        
        byte[] metaDataBytes = metaData.toByteArray();
        byte[] cipherMetaData = HybridEncryption.hybridEncryption(MODE_ENC, metaDataBytes, context);

        return cipherMetaData;
     }
     /*
     * Function to get MetaData element 
     */

     public static MetaData getMetaData(byte[] metaDataBytes, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
       
        byte[] plainMetaData = HybridEncryption.hybridEncryption(MODE_DEC, metaDataBytes, context);
        
        return MetaData.parseFrom(plainMetaData);
     }
    
}
