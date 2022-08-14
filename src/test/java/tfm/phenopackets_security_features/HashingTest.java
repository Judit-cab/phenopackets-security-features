package tfm.phenopackets_security_features;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import phenopackets.examples.Covid19;
import phenopackets.examples.Oncology;
import phenopackets.securityMechanisms.Hashing;


public class HashingTest {

    Oncology oncologyCase = new Oncology();

    @Test
    void checkPhenotypicElementHashFunction() throws IOException, GeneralSecurityException, URISyntaxException{
        Phenopacket phenopacket = oncologyCase.createOncologyPhenopacket();
        String phenopacketId = phenopacket.getId();

        List<PhenotypicFeature> phenotypicFeatures = phenopacket.getPhenotypicFeaturesList();
        PhenotypicFeature phenotypicFeature1 = phenotypicFeatures.get(0);
        // Get the phenotypic feature name
        String phenotypic1 = phenotypicFeature1.getType().getLabel();
        
        PhenotypicFeature phenotypicFeature2 = phenotypicFeatures.get(1);
         // Get the name of the second phenotypic feature
        String phenotypic2 = phenotypicFeature2.getType().getLabel();
        // Compute hash for both elements, the hash will be stored in a specific file
        String hash1 = Hashing.computePhenotypicFeatureHash(phenotypicFeature1, phenopacketId, phenotypic1);
        String hash2 = Hashing.computePhenotypicFeatureHash(phenotypicFeature2, phenopacketId, phenotypic2);

        // Retrieve the hash from the file
        String hash1File = Hashing.getHash(phenopacketId, phenotypic1);
        String hash2File = Hashing.getHash(phenopacketId, phenotypic2);

        // Compare both hashes to check that the storage is done correctly
        Assertions.assertEquals(hash1, hash1File);
        Assertions.assertEquals(hash2, hash2File);

    }

    @Test
    void checkDiseaseElementHashFunction() throws IOException, GeneralSecurityException, URISyntaxException{
        Phenopacket phenopacket = oncologyCase.createOncologyPhenopacket();
        String phenopacketId = phenopacket.getId();
        // Get Disease element
        Disease disease = phenopacket.getDiseases(0);
        // Get the name of the Disease
        String diseaseName = disease.getTerm().getLabel();
        // Compute hash
        String hash = Hashing.computeDiseaseHash(disease, phenopacketId, diseaseName);

         // Retrieve the hash from the file
        String hashFile = Hashing.getHash(phenopacketId, diseaseName);

        // Compare both hashes to check that the storage is done correctly
        Assertions.assertEquals(hash, hashFile);

    }

    @Test
    void checkMedicalActionHashFunction() throws IOException, GeneralSecurityException, URISyntaxException{
        // The covid case is used to check the hash function of the medical action
        Covid19 covidCase = new Covid19();
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = phenopacket.getId();

        List<MedicalAction> medicalActions = phenopacket.getMedicalActionsList();
        // Get Medical Action element
        MedicalAction procedure = medicalActions.get(0);
        // Get the name of the first action
        String procedureName = procedure.getProcedure().getCode().getLabel();
        
        MedicalAction treatment = medicalActions.get(1);
        // Get the name of the second action
        String treatmentName = treatment.getTreatment().getAgent().getLabel();
        
        // Compute hash
        String hash1 = Hashing.computeMedicalAction(procedure, phenopacketId, procedureName);
        String hash2 = Hashing.computeMedicalAction(treatment, phenopacketId, treatmentName);
        
        // Retrieve hashes from the file
        String hash1File = Hashing.getHash(phenopacketId, procedureName);
        String hash2File = Hashing.getHash(phenopacketId, treatmentName);

        // Compare both hashes to check that the storage is done correctly
        Assertions.assertEquals(hash1, hash1File);
        Assertions.assertEquals(hash2, hash2File);

    }

    @Test
    void checkHashValidation() throws URISyntaxException, IOException, GeneralSecurityException{
        Phenopacket phenopacket = oncologyCase.createOncologyPhenopacket();
        String phenopacketId = phenopacket.getId();

        List<PhenotypicFeature> phenotypicFeatures = phenopacket.getPhenotypicFeaturesList();
        PhenotypicFeature phenotypicFeature1 = phenotypicFeatures.get(0);
        // Get the phenotypic feature name
        String phenotypic1 = phenotypicFeature1.getType().getLabel();
        
        PhenotypicFeature phenotypicFeature2 = phenotypicFeatures.get(1);
         // Get the name of the second phenotypic feature
        String phenotypic2 = phenotypicFeature2.getType().getLabel();

        // Retrieve hashes from the file
        String storedHash1 = Hashing.getHash(phenopacketId, phenotypic1);
        String storedHash2 = Hashing.getHash(phenopacketId, phenotypic2);

        // Compute hashes compares them with stored ones
        boolean res1 = Hashing.checkHash(phenotypicFeature1.toByteArray(), storedHash1);
        boolean res2 = Hashing.checkHash(phenotypicFeature2.toByteArray(), storedHash2);

        // If both hashes are equal, the function returns True
        assertTrue(res1);
        assertTrue(res2);
    }
}
    