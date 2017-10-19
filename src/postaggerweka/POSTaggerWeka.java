package postaggerweka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author amal
 */
public class POSTaggerWeka {
    
    public static final String UNKNOWN_TAG_1 = "UNKNOWN1";
    public static final String UNKNOWN_TAG_2 = "UNKNOWN2";
    public static final String DOT = "xDOT";
    public static final String COMMA = "xCOMMA";
    public static final String SINGLE_QUOTES = "xSINGQUOT";
    public static final String DOUBLE_QUOTES = "xDOUBQUOT";
    public static final String DOUBLE_SINGLE_QUOTES = "xDOUBSINGQUOT";
    public static final String NUMBER = "xNUMBER";
    
    boolean verboseMode = false;
    
    void setVerboseMode(boolean isVerbose) {
        this.verboseMode = isVerbose;
    }
   
    public boolean isNumeric(String str) {  
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");  
    }  
    
    void conlluToArff(String connluFile, String arffFile) throws FileNotFoundException, IOException {
        FileReader file = new FileReader(connluFile);
        FileWriter writer = new FileWriter(arffFile);
        BufferedReader br = new BufferedReader(file);
        
        String line, word, tag="INIT";
        String tagBefore=UNKNOWN_TAG_1, tagTwoBefore=UNKNOWN_TAG_2;
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
  
                // replace special characters
                if (word.equals(".")) word = DOT;
                if (word.equals(",")) word = COMMA;
                if (word.equals("\'")) word = SINGLE_QUOTES;
                if (word.equals("\"")) word = DOUBLE_QUOTES;
                if (word.equals("''")) word = DOUBLE_SINGLE_QUOTES;
                if (isNumeric(word)) word = NUMBER;
                word = word.replace("\'", "");
                word = word.replace("\"", "");
                
                if (num==1) {
                    tagTwoBefore = UNKNOWN_TAG_2;
                    tagBefore = UNKNOWN_TAG_1;
                } else {
                    tagTwoBefore = tagBefore;
                    tagBefore = tag;
                }
                
                tag = splited[3];
                writer.write(word+", "+tagTwoBefore+", "+tagBefore+", "+tag+"\n");
                if (verboseMode) System.out.println(num+" "+word+": "+tag+" "+tagBefore+" "+tagTwoBefore);
            }
        }
        writer.close();
        System.out.println("Export complete");
    }

    public Instances convertToNominal(Instances data) throws Exception {
        int classIndex;
                
        StringToNominal s = new StringToNominal();
        s.setInputFormat(data);
        data = Filter.useFilter(data,s);
        
        classIndex = data.numAttributes() - 1;
        data.setClassIndex(classIndex);
        
        StringToWordVector converter = new StringToWordVector();
        converter.setInputFormat(data);
        Instances newData = Filter.useFilter(data, converter);
        
        newData.setClassIndex(0);
        
        NumericToNominal converter2 = new NumericToNominal();
        converter2.setInputFormat(newData);
        Instances newNewData = Filter.useFilter(newData, converter2);
        
        newData.setClassIndex(0);
        
        return newNewData;
    }
    
}
