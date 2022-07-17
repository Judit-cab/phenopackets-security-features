package phenopackets;

import java.util.List;

import org.phenopackets.secure.schema.core.*;

public class MedicalActions {

    public MedicalActions() {
    }

    /*
     * Create MedicalAction element with action equals to Procedure
     */
    public static MedicalAction medicalProcedure(Procedure procedure, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setProcedure(procedure)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }

     /*
     * Create MedicalAction element with action equals to Treatment
     */
    public static MedicalAction medicalTreatment(Treatment treatment, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setTreatment(treatment)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }

     /*
     * Create MedicalAction element with action equals to TherapeuticRegimen
     */
    public static MedicalAction medicalRegimen(TherapeuticRegimen regimen, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setTherapeuticRegimen(regimen)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }

     /*
     * Create MedicalAction element with action equals to RadiationTherapy
     */
    public static MedicalAction medicalTherapy(RadiationTherapy radiation, OntologyClass target, OntologyClass intent, OntologyClass response,
    List<OntologyClass> adverseEvents, OntologyClass termination){
        return MedicalAction.newBuilder()
                .setRadiationTherapy(radiation)
                .setTreatmentTarget(target)
                .setTreatmentIntent(intent)
                .setResponseToTreatment(response)
                .addAllAdverseEvents(adverseEvents)
                .setTreatmentTerminationReason(termination)
                .build();
    }


}