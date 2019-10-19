package dataobject;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class DocumentInfo {

    @JsonRawValue
    private int documentLength;

    private String sceneIdentifier;

    // Default constructor for Jackson deserialize
    public DocumentInfo(){}

    public DocumentInfo(int documentLength, String sceneIdentifier){
        this.documentLength = documentLength;
        this.sceneIdentifier = sceneIdentifier;
    }

    @JsonGetter("documentLength")
    public int getDocumentLength() {
        return documentLength;
    }

    @JsonGetter("sceneIdentifier")
    public String getSceneIdentifier(){
        return sceneIdentifier;
    }


    public void setSceneIdentifier(String sceneIdentifier) {
        this.sceneIdentifier = sceneIdentifier;
    }



}
