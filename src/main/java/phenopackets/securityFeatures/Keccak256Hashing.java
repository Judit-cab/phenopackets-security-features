package phenopackets.securityFeatures;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.phenopackets.secure.schema.Phenopacket;

import com.google.protobuf.ByteString;



public class Keccak256Hashing {

    public static final String KECCAK_256 = "Keccak-256";


    public static String computeHash(final Phenopacket phenopacket) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        ByteString idBytes = phenopacket.getIdBytes();
        byte[] hashbytes = digest256.digest(idBytes.toByteArray());
        return new String(Hex.encode(hashbytes));
    }
    
}
