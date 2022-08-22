package phenopackets.commands;

import java.util.concurrent.Callable;

import org.phenopackets.secure.schema.Phenopacket;

import phenopackets.SecurePhenopacket;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name="signature", mixinStandardHelpOptions = true)
public class DigitalSignatureCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Input phenopacketID")
    private String input;
    @Option(names={"--sign", "-s"}, description = "To sign the phenopacket")
    public boolean signElement = false;

    @Option(names={"--verify", "-v"}, description = "To verify the phenopacket")
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
        }

        if(verifyElement){
            SecurePhenopacket.verifyPhenopacket(phenopacket);
        }
        return 0;
    }

}
