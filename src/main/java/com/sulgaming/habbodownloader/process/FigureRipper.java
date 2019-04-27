package com.sulgaming.habbodownloader.process;

import com.sulgaming.habbodownloader.model.FigureMap;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class FigureRipper {
    private final Logger logger = LogManager.getLogger(FigureRipper.class);

    private final String figureDirectoryName;
    private final AtomicInteger downloadedCount;
    private final AtomicInteger skippedCount;
    private final AtomicInteger failedCount;

    private final boolean overwrite;

    private final File figureMap;

    public FigureRipper(boolean overwrite) {
        this.figureDirectoryName = "figure";
        this.downloadedCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);

        this.overwrite = overwrite;

        this.figureMap = new File(String.format("%s/figuremap.xml", this.figureDirectoryName));
    }

    public void start() throws IOException, JAXBException {
        this.logger.info("Starting the gordon ripping :)");

        this.createGordonFolder();
        this.downloadRequiredFiles();

        FigureMap figureMap = this.parseFigureMap();

        this.downloadFigureLibs(figureMap);

        this.logger.info("Completed! {} figures downloaded, {} figures already existed, {} figures failed to download.",
                this.downloadedCount.get(), this.skippedCount.get(), this.failedCount.get());
    }

    private void createGordonFolder() {
        File figureDirectory = new File(this.figureDirectoryName);

        if(!figureDirectory.exists()) {
            figureDirectory.mkdir();
        }
    }

    private void downloadRequiredFiles() throws IOException {
        this.logger.info("Downloading required files...");

        FileUtils.copyURLToFile(new URL("https://images.habbo.com/gordon/PRODUCTION-201904222208-183436269/figuremap.xml"), this.figureMap);
    }

    private FigureMap parseFigureMap() throws JAXBException {
        this.logger.info("Parsing figure map file...");

        JAXBContext jaxbContext = JAXBContext.newInstance(FigureMap.class);
        Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();

        FigureMap figureMap = (FigureMap) unMarshaller.unmarshal(this.figureMap);

        this.logger.info("Found {} figures.", figureMap.getFigureLibs().size());

        return figureMap;
    }

    private void downloadFigureLibs(FigureMap figureMap) {
        this.logger.info("Downloading figures...");

        figureMap.getFigureLibs().parallelStream().forEach((figureLib) -> {
            String id = figureLib.getId();

            File figure = new File(String.format("%s/%s.swf", this.figureDirectoryName, id));

            if(figure.exists() && !this.overwrite) {
                this.skippedCount.incrementAndGet();
                this.logger.info("Figure {} already exist, skipped it.", id);
            } else {
                try {
                    this.logger.info("Downloading figure {}...", id);

                    FileUtils.copyURLToFile(new URL(String.format("https://images.habbo.com/gordon/PRODUCTION-201904222208-183436269/%s.swf", id)), figure);

                    this.downloadedCount.incrementAndGet();
                } catch (IOException e) {
                    this.failedCount.incrementAndGet();

                    if(e instanceof FileNotFoundException) {
                        this.logger.warn("Failed to download figure {} because is unavailable.", id);
                        return;
                    }

                    this.logger.error(e);
                }
            }
        });

    }
}
