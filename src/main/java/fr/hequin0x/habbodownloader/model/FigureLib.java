package fr.hequin0x.habbodownloader.model;

import javax.xml.bind.annotation.XmlAttribute;

public class FigureLib {
    private String id;

    public FigureLib() {

    }

    public String getId() {
        return this.id;
    }

    @XmlAttribute(name = "id")
    public void setId(String id) {
        this.id = id;
    }
}
