package phenopackets;

import org.phenopackets.secure.schema.core.DoseInterval;
import org.phenopackets.secure.schema.core.OntologyClass;
import org.phenopackets.secure.schema.core.Quantity;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.TimeInterval;


public class blocksBuilder {
    
    public blocksBuilder() {
    }

    public static OntologyClass ontologyClass (String id, String label){
        return OntologyClass.newBuilder()
            .setId(id)
            .setLabel(label)
            .build();
    }


    public static Resource resource (String id, String name, String namespace, String url, String version, String iri){
        return Resource.newBuilder()
            .setId(id)
            .setName(name)
            .setNamespacePrefix(namespace)
            .setVersion(version)
            .setUrl(url)
            .setIriPrefix(iri)
            .build();
    }

    public static Quantity quantity(OntologyClass unit, Double value){
        return Quantity.newBuilder()
            .setUnit(unit)
            .setValue(value)
            .build();
    }

    public static DoseInterval doseInterval (Quantity quantity, OntologyClass schedule, TimeInterval interval){
        return DoseInterval.newBuilder()
            .setQuantity(quantity)
            .setScheduleFrequency(schedule)
            .setInterval(interval)
            .build();
    }

}
