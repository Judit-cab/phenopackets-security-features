package phenopackets.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Evidence;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.PhenotypicFeature;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.Update;
import org.phenopackets.secure.schema.core.VitalStatus;
import org.phenopackets.secure.schema.core.VitalStatus.Status;

import com.google.protobuf.Timestamp;

import phenopackets.schema.BlockBuilder;
import phenopackets.schema.MainElements;

public class Oncology {

    //PhenopacketID
    private String phenopacketId = "f9f2d029-e1e3-42a4-bb79-ee39652c8c07";

    // Individual values
    public int karyorypicSex = 2;
    public String isoAge = "P58Y";

    // PhenotypicFeatures values
    OntologyClass phenotypicType1 = BlockBuilder.createOntologyClass("HP:0000790", "Hematuria");
    OntologyClass phenotypicType2 = BlockBuilder.createOntologyClass("HP:0100518","Dysuria");
    OntologyClass severity = BlockBuilder.createOntologyClass("HP:0012828", "Severe");
    // Values not included in the example 
    public String onsetTime = "2020-03-18T00:00:00Z";
    public String resolutionTime = "2020-03-20T00:00:00Z";
    Evidence evidence = BlockBuilder.createEvidence("ECO:0006017", "author statement from published clinical study used in manual assertion");

    // Diseases values
    OntologyClass term = BlockBuilder.createOntologyClass("NCIT:C39853", "Infiltrating Urothelial Carcinoma");
    OntologyClass diseaseStage = BlockBuilder.createOntologyClass("NCIT:C27971", "Stage IV");
    OntologyClass clinicalTnmFinding1 = BlockBuilder.createOntologyClass("NCIT:C48766", "pT2b Stage Finding");
    OntologyClass clinicalTnmFinding2 = BlockBuilder.createOntologyClass("NCIT:C48750", "pN2 Stage Finding");
    // Values not included in the example
    OntologyClass primarySite = BlockBuilder.createOntologyClass("UBERON:0001255", "urinary bladder");
   
    // MetaData values
    Timestamp created = BlockBuilder.createTimestamp("2021-05-11T15:07:16.662Z");
    public String createdBy = "Peter R.";
    public String submittedBy = "Peter R.";
    Resource resource1 = BlockBuilder.createResource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
    Resource resource2 = BlockBuilder.createResource("uberon", "uber anatomy ontology", "UBERON", "http://purl.obolibrary.org/obo/uberon.owl", "2019-03-08", "http://purl.obolibrary.org/obo/UBERON_");
    public String phenopacketSchemaVersion = "2.0";


    public Individual createOncologyIndividual() throws IOException, GeneralSecurityException, URISyntaxException{
        // Create the age block
        TimeElement age = BlockBuilder.createTimeElementAge(isoAge.getBytes(), phenopacketId.getBytes());
        // Assign vital status
        VitalStatus vitalStatus = VitalStatus.newBuilder().setStatus(Status.ALIVE).build();
        // Create indivual element
        Individual individual = MainElements.createSubject(age, vitalStatus, karyorypicSex);
    
        return individual;
    }

    public List<PhenotypicFeature> createOncologyPhenotypicFeature(){
        List<PhenotypicFeature> phenotypicFeatures = new ArrayList<PhenotypicFeature>();
        List<Evidence> evidences =  new ArrayList<Evidence>();

        evidences.add(evidence);
        TimeElement onset = BlockBuilder.creaTimeElementTimestamp(onsetTime);
        TimeElement resolution = BlockBuilder.creaTimeElementTimestamp(resolutionTime);

        PhenotypicFeature phenotypicFeature1 = MainElements.createPhenotypicFeature(phenotypicType1, severity, evidences, onset,resolution);
        PhenotypicFeature phenotypicFeature2 = MainElements.createPhenotypicFeature(phenotypicType2, severity, evidences, onset,resolution);

        phenotypicFeatures.add(phenotypicFeature1);
        phenotypicFeatures.add(phenotypicFeature2);

        return phenotypicFeatures;

    }

    public List<Disease> createOncologyDisease() throws IOException, GeneralSecurityException, URISyntaxException{
        List<Disease> diseases = new ArrayList<Disease>();
        List<OntologyClass> diseaseStages = new ArrayList<OntologyClass>();
        List<OntologyClass> tnmFindings = new ArrayList<OntologyClass>();

        diseaseStages.add(diseaseStage);
        tnmFindings.add(clinicalTnmFinding1);
        tnmFindings.add(clinicalTnmFinding2);
        TimeElement onset = BlockBuilder.createTimeElementAge(isoAge.getBytes(), phenopacketId.getBytes());
        Disease disease = MainElements.createOncologicalDisease(term, false, diseaseStages, tnmFindings, primarySite, onset);

        diseases.add(disease);
        return diseases;

    }

    public MetaData createOncologyMetaData() throws IOException, GeneralSecurityException, URISyntaxException{

        // Create the variables needed for the creation
        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        resources.add(resource1);
        resources.add(resource2);
        
        Update update = Update.newBuilder().setTimestamp(created).build();
        updates.add(update);

        //Create the metadata element
        MetaData metaData = MainElements.createMetaData(created, createdBy, submittedBy, resources, updates, phenopacketSchemaVersion);
        
        return metaData;
    }

    public Phenopacket createOncologyPhenopacket() throws IOException, GeneralSecurityException, URISyntaxException{
        return Phenopacket.newBuilder()
            .setId(phenopacketId)
            .setSubject(createOncologyIndividual())
            .addAllPhenotypicFeatures(createOncologyPhenotypicFeature())
            .addAllDiseases(createOncologyDisease())
            .setMetaData(createOncologyMetaData())
            .build();
    } 
}