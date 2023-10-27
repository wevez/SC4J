package tech.tenamen.sc4j;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tech.tenamen.sc4j.util.JSONUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public abstract class SC4J {

    /** The host URL for soundclooud.com */
    private static final String HOST_URL = "https://soundcloud.com";
    /** The URL for API of soundcloud */
    private static final String API_URL = "https://api-v2.soundcloud.com";

    /** The global instance of Gson */
    private static final Gson GSON = new Gson();
    private static String clientId = null;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 OPR/91.0.4516.72 (Edition GX-CN)";
    private static final int SEARCH_LIMIT = 20;

    private final List<SCSearchResult> SEARCH_RESULTS = new ArrayList<>();

    private int searchOffset = 0;
    private String lastSearchKeyword = null;

    public final List<SCSearchResult> getSearchResults() {
        return this.SEARCH_RESULTS;
    }

    public final void startSearch(final String KEYWORD) {
        this.searchOffset = 0;
        this.lastSearchKeyword = KEYWORD;

        this.searchAndAddContents(KEYWORD, SEARCH_LIMIT, 0);
    }

    public final void continueSearch() {
        this.searchOffset += SEARCH_LIMIT;

        this.searchAndAddContents(this.lastSearchKeyword, SEARCH_LIMIT, this.searchOffset);
    }

    private final void searchAndAddContents(final String KEYWORD, final int LIMIT, final int OFFSET) {
        String encodedKeyword = null;
        try {
            encodedKeyword = URLEncoder.encode(KEYWORD, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        this.ensureClientId();
        final String response = this.getHTTP(
                String.format(
                        "%s/search?q=%s&client_id=%s&limit=%d&offset=%d",
                        API_URL,
                        encodedKeyword,
                        clientId,
                        LIMIT,
                        OFFSET
                ),
                USER_AGENT
        );

        final JsonObject responseJSON = GSON.fromJson(response, JsonObject.class);

        JSONUtil.streamOf(responseJSON.getAsJsonArray("collection"))
                .filter(j -> j.get("kind").getAsString().equalsIgnoreCase("track"))
                .forEach(j -> {
                    final String argworkURL = j.get("artwork_url").isJsonNull() ? null : j.get("artwork_url").getAsString();
                    final String title = j.get("title").getAsString();

                    final JsonObject user = j.getAsJsonObject("user");
                    final SCPublisher publisher = new SCPublisher(
                            user.get("username").getAsString(),
                            user.get("avatar_url").getAsString(),
                            user.get("id").getAsInt()
                    );

                    final String trackId = j
                            .getAsJsonObject("media")
                            .getAsJsonArray("transcodings")
                            .get(1).getAsJsonObject()
                            .get("url").getAsString();
                    final String trackAuth = j.get("track_authorization").getAsString();

                    this.SEARCH_RESULTS.add(new SCSearchResult(
                            title,
                            publisher,
                            argworkURL,
                            trackId,
                            trackAuth
                            )
                    );
                });
    }

    public final String getMP3URLOf(final SCSearchResult result) {
        ensureClientId();

        final String response = this.getHTTP(
                String.format(
                        "%s?client_id=%s&track_authorization=%s",
                        result.getTrackId(),
                        clientId,
                        result.getTrackAuth()
                ),
                USER_AGENT
        );

        final JsonObject responseObject = GSON.fromJson(response, JsonObject.class);

        return responseObject.get("url").getAsString();
    }

    private final void ensureClientId() {
        if (clientId != null) return;
        final String a = this.getHTTP("https://soundcloud.com/", USER_AGENT)
                .split("<script crossorigin src=\"https://a-v2.sndcdn.com/assets/")[5];
        clientId = clip(this.getHTTP(String.format("https://a-v2.sndcdn.com/assets/%s", a.substring(0, a.indexOf("\""))), USER_AGENT),
                "client_id=",
                "\"");
    }

    private static String clip(final String target, final String first, final String last) {
        final int startIndex = target.indexOf(first) + first.length();
        return target.substring(startIndex, target.indexOf(last, startIndex));
    }

    protected abstract String getHTTP(final String URL, final String USER_AGENT);
}
