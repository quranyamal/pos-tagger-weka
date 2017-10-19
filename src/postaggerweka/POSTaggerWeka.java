package postaggerweka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author amal
 */
public class POSTaggerWeka {
    
    public static final String UNK_TAG_1 = "UNKNOWN1";
    public static final String UNK_TAG_2 = "UNKNOWN2";
    public static final String DOT = "xDOT";
    public static final String COMMA = "xCOMMA";
    public static final String SINGLE_QUOTES = "xSINGQUOT";
    public static final String DOUBLE_QUOTES = "xDOUBQUOT";
    public static final String DOUBLE_SINGLE_QUOTES = "xDOUBSINGQUOT";
    
    void conlluToArff(String connluFile, String arffFile) throws FileNotFoundException, IOException {
        FileReader file = new FileReader(connluFile);
        FileWriter writer = new FileWriter(arffFile);
        BufferedReader br = new BufferedReader(file);
        
        String line, word, tag="INIT";
        String tagBefore=UNK_TAG_1, tagTwoBefore=UNK_TAG_2;
        int num;
        
        writer.write("@relation postag\n");
        writer.write("\n");
        writer.write("@attribute word string\n");
        writer.write("@attribute tag_two_before string\n");
        writer.write("@attribute tag_before string\n");
        writer.write("@attribute tag string\n");
        writer.write("\n");
        writer.write("@data\n");
        
        while ((line = br.readLine())!=null) {
            if (!line.equals("") && line.charAt(0)!='#') {
                //System.out.println(line);
                String[] splited = line.split("\t");
                num = Integer.parseInt(splited[0]);
                word = splited[1];
  
                if (word.equals(".")) word = DOT;
                if (word.equals(",")) word = COMMA;
                if (word.equals("\'")) word = SINGLE_QUOTES;
                if (word.equals("\"")) word = DOUBLE_QUOTES;
                if (word.equals("''")) word = DOUBLE_SINGLE_QUOTES;
                word = word.replace("\'", "");
                word = word.replace("\"", "");
                
                if (num==1) {
                    tagTwoBefore = UNK_TAG_2;
                    tagBefore = UNK_TAG_1;
                } else {
                    tagTwoBefore = tagBefore;
                    tagBefore = tag;
                }
                
                tag = splited[3];
                writer.write(word+", "+tagTwoBefore+", "+tagBefore+", "+tag+"\n");
                System.out.println(num+" "+word+": "+tag+" "+tagBefore+" "+tagTwoBefore);
            }
        }
        writer.close();
    }

    
}
