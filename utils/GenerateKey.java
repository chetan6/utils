package utils;
 
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.*;
import java.util.BitSet;
import java.util.Date;
import javax.crypto.*;
 
public class GenerateKey {
       
        public static String getHexKey() {
               
            String key = null;
           
            try {
                    KeyGenerator kg = KeyGenerator.getInstance("AES");
                    kg.init(256,new SecureRandom());
                    SecretKey sk = (kg.generateKey());
                    byte[] binary = sk.getEncoded();
                    key = String.format("%64x", new BigInteger(1, binary));
                    //BigInteger big = new BigInteger(key, 16);
                    //byte[] reversed = big.toByteArray();
                    //System.out.println(big);
                   
            } catch (Exception e) {
                    System.out.println("Key Generation Failed..");
                    e.printStackTrace();
            }
            return key;
        }
       
        public static String getBase64CharacterKey() {
               
            String key = "";
           
            try {
                    KeyGenerator kg = KeyGenerator.getInstance("AES");
                    kg.init(256,new SecureRandom());
                    SecretKey sk = (kg.generateKey());
                    byte[] binary = sk.getEncoded();
                    BitSet bS = BitSet.valueOf(binary);
                    int length = bS.length();
                    for (int i=0; i <= length; i=i+6) key = key + GenerateKey.lookup(bS.get(i, i+6));
 
                } catch (Exception e) {
                        System.out.println("Key Generation Failed..");
                    e.printStackTrace();
            }
            return key;
        }      
       
       
        public static char lookup(BitSet aBitSet) {
        	String base64Set = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
               
            int value = 0;
            for (int i=0; i<=5; i++) if(aBitSet.get(i)) value+= (int) Math.pow(i,2);
            return base64Set.charAt(value);
               
        }
       
        public static void saveKey(String destination, String key) {
            try {
                   
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = new Date();
                String today = dateFormat.format(date);
               
                BufferedWriter writer = new BufferedWriter(new FileWriter(destination+"\\Key_"+ today+".txt"));
               
                // write the key
                writer.write(key);
                writer.close();                                
                   
            }
            catch (Exception e) {
                    System.out.println("Could not save key to a file..");
                    e.printStackTrace();
            }
        }
       
       
        public static void main(String [] a) {
               
            String key = GenerateKey.getBase64CharacterKey();
           
            if (key != null) GenerateKey.saveKey("C:\\Users\\Tammy Shah", key);
                       
        }
                       
}