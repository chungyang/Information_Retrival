package index;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import cluster.DocumentVector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utilities.*;

public class IndexBuilder {
    private Map<Integer, String> sceneIdMap; 
    private Map<Integer, String> playIdMap;
    private Map<String, PostingList> invertedLists;
    private Map<Integer, Integer> docLengths;
    private Map<Integer, Map<String, Double>> documentVectors;
	private Compressors compression;

	public IndexBuilder() {
	    sceneIdMap = new HashMap<Integer, String>();
	    playIdMap = new HashMap<Integer, String>();
	    invertedLists = new HashMap<String, PostingList>();
	    docLengths = new HashMap<Integer, Integer>();
        documentVectors = new HashMap<>();
	}
    private void parseFile(String filename) {
        JSONParser parser = new JSONParser();
        try {
        	// fetch the scenes
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filename));
            JSONArray scenes = (JSONArray) jsonObject.get("corpus");
            // iterate over the scenes
            for (int idx = 0; idx < scenes.size(); idx++) {
                JSONObject scene = (JSONObject) scenes.get(idx);
                // start document ids at 1, not 0
                int docId = idx + 1;
                // record the external scene and play identifiers
                String sceneId = (String) scene.get("sceneId");
                sceneIdMap.put(docId, sceneId);
                String playId = (String) scene.get("playId");
                playIdMap.put(docId, playId);
                
                String text = (String) scene.get("text");
                String[] words = text.split("\\s+");
                //record the document length
                docLengths.put(docId, words.length);


                // iterate over the terms in the scene
                for (int pos = 0; pos < words.length; pos++) {
                	String word = words[pos];
                	invertedLists.putIfAbsent(word, new PostingList());
                	invertedLists.get(word).add(docId, pos+1);
                 }


            }

        } catch (ParseException e) {
        	// actually do something when bad things happen...
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveStringMap(String fileName, Map<Integer, String> map) {
        List<String> lines = new ArrayList<>();
        map.forEach((k,v) -> lines.add(k + " " + v));
        try {
            Path file = Paths.get(fileName);
            Files.write(file, lines, Charset.forName("UTF-8"));
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDocLengths(String fileName) {
        List<String> lines = new ArrayList<>();
        docLengths.forEach((k,v) -> lines.add(k + " " + v));
        try {
            Path file = Paths.get(fileName);
            Files.write(file, lines, Charset.forName("UTF-8"));
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveInvertedLists(String lookupName, String invListName) {
        long offset = 0;
        try {
            PrintWriter lookupWriter = new PrintWriter(lookupName, "UTF-8");
            RandomAccessFile invListWriter = new RandomAccessFile(invListName, "rw");
            Compression comp = CompressionFactory.getCompressor(compression);

            for (Map.Entry<String, PostingList> entry : invertedLists.entrySet()) {
                String term = entry.getKey();
                PostingList postings = entry.getValue();
                int docTermFreq = postings.documentCount();
                int collectionTermFreq = postings.termFrequency();
                Integer [] posts = postings.toIntegerArray();
                ByteBuffer byteBuffer = ByteBuffer.allocate(posts.length * 8);
                comp.encode(posts, byteBuffer);
                // only write the bytes we used (as may be fewer than capacity)
                byte [] array = byteBuffer.array();
                invListWriter.write(array, 0, byteBuffer.position());
                long bytesWritten = invListWriter.getFilePointer() - offset;
                lookupWriter.println(term + " " + offset + " " + bytesWritten + " " + docTermFreq + " " + collectionTermFreq);
                offset = invListWriter.getFilePointer();
            }
            invListWriter.close();
            lookupWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void getDocumentVector(){

        for(String term : invertedLists.keySet()){

            PostingList postingList = invertedLists.get(term);

            for(int i = 0; i < postingList.documentCount(); i++){

                Posting posting = postingList.get(i);
                int docid = posting.getDocId();
                Map<String, Double> docVector = documentVectors.getOrDefault(docid, new HashMap<>());
                double tf = posting.getTermFreq();
                double tdf = Math.log((sceneIdMap.size() + 1) / (postingList.documentCount() + 0.5));
                docVector.put(term, BigDecimal.valueOf(tf * tdf).setScale(3, RoundingMode.HALF_UP).doubleValue());
                documentVectors.put(docid, docVector);
            }
        }

    }

    private void saveDocumentVecotrs(String fileName){

        getDocumentVector();
        String jsonText = JSONValue.toJSONString(documentVectors);

        try(FileWriter file = new FileWriter(fileName)){
            file.write(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void buildIndex(String sourcefile, boolean compress) {
    	this.compression = compress ? Compressors.VBYTE : Compressors.EMPTY;
    	String invFile = compress ? "invListCompressed" : "invList";
        parseFile(sourcefile);
        // refactor the hardcoded names...
        saveStringMap("sceneId.txt", sceneIdMap);
        saveStringMap("playIds.txt", playIdMap);
        saveDocLengths("docLength.txt");
        saveInvertedLists("lookup.txt", invFile);
        saveDocumentVecotrs("docVectors.json");
    }


 }
