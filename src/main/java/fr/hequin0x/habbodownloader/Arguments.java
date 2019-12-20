package fr.hequin0x.habbodownloader;

import com.beust.jcommander.Parameter;

public class Arguments {

    @Parameter(names = "-furni", description = "Download furni")
    public boolean furni = false;

    @Parameter(names = "-figure", description = "Download figure files")
    public boolean figure = false;

    @Parameter(names = "-effect", description = "Download effect files")
    public boolean effect = false;

    @Parameter(names = "-badge", description = "Download badge files")
    public boolean badge = false;

    @Parameter(names = "-without-revision" , description = "Should create revision folders or not")
    public boolean withoutRevision = false;

    @Parameter(names = "-overwrite" , description = "Overwrite files that already exists")
    public boolean overwrite = false;
}
