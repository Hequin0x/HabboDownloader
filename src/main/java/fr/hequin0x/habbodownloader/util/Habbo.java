package fr.hequin0x.habbodownloader.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Habbo {

    public static String getCurrentRevision() throws Exception {
        URL habboVariables = new URL("https://www.habbo.com/gamedata/external_variables/0");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(habboVariables.openStream()));

        String revision = null;
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if(inputLine.contains("flash.client.url=")) {
                try {
                    Pattern p = Pattern.compile("flash.client.url=//images.habbo.com/gordon/(.*?)/");
                    Matcher m = p.matcher(inputLine);
                    m.find();
                    revision = m.group(1);
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
        in.close();

        if(revision == null) {
            throw new Exception("Failed to get current revision");
        }

        return revision;
    }
}
