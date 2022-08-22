package phenopackets.commands;

import java.util.concurrent.Callable;

import org.phenopackets.secure.schema.Phenopacket;
import org.phenopackets.secure.schema.core.Individual;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.TimeElement;

import phenopackets.SecurePhenopacket;
import phenopackets.schema.BlockBuilder;
import phenopackets.schema.MainElements;
import phenopackets.securityMechanisms.HybridEncryption;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="encryption", mixinStandardHelpOptions = true)
public class EncryptionCommand implements Callable<Integer> {
    @Parameters(index = "0", description = "Input phenopacketID")
    private String input;
    @Option(names={"--encrypt", "-e"}, description = "Protect Phenopacket schema")
    public Boolean encrypt = false;
  
    @Option(names={"--decrypt", "-d"}, description = "Decrypt Phenopacket schema")
    public Boolean decrypt = false;
   
    @Option(names={"--metaData", "-m"}, description = "Requires to encrypt MetaData element")
    public Boolean protectMetaData = false;

    @Override
    public Integer call() throws Exception {
        
        if (input == null) {
            System.err.println("Error! No input file provided");
            return 1;
        }

        Phenopacket phenopacket = SecurePhenopacket.importPhenopacket(input);

        if(encrypt){
            Individual individual = phenopacket.getSubject();
            MetaData metaData = phenopacket.getMetaData();

            phenopacket = Phenopacket.newBuilder(phenopacket).clearMetaData().clearSubject().build();
            
            if(!protectMetaData){
                metaData = phenopacket.getMetaData();
                metaData = MainElements.protectedMetaDataCreator(metaData, phenopacket.getId().getBytes());
            }

            String isoAge = individual.getTimeAtLastEncounter().getAge().getIso8601Duration();
            individual = Individual.newBuilder(individual).clearTimeAtLastEncounter().build();
            TimeElement age = BlockBuilder.createTimeElementAge(isoAge.getBytes(), phenopacket.getId().getBytes());
            individual = Individual.newBuilder().setTimeAtLastEncounter(age).build();

            phenopacket = Phenopacket.newBuilder().setSubject(individual).setMetaData(metaData).build();
            SecurePhenopacket.exportPhenopacket(phenopacket);
        }
        
        if(protectMetaData){
            SecurePhenopacket.protectMetaData(phenopacket);
        }
    
        if(decrypt){
           
            if(!protectMetaData){
                String phenopacketId = phenopacket.getId();
                MetaData metaData = phenopacket.getMetaData();
                TimeElement ageElement = phenopacket.getSubject().getTimeAtLastEncounter();
                String creatorByString = MainElements.getMetaDataCreator(metaData, phenopacketId);
                System.out.printf("%s %n", creatorByString);
                String isoAge = BlockBuilder.getAge(ageElement, phenopacketId.getBytes());
                System.out.printf("%s %n", isoAge);
                
            }else{
                byte[] cipherPhenopacket = HybridEncryption.getCipherBytes("Phenopacket", input);
                Phenopacket  phenopacketFile = Phenopacket.parseFrom(cipherPhenopacket);
                byte[] cipherMetadata = HybridEncryption.getCipherBytes("Metadata", phenopacketFile.getId());
                MetaData plainMetaData = MainElements.getMetaData(cipherMetadata, phenopacketFile.getId().getBytes());
                System.out.printf("%s %n", plainMetaData);
            }
        }
        return 0;
    }
    
}
