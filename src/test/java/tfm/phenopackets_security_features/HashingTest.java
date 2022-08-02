package tfm.phenopackets_security_features;


import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Evidence;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.PhenotypicFeature;
import org.phenopackets.secure.schema.core.TimeElement;

import phenopackets.BlockBuilder;
import phenopackets.MainElements;
import phenopackets.securityFeatures.Hashing;




public class HashingTest {

    @Test
    void checkPhenotypicElement() throws IOException, GeneralSecurityException, URISyntaxException{
        String phenopacketId = "123654";

        OntologyClass type = BlockBuilder.createOntologyClass("id", "label");
        OntologyClass severity = BlockBuilder.createOntologyClass("id", "label");
        List<Evidence> evidence =  new ArrayList<Evidence>();
        TimeElement element = BlockBuilder.creaTimeElementTimestamp("1081157732");
        
        PhenotypicFeature phenotypic = MainElements.createPhenotypicFeature(type, severity, evidence, element, element);

        String hash = Hashing.computePhenotypicFeatureHash(phenotypic, phenopacketId, "phenotypic3");

        String hashFile = Hashing.getHash(phenopacketId, "phenotypic3");

        Assertions.assertEquals(hash, hashFile);
    }

    @Test
    void createDiseaseElementHash() throws IOException, GeneralSecurityException, URISyntaxException{
        String phenopacketId = "123654";

        OntologyClass type = BlockBuilder.createOntologyClass("id", "label");
        TimeElement element = BlockBuilder.createTimeElementAge("isoAge".getBytes(), phenopacketId.getBytes());
        List<OntologyClass> stages = new ArrayList<OntologyClass>();
        Disease disease = MainElements.createDisease(type, true, stages, stages, type, element);
        
        String hash = Hashing.computeDiseaseHash(disease, phenopacketId, "Disease");

        String hashFile = Hashing.getHash(phenopacketId, "Disease");

        Assertions.assertEquals(hash, hashFile);

    }

    @Test
    void testingCheckHashFunction(){
        String hash1 = "212152121";
        String hash2 = "111111111";

        boolean result = Hashing.checkHash(hash1, hash2);

        System.out.println(result);
    }


}
    