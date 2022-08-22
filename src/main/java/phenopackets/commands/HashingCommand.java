package phenopackets.commands;

import java.util.concurrent.Callable;

import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Disease;
import org.phenopackets.secure.schema.core.MedicalAction;
import org.phenopackets.secure.schema.core.PhenotypicFeature;

import phenopackets.SecurePhenopacket;
import phenopackets.securityMechanisms.Hashing;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="hashing", mixinStandardHelpOptions = true)
public class HashingCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Input phenopacketID")
    private String phenopacketId;
    @Option(names="--hash")
    public boolean computeHash = false;
    @Option(names={"--check", "-c"}, description = "To check if the hash remains the same")
    public boolean checkHash = false;
    @Option(names={"--element", "-e"}, description = "Indicates the element to compute the hash")
    private String element;
    @Option(names={"--index", "-i"}, description = "Indicates the specific element from a list")
    private int index;
    @Option(names={"--medical action", "-ma"}, description = "Specify the medical action")
    private String medicalActionName;

    @Override
    public Integer call() throws Exception {
        
        if (phenopacketId == null) {
            System.err.println("Error! No input file provided");
            return 1;
        }
       
       Phenopacket phenopacket = SecurePhenopacket.importPhenopacket(phenopacketId);
        if(computeHash){
            if(element.contains("disease") || element.contains("Disease")){
               Disease disease = phenopacket.getDiseases(index);
               String hash = Hashing.computeDiseaseHash(disease, phenopacketId);
               System.out.printf("%s %n", hash);
            }

            if(element.contains("PhenotypicFeature") || element.contains("phenotypic feature")){
                PhenotypicFeature phenotypicFeature= phenopacket.getPhenotypicFeatures(index);
                String hash = Hashing.computePhenotypicFeatureHash(phenotypicFeature, phenopacketId);
                System.out.printf("%s %n", hash);
            }
            if(element.contains("MedicalActions") || element.contains("medical actions")){
                MedicalAction medicalAction = phenopacket.getMedicalActions(index);
                String hash = Hashing.computeMedicalAction(medicalAction, phenopacketId, medicalActionName);
                System.out.printf("%s %n", hash);
            }
        }

        if(checkHash){
            String storedHash = null;
            boolean isCorrect = false;
            if(element.contains("disease") || element.contains("Disease")){
                Disease disease = phenopacket.getDiseases(index);
                storedHash= Hashing.getHash(phenopacketId, disease.getTerm().getLabel());
                isCorrect = Hashing.checkHash(disease.toByteArray(), storedHash);
                System.out.printf("%s %n", isCorrect);
            }
            if(element.contains("PhenotypicFeature") || element.contains("phenotypic feature")){
                PhenotypicFeature phenotypicFeature= phenopacket.getPhenotypicFeatures(index);
                storedHash = Hashing.getHash(phenopacketId, phenotypicFeature.getType().getLabel());
                isCorrect = Hashing.checkHash(phenotypicFeature.toByteArray(), storedHash);
                System.out.printf("%s %n", isCorrect);
            }
            if(element.contains("MedicalActions") || element.contains("medical actions")){
                MedicalAction medicalAction = phenopacket.getMedicalActions(index);
                storedHash = Hashing.getHash(phenopacketId, medicalActionName);
                isCorrect = Hashing.checkHash(medicalAction.toByteArray(), storedHash);
                System.out.printf("%s %n", isCorrect);
            }

        }
        return 0;
    }

}
