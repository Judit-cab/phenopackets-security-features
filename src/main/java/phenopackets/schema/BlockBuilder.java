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
     * @param id required - CURIE identifier
     * @param label required - Ontology name
     * @return Ontology Class element
     */
    public static OntologyClass createOntologyClass (String id, String label){
        
        return OntologyClass.newBuilder()
            .setId(id)
            .setLabel(label)
            .build();
    }

    /**
     * Method to create a new Resource
     * @param id required - irentifier
     * @param name required - resource nam
     * @param namespace required - resource namespace
     * @param url required - Uniform Resource Locator
     * @param version required - resource version
     * @param iri required - Internationalized Resource Identifier
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

    /**
     * Method to create a new Evidence
     * @param codeId required - evidence type
     * @param label required - name of the evidence
     * @return Evidence element
     */
    public static Evidence createEvidence(String codeId, String label){
        OntologyClass evidenceCode = createOntologyClass(codeId, label); 
        
        return Evidence.newBuilder()
            .setEvidenceCode(evidenceCode)
            .build(); 
    }

    /**
     * Method to set new Quantity of items such as medications.
     * @param unit required - kind of unit
     * @param value required - quantity value
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
     * @param quantity required - quantity element
     * @param schedule required - how often are administered
     * @param interval required - interval time of dosage
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
     * @param isoDuration required - iso8601
     * @param context required - the Phenopacket ID
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

        // Create and return TimeElement
        return TimeElement.newBuilder().setAge(age).build();
    }

    /**
     * Method to create a new Timestamp
     * @param isoDate required - iso8601 date time
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
     * @param isoDate required - iso8601 date time
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
     * @param isoDateStart required - iso8601 date time
     * @param isoDateEnd required - iso8601 date time
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
     * Method to set the Procedure action 
     * @param code required - procedure perfomed
     * @param bodySite - in which part of the body
     * @param performed - time when it was performed
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
     * Method to set the Treatment action
     * @param agent required - drug 
     * @param adminRoute recommended - how it was administered
     * @param doseIntervals recommended - dosages
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
     * Method to set the Radiation Therapy action
     * @param modality required - modality of RT
     * @param bodySite required - anatomical site
     * @param dosage required - total dose
     * @param fractions required - number of fractions
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
     * Method to set the Therapeutic Regimen action
     * @param id required - identifier
     * @param startTime recommended - when it started
     * @param endTime recommended - when it ended
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
     * @param timeElement required - age value
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
        
        // Decrypt age
        byte[] ageBytes = Base64.getDecoder().decode(isoDuration);
        byte[] age = HybridEncryption.hybridEncryption(MODE_DEC, ageBytes, context);

        // Return the Age value as String 
        return new String(age);
    }

}