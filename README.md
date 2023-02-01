# SC4J
 SoundCloud API for java which one allows you to play soundcloud music on your java apps!
# Functions
 ## SC4J.search(String title)  
 Searches musics.   
 You can get the search result by the following function.  
 ## SC4J.getResult()  
 Returns search result.  
 ## SC4J.play(int index)  
 Plays music which is containsed in search result.  
The index is same one in getResult()  
 ## SC4J.getMusic()  
 Returns the playing music. If nothinh is played, returns null.  
 You can get details about this one such as Title, Artist and ArtworkURL.  
 ### Example  
    final SCMusic playingMusic = SC4J.getMusic();  
    if (playingMusic != null) {  
        System.out.println(String.format("Now playing %s by %s", playingMusic.getTitle(), playingMusic.getArtist()));  
    }  

## Contributes and Issues are welcome.  
