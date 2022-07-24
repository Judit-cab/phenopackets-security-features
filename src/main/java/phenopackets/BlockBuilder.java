package phenopackets;

import org.phenopackets.secure.schema.core.Age;
import org.phenopackets.secure.schema.core.DoseInterval;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.Quantity;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.TimeInterval;

import com.google.protobuf.Timestamp;

import phenopackets.securityFeatures.HybridEncryption;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Base64;


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
     * Method to create a new TimeELement that includes a Timestamp element
     * @param isoDate required
     * @return Time Element
     */
    public static TimeElement creaTimeElementTimestamp(String isoDate){
        
        //Change type String to Long 
        long time = Long.parseLong(isoDate);
        
        // Create timestamp element
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time).build();

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
        
        //Change type String to Long 
        long timeStart = Long.parseLong(isoDateStart);
        long timeEnd = Long.parseLong(isoDateEnd);
        
        // Create timestamp element
        Timestamp timestampStart = Timestamp.newBuilder().setSeconds(timeStart).build();
        Timestamp timestampEnd = Timestamp.newBuilder().setSeconds(timeEnd).build();
        
         // Create and return TimeInterval
        return TimeInterval.newBuilder()
            .setStart(timestampStart)
            .setEnd(timestampEnd)
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
