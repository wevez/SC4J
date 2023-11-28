package tech.tenamen.sc4j.data;

import java.util.List;

public class SCPlaylist extends SCData {

    private final String THUMBNAIL_URL;
    private final String TITLE;
    private final SCPublisher PUBLISHER;

    private final List<SCMusic> TRACKS;

    public SCPlaylist(final String TITLE, final SCPublisher PUBLISHER, final String THUMBNAIL_URL, final List<SCMusic> TRACKS) {
        this.THUMBNAIL_URL = THUMBNAIL_URL;
        this.TITLE = TITLE;
        this.PUBLISHER = PUBLISHER;
        this.TRACKS = TRACKS;
    }

    public final List<SCMusic> getTracks() {
        return this.TRACKS;
    }

    public final String getTitle() {
        return this.TITLE;
    }

    public final String getThumbnailURL() {
        return this.THUMBNAIL_URL;
    }

    public final SCPublisher getPublisher() {
        return this.PUBLISHER;
    }

    @Override
    public String toString() {
        return String.format(
                "title: %s, thumbnail URL: %s, publisher: {%s}, tracks: %s",
                this.TITLE,
                this.THUMBNAIL_URL,
                this.PUBLISHER.toString(),
                this.TRACKS.toString()
        );
    }
}
