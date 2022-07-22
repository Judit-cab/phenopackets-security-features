package phenopackets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import com.nimbusds.jose.shaded.json.parser.ParseException;

import phenopackets.securityFeatures.DigitalSignature;
import phenopackets.securityFeatures.Keccak256Hashing;

import org.phenopackets.secure.schema.Phenopacket;

public class securePhenopacket {

    public securePhenopacket() {
    }

    public static Phenopacket createPhenopacket (String id, Individual subject, List<PhenotypicFeature> phenotypics, MetaData metaData, List<Disease> diseases, List<MedicalAction> medicalActions){
        return Phenopacket.newBuilder()
        .setId(id)
        .setSubject(subject)
        .setMetaData(metaData)
        .addAllPhenotypicFeatures(phenotypics)
        .addAllDiseases(diseases)
        .addAllMedicalActions(medicalActions)
        .build();
    }
    

    public static  void signPhenopacket(Phenopacket phenopacket) throws IOException, URISyntaxException, ParseException{
        byte[] phenopacketBytes = phenopacket.toByteArray();
        String id = phenopacket.getId();

        DigitalSignature.protectWithDS("sign", phenopacketBytes, id);

    }

    public static  void verifyPhenopacket(Phenopacket phenopacket) throws IOException, URISyntaxException, ParseException{
        byte[] phenopacketBytes = phenopacket.toByteArray();
        String id = phenopacket.getId();

        DigitalSignature.protectWithDS("verify", phenopacketBytes, id);
    }
    
}
