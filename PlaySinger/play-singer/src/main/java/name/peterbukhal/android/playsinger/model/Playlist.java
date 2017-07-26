package name.peterbukhal.android.playsinger.model;

import java.util.List;

import name.peterbukhal.android.playsinger.model.impl.RadioChannel;

public interface Playlist {
    List<RadioChannel> getPlaylistEntries();
}
