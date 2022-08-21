package phenopackets.commands;

import java.util.concurrent.Callable;

import org.phenopackets.secure.schema.Phenopacket;

import phenopackets.SecurePhenopacket;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="digitalSignature", mixinStandardHelpOptions = true)
public class DigitalSignatureCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Input phenopacketID")
    private String input;
    @Option(names={"--sign", "-s"})
    public boolean signElement = false;

    @Option(names={"--verify", "-v"})
    public boolean verifyElement = false;

    @Override
    public Integer call() throws Exception {
        
        if (input == null) {
            System.err.println("Error! No input file provided");
            return 1;
        }
       
       Phenopacket phenopacket = SecurePhenopacket.importPhenopacket(input);
        if(signElement){
            SecurePhenopacket.signPhenopacket(phenopacket);
            return 1;
        }

        if(verifyElement){
            SecurePhenopacket.verifyPhenopacket(phenopacket);
            return 1;
        }
        return 0;
    }

}
