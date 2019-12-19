package fr.hequin0x.habbodownloader.process;

import fr.hequin0x.habbodownloader.model.EffectMap;
import fr.hequin0x.habbodownloader.util.Habbo;
import me.tongfei.progressbar.ProgressBar;
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

public class EffectRipper {
    private final Logger logger = LogManager.getLogger(EffectRipper.class);

    private final String effectDirectoryName;
    private final AtomicInteger downloadedCount;
    private final AtomicInteger skippedCount;
    private final AtomicInteger failedCount;

    private final boolean overwrite;

    private final File effectMap;

    private String currentRevision;

    public EffectRipper(boolean overwrite) {
        this.effectDirectoryName = "effect";
        this.downloadedCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);

        this.overwrite = overwrite;

        this.effectMap = new File(String.format("%s/effectmap.xml", this.effectDirectoryName));
    }

    public void start() throws Exception {
        this.logger.info("Starting the effect ripping :)");

        this.currentRevision = Habbo.getCurrentRevision();

        this.logger.info(String.format("Current Revision: %s", this.currentRevision));

        this.createEffectFolder();
        this.downloadEffectMap();

        EffectMap effectMap = this.parseEffectMap();

        this.downloadEffectLibs(effectMap);

        this.logger.info("Completed! {} effects downloaded, {} effects already existed, {} effects failed to download.",
                this.downloadedCount.get(), this.skippedCount.get(), this.failedCount.get());
    }

    private void createEffectFolder() {
        File effectDirectory = new File(this.effectDirectoryName);

        if(!effectDirectory.exists()) {
            effectDirectory.mkdir();
        }
    }

    private void downloadEffectMap() throws IOException {
        this.logger.info("Downloading required files...");

        FileUtils.copyURLToFile(new URL(String.format("https://images.habbo.com/gordon/%s/effectmap.xml", this.currentRevision)), this.effectMap);
    }

    private EffectMap parseEffectMap() throws JAXBException {
        this.logger.info("Parsing effect map file...");

        JAXBContext jaxbContext = JAXBContext.newInstance(EffectMap.class);
        Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();

        EffectMap effectMap = (EffectMap) unMarshaller.unmarshal(this.effectMap);

        this.logger.info("Found {} effects.", effectMap.getEffectLibs().size());

        return effectMap;
    }

    private void downloadEffectLibs(EffectMap effectMap) {
        this.logger.info("Downloading effects...");

        try (ProgressBar pb = new ProgressBar("Effects download", effectMap.getEffectLibs().size())) {

            effectMap.getEffectLibs().parallelStream().forEach((effectLib) -> {
                pb.step();

                String lib = effectLib.getLib();

                File effect = new File(String.format("%s/%s.swf", this.effectDirectoryName, lib));

                if (effect.exists() && !this.overwrite) {
                    this.skippedCount.incrementAndGet();
                    this.logger.info("Effect {} already exist, skipped it.", lib);
                } else {
                    try {
                        //this.logger.info("Downloading effect {}...", lib);

                        FileUtils.copyURLToFile(new URL(String.format("https://images.habbo.com/gordon/%s/%s.swf", this.currentRevision, lib)), effect);

                        this.downloadedCount.incrementAndGet();
                    } catch (IOException e) {
                        this.failedCount.incrementAndGet();

                        if (e instanceof FileNotFoundException) {
                            //this.logger.warn("Failed to download effect {} because is unavailable.", lib);
                            return;
                        }

                        this.logger.error(e);
                    }
                }
            });
        }
    }
}
