package postaggerweka;

import java.util.Random;
import java.util.Scanner;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author amal
 */
public class MainPOSTaggerWeka {
    public static void main(String[] args) {
        String connluFile = "data/id-ud-dev.conllu";
        String arffFile = "data/postag-train.arff";
        Instances rawData=null, nominalData=null;

        POSTaggerWeka ptw = new POSTaggerWeka();
        ptw.setVerboseMode(false);
        
        // load dataset
        try {
            // Convert connlu file to arff file format
            ptw.conlluToArff(connluFile, arffFile);
            
            // Load dataset from arff file
            DataSource source = new DataSource(arffFile);
            rawData = source.getDataSet();
            rawData.setClassIndex(rawData.numAttributes()-1);
            
            System.out.println("First instance of raw data:");
            System.out.println(rawData.firstInstance()+"\n");
            
            // get nominal dataset from raw data
            nominalData = ptw.convertToNominal(rawData);
            
            System.out.println("First instance of nominal data:");
            System.out.println(nominalData.firstInstance()+"\n");
            
            // save ARFF
            ptw.saveArff(nominalData, "test.arff"); // debugging purposes
            
            // Build J48 classifier model
            J48 tree = new J48();
            tree.buildClassifier(nominalData);
            
            // Evaluation with 10-fold cross validation
            Evaluation evalResult = new Evaluation(nominalData);
            evalResult.crossValidateModel(tree, nominalData, 10, new Random(1));
            System.out.println(evalResult.toSummaryString());
            
            // Test new string input
            Scanner scan = new Scanner(System.in);
            String line = scan.nextLine();
            String[] splited = line.split(" ");
            String postag;
            String postagTwoBefore = "UNKNOWN2";
            String postagBefore = "UNKNOWN1";
            for (String s : splited) {
                Instance ins = nominalData.get(0);
                ins.setValue(0, s);
                try {
                    ins.setValue(1, postagTwoBefore);
                } catch (IllegalArgumentException e) {
                    ins.setValue(1, "xCOMMA");
                }
                ins.setValue(2, postagBefore);
                ins.setValue(3, "NOUN");
                int indeks = (int) tree.classifyInstance(ins);
                postag = nominalData.classAttribute().value(indeks);
                System.out.println(ins.toString());
                System.out.println("Pos Tag untuk \""+s+"\" : " + postag);
                
                postagTwoBefore = postagBefore;
                postagBefore = postag;
            }            
            
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}
