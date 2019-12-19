package fr.hequin0x.habbodownloader.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "map")
public class FigureMap {

    @XmlElement(name = "lib")
    private ArrayList<FigureLib> figureLibsList;

    public FigureMap() {

    }

    public ArrayList<FigureLib> getFigureLibs() {
        return this.figureLibsList;
    }

    public void setFigureLibs(ArrayList<FigureLib> figureLibsList) {
        this.figureLibsList = figureLibsList;
    }
}
