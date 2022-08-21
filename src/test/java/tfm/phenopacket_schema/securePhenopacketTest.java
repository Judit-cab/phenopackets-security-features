package tfm.phenopacket_schema;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.SecurePhenopacket;
import phenopackets.examples.Covid19;
import phenopackets.securityMechanisms.ExternalResources;

public class securePhenopacketTest {
    Covid19 covidCase = new Covid19();

    @Test
    void checkIndividualCreation() throws IOException, GeneralSecurityException, URISyntaxException{
    
        Individual individual = covidCase.createCovidSubject();

        Assertions.assertNotEquals(covidCase.isoAge, individual.getTimeAtLastEncounter());
        Assertions.assertEquals(covidCase.karyorypicSex, individual.getKaryotypicSexValue());
    }

    @Test
    void checkPhenotypicFeatureCreation() throws IOException, GeneralSecurityException, URISyntaxException{ 

        List<PhenotypicFeature> phenotypicFeatures = covidCase.createCovidPhenotypicFeatures();

        PhenotypicFeature phenotypicFeature1 = phenotypicFeatures.get(0);
        PhenotypicFeature phenotypicFeature2 = phenotypicFeatures.get(1);

        Assertions.assertEquals(covidCase.phenotypicType1, phenotypicFeature1.getType());
        Assertions.assertEquals(covidCase.severity, phenotypicFeature1.getSeverity());
        Assertions.assertEquals(covidCase.evidence, phenotypicFeature1.getEvidenceList().get(0));
        Assertions.assertEquals(covidCase.onset, phenotypicFeature1.getOnset());
        Assertions.assertEquals(covidCase.resolution, phenotypicFeature1.getResolution());

        Assertions.assertEquals(covidCase.phenotypicType2, phenotypicFeature2.getType());
        Assertions.assertEquals(covidCase.severity, phenotypicFeature2.getSeverity());
        Assertions.assertEquals(covidCase.evidence, phenotypicFeature2.getEvidenceList().get(0));
        Assertions.assertEquals(covidCase.onset, phenotypicFeature2.getOnset());
        Assertions.assertEquals(covidCase.resolution, phenotypicFeature2.getResolution());
    }

    @Test
    void checkDiseaseCreation() throws IOException, GeneralSecurityException, URISyntaxException{ 

        List<Disease> diseases = covidCase.createCovidDisease();
        Disease disease1 = diseases.get(0);
        Disease disease2 = diseases.get(1);

        Assertions.assertEquals(covidCase.term1, disease1.getTerm());
        Assertions.assertEquals(covidCase.excluded, disease1.getExcluded());
        Assertions.assertEquals(covidCase.diseaseStage, disease1.getDiseaseStage(0));
        Assertions.assertEquals(covidCase.primarySite, disease1.getPrimarySite());
        Assertions.assertNotEquals(covidCase.isoAge, disease1.getOnset());
        
        Assertions.assertEquals(covidCase.term2, disease2.getTerm());
        Assertions.assertEquals(covidCase.excluded, disease2.getExcluded());
        Assertions.assertEquals(covidCase.diseaseStage, disease2.getDiseaseStage(0));
        Assertions.assertEquals(covidCase.primarySite, disease2.getPrimarySite());
        Assertions.assertNotEquals(covidCase.isoAge, disease2.getOnset());
    }

    @Test
    void checkMedicalActionCreation() throws IOException, GeneralSecurityException, URISyntaxException{ 

        List<MedicalAction> medicalActions = covidCase.createCovidMedicalActions();

        MedicalAction procedure = medicalActions.get(0);
        MedicalAction treatment = medicalActions.get(1);

        Assertions.assertEquals(covidCase.code, procedure.getProcedure().getCode());
        Assertions.assertEquals(covidCase.bodySite, procedure.getProcedure().getBodySite());
        Assertions.assertEquals(covidCase.performed, procedure.getProcedure().getPerformed());

        Assertions.assertEquals(covidCase.agent, treatment.getTreatment().getAgent());
        Assertions.assertEquals(covidCase.routeOfAdministration, treatment.getTreatment().getRouteOfAdministration());
        Assertions.assertEquals(covidCase.unit, treatment.getTreatment().getDoseIntervals(0).getQuantity().getUnit());
        Assertions.assertEquals(covidCase.value, treatment.getTreatment().getDoseIntervals(0).getQuantity().getValue());
        Assertions.assertEquals(covidCase.schedule, treatment.getTreatment().getDoseIntervals(0).getScheduleFrequency());
        Assertions.assertEquals(covidCase.interval, treatment.getTreatment().getDoseIntervals(0).getInterval());
    }

    @Test 
    void checkMetadaCreation() throws IOException, GeneralSecurityException, URISyntaxException{

        //MetaData
        MetaData metaData = covidCase.createCovidMetaData();
        TemporalAccessor date = DateTimeFormatter.ISO_DATE_TIME.parse(covidCase.created);
        Instant instant = Instant.from(date);
        
        Assertions.assertEquals(instant.getEpochSecond(), metaData.getCreated().getSeconds());
        Assertions.assertEquals(instant.getNano(), metaData.getCreated().getNanos());

        Assertions.assertEquals(covidCase.createdBy, metaData.getCreatedBy());
        Assertions.assertEquals(covidCase.submittedBy, metaData.getSubmittedBy());
        Assertions.assertEquals(covidCase.phenopacketSchemaVersion, metaData.getPhenopacketSchemaVersion());
    }

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

        Assertions.assertEquals(covidPhenopacket, phenopacket);
    }

    @Test
    void checkJsonExportation() throws IOException, GeneralSecurityException, URISyntaxException{
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        SecurePhenopacket.exportPhenopacket(phenopacket);  
    }

    @Test
    void checkImportPhenopacketFunction() throws IOException, GeneralSecurityException, URISyntaxException, ParseException{
        ExternalResources externalResource = new ExternalResources();
        Phenopacket phenopacket = covidCase.covid19Phenopacket();

        String path = externalResource.getNewPath(phenopacket.getId(), ".json");
        Phenopacket phenopacketFromFile = SecurePhenopacket.importPhenopacket(path);
        System.out.println(phenopacketFromFile);

        Assertions.assertEquals(phenopacket, phenopacketFromFile);
    }
    
}

