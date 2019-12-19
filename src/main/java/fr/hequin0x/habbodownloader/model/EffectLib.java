package fr.hequin0x.habbodownloader.model;

import javax.xml.bind.annotation.XmlAttribute;

public class EffectLib {
    private String lib;

    public EffectLib() {

    }

    public String getLib() {
        return this.lib;
    }

    @XmlAttribute(name = "lib")
    public void setLib(String lib) {
        this.lib = lib;
    }
}
