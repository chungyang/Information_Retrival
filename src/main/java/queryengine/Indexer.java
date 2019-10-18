package queryengine;

import dataobject.*;
import jsonutil.DocumentInfoSerializer;
import utilities.Compressor;
import utilities.DefaultCompressor;
import utilities.DeltaEncoder;
import utilities.VbyteCompressor;
import jsonutil.JsonParser;
import jsonutil.LookupItemSerializer;
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

    @JsonSerialize(using = DocumentInfoSerializer.class)
    private Map<String, DocumentInfo> documentInfo = new HashMap<>();


    public void buildIndex(String binaryFileName, String lookupJsonFileName, String documentInfoJson){

        JsonParser jsonParser = new JsonParser();
        Corpus corpus = jsonParser.parseJson2Corpus("shakespeare-scenes.json");
        int shortestSceneLength = Integer.MAX_VALUE;
        String shortestSceneID = "";
        float averageLength = 0;
        float count = 0;

        Map<String, Integer> playLengths = new HashMap<>();

        for(Scene scene : corpus.getCorpus()){

            String[] words = scene.getText().split("\\s+");
            int sceneNumber = scene.getSceneNum();
            averageLength += words.length;
            count++;


            String sceneId = scene.getSceneId();
            String playId = scene.getPlayId();
            Set<String> seenWords = new HashSet<>();

            int playLength = playLengths.getOrDefault(playId, 0);
            playLength += words.length;
            playLengths.put(playId, playLength);

            documentInfo.put(String.valueOf(sceneNumber + 1), new DocumentInfo(words.length, sceneId));

            if(words.length < shortestSceneLength){
                shortestSceneID = sceneId;
                shortestSceneLength = words.length;
            }

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
        System.out.println("Average Scene Length: " + (averageLength / count));
        System.out.println("Shortest Scene ID: " + shortestSceneID);

        PriorityQueue<Play> plays = new PriorityQueue<>((a,b)->a.length - b.length);

        for(Map.Entry<String, Integer> entry : playLengths.entrySet()){
            Play play = new Play(entry.getKey(), entry.getValue());
            plays.add(play);
        }

        System.out.println("Shortest Play ID: " + plays.poll().id);

        Play longestPlay = null;

        while(!plays.isEmpty()){
            longestPlay = plays.poll();
        }

        System.out.println("Longest Play ID: " + longestPlay.id);

        flushToDisk(invertedIndex, binaryFileName, lookupJsonFileName, documentInfoJson);
    }


    private void flushToDisk(Map<String, List<Posting>> invertedIndex, String binaryFileName,
                             String lookupJsonFileName, String documentInfoJson){

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(binaryFileName, "rw");
        RandomAccessFile compressRandomAccessFile = new RandomAccessFile("compress_" + binaryFileName, "rw")) {

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
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(lookupJsonFileName), lookupTable);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(documentInfoJson), documentInfo);
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

    class Play{
        protected String id;
        protected int length;

        Play(String id, int length){
            this.id = id;
            this.length = length;
        }
    }
    public static void main(String[] args){
        Indexer indexer = new Indexer();
        String binaryFileName = "binary.dat";
        String lookupJsonFileName = "lookup.json";
        String documentInfoJson = "documentinfo.json";

        indexer.buildIndex(binaryFileName, lookupJsonFileName, documentInfoJson);
    }
    
}
