package tech.tenamen.sc4j;

public class SCPublisher {

    private final String USERNAME;
    private final String AVATOR_URL;
    private final int ID;

    public SCPublisher(final String USERNAME, final String AVATOR_URL, final int ID) {
        this.USERNAME = USERNAME;
        this.AVATOR_URL = AVATOR_URL;
        this.ID = ID;
    }

    public final String getUsername() {
        return this.USERNAME;
    }

    public final String getAvatorURL() {
        return this.AVATOR_URL;
    }

    public final int getId() {
        return this.ID;
    }
}
