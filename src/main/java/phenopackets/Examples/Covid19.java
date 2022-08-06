package phenopackets.Examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.DoseInterval;
import org.phenopackets.secure.schema.core.Evidence;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.PhenotypicFeature;
import org.phenopackets.secure.schema.core.Procedure;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.TimeInterval;
import org.phenopackets.secure.schema.core.Treatment;
import org.phenopackets.secure.schema.core.Update;
import org.phenopackets.secure.schema.core.VitalStatus;
import org.phenopackets.secure.schema.core.VitalStatus.Status;

import com.google.protobuf.Timestamp;

import phenopackets.BlockBuilder;
import phenopackets.MainElements;
import phenopackets.SecurePhenopacket;

public class Covid19 {

  //PhenopacketID
  private String phenopacketID = SecurePhenopacket.generatePhenopacketId();

  // Individual Values
  public String isoAge = "P70Y";
  public int karyorypicSex = 2;

  // PhenotypicFeatures values
  public OntologyClass phenotypicType1 = BlockBuilder.createOntologyClass("NCIT:C27009", "Myalgia");
  public OntologyClass phenotypicType2 = BlockBuilder.createOntologyClass("NCIT:C2998","Dyspnea");
  public TimeElement onset = BlockBuilder.creaTimeElementTimestamp("2020-03-18T00:00:00Z");
  public TimeElement resolution = BlockBuilder.creaTimeElementTimestamp("2020-03-20T00:00:00Z");
  // Values not included in the example 
  public OntologyClass severity = BlockBuilder.createOntologyClass("HP:0012828","Severe");
  Evidence evidence = BlockBuilder.createEvidence("ECO:0006017", "author statement from published clinical study used in manual assertion");

  //MedicalActions values
  public OntologyClass code = BlockBuilder.createOntologyClass("NCIT:C80473","Left Ventricular Assist Device");
  public TimeElement  performed = BlockBuilder.creaTimeElementTimestamp("2016-01-01T00:00:00Z");
  public OntologyClass agent = BlockBuilder.createOntologyClass("NCIT:C722", "Oxygen");
  public OntologyClass routeOfAdministration = BlockBuilder.createOntologyClass("NCIT:C38284", "Nasal Route of Administration");
  public OntologyClass unit = BlockBuilder.createOntologyClass("NCIT:C67388", "Liter per Minute");

  public double value = 2.0;
  public String intervalStart = "2020-03-20T00:00:00Z";
  public String intervalEnd = "2020-03-22T00:00:00Z";

  //Values not included in the example
  public OntologyClass bodySite = BlockBuilder.createOntologyClass("UBERON:0003403", "skin of forearm");
  public OntologyClass schedule = BlockBuilder.createOntologyClass("NCIT:C64496","Twice Daily");
  
  // Disease values
  OntologyClass term1 = BlockBuilder.createOntologyClass("NCIT:C2985", "Diabetes Mellitus");
  OntologyClass term2 = BlockBuilder.createOntologyClass("NCIT:C34830", "Cardiomyopathy");
  Boolean excluded = true;

  // Values not included in the example
  OntologyClass primarySite = BlockBuilder.createOntologyClass("UBERON:0003403", "skin of forearm");
  OntologyClass diseaseStage = BlockBuilder.createOntologyClass("NCIT:C27971", "Stage IV");

  // MetaData values
  public String phenopacketSchemaVersion = "2.0";
  public String id = "ncit";
  public String name = "NCI Thesaurus OBO Edition";
  public String url = "http://purl.obolibrary.org/obo/ncit.owl";
  public String version = "http://purl.obolibrary.org/obo/ncit/releases/2019-11-26/ncit.owl";
  public String namespacePrefix = "NCIT";
  //Values not included in the example
  public String created = "2021-05-11T15:07:16.662Z";
  public String createdBy = "Peter R.";
  public String submittedBy = "Peter R.";
  

  public TimeElement createAge() throws IOException, GeneralSecurityException, URISyntaxException{
    
    //Create the Age block with hybrid encryption
    TimeElement age = BlockBuilder.createTimeElementAge(isoAge.getBytes(), phenopacketID.getBytes());

    System.out.println("The value of the encrypted age is:");
    System.out.println(age);

    return age;
  }
  
  public Individual createCovidSubject() throws IOException, GeneralSecurityException, URISyntaxException{
    // Create the age block
    TimeElement age = createAge();
    // Assign vital status
    VitalStatus vitalStatus = VitalStatus.newBuilder().setStatus(Status.DECEASED).build();
    // Create indivual element
    Individual individual = MainElements.createSubject(age, vitalStatus, karyorypicSex);

    return individual;
  }
  
  public List<PhenotypicFeature> createCovidPhenotypicFeatures(){
    
    List<PhenotypicFeature> phenotypicFeatures = new ArrayList<PhenotypicFeature>();
    List<Evidence> evidences =  new ArrayList<Evidence>();
    evidences.add(evidence);

    PhenotypicFeature phenotypicFeature1 = MainElements.createPhenotypicFeature(phenotypicType1, severity, evidences, onset,resolution);
    PhenotypicFeature phenotypicFeature2 = MainElements.createPhenotypicFeature(phenotypicType1, severity, evidences, onset,resolution);

    phenotypicFeatures.add(phenotypicFeature1);
    phenotypicFeatures.add(phenotypicFeature2);

    return phenotypicFeatures;
  }

  public List<MedicalAction> createCovidMedicalActions(){
    
    List<MedicalAction> medicalActions = new ArrayList<MedicalAction>();
    List<DoseInterval> doseIntervals = new ArrayList<DoseInterval>();
    
    TimeInterval interval = BlockBuilder.createTimeInterval(intervalStart, intervalEnd);

    DoseInterval doseInterval = BlockBuilder.setDoseInterval(BlockBuilder.setQuantity(unit, value), schedule, interval);
    doseIntervals.add(doseInterval);
    
    Procedure procedure = BlockBuilder.setProcedure(code, bodySite, performed);
    Treatment treatment = BlockBuilder.setTreatment(agent, routeOfAdministration, doseIntervals);

    MedicalAction medicalActionProcedure = MainElements.createProcedure(procedure);
    MedicalAction medicalActionTreatment = MainElements.createTreatment(treatment);

    medicalActions.add(medicalActionProcedure);
    medicalActions.add(medicalActionTreatment);

    return medicalActions;
  }

  private  List<Disease> createCovidDisease() throws IOException, GeneralSecurityException, URISyntaxException{
    
    List<Disease> diseases = new ArrayList<Disease>();
    List<OntologyClass> diseaseStages = new ArrayList<OntologyClass>();
    
    diseaseStages.add(diseaseStage);
    TimeElement onset = BlockBuilder.createTimeElementAge(isoAge.getBytes(), phenopacketID.getBytes());
    Disease disease1 = MainElements.createDisease(term1, excluded, diseaseStages, primarySite, onset);
    Disease disease2 = MainElements.createDisease(term2, false, diseaseStages, primarySite, onset);
    
    diseases.add(disease1);
    diseases.add(disease2);

    return diseases;
  }

  public MetaData createCovidMetaData() throws IOException, GeneralSecurityException, URISyntaxException{
    List<Resource> resources =  new ArrayList<Resource>();
    List<Update> updates = new ArrayList<Update>();

    Timestamp timestamp = BlockBuilder.createTimestamp(created);

    Update update = Update.newBuilder().setTimestamp(timestamp).build();
    updates.add(update);
    
    Resource resource = BlockBuilder.createResource(id, name, namespacePrefix, url, version, url);
    resources.add(resource);

    MetaData metaData = MainElements.createMetaData(timestamp, createdBy, submittedBy, resources, updates, phenopacketSchemaVersion);

    return metaData;
  }

  public Phenopacket covid19Phenopacket() throws IOException, GeneralSecurityException, URISyntaxException{
    return SecurePhenopacket.createPhenopacket(id, createCovidSubject(), createCovidPhenotypicFeatures(), createCovidMetaData(), createCovidDisease(), createCovidMedicalActions());
  }
  
}