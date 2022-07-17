package phenopackets;

import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import phenopackets.securityFeatures.Keccak256Hashing;



public final class ExchangePheno {

    private ExchangePheno() {}

    public String phenopacketHash (Individual proband, PhenotypicFeature pheno, MetaData metadata){
        
        String hash = null; 
        Phenopacket securePhenopacket = Phenopacket.newBuilder().setSubject(proband).addPhenotypicFeatures(pheno).setMetaData(metadata).build();
        hash = Keccak256Hashing.computeHash(securePhenopacket);
        return hash;
    }
}