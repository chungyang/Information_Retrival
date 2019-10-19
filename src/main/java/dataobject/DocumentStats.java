package dataobject;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.Map;

public class DocumentStats {


    @JsonRawValue
    private float averageDocumentLength;


    @JsonRawValue
    private float totalDocumentLength;

    private Map<String, DocumentInfo> documentInfos;

    public DocumentStats(){}


    public DocumentStats(float averageDocumentLength, float totalDocumentLength,
                         Map<String, DocumentInfo> documentInfos){

        this.averageDocumentLength = averageDocumentLength;
        this.totalDocumentLength = totalDocumentLength;
        this.documentInfos = documentInfos;
    }

    @JsonGetter("documentInfos")
    public Map<String, DocumentInfo> getDocumentInfos() {
        return documentInfos;
    }

    @JsonGetter("totalDocumentLength")
    public float getTotalDocumentLength() {
        return totalDocumentLength;
    }


    @JsonGetter("averageDocumentLength")
    public float getAverageDocumentLength() {
        return averageDocumentLength;
    }

}
