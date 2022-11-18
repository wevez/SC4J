# SC4J
 SoundCloud API for java which one allows you to play soundcloud music on your java apps!
# Functions
 ## SC4J.search(String title)  
 Searchs musics.   
 ## SC4J.getResult()  
 Returns seach result.  
 ## SC4J.play(int index)  
 Plays music. This index is the one of SC4J.instance.getResult().
 ## SC4J.getMusic()  
 Returns the playing music. If no music is played, returns null.  
 You can get details about this one such as Title, Artist and ArtworkURL.  
 ### Example  
    final SCMusic playingMusic = SC4J.getMusic();  
    if (playingMusic != null) {  
        System.out.println(String.format("Now playing %s by %s", playingMusic.getTitle(), playingMusic.getArtist()));  
    }  
## Contributes and Issues are welcome.  
