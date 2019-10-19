package dataobject;


import java.util.List;

public class Corpus {

    public List<Scene> getCorpus() {
        return corpus;
    }

    public void setCorpus(List<Scene> scenes) {
        this.corpus = scenes;
    }

    private List<Scene> corpus;
}
