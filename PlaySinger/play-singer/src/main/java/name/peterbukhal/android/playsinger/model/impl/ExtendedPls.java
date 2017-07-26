package name.peterbukhal.android.playsinger.model.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import name.peterbukhal.android.playsinger.model.Playlist;

/**
 * @link http://schworak.com/blog/e41/extended-pls-plsv2/
 */
public class ExtendedPls implements Playlist {

    private Properties configuration = new Properties();;
    private String name;
    private Integer mNumberOfEntries;

    public ExtendedPls(String file) {
        name = file;
    }

    @Override
    public List<RadioChannel> getPlaylistEntries() {
        List<RadioChannel> entries = new ArrayList<>();
        try {
            URL url = new URL(name);
            configuration.load(url.openStream());
            if (configuration.getProperty("NumberOfEntries") == null) return Collections.EMPTY_LIST;
            mNumberOfEntries = Integer.valueOf(configuration.getProperty("NumberOfEntries"));
            for (int i = 1; i <= mNumberOfEntries; i++) {
                RadioChannel channel = new RadioChannel();
                channel.setUri(configuration.getProperty("File" + i));

                entries.add(channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public List<String> getPlaylistEntriesString() {
        List<String> entries = new ArrayList<>();
        try {
            URL url = new URL(name);
            configuration.load(url.openStream());
            if (configuration.getProperty("NumberOfEntries") == null) return Collections.EMPTY_LIST;
            mNumberOfEntries = Integer.valueOf(configuration.getProperty("NumberOfEntries"));
            for (int i = 1; i <= mNumberOfEntries; i++) {
                entries.add(configuration.getProperty("File" + i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return entries;
    }

}
