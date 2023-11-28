package tech.tenamen.sc4j.data;

public class SCPublisher extends SCData {

    private final String USERNAME;
    private final String AVATAR_URL;
    private final int ID;

    public SCPublisher(final String USERNAME, final String AVATAR_URL, final int ID) {
        this.USERNAME = USERNAME;
        this.AVATAR_URL = AVATAR_URL;
        this.ID = ID;
    }

    public final String getUsername() {
        return this.USERNAME;
    }

    public final String getAvatarURL() {
        return this.AVATAR_URL;
    }

    public final int getId() {
        return this.ID;
    }

    @Override
    public String toString() {
        return String.format(
                "username: %s, avatar URL: %s, id: %d",
                this.USERNAME,
                this.AVATAR_URL,
                this.ID
        );
    }
}
