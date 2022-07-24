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

    private static byte[] computeHash(byte[] element) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashBytes = digest256.digest(element);
        return hashBytes;
    }

    public static String computeDiseaseHash(Disease diseaseElement, String phenopacket, String diseaseName) throws IOException, URISyntaxException{

        byte[] diseaseBytes = diseaseElement.toByteArray();
        byte [] hash = computeHash(diseaseBytes);

        externalFile.addHashToFile(phenopacket, hash, diseaseName);


        return new String(Hex.encode(hash));
        
    }

    public static String computePhenotypicFeatureHash(PhenotypicFeature phenotypicFeature, String phenopacket, String phenotypicName) throws IOException, URISyntaxException{

        byte[] phenotypicFeatureBytes = phenotypicFeature.toByteArray();
        byte [] hash = computeHash(phenotypicFeatureBytes);

        externalFile.addHashToFile(phenopacket, hash, phenotypicName);

        return new String(Hex.encode(hash));
        
    }

    public static String  computeMedicalAction(MedicalAction medicalAction, String phenopacket, String medicalActionName) throws IOException, URISyntaxException{

        byte[] medicalActionBytes = medicalAction.toByteArray();
        byte [] hash = computeHash(medicalActionBytes);

        externalFile.addHashToFile(phenopacket, hash, medicalActionName);

        return new String(Hex.encode(hash));
        
    }

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

}
