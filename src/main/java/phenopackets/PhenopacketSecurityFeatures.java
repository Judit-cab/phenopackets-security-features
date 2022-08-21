package phenopackets;

import picocli.AutoComplete;

import static picocli.CommandLine.*;

import phenopackets.commands.CreatePhenopacketCommand;
import phenopackets.commands.DigitalSignatureCommand;
import phenopackets.commands.HashingCommand;

@Command(name = "subcommands",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                AutoComplete.GenerateCompletion.class,
                CreatePhenopacketCommand.class,
                DigitalSignatureCommand.class,
                HashingCommand.class
        }
)
public class PhenopacketSecurityFeatures {
}