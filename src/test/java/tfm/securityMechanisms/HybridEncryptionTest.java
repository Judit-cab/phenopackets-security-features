package tfm.securityMechanisms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.TimeElement;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.SecurePhenopacket;
import phenopackets.examples.Covid19;
import phenopackets.securityMechanisms.HybridEncryption;
import phenopackets.schema.BlockBuilder;
import phenopackets.schema.MainElements;

public class HybridEncryptionTest {

    Covid19 covidCase = new Covid19();
    
    /*
     * TEST: encrypt age block 
     * The expected value has to be different from isoAge = "P70Y"
     */
    @Test
    void encryptAge() throws IOException, GeneralSecurityException, URISyntaxException{

        //Create the Age block with hybrid encryption
        TimeElement age = covidCase.createAge();
        
        System.out.println("The value of the encrypted age is:");
        System.out.println(age);
        //Get the iso8601 value
        String iso8601 = age.getAge().getIso8601Duration();
        
        
        //If the encryption is correct, the values will be not equal
        Assertions.assertNotEquals(iso8601, covidCase.isoAge, 
        "Expected different values, but the result is equals");
    }

    /*
     * TEST: check if the decryption works for Age element
     * Expected value: isoAge = "P70Y"
     */
    @Test
    void checkAgeDecryption() throws IOException, GeneralSecurityException, URISyntaxException { 
        
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = phenopacket.getId();

        TimeElement age = phenopacket.getSubject().getTimeAtLastEncounter();
        String plainAge = BlockBuilder.getAge(age, phenopacketId.getBytes());
        
        System.out.println("After decryption, the same age value is obtained: " + plainAge);
        
        Assertions.assertEquals(covidCase.isoAge, plainAge, 
        "Expected value isoAge = P70Y");
    }

    /*
     * TEST: Encrypt and decrypt Phenopacket creator stored in metaData element
     * Value to check: createdBy = "Judit C.";
     */
    @Test 
    void createMetadaProtectingCreator() throws IOException, GeneralSecurityException, URISyntaxException{

        // Get Covid19 Phenopacket 
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = phenopacket.getId();
        
        // Get non-encrypted MetaData
        MetaData metaData = phenopacket.getMetaData();

        // This method provides hybrid encryption
        MetaData metaDataProtectingCreator = MainElements.protectedMetaDataCreator(metaData.getCreated(), metaData.getCreatedBy(), metaData.getSubmittedBy(), metaData.getResourcesList(), metaData.getUpdatesList(), metaData.getPhenopacketSchemaVersion(), phenopacketId.getBytes());
        
        System.out.println("The value of the encrypted createdBy is:");
        System.out.println(metaDataProtectingCreator.getCreatedBy());

        Assertions.assertNotEquals(metaData.getCreatedBy(), metaDataProtectingCreator.getCreatedBy(), 
        "Both elements are the same");

        // Function to decrypt the field
        String plaintext = MainElements.getMetaDataCreator(metaDataProtectingCreator, phenopacketId);
        System.out.println("After decryption, the original createdBy value is obtained: " + plaintext);
        
        Assertions.assertEquals(metaData.getCreatedBy(), plaintext, 
        "Expected value: createdBy = Judit C.");
    }


    /*
     * TEST: encryption and decryption of the metadata element
     * The values used can be found at covid19 class
     */
    @Test
    void checkMetaDataEncyption() throws IOException, GeneralSecurityException, URISyntaxException{
        
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = phenopacket.getId();

        MetaData metaData = phenopacket.getMetaData();
        
        byte[] cipherMetadata = MainElements.protectedMetaData(metaData, phenopacketId.getBytes());

        // Check if the original and the encrypted metadata are not the same
        Assertions.assertNotEquals(metaData.toByteArray(), cipherMetadata, "Both elements are the same");
        
        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketId.getBytes());
        
        System.out.println("After decryption, the original MetaData element is obtained: ");
        System.out.println(plainMetaData);
        
        Assertions.assertEquals(metaData, plainMetaData);
    }


    /*
     * TEST: 
     *  1. save both phenopacket and metadata, 
     *  2. get all the encrypted elements (metadata bytes and age element from Phenopacket)
     *  3. decrypt the elements to check if the process works well
     */
    @Test
    void getAndDecryptElementsFromFile() throws URISyntaxException, IOException, GeneralSecurityException, ParseException{
        
        // Create Covid19 Phenopacket 
        Phenopacket phenopacket = covidCase.covid19Phenopacket();
        String phenopacketId = phenopacket.getId();
        // Save the Phenopacket and the encrypted MetaData
        SecurePhenopacket.protectMetaData(phenopacket);

        // Get Phenopacket bytes from file
        byte[] phenopacketBytes = HybridEncryption.getCipherBytes("Phenopacket", phenopacketId);

        // Get encrypted MetaData
        byte[] cipherMetadata = HybridEncryption.getCipherBytes("Metadata", phenopacketId);

        // Decrypt MetaData
        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketId.getBytes());

        Phenopacket phenopacketFromFile = Phenopacket.parseFrom(phenopacketBytes);
        TimeElement age = phenopacketFromFile.getSubject().getTimeAtLastEncounter();
        String plainAge = BlockBuilder.getAge(age, phenopacketId.getBytes());

        // Checks 
        Assertions.assertNotEquals(phenopacket.getMetaData().toByteArray(), cipherMetadata, 
        "The two elements are the same");
        Assertions.assertEquals(phenopacket.getMetaData(), plainMetaData, 
        "Expected same MetaData element");
        Assertions.assertEquals(covidCase.isoAge, plainAge,"Expected value isoAge = P70Y");
    }
}