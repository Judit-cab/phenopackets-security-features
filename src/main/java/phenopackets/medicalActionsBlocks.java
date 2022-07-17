package phenopackets;

import java.util.List;

import org.phenopackets.secure.schema.core.*;

public class medicalActionsBlocks {

    public medicalActionsBlocks() {
    }

    public static Procedure procedure (OntologyClass code, OntologyClass site, TimeElement performed){
        return Procedure.newBuilder()
        .setCode(code)
        .setBodySite(site)
        .setPerformed(performed)
        .build();
    }

    public static Treatment treatment (OntologyClass agent, OntologyClass route, List<DoseInterval> doseIntervals){
        return Treatment.newBuilder()
        .setAgent(agent)
        .setRouteOfAdministration(route)
        .addAllDoseIntervals(doseIntervals)
        .build();
    }

    public static RadiationTherapy radiation(OntologyClass modality, OntologyClass body, int dosage, int fraction){
        return RadiationTherapy.newBuilder()
        .setModality(modality)
        .setBodySite(body)
        .setDosage(dosage)
        .setFractions(fraction)
        .build();
    }

    public static TherapeuticRegimen therapeutic (OntologyClass id, TimeElement start, TimeElement end, int status){
        return TherapeuticRegimen.newBuilder()
            .setOntologyClass(id)
            .setStartTime(start)
            .setEndTime(end)
            .setRegimenStatusValue(status)
            .build();
    }

}
