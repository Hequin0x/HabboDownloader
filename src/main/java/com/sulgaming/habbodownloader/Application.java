package com.sulgaming.habbodownloader;

import com.beust.jcommander.JCommander;
import com.sulgaming.habbodownloader.process.EffectRipper;
import com.sulgaming.habbodownloader.process.FurniRipper;
import com.sulgaming.habbodownloader.process.FigureRipper;

public class Application {

    public static void main(String[] args) throws Exception {
        Arguments arguments = new Arguments();

        JCommander.newBuilder().addObject(arguments).build().parse(args);

        if(arguments.furni) {
            new FurniRipper(arguments.withoutRevision, arguments.overwrite).start();
        } else if(arguments.figure) {
            new FigureRipper(arguments.overwrite).start();
        } else if(arguments.effect) {
            new EffectRipper(arguments.overwrite).start();
        }
    }
}
