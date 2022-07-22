package phenopackets.securityFeatures;
import org.bouncycastle.jcajce.provider.digest.Keccak;


public class Keccak256Hashing {

    public static byte[] computeHash(byte[] element) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashBytes = digest256.digest(element);
        return hashBytes;
    }
}
