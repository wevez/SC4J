# SC4J
SoundCloud API for Java.
## Features
- Search
- Download/Play
- Get uploads of artists
## Roadmap
- Playlist  
- Login
- Recommendation
- Like/Repost/Follow
## How to use
1. Create a class that inherits from SC4J and implement the getHTTP functions.
### In case of using [HttpURLConnection](https://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html)
```java
public class HttpURLConnectionSC4J extends SC4J {

    @Override
    protected void getHTTP(String URL, String USER_AGENT, Consumer<String> RESPONSE_HANDLER) {
        try {
            final URL requestUrl = new URL(URL);

            final HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestMethod("GET");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            final StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            RESPONSE_HANDLER.accept(response.toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```
### In case of using [Volley](https://github.com/google/volley)
```java
public class VolleySC4J extends SC4J {

  private final Context CONTEXT;

  public VolleySC4J(final Context CONTEXT) {
    this.CONTEXT = CONTEXT;
  }

  @Override
  protected void getHTTP(String URL, String USER_AGENT, Consumer<String> RESPONSE_HANDLER) {
      RequestQueue queue = Volley.newRequestQueue(this.CONTEXT);

      StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                  new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            RESPONSE_HANDLER.accept(response);
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            // Error handling here
          }
      });

      queue.add(stringRequest);
  }
}
```
2. Create an instance of your custom SC4J and enjoy your scraping!
```java
// Create an instance of CustomSC4J.
final HttpURLConnectionSC4J sc4J = new HttpURLConnectionSC4J();

// Start searching with title 'moeshop'.
sc4J.getSearchResult(result -> {
    // Print all detail in search result
    result.forEach(r -> System.out.println(r.toString()));
}, "moeshop", 20, 0);
```
## This project contains following libraries.
- [gson](https://github.com/google/gson) For parsing json data.
