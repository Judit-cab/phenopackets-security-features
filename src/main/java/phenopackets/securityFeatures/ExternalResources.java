package phenopackets.securityFeatures;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.crypto.tink.subtle.Hex;
import com.nimbusds.jose.shaded.json.JSONObject;


public class ExternalResources {

    private static final String DEFAULT_PATH = "information.txt";
    //https://mkyong.com/java/java-read-a-file-from-resources-folder/
    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.

    /**
     * @param fileName
     * @return
     * @throws URISyntaxException
     */
    public File getFileFromResource(String fileName) throws URISyntaxException{

        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            // failed if files have whitespaces or special characters
            return new File(resource.toURI());
        }

    }

    public String getNewPath(String fileName){
        URL resource = getClass().getClassLoader().getResource(DEFAULT_PATH);
        String path = resource.toString().replace(DEFAULT_PATH, fileName);
        return path;
    }

    public URL getURL(String fileName){
        URL resource = getClass().getClassLoader().getResource(fileName);
        return resource;
    }


    // print a file
    public static void printFile(File file) {

        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Create a JSON File
     */
    public void createSignFile(String fileName, String signature) throws URISyntaxException, IOException{
        try {
            File file = getFileFromResource(fileName);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(signature);
            fileWriter.close();

         } catch (IOException e) {
            e.printStackTrace();
         }
         System.out.println("File successfully created!");
      }

    public void addContentToJsonFile(String fileName, JSONObject jsonObject) throws URISyntaxException{
        try {
            URL url = getClass().getClassLoader().getResource(fileName);
            File file = new File (url.toURI());

            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.close();

         } catch (IOException e) {
            e.printStackTrace();
         }
         System.out.println("JSON file created: "+jsonObject);
      }

}