import tech.tenamen.sc4j.SCSearchResult;

public class Main {
    public static void main(String[] args) {
        final CustomSC4J sc4J = new CustomSC4J();

        sc4J.startSearch("seikin");

        for (SCSearchResult searchResult : sc4J.getSearchResults()) {
            System.out.println(searchResult.getTitle());
        }

        sc4J.continueSearch();

        sc4J.getSearchResults().forEach(s -> {
            System.out.println(s.getTitle() + ";" + s.getThumbnailURL());
        });
    }
}