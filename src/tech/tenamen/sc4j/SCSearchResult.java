package tech.tenamen.sc4j;

public class SCSearchResult {

    private final String TITLE;
    private final SCPublisher PUBLISHER;
    private final String THUMBNAIL_URL;

    public SCSearchResult(final String TITLE, final SCPublisher PUBLISHER, final String THUMBNAIL_URL) {
        this.TITLE = TITLE;
        this.PUBLISHER = PUBLISHER;
        this.THUMBNAIL_URL = THUMBNAIL_URL;
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
}
