package utilities;

import dataobject.Corpus;
import dataobject.DocumentInfo;
import dataobject.LookupItem;
import dataobject.Scene;
import queryengine.DocumentScore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Utils {

    /**
     * This method writes the document ranking into TREC format. It writes entries according to
     * the list of documentScore provided so it is assumed the list is already sorted with the highest
     * score first.
     *
     * @param trecFilename      file name for the file to write to
     * @param isappend          Whether to keep appending to file or overwrite it
     * @param documentScores
     * @param documentInfoMap
     * @param entryPrefix       prefix for the sixth column in TREC
     * @param fileparams        file name parameters
     */
    public static void writeTREC(String trecFilename, boolean isappend, List<DocumentScore> documentScores,
                                 Map<String, DocumentInfo> documentInfoMap, String entryPrefix,
                                 String queryID, String... fileparams){

        final String TAB = "\t\t\t";
        final String TREC_FORMAT = "%-" + 5 + "s %s %-" + 50 + "s %s %s %s";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(trecFilename, isappend))) {

            int rank = 1;
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(3);

            for(DocumentScore score : documentScores){

                StringBuilder stringBuilder = new StringBuilder();
                String sceneIdentifier = documentInfoMap.get(String.valueOf(score.getId())).getSceneIdentifier();

                stringBuilder.append(entryPrefix);
                for(String param : fileparams){
                    stringBuilder.append("-<").append(param).append(">");
                }

                String trecEntry = String.format(TREC_FORMAT, queryID, "skip", sceneIdentifier,
                        rank, decimalFormat.format(score.getScore()), stringBuilder.toString());

                writer.append(trecEntry).append("\n");
                rank++;

            }
            } catch (IOException e1) {
            e1.printStackTrace();
        }

    }


    public static float getAverageDocLength(Corpus corpus){
        int numberOfDocument = corpus.getCorpus().size();
        float lengthSum = 0;

        for(Scene scene : corpus.getCorpus()){
            lengthSum += scene.getText().split("\\s+").length;
        }

        return lengthSum / numberOfDocument;
    }

    public static float getTotalWordOccurences(Corpus corpus){

        float totalWordOccurences = 0;

        for(Scene scene : corpus.getCorpus()){
            totalWordOccurences += scene.getText().split("\\s+").length;
        }

        return totalWordOccurences;
    }

    public static void randomSelect(int numberOfTerms, int numberOfSets, String outputFileName,
                                    List<String> allTerms, Map<String, LookupItem> lookupTable){

        Random random = new Random();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName, true))) {

            for(int i = 0; i < numberOfSets; i++){

                for(int j = 0; j < numberOfTerms; j++) {
                    String term = allTerms.get(random.nextInt(lookupTable.size()));
                    writer.append(term);
                    writer.append(" ");
                }
                writer.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void deleteFile(String filename){

        File file = new File(filename);

        file.delete();
    }


}
