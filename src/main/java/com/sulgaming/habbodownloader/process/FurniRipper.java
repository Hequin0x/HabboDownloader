package com.sulgaming.habbodownloader.process;

import com.sulgaming.habbodownloader.model.FurniData;
import com.sulgaming.habbodownloader.model.FurniType;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FurniRipper {
    private final Logger logger = LogManager.getLogger(FurniRipper.class);

    private final String furniDirectoryName;
    private final AtomicInteger downloadedCount;
    private final AtomicInteger skippedCount;
    private final AtomicInteger failedCount;

    private final boolean withoutRevision;
    private final boolean overwrite;

    public FurniRipper(boolean withoutRevision, boolean overwrite) {
        this.furniDirectoryName = "hof_furni";
        this.downloadedCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);

        this.withoutRevision = withoutRevision;
        this.overwrite = overwrite;
    }

    public void start() throws IOException, JAXBException {
        this.logger.info("Starting the furni ripping :)");

        this.createFurniFolder();

        final FurniData furniData = this.parseFurniData();

        this.downloadFurni(furniData);

        this.logger.info("Completed! {} furni downloaded, {} furni already existed, {} furni failed to download.",
                this.downloadedCount.get(), this.skippedCount.get(), this.failedCount.get());
    }

    private void createFurniFolder() {
        File furniDirectory = new File(this.furniDirectoryName);

        if(!furniDirectory.exists()) {
            furniDirectory.mkdir();
        }
    }

    private FurniData parseFurniData() throws IOException, JAXBException {
        this.logger.info("Parsing furni data file...");

        URL furniDataUrl = new URL("https://www.habbo.com/gamedata/furnidata_xml/0");

        JAXBContext jaxbContext = JAXBContext.newInstance(FurniData.class);
        Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();

        FurniData furniData = (FurniData) unMarshaller.unmarshal(furniDataUrl);

        this.logger.info("Found {} room items and {} wall items.", furniData.getRoomItems().size(), furniData.getWallItems().size());

        return furniData;
    }

    private void downloadFurni(FurniData furniData) {
        this.logger.info("Downloading room items and wall items.");

        List<FurniType> mergedFurnis = Stream.concat(furniData.getRoomItems().stream(), furniData.getWallItems().stream())
                .collect(Collectors.toList());

        mergedFurnis.parallelStream().forEach((furniType) -> {
            int revision = furniType.getRevision();
            String className = furniType.getClassName();

            File furni = !withoutRevision ? new File(String.format("%s/%d/%s.swf", this.furniDirectoryName, revision, className)) :
                    new File(String.format("%s/%s.swf", this.furniDirectoryName, className));

            if(furni.exists() && !this.overwrite) {
                this.skippedCount.incrementAndGet();
                this.logger.info("Furni {} already exist, skipped it.", className);
            } else {
                try {
                    this.logger.info("Downloading furni {}...", className);

                    FileUtils.copyURLToFile(new URL(String.format("https://images.habbo.com/dcr/hof_furni/%d/%s.swf", revision, className)), furni);

                    this.downloadedCount.incrementAndGet();
                } catch (IOException e) {
                    this.failedCount.incrementAndGet();

                    if(e instanceof FileNotFoundException) {
                        this.logger.warn("Failed to download furni {} because is unavailable.", className);
                        return;
                    }

                    this.logger.error(e);
                }
            }
        });
    }
}
