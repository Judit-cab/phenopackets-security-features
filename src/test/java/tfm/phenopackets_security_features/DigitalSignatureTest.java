package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.*;
import org.phenopackets.secure.schema.core.VitalStatus.Status;

import com.google.protobuf.Timestamp;
import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.MainElements;
import phenopackets.BlockBuilder;
import phenopackets.SecurePhenopacket;

public class DigitalSignatureTest {

    @Test
    
    void checkDigitalSignature() throws IOException, URISyntaxException, ParseException, GeneralSecurityException{
        
        String id = SecurePhenopacket.generatePhenopacketId();

        System.out.println("The unique identifiers is :" + id);
        
        // Create Individual subject
        String isoDuration = "P27Y3M";
        TimeElement age = BlockBuilder.createTimeElementAge(isoDuration.getBytes(), id.getBytes());
        VitalStatus vitalStatus = VitalStatus.newBuilder().setStatus(Status.ALIVE).build();
        Individual individual = MainElements.createSubject(age, vitalStatus, 4);

        System.out.println(individual);

        //Create phenotypic feature
        OntologyClass type = BlockBuilder.createOntologyClass("HP:0000952", "Jaundice");
        OntologyClass severity = BlockBuilder.createOntologyClass("id", "label");
        List<Evidence> evidence =  new ArrayList<Evidence>();
        TimeElement element = BlockBuilder.creaTimeElementTimestamp("1081158832");
        PhenotypicFeature phenotypic = MainElements.createPhenotypicFeature(type, severity, evidence, element, element);
        List<PhenotypicFeature> phenotypics = new ArrayList<PhenotypicFeature>();
        phenotypics.add(phenotypic);

        //create metadata
        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        String epochString = "1081157732";
        long epoch = Long.parseLong( epochString );
      
        Timestamp time = Timestamp.newBuilder().setSeconds(epoch).build();
        Resource resource = BlockBuilder.createResource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
        resources.add(resource);
        Update update = Update.newBuilder().setTimestamp(time).build();
        updates.add(update);

        MetaData metaData = MainElements.createMetaData(time, "Peter R.", "Peter S.", resources, updates, "2.0");

        //Create disease
        List<OntologyClass> stages = new ArrayList<OntologyClass>();
        Disease disease = MainElements.createDisease(type, true, stages, stages, type, element);
        List<Disease> diseases = new ArrayList<Disease>();
        diseases.add(disease);

        //Create medicalAction
        List<MedicalAction> medicalActions = new ArrayList<MedicalAction>();
        Phenopacket phenopacket = SecurePhenopacket.createPhenopacket(id, individual, phenotypics, metaData, diseases, medicalActions);
        
        try{
            SecurePhenopacket.signPhenopacket(phenopacket);
            System.out.println("Phenopacket successfully signed");
        }catch(Exception ex){
            System.out.println(ex);
        }

        try{
            SecurePhenopacket.verifyPhenopacket(phenopacket);
            System.out.println("Phenopacket successfully verified");
        }catch(Exception ex){
            System.out.println(ex);
        }
        
    }
    
}