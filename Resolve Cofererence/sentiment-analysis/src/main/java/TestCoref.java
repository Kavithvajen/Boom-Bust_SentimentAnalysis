
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StanfordNLP code to find coref and using that filter sentences based on word passed as key.
 */
public class TestCoref {
    public static void main(String[] args) throws IOException {
        String path;
        String key;
        if(args[0] != null && args[1] != null){
            path = args[0];
            key = args[1];
        }
        else{
            return;
        }
        File dir = new File(path+"/input/");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                System.out.println("Reading file. "+ child.getAbsolutePath());
                String story = readAllBytes(child.getAbsolutePath());
                filterUsingCoRef(key,story,path,child.getName(),child);
            }
        } else {
            System.out.println("No file Found in Directory. "+ path + "/input/");
        }
    }
    private static String readAllBytes(String filePath)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    private static void filterUsingCoRef(String key, String text, String location, String filename, File rawFile) throws IOException {
        Annotation doc = new Annotation(text);
        Boolean containsKey = Boolean.FALSE;
        String filteredSentences = new String();
        String[] keys = key.split("\\s+");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,coref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(doc);

        Map<Integer, CorefChain> corefs = doc.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);


        List<String> resolved = new ArrayList<String>();

        for (CoreMap sentence : sentences) {

            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            containsKey = Boolean.FALSE;

            for (CoreLabel token : tokens) {

                Integer corefClustId= token.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
                //System.out.println(token.word() +  " --> corefClusterID = " + corefClustId);


                CorefChain chain = corefs.get(corefClustId);
                for(String singleKey: keys) {
                    if (token.word().toLowerCase().contains(singleKey.toLowerCase())) {
                        //if(Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(token.word()).find())
                        containsKey = Boolean.TRUE;
                    }
                }

                if (chain==null || chain.getMentionsInTextualOrder().size() == 1){
                    resolved.add(token.word());
                }else{

                    int sentINdx = chain.getRepresentativeMention().sentNum -1;
                    CoreMap corefSentence = sentences.get(sentINdx);
                    List<CoreLabel> corefSentenceTokens = corefSentence.get(CoreAnnotations.TokensAnnotation.class);

                    String newwords = "";
                    CorefChain.CorefMention reprMent = chain.getRepresentativeMention();
                    //System.out.println(reprMent);
                    if (token.index() < reprMent.startIndex || token.index() > reprMent.endIndex) {
                        for (int i = reprMent.startIndex; i < reprMent.endIndex; i++) {
                            CoreLabel matchedLabel = corefSentenceTokens.get(i - 1); //resolved.add(tokens.get(i).word());
                            resolved.add(matchedLabel.word());

                            for(String singleKey: keys) {
                                if (matchedLabel.word().toLowerCase().contains(singleKey.toLowerCase())) {
                                    //if(Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(token.word()).find())
                                    containsKey = Boolean.TRUE;
                                }
                            }
                            //if(Pattern.compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE).matcher(matchedLabel.word()).find())
                            //    containsKey = Boolean.TRUE;

                            newwords += matchedLabel.word() + " ";

                        }
                    }
                    else {
                        resolved.add(token.word());

                    }
                    //System.out.println("converting " + token.word() + " to " + newwords);
                }
                //System.out.println();
                //System.out.println();
                //System.out.println("-----------------------------------------------------------------");
            }
            //Filters Sentences and prints them.
            if(containsKey){
                //System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
                String line = searchString(rawFile, sentence.get(CoreAnnotations.TextAnnotation.class));
                if(!filteredSentences.contains(line))
                    filteredSentences = line + System.lineSeparator() + filteredSentences;
            }

        }
        writeToFile(location+"/output/", filename, filteredSentences);
        /*
        String resolvedStr ="";
        //System.out.println();
        for (String str : resolved) {
            resolvedStr+=str+" ";
        }
        System.out.println(resolvedStr);
         */
    }

    private static void writeToFile(String location, String filename, String content){
        try {
            System.out.println("Writing to file:" + location+filename);
            FileWriter myWriter = new FileWriter(location+filename);
            myWriter.write(content);
            myWriter.close();
            System.out.println("Successfully wrote to the file:" + filename);
        } catch (IOException e) {
            System.out.println("An error occurred with "+ filename);
            e.printStackTrace();
        }
    }

    public static String searchString(File rawFile, String phrase) throws IOException{
        Scanner fileScanner = new Scanner(rawFile);
        Pattern pattern =  Pattern.compile(phrase);
        Matcher matcher = null;
        while(fileScanner.hasNextLine()){
            String line = fileScanner.nextLine();
            matcher = pattern.matcher(line);
            if(matcher.find()) {
                return line;
            }
        }
        return phrase;
    }
}
