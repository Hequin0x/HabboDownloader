package com.sulgaming.habbodownloader;

import com.beust.jcommander.JCommander;
import com.sulgaming.habbodownloader.process.FurniRipper;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException, JAXBException {
        Arguments arguments = new Arguments();

        JCommander.newBuilder().addObject(arguments).build().parse(args);

        if(arguments.furni) {
            new FurniRipper(arguments.withoutRevision).start();
        }
    }
}
