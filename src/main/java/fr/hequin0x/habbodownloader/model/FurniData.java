package fr.hequin0x.habbodownloader.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "furnidata")
public class FurniData {

    @XmlElementWrapper(name = "roomitemtypes")
    @XmlElement(name = "furnitype")
    private ArrayList<FurniType> roomItemsList;

    @XmlElementWrapper(name = "wallitemtypes")
    @XmlElement(name = "furnitype")
    private ArrayList<FurniType> wallItemsList;

    public FurniData() {

    }

    public ArrayList<FurniType> getRoomItems() {
        return this.roomItemsList;
    }

    public void setRoomItems(ArrayList<FurniType> roomItemsList) {
        this.roomItemsList = roomItemsList;
    }

    public ArrayList<FurniType> getWallItems() {
        return this.wallItemsList;
    }

    public void setWallItems(ArrayList<FurniType> wallItemsList) {
        this.wallItemsList = wallItemsList;
    }
}
