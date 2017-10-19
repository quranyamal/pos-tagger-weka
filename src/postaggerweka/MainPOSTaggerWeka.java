package postaggerweka;

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
        Instances data = null;

        POSTaggerWeka ptw = new POSTaggerWeka();
        
        // load dataset
        try {
            ptw.conlluToArff(connluFile, arffFile);
            DataSource source = new DataSource(arffFile);
            data = source.getDataSet();
            data.setClassIndex(data.numAttributes()-1);
        } catch (Exception e) {
            System.err.println(e);
        }

        System.out.println(data.firstInstance());
        
    }
    
}
