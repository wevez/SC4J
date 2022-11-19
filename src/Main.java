import tech.sc4j.SC4J;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        SC4J.search("KOTONOHOUSE In my world");
        SC4J.play(1);
        Thread.sleep(50000);
        System.out.println("Started");
        SC4J.getMusic().setMS(10000);
    }

}