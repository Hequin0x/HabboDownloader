package com.sulgaming.habbodownloader.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class FurniType {
    private String className;
    private int revision;

    public FurniType() {

    }

    public String getClassName() {
        return this.className;
    }

    @XmlAttribute(name = "classname")
    public void setClassName(String className) {
        this.className = className;
    }

    public int getRevision() {
        return this.revision;
    }

    @XmlElement
    public void setRevision(int revision) {
        this.revision = revision;
    }
}
