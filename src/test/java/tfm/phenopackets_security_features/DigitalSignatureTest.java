package tfm.phenopackets_security_features;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Age;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Evidence;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.PhenotypicFeature;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.Update;
import org.phenopackets.secure.schema.core.VitalStatus;
import org.phenopackets.secure.schema.core.VitalStatus.Status;

import com.google.protobuf.Timestamp;
import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.ElementsBuilder;
import phenopackets.blocksBuilder;
import phenopackets.securePhenopacket;

public class DigitalSignatureTest {

    @Test
    
    void checkDigitalSignature() throws IOException, URISyntaxException, ParseException{
        Phenopacket phenopacket;
        // create subject
        String isoAge = "P70Y";
        Age age = Age.newBuilder().setIso8601Duration(isoAge).build();
        TimeElement element = TimeElement.newBuilder().setAge(age).build();
        VitalStatus vitalStatus = VitalStatus.newBuilder().setStatus(Status.ALIVE).build();
        Individual subject = ElementsBuilder.createSubjectWithId("patient", element, vitalStatus);

        //Create phenotypic feature
        OntologyClass type = blocksBuilder.ontologyClass("id", "label");
        OntologyClass severity = blocksBuilder.ontologyClass("id", "label");
        List<Evidence> evidence =  new ArrayList<Evidence>();
        PhenotypicFeature phenotypic = ElementsBuilder.phenotypicFeature(type, severity, evidence, element, element);
        List<PhenotypicFeature> phenotypics = new ArrayList<PhenotypicFeature>();
        phenotypics.add(phenotypic);

        //create metadata
        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        String epochString = "1081157732";
        long epoch = Long.parseLong( epochString );
      
        Timestamp time = Timestamp.newBuilder().setSeconds(epoch).build();
        Resource resource = blocksBuilder.resource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
        resources.add(resource);
        Update update = Update.newBuilder().setTimestamp(time).build();
        updates.add(update);

        MetaData metaData = ElementsBuilder.createMetaData(time, "Peter R.", "Peter S.", resources, updates, "2.0");

        //Create disease
        List<OntologyClass> stages = new ArrayList<OntologyClass>();
        Disease disease = ElementsBuilder.disease(type, true, stages, stages, type, element);
        List<Disease> diseases = new ArrayList<Disease>();
        diseases.add(disease);

        //Create medicalAction
        List<MedicalAction> medicalActions = new ArrayList<MedicalAction>();
        phenopacket = securePhenopacket.createPhenopacket("123456", subject, phenotypics, metaData, diseases, medicalActions);
        
        try{
            securePhenopacket.signPhenopacket(phenopacket);
            System.out.println("Phenopacket successfully signed");
        }catch(Exception ex){
            System.out.println(ex);
        }

        try{
            securePhenopacket.verifyPhenopacket(phenopacket);
            System.out.println("Phenopacket successfully verified");
        }catch(Exception ex){
            System.out.println(ex);
        }

        
    }

    
}