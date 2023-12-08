public class Main {
    public static void main(String[] args) {
        // Create an instance of CustomSC4J.
        final CustomSC4J sc4J = new CustomSC4J();

        // Start searching with title 'moeshop'.
        sc4J.getSearchResult(result -> {
            // Print all detail in search result
            result.forEach(r -> System.out.println(r.toString()));
        }, "moeshop", 20, 0);
    }
}