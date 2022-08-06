package tfm.phenopackets_security_features;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.TimeElement;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.MainElements;
import phenopackets.SecurePhenopacket;
import phenopackets.Examples.Covid19;
import phenopackets.securityFeatures.HybridEncryption;
import phenopackets.BlockBuilder;

public class HybridEncryptionTest {

    Covid19 covidCase = new Covid19();
    /*
     * Method to test if an encrypted age block is created correctly
     * The values used are extracted from COVID-19 example
     */
    @Test
    void checkcreationAge() throws IOException, GeneralSecurityException, URISyntaxException{

        //Create the Age block with hybrid encryption
        TimeElement age = covidCase.createAge();

        assertNotEquals(age, covidCase.isoAge);
    }

    /*
     * Methoc to check metadata can be created
     * The values used are from ONCOLOGY example
     */

    @Test 
    void checkcreationMetada() throws IOException, GeneralSecurityException, URISyntaxException{

        //MetaData
        MetaData metaData = covidCase.createCovidMetaData();
        
        Assertions.assertEquals(metaData.getCreated(), covidCase.created);
        Assertions.assertEquals(metaData.getCreatedBy(), covidCase.createdBy);
        Assertions.assertEquals(metaData.getSubmittedBy(), covidCase.submittedBy);
        Assertions.assertEquals(metaData.getPhenopacketSchemaVersion(), covidCase.phenopacketSchemaVersion);
    }

    /*
     * Methoc to check metadata can be created protecting its creator
     * The values used are from ONCOLOGY example
     */

    @Test 
    void createMetadaProtectingCreator(String id) throws IOException, GeneralSecurityException, URISyntaxException{

        //MetaData
        MetaData metaData = covidCase.createCovidMetaData();

        // This method provides the hybrid encryption
        MetaData metaDataProtectingCreator = MainElements.protectedMetaDataCreator(metaData.getCreated(),metaData.getCreatedBy(), metaData.getSubmittedBy(), metaData.getResourcesList(), metaData.getUpdatesList(),metaData.getPhenopacketSchemaVersion(), id.getBytes());
        
        assertNotEquals(metaData, metaDataProtectingCreator);
       
    }

    /*
     * Method to check an individual can be successfully created
     */

    void checkcreationIndividual() throws IOException, GeneralSecurityException, URISyntaxException{
    
        Individual individual = covidCase.createCovidSubject();

        Assertions.assertEquals(individual.getTimeAtLastEncounter(), covidCase.isoAge);
        Assertions.assertEquals(individual.getKaryotypicSexValue(), covidCase.karyorypicSex);
    }

    /*
     * Method to check if a phenopacket can be created
     * In this case, metadata is no protected
     */

    @Test
    Phenopacket createPhenopacket() throws IOException, GeneralSecurityException, URISyntaxException{
        
        // Create an arbitrary identifier
        String phenopacketID = SecurePhenopacket.generatePhenopacketId();

        
        // Create the subject
        Individual subject = covidCase.createCovidSubject();
     
        // Create the metadata element
        MetaData metaData = covidCase.createCovidMetaData();

        // Create and return a secure phenopacket
        return SecurePhenopacket.createPhenopacketToHybrydEncryption(phenopacketID, subject, metaData); 
    }

    /*
     * Method to check if a phenopacket can be created
     * In this case, the creator of the Phenopacket is protected
     */

    @Test
    Phenopacket createPhenopacketProtectingCreator() throws IOException, GeneralSecurityException, URISyntaxException{
        
        // Create an arbitrary identifier
        String phenopacketID = SecurePhenopacket.generatePhenopacketId();
        // Create the subject
        Individual subject = covidCase.createCovidSubject();
        // Create the metadata element
        MetaData metaData = covidCase.createCovidMetaData();

        // Create and return a secure phenopacket
        return SecurePhenopacket.createPhenopacketToHybrydEncryption(phenopacketID, subject, metaData); 
    }

    /*
     * Method to check if the decryption works 
     */

    @Test
    void checkAgeDecryption() throws IOException, GeneralSecurityException, URISyntaxException { 
        
        Phenopacket phenopacket = createPhenopacket();
        String phenopacketID = phenopacket.getId();
    
        TimeElement age = phenopacket.getSubject().getTimeAtLastEncounter();
        String plainAge = BlockBuilder.getAge(age, phenopacketID.getBytes());
        
        System.out.println("After decryption it gets the same age value:" + plainAge);
        
        Assertions.assertEquals(covidCase.isoAge, plainAge);
    }

    /*
     * Method to check if the entire metadata is correctly encrypted and decrypted
     */
    @Test
    void checkMetaDataEncyption() throws IOException, GeneralSecurityException, URISyntaxException{
        
        Phenopacket phenopacket = createPhenopacket();
        String phenopacketID = phenopacket.getId();

        MetaData metaData = phenopacket.getMetaData();
        
        byte[] cipherMetadata = MainElements.protectedMetaData(metaData, phenopacketID.getBytes());
        
        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketID.getBytes());
        
        System.out.println("After decryption it gets the same metadata value:");
        System.out.println(plainMetaData);
        
        Assertions.assertEquals(metaData, plainMetaData);
    }

    /*
     * Method to check if the createdBy field a is correctly encrypted and decrypted
     */
    @Test
    void checkMetaDataCreator() throws IOException, GeneralSecurityException, URISyntaxException{
        // Create phenopacket
        Phenopacket phenopacket = createPhenopacket();
        // Get the arbitrary id
        String phenopacketID = phenopacket.getId();
        // Get the stored metadata
        MetaData metaData = phenopacket.getMetaData();
        // Get the createdBy value
        String createdBy = metaData.getCreatedBy();

        System.out.println("Stored age in phenopacket as:" + createdBy);

        // Function to decrypt the field
        String plaintext = MainElements.getMetaDataCreator(metaData, phenopacketID);

        System.out.println("After decryption the original createdBy value is" + plaintext);
        
        Assertions.assertEquals(createdBy, plaintext);
    }


    /*
     * Method to check if a Phenopacket is correctly saved in a File
     */
    @Test
    void savePhenopacket() throws IOException, GeneralSecurityException, URISyntaxException, ParseException{
        Phenopacket phenopacket = createPhenopacket();
        String phenopacketID = phenopacket.getId();

        byte[] phenopacketBytes = phenopacket.toByteArray();
        HybridEncryption.saveInFile(phenopacketBytes, "Phenopacket", phenopacketID);
    }

    /*
     * Method to save both phenopacket and metadata
     * This functionality works when a user want to encrypt the whole element
     * Then, the Phenopacket and the encrypted metadata will be save into a file and send together
     */
    @Test
    void saveMetaData() throws IOException, GeneralSecurityException, URISyntaxException, ParseException{
        Phenopacket phenopacket = createPhenopacket();
        SecurePhenopacket.protectMetaData(phenopacket);
    }

    /*
     * Method to get the Age block from a phenopacket stored in a file
     * Then, decryption will be performed in order to check if the process works well
     */
    @Test
    void getAndCheckAgeFromFile() throws URISyntaxException, IOException, GeneralSecurityException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";
        byte[] phenopacketBytes = HybridEncryption.getCipherBytes("Phenopacket", phenopacketID);

        Phenopacket phenopacket = Phenopacket.parseFrom(phenopacketBytes);

        TimeElement age = phenopacket.getSubject().getTimeAtLastEncounter();

        System.out.println("Stored age in phenopacket as:");
        System.out.println(age);

        String plainAge = BlockBuilder.getAge(age, phenopacketID.getBytes());
        
        System.out.println("After decryption it gets the same age value:" + plainAge);
    }

    /*
     * Method to get the metadata element from a phenopacket stored in a file
     * Then, decryption will be performed in order to check if the process works well
     */
    @Test
    void getMetadataFromFile() throws URISyntaxException, IOException, GeneralSecurityException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";

        byte[] cipherMetadata = HybridEncryption.getCipherBytes("metaData", phenopacketID);

        MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketID.getBytes());
        
        System.out.println(plainMetaData);
    }

    /*
     * Method to get the creator field from a phenopacket stored in a file
     * Then, decryption will be performed in order to check if the process works well
     */
    @Test
    void getAndCheckCreatorFromFile() throws URISyntaxException, IOException, GeneralSecurityException{
        String phenopacketID = "17a1a6ad-2ea1-40ee-9308-1401fa096c0c";
        byte[] phenopacketBytes = HybridEncryption.getCipherBytes("Phenopacket", phenopacketID);

        Phenopacket phenopacket = Phenopacket.parseFrom(phenopacketBytes);

        MetaData metaData = phenopacket.getMetaData();
        String createdBy = MainElements.getMetaDataCreator(metaData, phenopacketID);
        
        System.out.println("After decryption it gets the same age value:" + createdBy);
    }
}