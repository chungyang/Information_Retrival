package DataObject;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class LookupItem {

    @JsonRawValue
    private int offset;
    @JsonRawValue
    private int compressOffset;
    @JsonRawValue
    private int termFrequency;
    @JsonRawValue
    private int documentFrequency;

    // Default constructor for Jackson deserialize
    public LookupItem(){}

    public LookupItem(int offset, int compressOffset, int termFrequency, int documentFrequency){
        this.offset = offset;
        this.compressOffset = compressOffset;
        this.termFrequency = termFrequency;
        this.documentFrequency = documentFrequency;
    }

    @JsonGetter("offset")
    public int getOffset(){
        return this.offset;
    }

    @JsonGetter("termFrequency")
    public int getTermFrequency() {
        return termFrequency;
    }

    @JsonGetter("documentFrequency")
    public int getDocumentFrequency() {
        return documentFrequency;
    }

    @JsonGetter("compressOffset")
    public int getCompressOffset() {
        return compressOffset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public void setCompressOffset(int compressOffset) {
        this.compressOffset = compressOffset;
    }
}
