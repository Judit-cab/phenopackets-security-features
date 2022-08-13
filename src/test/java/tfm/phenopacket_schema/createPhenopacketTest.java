package tfm.phenopacket_schema;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import phenopackets.SecurePhenopacket;
import phenopackets.Examples.Covid19;

public class createPhenopacketTest {
    Covid19 covidCase = new Covid19();

    /*
     * Test to check an individual can be successfully created
     */
    @Test
    void checkIndividualCreation() throws IOException, GeneralSecurityException, URISyntaxException{
    
        Individual individual = covidCase.createCovidSubject();

        Assertions.assertEquals(individual.getTimeAtLastEncounter(), covidCase.isoAge);
        Assertions.assertEquals(individual.getKaryotypicSexValue(), covidCase.karyorypicSex);
    }

    /*
     * Test to check Phenotypic Features can be successfully created
     * 
     */
    @Test
    void checkPhenotypicFeatureCreation() throws IOException, GeneralSecurityException, URISyntaxException{ 

        List<PhenotypicFeature> phenotypicFeatures = covidCase.createCovidPhenotypicFeatures();

        PhenotypicFeature phenotypicFeature1 = phenotypicFeatures.get(0);
        PhenotypicFeature phenotypicFeature2 = phenotypicFeatures.get(1);

        Assertions.assertEquals(phenotypicFeature1.getType(), covidCase.phenotypicType1);
        Assertions.assertEquals(phenotypicFeature1.getSeverity(), covidCase.severity);
        Assertions.assertEquals(phenotypicFeature1.getEvidenceList().get(0), covidCase.evidence);
        Assertions.assertEquals(phenotypicFeature1.getOnset(), covidCase.onset);
        Assertions.assertEquals(phenotypicFeature1.getResolution(), covidCase.resolution);

        Assertions.assertEquals(phenotypicFeature2.getType(), covidCase.phenotypicType2);
        Assertions.assertEquals(phenotypicFeature2.getSeverity(), covidCase.severity);
        Assertions.assertEquals(phenotypicFeature2.getEvidenceList().get(0), covidCase.evidence);
        Assertions.assertEquals(phenotypicFeature2.getOnset(), covidCase.onset);
        Assertions.assertEquals(phenotypicFeature2.getResolution(), covidCase.resolution);

    }

    /*
     * Test to check Disease can be successfully created
     * 
     */
    @Test
    void checkDiseaseCreation() throws IOException, GeneralSecurityException, URISyntaxException{ 

        List<Disease> diseases = covidCase.createCovidDisease();
        Disease disease1 = diseases.get(0);
        Disease disease2 = diseases.get(1);

        Assertions.assertEquals(disease1.getTerm(), covidCase.term1);
        Assertions.assertEquals(disease1.getExcluded(), covidCase.excluded);
        Assertions.assertEquals(disease1.getDiseaseStage(0), covidCase.diseaseStage);
        Assertions.assertEquals(disease1.getPrimarySite(), covidCase.primarySite);
        Assertions.assertNotEquals(disease1.getOnset(), covidCase.isoAge);
        
        Assertions.assertEquals(disease2.getTerm(), covidCase.term2);
        Assertions.assertFalse(disease2.getExcluded());
        Assertions.assertEquals(disease2.getDiseaseStage(0), covidCase.diseaseStage);
        Assertions.assertEquals(disease2.getPrimarySite(), covidCase.primarySite);
        Assertions.assertNotEquals(disease2.getOnset(), covidCase.isoAge);

    }

    /*
     * Test to check MedicalActions can be successfully created
     * 
     */
    @Test
    void checkMedicalActionCreation() throws IOException, GeneralSecurityException, URISyntaxException{ 

        List<MedicalAction> medicalActions = covidCase.createCovidMedicalActions();

        MedicalAction procedure = medicalActions.get(0);
        MedicalAction treatment = medicalActions.get(1);

        Assertions.assertEquals(procedure.getProcedure().getCode(), covidCase.code);
        Assertions.assertEquals(procedure.getProcedure().getBodySite(), covidCase.bodySite);
        Assertions.assertEquals(procedure.getProcedure().getPerformed(), covidCase.performed);

        Assertions.assertEquals(treatment.getTreatment().getAgent(), covidCase.agent);
        Assertions.assertEquals(treatment.getTreatment().getRouteOfAdministration(), covidCase.agent);
        Assertions.assertEquals(treatment.getTreatment().getDoseIntervals(0).getQuantity().getUnit(), covidCase.unit);
        Assertions.assertEquals(treatment.getTreatment().getDoseIntervals(0).getQuantity().getValue(), covidCase.value);
        Assertions.assertEquals(treatment.getTreatment().getDoseIntervals(0).getScheduleFrequency(), covidCase.schedule);
        Assertions.assertEquals(treatment.getTreatment().getDoseIntervals(0).getInterval(), covidCase.interval);
    }

    /*
     * Test to check metadata can be successfully created
     * 
     */
    @Test 
    void checkMetadaCreation() throws IOException, GeneralSecurityException, URISyntaxException{

        //MetaData
        MetaData metaData = covidCase.createCovidMetaData();
        
        Assertions.assertEquals(metaData.getCreated(), covidCase.created);
        Assertions.assertEquals(metaData.getCreatedBy(), covidCase.createdBy);
        Assertions.assertEquals(metaData.getSubmittedBy(), covidCase.submittedBy);
        Assertions.assertEquals(metaData.getPhenopacketSchemaVersion(), covidCase.phenopacketSchemaVersion);
    }

    /*
     * Test to check if a phenopacket can be created
     * In this case, metadata is no protected
     */

    @Test
    void checkPhenopacketCreation() throws IOException, GeneralSecurityException, URISyntaxException{
        
        // Create an arbitrary identifier
        String phenopacketID = SecurePhenopacket.generatePhenopacketId();
        Phenopacket covidPhenopacket = covidCase.covid19Phenopacket();

        
        // Create the subject
        Individual subject = covidCase.createCovidSubject();

        // Create Phenotypic Feature element
        List<PhenotypicFeature> phenotypicFeatures = covidCase.createCovidPhenotypicFeatures();

        // Create Disease element
        List<Disease> diseases = covidCase.createCovidDisease();
        // Create Medical Action element
        List<MedicalAction> medicalActions = covidCase.createCovidMedicalActions();
     
        // Create the metadata element
        MetaData metaData = covidCase.createCovidMetaData();

        // Create secure phenopacket
        Phenopacket phenopacket = SecurePhenopacket.createPhenopacket(phenopacketID, subject, phenotypicFeatures, metaData, diseases, medicalActions);

        Assertions.assertEquals(phenopacket, covidPhenopacket);


    }
    
}

