import tech.tenamen.sc4j.data.SCData;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create an instance of CustomSC4J.
        final CustomSC4J sc4J = new CustomSC4J();

        // Start searching with title 'moeshop'.
        final List<SCData> searchResults = sc4J.getSearchResults("moeshop", 20, 0);

        // Print all detail in search result
        searchResults.forEach(y -> {
            System.out.println(y.toString());
        });
    }
}