package index;

import java.util.ArrayList;
import java.util.List;

/**
 * Fundamental inverted index data type. Contains a document id and list of positions.
 */
public class Posting {

	private List<Integer> positions;
	private Integer docId;
	private int positionIndex = 0;
	
	/**
	 * Create a new Posting for a term
	 * @param position the first position of the term
	 * @param docId the document id
	 */
	public Posting(Integer docId, Integer position) {
		this.positions = new ArrayList<Integer>();
		this.positions.add(position);
		this.docId = docId;
	}

	public Posting(Integer docId, List<Integer> positions){
		this.docId = docId;
		this.positions = positions;
	}

	/**
	 * @param pos position to add to the list of positions
	 */
	public void add(Integer pos){
		this.positions.add(pos);
	}

	/**
	 * @return the list of positions as an array
	 */
	public Integer[] getPositionsArray() {
		return positions.toArray(new Integer[positions.size()]);
	}

	/**
	 * @return the entire posting as an array
	 */
	public ArrayList<Integer> toIntegerArray() {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ret.add(docId);
		ret.add(positions.size());
		ret.addAll(positions);
		return ret;
	}
	/**
	 * @return the number of occurrences of the term within the document
	 */
	public Integer getTermFreq() {		
		return this.positions.size();
	}

	/**
	 * @return the document id of this Posting
	 */
	public Integer getDocId() {
		return docId;
	}


	public int getCurrentPosition(){
		return positions.get(positionIndex);
	}

	public int getPositionSize(){
		return positions.size();
	}

	public void resetPositionIndex(){
		this.positionIndex = 0;
	}

	public void skipToNextPosition(int position){

		while(positionIndex < positions.size() && positions.get(positionIndex) <= position){
			positionIndex++;
		}
	}

	public boolean hasMore(){
		return positionIndex >= 0 && positionIndex < positions.size();
	}

}