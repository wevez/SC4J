package tech.tenamen.sc4j;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tech.tenamen.sc4j.data.SCData;
import tech.tenamen.sc4j.data.SCMusic;
import tech.tenamen.sc4j.data.SCPublisher;
import tech.tenamen.sc4j.util.JSONUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SC4J {

    /** The URL for API of soundcloud */
    private static final String API_URL = "https://api-v2.soundcloud.com";

    /** The global instance of Gson */
    private static final Gson GSON = new Gson();

    /** Anything like API Key */
    private static String clientId = null;

    /** Define the user-agent */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 OPR/91.0.4516.72 (Edition GX-CN)";

    /**
     * Get search result using soundcloud API
     *
     * @param ON_SUCCESS function executed when data is successfully fetched
     * @param KEYWORD keyword for searching
     * @param LIMIT search limit
     * @param OFFSET search offset
     */
    public final void getSearchResult(final Consumer<List<SCData>> ON_SUCCESS, final String KEYWORD, final int LIMIT, final int OFFSET) {
        this.ensureClientId(clientId -> {
            String encodedKeyword = null;
            try {
                encodedKeyword = URLEncoder.encode(KEYWORD, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            this.getHTTP(
                    String.format(
                            "%s/search?q=%s&client_id=%s&limit=%d&offset=%d",
                            API_URL,
                            encodedKeyword,
                            clientId,
                            LIMIT,
                            OFFSET
                    ),
                    USER_AGENT,
                    response -> {
                        final JsonObject responseJSON = GSON.fromJson(response, JsonObject.class);
                        ON_SUCCESS.accept(parseDataCollection(responseJSON.getAsJsonArray("collection")));
                    }
            );
        });
    }

    /**
     * Get music mp3 data from music snippet
     *
     * @param ON_SUCCESS function executed when data is successfully fetched
     * @param result music snippet
     */
    public final void getMP3Url(final Consumer<String> ON_SUCCESS, final SCMusic result) {
        ensureClientId(clientId -> {
            this.getHTTP(
                    String.format(
                            "%s?client_id=%s&track_authorization=%s",
                            result.getTrackId(),
                            clientId,
                            result.getTrackAuth()
                    ),
                    USER_AGENT,
                    response -> {
                        final JsonObject responseObject = GSON.fromJson(response, JsonObject.class);
                        ON_SUCCESS.accept(responseObject.get("url").getAsString());
                    }
            );
        });
    }

    /**
     * Get data uploaded by publisher
     *
     * @param ON_SUCCESS function executed when data is successfully fetched
     * @param publisher publisher
     */
    public final void getUploads(final Consumer<List<SCData>> ON_SUCCESS, SCPublisher publisher, final int LIMIT, final int OFFSET) {
        ensureClientId(clientId -> {
            this.getHTTP(
                    String.format(
                            "%s/users/%d/tracks?representation=&client_id=%s&limit=%d&offset=%d",
                            API_URL,
                            publisher.getId(),
                            clientId,
                            LIMIT,
                            OFFSET
                    ),
                    USER_AGENT,
                    response -> {
                        final JsonObject responseJSON = GSON.fromJson(response, JsonObject.class);
                        ON_SUCCESS.accept(parseDataCollection(responseJSON.getAsJsonArray("collection")));
                    }
            );
        });
    }

    /**
     * Create an instance of SCPublisher from JsonObject
     *
     * @param user JsonObject
     * @return instance of SCPublisher
     */
    private final SCPublisher parsePublisher(final JsonObject user) {
        return new SCPublisher(
                user.get("username").getAsString(),
                user.get("avatar_url").getAsString(),
                user.get("id").getAsInt()
        );
    }

    /**
     * Create an instance of SCMusic from JsonObject
     *
     * @param j JsonObject
     * @return instance of SCMusic
     */
    private final SCMusic parseMusic(final JsonObject j) {
        return new SCMusic(
                j.get("title") == null ? null : j.get("title").getAsString(), // title
                this.parsePublisher(j.getAsJsonObject("user")), // publisher
                j.get("artwork_url").isJsonNull() ? null : j.get("artwork_url").getAsString(), // artwork URL
                j.getAsJsonObject("media")
                        .getAsJsonArray("transcodings")
                        .get(1).getAsJsonObject()
                        .get("url").getAsString(), // track id
                j.get("track_authorization").getAsString() // track auth
        );
    }

    /**
     * Parse data collection of JsonObject
     *
     * @param array JSONArray for parsing
     * @return parsed data list
     */
    private final List<SCData> parseDataCollection(final JsonArray array) {
        final List<SCData> searchResults = new ArrayList<>();

        JSONUtil.streamOf(array)
                .forEach(j -> {
                    SCData pushData = null;

                    switch (j.get("kind").getAsString()) {
                        case "track": {
                            pushData = this.parseMusic(j);
                            break;
                        }
                        case "user": {
                            pushData = this.parsePublisher(j);
                            break;
                        }
                        case "playlist": {
                            // TODO
                            /*
                            pushData = new SCPlaylist(
                                    j.get("title").getAsString(), // title
                                    this.parsePublisher(j.getAsJsonObject("user")), // publisher
                                    j.get("artwork_url").getAsString(), // artwork url
                                    this.parseDataCollection(j.getAsJsonArray("tracks"))
                                            .stream()
                                            .map(r -> (SCMusic)r)
                                            .collect(Collectors.toList())// tracks
                            );
                             */
                            break;
                        }
                        default:
                            System.out.println("Unknown kind: " + j.get("kind").getAsString());
                            System.out.println(j);
                            break;
                    }

                    if (pushData != null) searchResults.add(pushData);
                });

        return searchResults;
    }

    /**
     * Fetch client id if necessary
     *
     * @param PROCESS process which one executed with client id.
     */
    private final void ensureClientId(final Consumer<String> PROCESS) {
        if (clientId != null) {
            PROCESS.accept(clientId);
            return;
        }
        this.getHTTP(
        "https://soundcloud.com/",
            USER_AGENT,
            response -> {
                final String a = response.split("<script crossorigin src=\"https://a-v2.sndcdn.com/assets/")[5];
                this.getHTTP(
                        String.format("https://a-v2.sndcdn.com/assets/%s", a.substring(0, a.indexOf("\""))),
                        USER_AGENT,
                        response2 -> {
                            clientId = clip(response2, "client_id=", "\"");
                            PROCESS.accept(clientId);
                        }
                );
            }
        );
    }

    /**
     * "Clips `target` to remove any characters before the first occurrence of `first` and after the last occurrence of `last`."
     * @param target
     * @param first
     * @param last
     * @return
     */
    private static String clip(final String target, final String first, final String last) {
        final int startIndex = target.indexOf(first) + first.length();
        return target.substring(startIndex, target.indexOf(last, startIndex));
    }

    protected abstract void getHTTP(final String URL, final String USER_AGENT, final Consumer<String> RESPONSE_HANDLER);
}
