package com.sulgaming.habbodownloader;

import com.beust.jcommander.Parameter;

public class Arguments {

    @Parameter(names = "-furni", description = "Download furni")
    public boolean furni = false;

    @Parameter(names = "-figure", description = "Download gordon files")
    public boolean figure = false;

    @Parameter(names = "-without-revision" , description = "Should create revision folders or not")
    public boolean withoutRevision = false;

    @Parameter(names = "-overwrite" , description = "Overwrite files that already exists")
    public boolean overwrite = false;
}
