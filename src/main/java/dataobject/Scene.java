package dataobject;

import com.fasterxml.jackson.annotation.JsonRawValue;


public class Scene {

    private String playId;

    private String sceneId;

    @JsonRawValue
    private int sceneNum;

    private String text;


    public int getSceneNum() {
        return sceneNum;
    }


    public void setSceneNum(int sceneNum) {
        this.sceneNum = sceneNum;
    }


    public String getSceneId() {
        return sceneId;
    }


    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }


    public String getPlayId() {
        return playId;
    }


    public void setPlayId(String playId) {
        this.playId = playId;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

}
