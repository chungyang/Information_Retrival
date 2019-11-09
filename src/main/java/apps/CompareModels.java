package apps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import index.Index;
import index.InvertedIndex;
import retrieval.BM25;
import retrieval.Dirichlet;
import retrieval.JelinikMercer;
import retrieval.RetrievalModel;

public class CompareModels {

	public static void main(String[] args) {
		try {
			Index index = new InvertedIndex();
			int k = Integer.parseInt(args[0]);
			boolean compressed = Boolean.parseBoolean(args[1]);
			String querysFile = args[2];
			index.load(compressed);
			List<Map.Entry<Integer, Double>> results;
			PrintWriter outputWriter;
			BufferedReader reader = new BufferedReader(new FileReader(querysFile));
			String line;
			List <String> queries = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				queries.add(line);
			}
			reader.close();
			String runid = "dafisher-bm25-1.2-1.0-0.75";
			String outputFile = "bm25.trecrun";
			// do all three, writing output to files
			//BM25
			double k1 = 1.2;
			double k2 = 1.0;
			double b = 0.75;
			BM25 bm25 = new BM25(index, k1, k2, b);
			int qNum = 1;
			outputWriter = new PrintWriter(outputFile);
			for (String query: queries) {
				results = bm25.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<Integer, Double> result: results) {
					outputWriter.println("Q" + qNum + "\tskip\t" + index.getDocName(result.getKey()) +
							"\t" + rank + "\t" + result.getValue() + "\t" + runid);
					rank++;
				}
				qNum++;
			}
			outputWriter.close();
			//JM
			double lambda = 0.2;
			runid = "dafisher-jm-0.2";
			outputFile = "ql-jm.trecrun";
			RetrievalModel model = new JelinikMercer(index, lambda);
			qNum = 1;
			outputWriter = new PrintWriter(outputFile);
			for (String query: queries) {
				results = model.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<Integer, Double> result: results) {
					outputWriter.println("Q" + qNum + "\tskip\t" + index.getDocName(result.getKey()) +
							"\t" + rank + "\t" + result.getValue() + "\t" + runid);
					rank++;
				}
				qNum++;
			}
			outputWriter.close();
			//Dir
			double mu = 1500.0;
			runid = "dafisher-dir-1500";
			outputFile = "ql-dir.trecrun";
			model = new Dirichlet(index, mu);
			qNum = 1;
			outputWriter = new PrintWriter(outputFile);
			for (String query: queries) {
				results = model.retrieveQuery(query, k);
				int rank = 1;
				for (Map.Entry<Integer, Double> result: results) {
					outputWriter.println("Q" + qNum + "\tskip\t" + index.getDocName(result.getKey()) +
							"\t" + rank + "\t" + result.getValue() + "\t" + runid);
					rank++;
				}
				qNum++;
			}
			outputWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			// ignore
		}
	}
}
