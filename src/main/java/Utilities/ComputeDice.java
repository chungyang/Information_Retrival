package Utilities;

import Utilities.Compressor;
import Utilities.DefaultCompressor;
import Utilities.VbyteCompressor;
import DataObject.LookupItem;
import DataObject.Posting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class ComputeDice {



    public static void getDiceCoefficient(List<List<String>> termSets, boolean isCompress, String postingFileName,
                                          List<String> allTemrs, Map<String, LookupItem> lookupTable) throws IOException {

        Compressor compressor = isCompress? new VbyteCompressor() : new DefaultCompressor();

        RandomAccessFile randomAccessFile = new RandomAccessFile(postingFileName, "rw");

        for(List<String> set : termSets){

            int size = set.size();
            for(int i = 0; i < size; i++) {

                String queryTerm = set.get(i);
                String correspondingTerm = null;
                float highestScore = 0;
                List<Posting> postings1 = compressor.decompress(lookupTable.get(queryTerm), randomAccessFile);
                Set<Integer> documentSet1 = getDocumentSet(postings1);
                float na = postings1.size();

                for (String term : allTemrs) {

                    List<Posting> postings2 = compressor.decompress(lookupTable.get(term), randomAccessFile);

                    float nb = postings2.size();

                    Set<Integer> documentSet2 = getDocumentSet(postings2);

                    documentSet2.retainAll(documentSet1);

                    if (documentSet2.size() == 0 || queryTerm.equals(term)) {
                        continue;
                    }

                    Map<Integer, List<List<Integer>>> commonDocPositions = new HashMap<>();

                    for(Posting posting : postings1){

                        if(documentSet2.contains(posting.getDocumentId())){
                            List<List<Integer>> positions = new ArrayList<>();
                            positions.add(posting.getPositions());
                            commonDocPositions.put(posting.getDocumentId(), positions);
                        }
                    }

                    for(Posting posting : postings2){

                        if(documentSet2.contains(posting.getDocumentId())){
                            List<List<Integer>> positions = commonDocPositions.get(posting.getDocumentId());
                            positions.add(posting.getPositions());
                            commonDocPositions.put(posting.getDocumentId(), positions);
                        }
                    }

                    float nab = 0;

                    for(Map.Entry<Integer, List<List<Integer>>> entry : commonDocPositions.entrySet()){
                        List<List<Integer>> positions = entry.getValue();
                        List<Integer> positions1 = positions.get(0);
                        List<Integer> positions2 = positions.get(1);

                        nab += getNab(positions1, positions2);

                    }

                    if((nab/ (na + nb)) > highestScore){
                        highestScore = (nab/ (na + nb));
                        correspondingTerm = term;
                    }
                }

                set.add(correspondingTerm);
            }

            randomAccessFile.close();

        }

        if(termSets.get(0) != null){
            writeToFile(termSets, String.valueOf(termSets.get(0).size()) + "term_" + postingFileName);
        }
    }
    private static float getNab(List<Integer> positions1, List<Integer> positions2){
        float count = 0;

        for(int position1 : positions1){
            for(int position2 : positions2){
                if(position2 - position1 == 1){
                    count++;
                }
            }
        }

        return count;
    }

    private static void writeToFile(List<List<String>> termSets, String fileName){

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

            for(List<String> set : termSets){
                for(String s : set){
                    writer.append(s).append(" ");
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private static Set<Integer> getDocumentSet(List<Posting> postings){
        Set<Integer> documentSet = new HashSet<>();

        for(Posting posting : postings){
            documentSet.add(posting.getDocumentId());
        }

        return documentSet;
    }

}
