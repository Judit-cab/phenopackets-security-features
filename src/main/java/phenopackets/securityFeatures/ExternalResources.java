package phenopackets.securityFeatures;

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

    public String getNewPath(String fileName, String format) throws URISyntaxException{
        
        URL resource = getClass().getClassLoader().getResource(DEFAULT_PATH);
        String path = resource.toString().replace(DEFAULT_PATH, fileName+format).replace("file:", "");

        return path;
    }

    public URL getURL(String fileName){
        URL resource = getClass().getClassLoader().getResource(fileName);
        return resource;
    }

      public void addHashToFile(String fileName, byte[] hash, String element) throws IOException, URISyntaxException{
        try{ 
            
            String path = getNewPath(fileName, FORMAT_TXT);
            File hashFile = new File(path);

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(hashFile,true));
            
            fileWriter.write(element+":"+Hex.encode(hash));
            fileWriter.newLine();
            fileWriter.close();

        }catch (IOException ex){
            ex.printStackTrace();
        }
      }

      public void createJSONFile(String fileName, String bytes, String element) throws URISyntaxException, ParseException{
        try {
            String path = getNewPath(fileName,FORMAT_JSON);
            File jsonFile = new File(path);

            JSONObject js = getJSONFromFile(path);
            
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(jsonFile));
            js.appendField(element, bytes);
            fileWriter.write(js.toJSONString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
      }
      
      public JSONObject getJSONFromFile(String path) throws ParseException, IOException{
        JSONObject js = new JSONObject();
        File jsonFile = new File(path);

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

        System.out.println("JSON file created: " +js);
        return js;

      }

}