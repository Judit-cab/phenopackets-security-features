package phenopackets.securityFeatures;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.PhenotypicFeature;


public class Hashing {

    private static byte[] computeHash(byte[] element) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashBytes = digest256.digest(element);
        return hashBytes;
    }

    public static byte[] computeDiseaseHash(Disease disease){

        byte[] diseaseBytes = disease.toByteArray();
        byte [] hash = computeHash(diseaseBytes);

        return hash;
        
    }

    public static byte[] computePhenotypicFeatureHash(PhenotypicFeature phenotypicFeature ){

        byte[] phenotypicFeatureBytes = phenotypicFeature.toByteArray();
        byte [] hash = computeHash(phenotypicFeatureBytes);

        return hash;
        
    }

    public static byte[] computeMedicalAction(MedicalAction medicalAction){

        byte[] medicalActionBytes = medicalAction.toByteArray();
        byte [] hash = computeHash(medicalActionBytes);

        return hash;
        
    }

    
}
