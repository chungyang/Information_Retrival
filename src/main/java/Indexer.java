import DataObject.Corpus;
import DataObject.LookupItem;
import DataObject.Posting;
import DataObject.Scene;
import JsonUtil.JsonParser;
import JsonUtil.LookupItemSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;


public class Indexer {

    private Map<String, List<Posting>> invertedIndex = new HashMap<>();

    @JsonSerialize(using = LookupItemSerializer.class)
    private Map<String, LookupItem> lookupTable = new HashMap<>();


    public void buildIndex(){

        JsonParser jsonParser = new JsonParser();
        Corpus corpus = jsonParser.parseJson2Corpus("shakespeare-scenes.json");

        for(Scene scene : corpus.getCorpus()){

            String[] words = scene.getText().split("\\s+");
            String sceneId = scene.getSceneId();
            String playId = scene.getPlayId();
            int sceneNumber = scene.getSceneNum();
            Set<String> seenWords = new HashSet<>();

            for(int position = 0; position < words.length; position++){

                String currentWord = words[position];
                List<Posting> postings = invertedIndex.getOrDefault(currentWord, new ArrayList<>());

                Posting  currentPosting = seenWords.contains(currentWord)?
                            postings.remove(postings.size() - 1) : new Posting(sceneNumber + 1);

                seenWords.add(currentWord);

                currentPosting.addPosition(position + 1);

                postings.add(currentPosting);

                invertedIndex.put(currentWord, postings);
            }

        }

        flushToDisk(invertedIndex, "binary.dat");
    }


    private void flushToDisk(Map<String, List<Posting>> invertedIndex, String fileName){

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        RandomAccessFile compressRandomAccessFile = new RandomAccessFile("compress_" + fileName, "rw")) {

            int uncompressOffset = 0;
            int compressOffset = 0;

            Compressor defaultCompressor = new DefaultCompressor();
            Compressor vByteCompressor = new VbyteCompressor();

            for(Map.Entry<String, List<Posting>> entry : invertedIndex.entrySet()){

                List<Posting> postings = entry.getValue();
                int[] v = postingsToIntegers(postings);
                byte[] uncompressBytes = defaultCompressor.compress(v);
                randomAccessFile.write(uncompressBytes);

                List<Posting> encodedPostings = DeltaEncoder.deltaEncode(postings);
                v = postingsToIntegers(encodedPostings);
                byte[] compressBytes = vByteCompressor.compress(v);
                compressRandomAccessFile.write(compressBytes);


                int termFrequency = getTermFrequency(postings);
                lookupTable.put(entry.getKey(), new LookupItem(uncompressOffset, compressOffset, termFrequency, postings.size()));
                uncompressOffset += uncompressBytes.length;
                compressOffset += compressBytes.length;

            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("lookup.json"), lookupTable);


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int[] postingsToIntegers(List<Posting> postings){
        List<Integer> integersToWrite = new ArrayList<>();

        integersToWrite.add(postings.size());

        for(Posting posting : postings){

            integersToWrite.add(posting.getPositions().size());
            integersToWrite.add(posting.getDocumentId());

            for(int position : posting.getPositions()){
                integersToWrite.add(position);
            }
        }

        int[] integers = new int[integersToWrite.size()];

        for(int i = 0; i < integers.length; i++){
            integers[i] = integersToWrite.get(i);
        }

        return integers;
    }


    private int getTermFrequency(List<Posting> postings){
        int frequency = 0;

        for(Posting posting : postings){
            frequency += posting.getPositions().size();
        }

        return frequency;
    }



    public static void main(String[] args){
        Indexer indexer = new Indexer();
        indexer.buildIndex();
    }
    
}
