package phenopackets.schema;

import org.phenopackets.secure.schema.core.*;

import com.google.protobuf.Timestamp;

import phenopackets.securityMechanisms.HybridEncryption;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Base64;
import java.util.List;


public class BlockBuilder {

    private static final String MODE_ENC = "encrypt"; 
    private static final String MODE_DEC = "decrypt"; 
    
    public BlockBuilder() {
    }

    /**
     * Method to create a new Ontology Class
     * @param id required 
     * @param label required
     * @return Ontology Class
     */
    public static OntologyClass createOntologyClass (String id, String label){
        
        return OntologyClass.newBuilder()
            .setId(id)
            .setLabel(label)
            .build();
    }

    /**
     * Method to create a new Resource
     * @param id required 
     * @param name required 
     * @param namespace required 
     * @param url required 
     * @param version required 
     * @param iri required 
     * @return Resource element
     */
    public static Resource createResource (String id, String name, String namespace, String url, String version, String iri){
        
        return Resource.newBuilder()
            .setId(id)
            .setName(name)
            .setNamespacePrefix(namespace)
            .setVersion(version)
            .setUrl(url)
            .setIriPrefix(iri)
            .build();
    }

    public static Evidence createEvidence(String codeId, String label){
        OntologyClass evidenceCode = createOntologyClass(codeId, label); 
        
        return Evidence.newBuilder()
            .setEvidenceCode(evidenceCode)
            .build(); 
    }

    /**
     * Method to set new Quantity of items such as medications.
     * @param unit required
     * @param value required
     * @return  Quantity element
     */
    public static Quantity setQuantity(OntologyClass unit, Double value){
        
        return Quantity.newBuilder()
            .setUnit(unit)
            .setValue(value)
            .build();
    }


    /**
     * Method to set Dose Interval of a medication
     * @param quantity required
     * @param schedule required
     * @param interval required
     * @return Dose Intarval element
     */
    public static DoseInterval setDoseInterval (Quantity quantity, OntologyClass schedule, TimeInterval interval){
        
        return DoseInterval.newBuilder()
            .setQuantity(quantity)
            .setScheduleFrequency(schedule)
            .setInterval(interval)
            .build();
    }


    /**
     * Method to create a new TimeElement that includes the Age element
     * @param isoDuration required
     * @param context required being the Phenopacket ID
     * @return Time Element
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static TimeElement createTimeElementAge(byte[] isoDuration, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        // Encrypt the age and store it in Base64
        byte[] cipherBytes = HybridEncryption.hybridEncryption(MODE_ENC, isoDuration, context);
        String cipherAge = Base64.getEncoder().encodeToString(cipherBytes);

        // Create age element
        Age age = Age.newBuilder().setIso8601Duration(cipherAge).build();

        // Create and return TimeELement
        return TimeElement.newBuilder().setAge(age).build();
    }

    /**
     * Method to create a new Timestamp
     * @param isoDate required 
     * @return a timestamp element
     */
    public static Timestamp createTimestamp(String isoDate){
        TemporalAccessor date = DateTimeFormatter.ISO_DATE_TIME.parse(isoDate);
        
        Instant instant = Instant.from(date);

        // Create timestamp element
        return Timestamp.newBuilder()
            .setSeconds(instant.getEpochSecond())
            .setNanos(instant.getNano())
            .build();
    }

    /**
     * Method to create a new TimeELement that includes a Timestamp element
     * @param isoDate required
     * @return Time Element
     */
    public static TimeElement creaTimeElementTimestamp(String isoDate){

        // Create timestamp element
        Timestamp timestamp = createTimestamp(isoDate);
        // Create and return TimeELement
        return TimeElement.newBuilder().setTimestamp(timestamp).build();

    }

    /**
     * Create Time Interval element
     * @param isoDateStart required
     * @param isoDateEnd required
     * @return
     */
    public static TimeInterval createTimeInterval(String isoDateStart, String isoDateEnd){

        // Create timestamp element
        Timestamp timestampStart = createTimestamp(isoDateStart);
        Timestamp timestampEnd = createTimestamp(isoDateEnd);
        
         // Create and return TimeInterval
        return TimeInterval.newBuilder()
            .setStart(timestampStart)
            .setEnd(timestampEnd)
            .build();
    }


    /**
     * Method to set the action Procedure
     * @param code required
     * @param bodySite
     * @param performed
     * @return new Procedure action
     */
    public static Procedure setProcedure (OntologyClass code, OntologyClass bodySite, TimeElement performed){
        
        return Procedure.newBuilder()
            .setCode(code)
            .setBodySite(bodySite)
            .setPerformed(performed)
            .build();
    }

    /**
     * Method to set the action Treatment
     * @param agent required
     * @param adminRoute recommended
     * @param doseIntervals recommended
     * @return new Treatment action
     */
    public static Treatment setTreatment (OntologyClass agent, OntologyClass adminRoute, List<DoseInterval> doseIntervals){
        
        return Treatment.newBuilder()
            .setAgent(agent)
            .setRouteOfAdministration(adminRoute)
            .addAllDoseIntervals(doseIntervals)
            .build();
    }

    /**
     * Method to set the action Radiation Therapy
     * @param modality required
     * @param bodySite required
     * @param dosage required 
     * @param fractions required
     * @return new RadiationTherapy action
     */
    public static RadiationTherapy setRadiationTheraphy(OntologyClass modality, OntologyClass bodySite, int dosage, int fractions){
        
        return RadiationTherapy.newBuilder()
            .setModality(modality)
            .setBodySite(bodySite)
            .setDosage(dosage)
            .setFractions(fractions)
            .build();
    }

    /**
     * Method to set the action Therapeutic Regimen 
     * @param id required
     * @param startTime recommended
     * @param endTime recommended 
     * @param status required - Values : Unkown, started, completed and discontinued
     * @return new TherapeuticRegimen action
     */
    public static TherapeuticRegimen setTherapeuticRegimen(OntologyClass id, TimeElement startTime, TimeElement endTime, int status){
        return TherapeuticRegimen.newBuilder()
            .setOntologyClass(id)
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setRegimenStatusValue(status)
            .build();
    }

    /**
     * Method to obtain the age from the TimeElement object
     * @param timeElement required
     * @param context required, being the Phenopacket ID
     * @return Value of age
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws URISyntaxException
     */
    public static String getAge(TimeElement timeElement, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        
        // Get age from TimeElement and the corresponding value 
        Age ageElement = timeElement.getAge();
        String isoDuration = ageElement.getIso8601Duration();
        
        //Decrypt age
        byte[] ageBytes = Base64.getDecoder().decode(isoDuration);
        byte[] age = HybridEncryption.hybridEncryption(MODE_DEC, ageBytes, context);

        return new String(age);
    }

}