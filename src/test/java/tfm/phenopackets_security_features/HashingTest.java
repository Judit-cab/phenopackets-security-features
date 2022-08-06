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

import phenopackets.Examples.Covid19;
import phenopackets.Examples.Oncology;
import phenopackets.securityFeatures.Hashing;


public class HashingTest {

    Oncology oncologyCase = new Oncology();

    @Test
    void checkPhenotypicElementHashFunction() throws IOException, GeneralSecurityException, URISyntaxException{
        Phenopacket phenopacket = oncologyCase.createOntologyPhenopacket();
        String phenopacketId = phenopacket.getId();

        List<PhenotypicFeature> phenotypicFeatures = phenopacket.getPhenotypicFeaturesList();
        PhenotypicFeature phenotypicFeature1 = phenotypicFeatures.get(0);
        String phenotypic1 = phenotypicFeature1.getType().getLabel();
        
        PhenotypicFeature phenotypicFeature2 = phenotypicFeatures.get(1);
        String phenotypic2 = phenotypicFeature2.getType().getLabel();

        String hash1 = Hashing.computePhenotypicFeatureHash(phenotypicFeature1, phenopacketId, phenotypic1);
        String hash2 = Hashing.computePhenotypicFeatureHash(phenotypicFeature2, phenopacketId, phenotypic2);

        String hash1File = Hashing.getHash(phenopacketId, phenotypic1);
        String hash2File = Hashing.getHash(phenopacketId, phenotypic2);

        Assertions.assertEquals(hash1, hash1File);
        Assertions.assertEquals(hash2, hash2File);

    }

    @Test
    void checkDiseaseElementHashFunction() throws IOException, GeneralSecurityException, URISyntaxException{
        Phenopacket phenopacket = oncologyCase.createOntologyPhenopacket();
        String phenopacketId = phenopacket.getId();

        Disease disease = phenopacket.getDiseases(0);
        String diseaseName = disease.getTerm().getLabel();
        
        String hash = Hashing.computeDiseaseHash(disease, phenopacketId, diseaseName);
        String hashFile = Hashing.getHash(phenopacketId, diseaseName);

        Assertions.assertEquals(hash, hashFile);

    }

    @Test
    void checkMedicalActionsHashFunction() throws IOException, GeneralSecurityException, URISyntaxException{
        Covid19 covidCase = new Covid19();
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = phenopacket.getId();

        List<MedicalAction> medicalActions = phenopacket.getMedicalActionsList();
        
        MedicalAction procedure = medicalActions.get(0);
        String procedureName = procedure.getProcedure().getCode().getLabel();
        
        MedicalAction treatment = medicalActions.get(1);
        String treatmentName = treatment.getTreatment().getAgent().getLabel();
        
        String hash1 = Hashing.computeMedicalAction(procedure, phenopacketId, procedureName);
        String hash2 = Hashing.computeMedicalAction(treatment, phenopacketId, treatmentName);
        
        String hash1File = Hashing.getHash(phenopacketId, procedureName);
        String hash2File = Hashing.getHash(phenopacketId, treatmentName);

        Assertions.assertEquals(hash1, hash1File);
        Assertions.assertEquals(hash2, hash2File);

    }

    @Test
    void checkHashFunction() throws URISyntaxException, IOException{
        String phenopacketID = "f9f2d029-e1e3-42a4-bb79-ee39652c8c07";

        List<PhenotypicFeature> phenotypicFeatures = oncologyCase.createOntologyPhenotypicFeature();

        PhenotypicFeature phenotypicFeature1 = phenotypicFeatures.get(0);
        String phenotypic1 = phenotypicFeature1.getType().getLabel();

        PhenotypicFeature phenotypicFeature2 = phenotypicFeatures.get(1);
        String phenotypic2 = phenotypicFeature2.getType().getLabel();
        
        String storedHash1 = Hashing.getHash(phenopacketID, phenotypic1);
        String storedHash2 = Hashing.getHash(phenopacketID, phenotypic2);

        boolean res1 = Hashing.checkHash(phenotypicFeature1.toByteArray(), storedHash1);
        boolean res2 = Hashing.checkHash(phenotypicFeature2.toByteArray(), storedHash2);

        assertTrue(res1);
        assertTrue(res2);
    }
}
    