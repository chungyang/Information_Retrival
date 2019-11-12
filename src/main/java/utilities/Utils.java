package utilities;


import index.InvertedIndex;
import retrieval.DocumentScore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class Utils {

    /**
     * This method writes the document ranking into TREC format. It writes entries according to
     * the list of documentScore provided so it is assumed the list is already sorted with the highest
     * score first.
     *
     * @param trecFilename      file name for the file to write to
     * @param isappend          Whether to keep appending to file or overwrite it
     * @param documentScores
     * @param sceneIdMap
     * @param entryPrefix       prefix for the sixth column in TREC
     * @param fileparams        file name parameters
     */
    public static void writeTREC(String trecFilename, boolean isappend, List<DocumentScore> documentScores,
                                 Map<Integer, String> sceneIdMap, String entryPrefix,
                                 String queryID, String... fileparams){

        final String TREC_FORMAT = "%-" + 5 + "s %s %-" + 50 + "s %s %s %s";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(trecFilename, isappend))) {

            int rank = 1;
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(3);

            for(DocumentScore score : documentScores){

                StringBuilder stringBuilder = new StringBuilder();
                String sceneIdentifier = sceneIdMap.get(score.getId());

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

    public static void deleteFile(String filename){

        File file = new File(filename);

        file.delete();
    }


}
