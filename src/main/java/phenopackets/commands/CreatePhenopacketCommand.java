package phenopackets.commands;

import java.nio.file.Path;
import java.util.concurrent.Callable;

//import org.phenopackets.secure.schema.Phenopacket;
//import org.phenopackets.secure.schema.core.Individual;

//import phenopackets.SecurePhenopacket;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name = "create")
public class CreatePhenopacketCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Input phenopacket file")
    private String input;
    
    @Option(names = {"-o", "--output"}, description = "Output file")
    private Path output = null;

    @Override
    public Integer call() throws Exception {
        if (input == null) {
            System.err.println("Error! No input file provided");
            return 1;
        }
        
        //Phenopacket phenopacket = SecurePhenopacket.importPhenopacket(input);
        return 0;
    }

}