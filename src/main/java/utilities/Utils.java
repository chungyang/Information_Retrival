package utilities;

import dataobject.LookupItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Utils {

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
}
