package com.sulgaming.habbodownloader.process;

import com.sulgaming.habbodownloader.model.FurniData;
import com.sulgaming.habbodownloader.model.FurniType;
import org.apache.commons.io.FileUtils;

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

    private final String furniDirectoryName;
    private final AtomicInteger downloadedCount;
    private final AtomicInteger skippedCount;
    private final AtomicInteger failedCount;

    private final boolean withoutRevision;

    public FurniRipper(boolean withoutRevision) {
        this.furniDirectoryName = "furni";
        this.downloadedCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);

        this.withoutRevision = withoutRevision;
    }

    public void start() throws IOException, JAXBException {
        System.out.println("Starting the furni ripping :)");

        this.createFurniFolder();

        final FurniData furniData = parseFurniData();

        this.downloadFurni(furniData);

        System.out.println(String.format("Completed! %d furni downloaded, %d furni already existed, %d furni failed to download.",
                this.downloadedCount.get(), this.skippedCount.get(), this.failedCount.get()));
    }

    private void createFurniFolder() {
        File furniDirectory = new File(this.furniDirectoryName);

        if(!furniDirectory.exists()) {
            furniDirectory.mkdir();
        }
    }

    private FurniData parseFurniData() throws IOException, JAXBException {
        URL furniDataUrl = new URL("https://www.habbo.com/gamedata/furnidata_xml/0");

        JAXBContext jaxbContext = JAXBContext.newInstance(FurniData.class);
        Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();

        FurniData furniData = (FurniData) unMarshaller.unmarshal(furniDataUrl);

        System.out.println(String.format("Found %d room items and %d wall items.", furniData.getRoomItems().size(), furniData.getWallItems().size()));

        return furniData;
    }

    private void downloadFurni(FurniData furniData) {
        System.out.println("Downloading room items and wall items.");

        List<FurniType> mergedFurnis = Stream.concat(furniData.getRoomItems().stream(), furniData.getWallItems().stream())
                .collect(Collectors.toList());

        mergedFurnis.parallelStream().forEach((furniType) -> {
            int revision = furniType.getRevision();
            String className = furniType.getClassName();

            File furni = !withoutRevision ? new File(String.format("%s/%d/%s.swf", this.furniDirectoryName, revision, className)) :
                    new File(String.format("%s/%s.swf", this.furniDirectoryName, className));

            if(furni.exists()) {
                this.skippedCount.incrementAndGet();

                System.out.println(String.format("Furni %s already exist, skipped it.", className));
            } else {
                try {
                    System.out.println(String.format("Downloading furni %s...", className));

                    FileUtils.copyURLToFile(new URL(String.format("https://images.habbo.com/dcr/hof_furni/%d/%s.swf", revision, className)), furni);

                    this.downloadedCount.incrementAndGet();
                } catch (IOException e) {
                    this.failedCount.incrementAndGet();

                    if(e instanceof FileNotFoundException) {
                        System.out.println(String.format("Failed to download furni %s because is unavailable.", className));
                        return;
                    }

                    e.printStackTrace();
                }
            }
        });
    }
}
