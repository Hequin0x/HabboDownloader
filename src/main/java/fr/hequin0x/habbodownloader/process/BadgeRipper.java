package fr.hequin0x.habbodownloader.process;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class BadgeRipper {
    private final Logger logger = LogManager.getLogger(BadgeRipper.class);

    private final String badgeDirectoryName;
    private final AtomicInteger downloadedCount;
    private final AtomicInteger skippedCount;
    private final AtomicInteger failedCount;

    public BadgeRipper() {
        this.badgeDirectoryName = "c_images/album1584";
        this.downloadedCount = new AtomicInteger(0);
        this.skippedCount = new AtomicInteger(0);
        this.failedCount = new AtomicInteger(0);
    }

    public void start() throws Exception {
        this.logger.info("Starting the badge ripping :)");

        this.createBadgeFolder();

        List<String> badges = this.fetchBadgeNames();

        this.downloadBadges(badges);

        this.logger.info("Completed! {} badges downloaded, {} badges already existed, {} badges failed to download.",
                this.downloadedCount.get(), this.skippedCount.get(), this.failedCount.get());
    }

    private void createBadgeFolder() {
        File effectDirectory = new File(this.badgeDirectoryName);

        if(!effectDirectory.exists()) {
            effectDirectory.mkdir();
        }
    }

    private List<String> fetchBadgeNames() throws IOException {
        List<String> badges = new ArrayList<>();

        this.logger.info("Fetching badges names...");

        URL externalTextsUrl = new URL("https://www.habbo.com/gamedata/external_flash_texts/0");

        String externalTextsContent;

        try (Scanner scanner = new Scanner(externalTextsUrl.openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            externalTextsContent = scanner.hasNext() ? scanner.next() : "";
        }

        for(String line : externalTextsContent.split("\n")) {
            if(line.contains("_badge_name")) {
                int endIndex = line.indexOf("_badge_name");
                String badgeName = line.substring(0, endIndex);

                if(badges.contains(badgeName)) continue;

                badges.add(badgeName);
            }
        }

        this.logger.info("Found {} badges", badges.size());

        return badges;
    }

    private void downloadBadges(List<String> badges) {
        this.logger.info("Downloading badges...");

        try (ProgressBar pb = new ProgressBar("Badges download", badges.size())) {
            badges.parallelStream().forEach((badgeName) -> {
                pb.step();

                File badge = new File(String.format("%s/%s.gif", this.badgeDirectoryName, badgeName));

                if (badge.exists()) {
                    this.skippedCount.incrementAndGet();
                    this.logger.info("Badge {} already exist, skipped it.", badgeName);
                } else {
                    try {
                        FileUtils.copyURLToFile(new URL(String.format("https://images.habbo.com/c_images/album1584/%s.gif", badgeName)), badge);

                        this.downloadedCount.incrementAndGet();
                    } catch (IOException e) {
                        this.failedCount.incrementAndGet();

                        if (e instanceof FileNotFoundException) {
                            return;
                        }

                        this.logger.error(e);
                    }
                }
            });
        }
    }
}
