package tech.tenamen.sc4j;

public class SCSearchResult {

    private final String TITLE;
    private final SCPublisher PUBLISHER;
    private final String THUMBNAIL_URL;
    private final String TRACK_ID;
    private final String TRACK_AUTH;

    public SCSearchResult(final String TITLE, final SCPublisher PUBLISHER, final String THUMBNAIL_URL, final String TRACK_ID, final String TRACK_AUTH) {
        this.TITLE = TITLE;
        this.PUBLISHER = PUBLISHER;
        this.THUMBNAIL_URL = THUMBNAIL_URL;
        this.TRACK_AUTH = TRACK_AUTH;
        this.TRACK_ID = TRACK_ID;
    }

    public final String getTitle() {
        return this.TITLE;
    }

    public final SCPublisher getPublisher() {
        return this.PUBLISHER;
    }

    public final String getThumbnailURL() {
        return this.THUMBNAIL_URL;
    }

    public final String getTrackId() {
        return this.TRACK_ID;
    }

    public final String getTrackAuth() {
        return this.TRACK_AUTH;
    }
}
