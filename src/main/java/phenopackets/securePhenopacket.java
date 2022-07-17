package phenopackets;

import java.util.List;

import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.PhenotypicFeature;
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
    
}
