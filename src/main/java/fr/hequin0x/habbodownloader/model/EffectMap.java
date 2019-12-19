package fr.hequin0x.habbodownloader.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "map")
public class EffectMap {

    @XmlElement(name = "effect")
    private ArrayList<EffectLib> effectLibsList;

    public EffectMap() {

    }

    public ArrayList<EffectLib> getEffectLibs() {
        return this.effectLibsList;
    }

    public void setEffectLibs(ArrayList<EffectLib> effectLibsList) {
        this.effectLibsList = effectLibsList;
    }
}
