// package phenopackets.commands;

// import java.util.concurrent.Callable;

// import picocli.CommandLine.Command;
// import picocli.CommandLine.Parameters;

// @Command(name = "create",
//         mixinStandardHelpOptions = true)
// public class CreatePhenopacket implements Callable<Integer> {

//     @Override
//     public Integer call() {
//         // What type of validation do we run?
//         List<ValidatorInfo> validationTypes = new ArrayList<>();
//         validationTypes.add(ValidatorInfo.generic()); // we run this by default
//         if (rareHpoConstraints) {
//             validationTypes.add(ValidatorInfo.rareDiseaseValidation());
//         }

//         PhenopacketValidatorFactory phenopacketValidatorFactory = ClasspathJsonSchemaValidatorFactory.defaultValidators();
//         ValidatorRunner validatorRunner = new ValidatorRunner(phenopacketValidatorFactory);

//         for (Path phenopacket : phenopackets) {
//             try (InputStream in = Files.newInputStream(phenopacket)) {
//                 List<ValidationItem> validationItems = validatorRunner.validate(in, validationTypes);
//                 Path fileName = phenopacket.getFileName();
//                 if (validationItems.isEmpty()) {
//                     System.out.printf("%s - OK%n", fileName);
//                     printSeparator();
//                 } else {
//                     for (ValidationItem item : validationItems) {
//                         System.out.printf("%s - (%s) %s%n", fileName, item.errorType(), item.message());
//                     }
//                     printSeparator();
//                 }

//             } catch (IOException e) {
//                 System.out.println("Error opening the phenopacket: " + e);
//             }
//         }
//         return 0;
//     }

//     private void printSeparator() {
//         if (phenopackets.size() > 1) {
//             System.out.println("---");
//         }
//     }

// }