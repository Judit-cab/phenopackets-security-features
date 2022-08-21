package phenopackets.securityMechanisms;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.crypto.tink.subtle.Hex;

import com.google.gson.stream.JsonReader;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.ParseException;


public class ExternalResources {

    private static final String DEFAULT_PATH = "readME.txt";
    private static final String FORMAT_TXT =".txt";
    private static final String FORMAT_JSON = ".json";

    JSONObject js = new JSONObject();

    /**
     * Method to get file from resources folder. In case the file doesn't exist, the method creates a new one
     * @param fileName
     * @return the required file
     * @throws URISyntaxException
     */
    public File getFileFromResource(String fileName) throws URISyntaxException{

        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            System.out.println("File not found, new one will be created");
            return createNewFile(fileName);
        } else {
            // failed if files have whitespaces or special characters
            return new File(resource.toURI());
        }
    }

    public String getNewPath(String fileName, String format) throws URISyntaxException{
        
        URL resource = getClass().getClassLoader().getResource(DEFAULT_PATH);
        String path = resource.toString().replace(DEFAULT_PATH, fileName+format).replace("file:", "");

        return path;
    }

    /**
     * Method to create a new file
     * @param fileName required - name of the file
     * @return the new File
     * @throws URISyntaxException
     */
    public File createNewFile(String fileName) throws URISyntaxException{

        URL resource = getClass().getClassLoader().getResource(DEFAULT_PATH);
        String path = resource.toString().replace(DEFAULT_PATH, fileName).replace("file:", "");
        File newFile = new File(path);

        return newFile;
    }

    /**
     * Method to add a new hash to File
     * @param fileName required - name of the file
     * @param hash required - hash value
     * @param element required - element linket to the hash
     * @throws IOException
     * @throws URISyntaxException
     */
    public void addHashToFile(String fileName, byte[] hash, String element) throws IOException, URISyntaxException{
        try{
            //String path = getNewPath(fileName, FORMAT_TXT);
            File hashFile = getFileFromResource(fileName);

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(hashFile,true));
            
            fileWriter.write(element+":"+Hex.encode(hash));
            fileWriter.newLine();
            fileWriter.close();

        }catch (IOException ex){
            ex.printStackTrace();
        }
      }

    /**
     * Method to create JSON file and adds a JSON object
     * @param fileName required - name of the file
     * @param bytes required - bytes element
     * @param element required - name of the element
     * @throws URISyntaxException
     * @throws ParseException
     */
    public void createJSONFile(String fileName, String bytes, String element) throws URISyntaxException, ParseException{
        // Input validation
         if (bytes == null ||  bytes.length() == 0){
            throw new NullPointerException();
        }
        try {
            File jsonFile = createNewFile(fileName);

            // Get the stored json Obj
            if (jsonFile.length()==0) {
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(jsonFile));
                js.appendField(element, bytes);
                fileWriter.write(js.toJSONString());
                fileWriter.close();
            }else{
                JSONObject js = getJSONFromFile(jsonFile);
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(jsonFile));
                js.appendField(element, bytes);
                fileWriter.write(js.toJSONString());
                fileWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
      }
      
    /**
     * Method to get JSON object from file
     * @param jsonFile required - JSON File
     * @return JSON object
     * @throws ParseException
     * @throws IOException
     */
    public JSONObject getJSONFromFile(File jsonFile) throws ParseException, IOException{
        JSONObject js = new JSONObject();

        try(FileReader reader = new FileReader(jsonFile)){
            JsonReader jsReader =  new JsonReader(reader);
            jsReader.beginObject();
            // Save fields and keys
            while (jsReader.hasNext()) {
                js.appendField(jsReader.nextName(), jsReader.nextString());
            }
            jsReader.endObject();
            jsReader.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return js;
      }
}