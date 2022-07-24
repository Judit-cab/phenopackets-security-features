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

    /**
     * Function to create a new Individual element with all fields
     * @param id required, arbitrary
     * @param timeAtLastEncounter recommended - age of individual
     * @param vitalStatus recommended - vital status of the individual
     * @param karyotypicSex optional, 0 by default
     * @return Individual element 
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static  Individual createSubject (String id, TimeElement timeAtLastEncounter, VitalStatus vitalStatus, int karyotypicSex) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
        
        // Check timeElement is actually an age element
        if (!timeAtLastEncounter.hasAge()){
            System.out.println("Incorret TimeElement");
            System.exit(0);
        }

        return Individual.newBuilder()
            .setId(id)
            .setTimeAtLastEncounter(timeAtLastEncounter)
            .setVitalStatus(vitalStatus)
            .setKaryotypicSexValue(karyotypicSex)
            .build();

    }

    /**
     * Function to create a new Phenotypic Feature element
     * @param type required - term denoting the phenotypic feature
     * @param severity optional - description of the severity
     * @param evidence optional - List of evidences
     * @param onset optional - time at was first observed 
     * @param resolution optional - time at was resolved
     * @return PhenotypicFeature element
     */
    public static PhenotypicFeature phenotypicFeature (OntologyClass type, OntologyClass severity, List<Evidence> evidence, TimeElement onset, TimeElement resolution){
        
        // Check timeElements correspond to Timestamp Elements
        if (!onset.hasTimestamp() || !resolution.hasTimestamp()){
            System.out.println("Incorret TimeElement");
            throw new IllegalArgumentException();
        }
        
        return PhenotypicFeature.newBuilder()
            .setType(type)
            .setSeverity(severity)
            .addAllEvidence(evidence)
            .setOnset(onset)
            .setResolution(resolution)
            .build();
    }

    /**
     *  Function to create a new Disease element
     * @param term required - represents the disease
     * @param excluded optional - boolean to indicate if was observed
     * @param diseaseStages optional - List of stages
     * @param tnmFindings optional -List of terms related to cancer
     * @param primarySite optional - site of disease
     * @param onset optional - represent the age of onset of the disease
     * @return Disease element
     */
    public static Disease disease (OntologyClass term, boolean excluded, List<OntologyClass> diseaseStages, List<OntologyClass>tnmFindings, OntologyClass primarySite, TimeElement onset){
        
        // Check timeElement is actually an age element

        if (!onset.hasAge()){
            System.out.println("Incorret TimeElement");
            System.exit(1);
        }

        return Disease.newBuilder()
            .setTerm(term)
            .setExcluded(excluded)
            .addAllDiseaseStage(diseaseStages)
            .addAllClinicalTnmFinding(tnmFindings)
            .setPrimarySite(primarySite)
            .setOnset(onset)
            .build();
        
    }


    /**
     * Function to create new metadata element 
     * @param created
     * @param createdBy
     * @param submittedBy
     * @param resources
     * @param updates
     * @param schemaVersion
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
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

       /*
     * Create MedicalAction element with action equals to Procedure
     */
    public static MedicalAction medicalProcedure(Procedure procedure, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setProcedure(procedure)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }

     /*
     * Create MedicalAction element with action equals to Treatment
     */
    public static MedicalAction medicalTreatment(Treatment treatment, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setTreatment(treatment)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }

     /*
     * Create MedicalAction element with action equals to TherapeuticRegimen
     */
    public static MedicalAction medicalRegimen(TherapeuticRegimen regimen, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setTherapeuticRegimen(regimen)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }

     /*
     * Create MedicalAction element with action equals to RadiationTherapy
     */
    public static MedicalAction medicalTherapy(RadiationTherapy radiation, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setRadiationTherapy(radiation)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }
    
}
