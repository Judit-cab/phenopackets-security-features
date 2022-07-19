package tfm.phenopackets_security_features;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.phenopackets.secure.schema.core.Age;
import org.phenopackets.secure.schema.core.MetaData;
import org.phenopackets.secure.schema.core.Resource;
import org.phenopackets.secure.schema.core.Update;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;

import phenopackets.ElementsBuilder;
import phenopackets.blocksBuilder;
import phenopackets.securityFeatures.SymmetricEncryption;

public class EncryptionTest {
    
    @Test
    void checkAge() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException { 
        String isoAge = "P70Y";
        Age age = Age.newBuilder().setIso8601Duration(isoAge).build();
        
        
        SecretKey key = SymmetricEncryption.generateKey();
        IvParameterSpec iv = SymmetricEncryption.generateIv();

        String cipherAge = SymmetricEncryption.encryptStrings(age.getIso8601Duration(), key, iv);
        String plainAge = SymmetricEncryption.decrypt(cipherAge, key, iv);

        System.out.println(cipherAge);

        Assertions.assertEquals(isoAge, plainAge);

    }

    @Test
    void checkMetaDataEncyption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, InvalidProtocolBufferException{
        List<Resource> resources =  new ArrayList<Resource>();
        List<Update> updates = new ArrayList<Update>();
        String epochString = "1081157732";
        long epoch = Long.parseLong( epochString );
      
        Timestamp time = Timestamp.newBuilder().setSeconds(epoch).build();
        Resource resource = blocksBuilder.resource("hp", "human phenotype ontology", "HP", "http://purl.obolibrary.org/obo/hp.owl", "2018-03-08", "http://purl.obolibrary.org/obo/HP_");
        resources.add(resource);
        Update update = Update.newBuilder().setTimestamp(time).build();
        updates.add(update);

        MetaData metaDataTest = ElementsBuilder.createMetaData(time, "Peter R.", "Peter S.", resources, updates, "2.0");

        SecretKey key = SymmetricEncryption.generateKey();
        IvParameterSpec iv = SymmetricEncryption.generateIv();

        byte[] cipherMD = SymmetricEncryption.encryptAES(metaDataTest.toByteArray(), key, iv);
        byte [] plaintMD = SymmetricEncryption.decryptAES(cipherMD, key, iv);

        MetaData metaDataplain = MetaData.parseFrom(plaintMD);

        Assertions.assertEquals(metaDataTest, metaDataplain);

    }
}
