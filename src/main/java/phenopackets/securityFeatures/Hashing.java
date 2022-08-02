package phenopackets.securityFeatures;

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

    static ExternalResources externalFile = new ExternalResources();
    private static final String FORMAT_FILE =".txt";

    /**
     * Private method to calculate the hash with Keccak
     * @param element required - the element to compute the hash
     * @return the hash bytes
     */
    private static byte[] computeHash(byte[] element) {

        // Generate a new Keccak instance
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        // Compute hash of an element
        byte[] hashBytes = digest256.digest(element);

        return hashBytes;
    }

    /**
     * Method to compute the hash of the Disease element
     * @param diseaseElement required - the Disease element
     * @param phenopacketID required - Phenopacket ID
     * @param diseaseName required - the specific disease name
     * @return the hash 
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String computeDiseaseHash(Disease diseaseElement, String phenopacketID, String diseaseName) throws IOException, URISyntaxException{

        byte[] diseaseBytes = diseaseElement.toByteArray();
        byte [] hash = computeHash(diseaseBytes);

        externalFile.addHashToFile(phenopacketID, hash, diseaseName);


        return new String(Hex.encode(hash));
        
    }

    /**
     * Method to compute the hash of the Phenotypic Feature element
     * @param phenotypicFeature required - the Phenotypic Feature element
     * @param phenopacketID required - Phenopacket ID
     * @param phenotypicName required - the specific phenotypic name
     * @return the hash String
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String computePhenotypicFeatureHash(PhenotypicFeature phenotypicFeature, String phenopacketID, String phenotypicName) throws IOException, URISyntaxException{

        byte[] phenotypicFeatureBytes = phenotypicFeature.toByteArray();
        byte [] hash = computeHash(phenotypicFeatureBytes);

        externalFile.addHashToFile(phenopacketID, hash, phenotypicName);

        return new String(Hex.encode(hash));
        
    }

    /**
     * Method to compute the hash of the Disease element
     * @param medicalAction required- a MedicalAction element
     * @param phenopacketID required - Phenopacket ID
     * @param medicalActionName required - the specific medical action performed
     * @return the hash String
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String  computeMedicalAction(MedicalAction medicalAction, String phenopacketID, String medicalActionName) throws IOException, URISyntaxException{

        byte[] medicalActionBytes = medicalAction.toByteArray();
        byte [] hash = computeHash(medicalActionBytes);

        externalFile.addHashToFile(phenopacketID, hash, medicalActionName);

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
        String hash = null;

        File hashFile = externalFile.getFileFromResource(fileName+FORMAT_FILE);
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
    public static boolean checkHash(String computedHash, String storedHash){
        
        boolean result = false;
        // Input parameter verification
        if(!computedHash.isBlank() && !storedHash.isBlank()){
            // Compare both values
            result = computedHash.equals(storedHash);
            System.out.println(result);
        }else{
            throw new NullPointerException();
        }

        return result;
    }



}
