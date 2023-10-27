import tech.tenamen.sc4j.SCSearchResult;

public class Main {
    public static void main(String[] args) {
        final CustomSC4J sc4J = new CustomSC4J();

        // Start searching with title 'seikin'
        sc4J.startSearch("seikin");

        // Continue searching for more result
        sc4J.continueSearch();

        // Print details of search result
        sc4J.getSearchResults().forEach(r -> {
            System.out.println(r.getTitle() + ", " + r.getThumbnailURL() + ", " + r.getPublisher().getUsername());
        });

        // Print the url containing mp3 data
        final SCSearchResult firstResult = sc4J.getSearchResults().get(0);
        System.out.println(sc4J.getMP3URLOf(firstResult));
    }
}