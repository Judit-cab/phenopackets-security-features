package phenopackets;


import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import org.phenopackets.secure.schema.core.Age;
import org.phenopackets.secure.schema.core.DoseInterval;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.Quantity;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeElement;
import org.phenopackets.secure.schema.core.TimeInterval;

import com.google.protobuf.Timestamp;

import phenopackets.securityFeatures.HybridEncryption;


public class BlockBuilder {

    private static final String MODE_ENC = "encrypt"; 
    private static final String MODE_DEC = "decrypt"; 
    
    public BlockBuilder() {
    }

    public static OntologyClass createOntologyClass (String id, String label){
        return OntologyClass.newBuilder()
            .setId(id)
            .setLabel(label)
            .build();
    }


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

    public static Quantity quantity(OntologyClass unit, Double value){
        return Quantity.newBuilder()
            .setUnit(unit)
            .setValue(value)
            .build();
    }

    public static DoseInterval doseInterval (Quantity quantity, OntologyClass schedule, TimeInterval interval){
        return DoseInterval.newBuilder()
            .setQuantity(quantity)
            .setScheduleFrequency(schedule)
            .setInterval(interval)
            .build();
    }


    public static TimeElement creaTimeElementAge(byte[] isoAge, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        
        byte[] cipher = HybridEncryption.hybridEncryption(MODE_ENC, isoAge, context);
        String cipherAge = Base64.getEncoder().encodeToString(cipher);
        Age age = Age.newBuilder().setIso8601Duration(cipherAge).build();

        return TimeElement.newBuilder().setAge(age).build();
    }

    public static TimeElement creaTimeElementTimestamp(String timeString){
        
        long time = Long.parseLong(timeString);

        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time).build();

        return TimeElement.newBuilder().setTimestamp(timestamp).build();

    }

    public static TimeInterval createTimeInterval(String timeStringStart, String timeStringtimeEnd){
        long timeStart = Long.parseLong(timeStringStart);
        long timeEnd = Long.parseLong(timeStringtimeEnd);

        Timestamp timestampStart = Timestamp.newBuilder().setSeconds(timeStart).build();
        Timestamp timestampEnd = Timestamp.newBuilder().setSeconds(timeEnd).build();

        return TimeInterval.newBuilder()
            .setStart(timestampStart)
            .setEnd(timestampEnd)
            .build();
    }

    public static String getAge(TimeElement timeElement, byte[] context) throws IOException, GeneralSecurityException, URISyntaxException{
        Age age = timeElement.getAge();

        String isoAge = age.getIso8601Duration();
        byte[] bytesAge = Base64.getDecoder().decode(isoAge);
        byte[] plainAge = HybridEncryption.hybridEncryption(MODE_DEC, bytesAge, context);

        return new String(plainAge);
    }


}
