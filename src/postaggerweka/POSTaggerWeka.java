package postaggerweka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToNominal;

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
    
    private void writeHeader(FileWriter writer) throws IOException {
        writer.write("@relation postag\n");
        writer.write("\n");
        writer.write("@attribute word string\n");
        writer.write("@attribute tag_two_before string\n");
        writer.write("@attribute tag_before string\n");
        writer.write("@attribute tag string\n");
        writer.write("\n");
        writer.write("@data\n");
    }
    
    private String handleSpecialCase(String word) {
        if (word.equals(".")) word = DOT;
        if (word.equals(",")) word = COMMA;
        if (word.equals("\'")) word = SINGLE_QUOTES;
        if (word.equals("\"")) word = DOUBLE_QUOTES;
        if (word.equals("''")) word = DOUBLE_SINGLE_QUOTES;
        word = word.replace(",", "");
        if (isNumeric(word)) word = NUMBER;
        word = word.replace("\'", "");
        word = word.replace("\"", "");
        
        return word;
    }
    
    void conlluToArff(String connluFile, String arffFile) throws FileNotFoundException, IOException {
        FileReader file = new FileReader(connluFile);
        FileWriter writer = new FileWriter(arffFile);
        BufferedReader br = new BufferedReader(file);
        
        String line, word, tag="INIT";
        String tagBefore=UNKNOWN_TAG_1, tagTwoBefore=UNKNOWN_TAG_2;
        int num;
        
        writeHeader(writer);
        
        while ((line = br.readLine())!=null) {
            if (!line.equals("") && line.charAt(0)!='#') {
                //System.out.println(line);
                String[] splited = line.split("\t");
                num = Integer.parseInt(splited[0]);
                word = splited[1];
  
                word = handleSpecialCase(word);
                
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
    
    public static Instances removeAttribute(Instances data, int indeks) throws Exception {
        Remove remove = new Remove();
        int[] removeIndeks = new int[1];
        removeIndeks[0] = indeks;
        remove.setAttributeIndicesArray(removeIndeks);
        remove.setInputFormat(data);
        Instances instNew = Filter.useFilter(data, remove);
        
        return instNew;
    }
    
    public static void saveArff(Instances data, String filename) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + filename));
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }
    
    public static Instances convertStringToNominal(Instances data, int i) throws Exception {
        StringToNominal s = new StringToNominal();
        s.setInputFormat(data);
        
        String[] options = new String[2];
        options[0] = "-R"; //Range option
        options[1] = Integer.toString(i); //The attribute index
        s.setOptions(options);
        Instances newData = Filter.useFilter(data,s);
        
        return newData;
    }
    
    public static Instances convertToNominal(Instances data) throws Exception {
        int classIndex;
        
        for (int i=1; i<=4; i++)
            data = convertStringToNominal(data, i);
        
        classIndex = data.numAttributes() - 1;
        data.setClassIndex(classIndex);
        
        return data;
    }

}
