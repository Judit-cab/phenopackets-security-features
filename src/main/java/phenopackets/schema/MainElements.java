package phenopackets.schema;

import org.phenopackets.secure.schema.core.*;

import com.google.protobuf.Timestamp;

import phenopackets.securityMechanisms.HybridEncryption;


import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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
     * Method to create an arbitrary identifier for Individual Element
     * @return the new id
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    private static String generateIndividualId() throws NoSuchAlgorithmException, NoSuchProviderException{

        SecureRandom secureRandom;
        String randomNumber = new String();

        // Get a true random number generator
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException ex) {
            secureRandom = new SecureRandom();
        }

        // Get a random number of 6 digits
        for(int i=0; i<6; i++){
            randomNumber += String.valueOf(secureRandom.nextInt(9));

        }
        // Individual identifier begins with P
        String id = "P"+randomNumber;

        return id;
    }

    /**
     * Function to create a new Individual element with all fields
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
     * @throws NoSuchProviderException
     */
    public static  Individual createSubject (TimeElement timeAtLastEncounter, VitalStatus vitalStatus, int karyotypicSex) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException{
        
        // Check timeElement is actually an age element
        if (!timeAtLastEncounter.hasAge()){
            System.out.println("Incorret TimeElement");
            throw new IllegalArgumentException();
        }

        // Create an arbitrary identifier
        final String PROBAND_ID = generateIndividualId();

        return Individual.newBuilder()
            .setId(PROBAND_ID)
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
    public static PhenotypicFeature createPhenotypicFeature (OntologyClass type, OntologyClass severity, List<Evidence> evidence, TimeElement onset, TimeElement resolution){
        
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
     * @param primarySite optional - site of disease
     * @param onset optional - represent the age of onset of the disease
     * @return Disease element
     */
    public static Disease createDisease (OntologyClass term, boolean excluded, List<OntologyClass> diseaseStages, OntologyClass primarySite, TimeElement onset){
        
        // Check timeElement is actually an age element
        if (!onset.hasAge()){
            System.out.println("Incorret TimeElement");
            throw new IllegalArgumentException();
        }

        return Disease.newBuilder()
            .setTerm(term)
            .setExcluded(excluded)
            .addAllDiseaseStage(diseaseStages)
            .setPrimarySite(primarySite)
            .setOnset(onset)
            .build(); 
    }

    /**
     * Function to create a new Disease element
     * @param term required - represents the disease
     * @param excluded optional - boolean to indicate if was observed
     * @param diseaseStages optional - List of stages
     * @param tnmFindings optional -List of terms related to cancer
     * @param primarySite optional - site of disease
     * @param onset optional - represent the age of onset of the disease
     * @return Disease element
     */
    public static Disease createOncologicalDisease(OntologyClass term, boolean excluded, List<OntologyClass> diseaseStages, List<OntologyClass>tnmFindings, OntologyClass primarySite, TimeElement onset){
        
        // Check timeElement is actually an age element
        if (!onset.hasAge()){
            System.out.println("Incorret TimeElement");
            throw new IllegalArgumentException();
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
     * Function to create new MetaData element 
     * @param created optional - time was created
     * @param createdBy optional - person who created the phenopacket
     * @param submittedBy optional - person who submitted the phenopacket
     * @param resources optional - List of ontologies used
     * @param updates optional - List of updates
     * @param schemaVersion optional 
     * @return new Metadata Element
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

    /**
     * Method to encrypt the MetaData creator 
     * protecting the name of the person who creates the Phenopacket
     * @param metaData required - MetaData element
     * @param context required to encrypt the data
     * @return MetaData element
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static MetaData protectedMetaDataCreator(MetaData metaData, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        String createdBy = metaData.getCreatedBy();
        metaData = MetaData.newBuilder(metaData).clearCreatedBy().build();

        // Use of hybridEncryption to protect the creator of the phenopacket
        byte[] cipher = HybridEncryption.hybridEncryption(MODE_ENC, createdBy.getBytes(), context);
        String cipherCreatedBy = Base64.getEncoder().encodeToString(cipher);
       
        return MetaData.newBuilder().setCreatedBy(cipherCreatedBy).build(); 
    }
    
     /**
     * Function to create protected MetaData element 
     * @param metaData required - MetaData element to protect
     * @param context required - context used in the encryption
     * @return encrypted byte[] representing the element
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static byte[] protectedMetaData (MetaData metaData, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        
        // Get the bytes of the Element
        byte[] metaDataBytes = metaData.toByteArray();
        
        // Encrypt the whole element
        byte[] cipherMetaData = HybridEncryption.hybridEncryption(MODE_ENC, metaDataBytes, context);

        // return the cipher element
        return cipherMetaData;
     }


     /**
     * Function to decrypt and get the MetaData element 
     * @param metaDataBytes required - the cipher element
     * @param context required 
     * @return plain MetaData
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static MetaData getMetaData(byte[] metaDataBytes, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
       
        // Decrypt the above bytes with the decryption method
        byte[] plainMetaData = HybridEncryption.hybridEncryption(MODE_DEC, metaDataBytes, context);
        
        // return the MetaData element
        return MetaData.parseFrom(plainMetaData);
     }

     /**
     * Method to decrypt and get the creator of the phenopacket
     * @param metaData required - the metadata element with the encrypted creator
     * @param phenopacketId required 
     * @return plain Created_by field
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static String getMetaDataCreator(MetaData metaData, String phenopacketId) throws IOException, GeneralSecurityException, URISyntaxException{
       
        // Get the created_by field bytes
        String createdBy = metaData.getCreatedBy();
        byte[] createdBytes = Base64.getDecoder().decode(createdBy);
        
        // Decrypt the above bytes with the decryption method
        byte[] plainCreatedBy = HybridEncryption.hybridEncryption(MODE_DEC, createdBytes, phenopacketId.getBytes());
        
        // return the Creator
        return new String(plainCreatedBy);
     }

    /**
     * Method to reate MedicalAction element with Procedure action (all fields included)
     * @param procedure required - Procedure action
     * @param treatmentTarget optional - condition or disease
     * @param treatmentIntent optional - intention of the treatment
     * @param treatmentResponse optional - how patient responded
     * @param adverseEvents optional 
     * @param treatmentTermination optional - reason to stop
     * @return new MedicalAction element
     */
    public static MedicalAction createMedicalProcedure(Procedure procedure, OntologyClass treatmentTarget, OntologyClass treatmentIntent, OntologyClass treatmentResponse,
    List<OntologyClass> adverseEvents, OntologyClass treatmentTermination){
        
        return MedicalAction.newBuilder()
                .setProcedure(procedure)
                .setTreatmentTarget(treatmentTarget)
                .setTreatmentIntent(treatmentIntent)
                .setResponseToTreatment(treatmentResponse)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(treatmentTermination)
                .build();
    }

    /**
     * Method to create MedicalAction element with Treatment action (all fields included)
     * @param treatment required - Treatment action
     * @param treatmentTarget optional - condition or disease
     * @param treatmentIntent optional - intention of the treatment
     * @param treatmentResponse optional - how patient responded
     * @param adverseEvents optional 
     * @param treatmentTermination optional - reason to stop
     * @return
     */
    public static MedicalAction createMedicalTreatment(Treatment treatment, OntologyClass treatmentTarget, OntologyClass treatmentIntent, OntologyClass treatmentResponse,
    List<OntologyClass> adverseEvents, OntologyClass treatmentTermination){
        
        return MedicalAction.newBuilder()
                .setTreatment(treatment)
                .setTreatmentTarget(treatmentTarget)
                .setTreatmentIntent(treatmentIntent)
                .setResponseToTreatment(treatmentResponse)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(treatmentTermination)
                .build();
    }

    /**
     * Method to reate MedicalAction element with Therapeutic Regimen action
     * @param therapeuticRegimen required - Therapeutic Regimen action
     * @param treatmentTarget optional - condition or disease
     * @param treatmentIntent optional - intention of the treatment
     * @param treatmentResponse optional - how patient responded
     * @param adverseEvents optional 
     * @param treatmentTermination optional - reason to stop
     * @return
     */
    public static MedicalAction createMedicalTherapeuticRegimen(TherapeuticRegimen therapeuticRegimen, OntologyClass treatmentTarget, OntologyClass treatmentIntent, OntologyClass treatmentResponse,
    List<OntologyClass> adverseEvents, OntologyClass treatmentTermination){
        
        return MedicalAction.newBuilder()
                .setTherapeuticRegimen(therapeuticRegimen)
                .setTreatmentTarget(treatmentTarget)
                .setTreatmentIntent(treatmentIntent)
                .setResponseToTreatment(treatmentResponse)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(treatmentTermination)
                .build();
    }


    /**
     * Method to reate MedicalAction element with Radiation Therapy action
     * @param radiationTherapy required - Radiation Therapy action
     * @param treatmentTarget optional - condition or disease
     * @param treatmentIntent optional - intention of the treatment
     * @param treatmentResponse optional - how patient responded
     * @param adverseEvents optional 
     * @param treatmentTermination optional - reason to stop
     * @return
     */
    public static MedicalAction createMedicalRadiationTherapy(RadiationTherapy radiationTherapy, OntologyClass treatmentTarget, OntologyClass treatmentIntent, OntologyClass treatmentResponse,
    List<OntologyClass> adverseEvents, OntologyClass treatmentTermination){
        return MedicalAction.newBuilder()
                .setRadiationTherapy(radiationTherapy)
                .setTreatmentTarget(treatmentTarget)
                .setTreatmentIntent(treatmentIntent)
                .setResponseToTreatment(treatmentResponse)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(treatmentTermination)
                .build();
    }

    /**
     * Method to create Procedure medical action
     * @param procedure required - Procedure element
     * @return MedicalAction element
     */
    public static MedicalAction createProcedure (Procedure procedure){
        return MedicalAction.newBuilder()
            .setProcedure(procedure)
            .build();
    }

    /**
     * Method to create Treatment medical action
     * @param procedure required - Treatment element
     * @return MedicalAction element
     */
    public static MedicalAction createTreatment (Treatment treatment){
        return MedicalAction.newBuilder()
            .setTreatment(treatment)
            .build();
    }

    /**
     * Method to create Therapeutic Regimen medical action
     * @param procedure required - Therapeutic Regimen element
     * @return MedicalAction element
     */
    public static MedicalAction createTherapeuticRegimen(TherapeuticRegimen therapeuticRegimen){
        return MedicalAction.newBuilder()
            .setTherapeuticRegimen(therapeuticRegimen)
            .build();
    }

    /**
     * Method to create Radiation Therapy medical action
     * @param procedure required - Radiation Therapy element
     * @return MedicalAction element
     */
    public static MedicalAction createRadiationTherapy(RadiationTherapy radiationTherapy){
        return MedicalAction.newBuilder()
            .setRadiationTherapy(radiationTherapy)
            .build();
    }
    
}