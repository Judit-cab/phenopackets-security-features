package phenopackets;

import org.phenopackets.secure.schema.core.*;
import com.google.protobuf.Timestamp;

import phenopackets.securityFeatures.SymmetricEncryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


public class ElementsBuilder {

    static SecretKey key ;
    static IvParameterSpec iv;

    public ElementsBuilder() throws NoSuchAlgorithmException {
        key = SymmetricEncryption.generateKey();
        iv = SymmetricEncryption.generateIv();
    }


    /*
     * Function to create a Individual element with all fields
     */

    public static Individual createSubject (String id, TimeElement timeAtLastEncounter, VitalStatus vitalStatus, KaryotypicSex karyotypicSex) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException{
        
        // block to encrypt restricted field
        String lastEncounter = timeAtLastEncounter.toString();
        lastEncounter = SymmetricEncryption.encrypt(lastEncounter, key, iv);
        Age cipherAge = Age.newBuilder().setIso8601Duration(lastEncounter).build();
        TimeElement cipherTimeEncounter = TimeElement.newBuilder().setAge(cipherAge).build();

        // Create a secure Individual element
        Individual subject = Individual.newBuilder()
        .setId(id)
        .setTimeAtLastEncounter(cipherTimeEncounter)
        .setVitalStatus(vitalStatus)
        .setKaryotypicSex(karyotypicSex)
        .build();

        return subject;
    }

    /*
     * Function to create a Individual element with required and recommended fields
     */

    public static Individual createSubjectWithId (String id, TimeElement timeAtLastEncounter, VitalStatus vitalStatus){
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

     public static MetaData metaData (Timestamp created, String createdBy, String submittedBy, List<Resource> resources, List<Update> updates, String schemaVersion){
        return MetaData.newBuilder()
        .setCreated(created)
        .setCreatedBy(createdBy)
        .setSubmittedBy(submittedBy)
        .addAllResources(resources)
        .addAllUpdates(updates)
        .setPhenopacketSchemaVersion(schemaVersion)
        .build();  
     }

}
