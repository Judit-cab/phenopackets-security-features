package phenopackets.securityMechanisms;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.PhenotypicFeature;


public class Hashing {

    static ExternalResources externalResource = new ExternalResources();
    private static final String FORMAT_FILE =".txt";

    /**
     * Private method to calculate the hash with Keccak
     * @param element required - the element to compute the hash
     * @return the hash bytes
     */
    private static byte[] computeHash(byte[] element) {
         // Input validation
         if (element == null || element.length == 0){
            throw new NullPointerException();
        }
        // Generate a new Keccak instance
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        // Compute hash of an element
        byte[] hashBytes = digest256.digest(element);

        return hashBytes;
    }

    /**
     * Method to compute the hash of the Disease element
     * @param disease required - the Disease element
     * @param phenopacketId required - Phenopacket ID
     * @return the hash 
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String computeDiseaseHash(Disease disease, String phenopacketId) throws IOException, URISyntaxException{
        // Input validation
        if (disease == null){
            throw new NullPointerException();
        }
        if (phenopacketId == null || phenopacketId.length()==0){
            throw new NullPointerException();
        }
  
        // Serialize the Disease element to a byte array
        byte[] diseaseBytes = disease.toByteArray();
        // Compute the hash
        byte [] hash = computeHash(diseaseBytes);
        // Store the hash in a file linked with its name
        externalResource.addHashToFile(phenopacketId, hash, disease.getTerm().getLabel());
        // Return the hash as String
        return new String(Hex.encode(hash));
    }

    /**
     * Method to compute the hash of the PhenotypicFeature element
     * @param phenotypicFeature required - the PhenotypicFeature element
     * @param phenopacketId required - Phenopacket ID
     * @return the hash String
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String computePhenotypicFeatureHash(PhenotypicFeature phenotypicFeature, String phenopacketId) throws IOException, URISyntaxException{
        // Input validation
        if (phenotypicFeature == null){
            throw new NullPointerException();
        }
        if (phenopacketId == null || phenopacketId.length()==0){
            throw new NullPointerException();
        }
        // Serialize the PhenotypicFeature element to a byte array
        byte[] phenotypicFeatureBytes = phenotypicFeature.toByteArray();
        // Compute the hash
        byte [] hash = computeHash(phenotypicFeatureBytes);
        // Store the hash in a file linked with its name
        externalResource.addHashToFile(phenopacketId, hash, phenotypicFeature.getType().getLabel());
        // Return the hash as String
        return new String(Hex.encode(hash));
        
    }

    /**
     * Method to compute the hash of the MedicalAction element
     * @param medicalAction required- a MedicalAction element
     * @param phenopacketId required - Phenopacket ID
     * @param medicalActionName required - the specific medical action performed
     * @return the hash String
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String  computeMedicalActionHash(MedicalAction medicalAction, String phenopacketId, String medicalActionName) throws IOException, URISyntaxException{
        // Input validation
        if (medicalAction == null){
            throw new NullPointerException();
        }
        if (phenopacketId == null || phenopacketId.length()==0){
            throw new NullPointerException();
        }
        if (medicalActionName == null || medicalActionName.length()==0){
            throw new NullPointerException();
        }

        // Serialize the PhenotypicFeature element to a byte array
        byte[] medicalActionBytes = medicalAction.toByteArray();
        // Compute the hash
        byte [] hash = computeHash(medicalActionBytes);
        // Store the hash in a file linked with its name
        externalResource.addHashToFile(phenopacketId, hash, medicalActionName);
        // Return the hash as String
        return new String(Hex.encode(hash));
    }

    /**
     * Method to calculate any hash of any Phenopacket element
     * @param element required - Element to compute hash
     * @return the hash as string
     */
    public String computeHashElement(byte[] element){
        byte[] hash = computeHash(element);
         // Return the hash as String
         return new String(Hex.encode(hash));
    }

    /**
     * Method to get the hash of an element
     * @param fileName required - the file where the hash is stored
     * @param element required - the element to obtain the hash
     * @return the hash String
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String getHash(String fileName, String element) throws URISyntaxException, IOException{
        // Input validation
        if (fileName == null || fileName.length()==0){
            throw new NullPointerException();
        }
        if (element == null || element.length()==0){
            throw new NullPointerException();
        }
        
        String hash = null;

        File hashFile = externalResource.getFileFromResource(fileName+FORMAT_FILE);
        List<String> lines=Files.readAllLines(hashFile.toPath());

        for (String line: lines){
            if(line.contains(element)){
                String[] hashLine = line.split(":");
                System.out.println(hashLine[1]);
                hash = hashLine[1];
            }
        }
        return hash;
    }

    /**
     * Function to check if the stored hash is the same as the new computed one
     * @param computedHash required
     * @param storedHash required
     * @return boolean - if hashes are equal then returns true, otherwise returns false
     */
    public static boolean checkHash(byte[] element, String storedHash){
        
        boolean result = false;
        // Input validation
        if(element!=null && !storedHash.isBlank()){
            
            byte [] hash = computeHash(element);
            String computedHash = new String(Hex.encode(hash));

            // Compare both values
            result = computedHash.equals(storedHash);
            System.out.println(result);
        }else{
            throw new NullPointerException();
        }
        return result;
    }
}
