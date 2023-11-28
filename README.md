# SC4J
SoundCloud API for Java.
  
## Features
- Search  
  See getSearchResults(final String KEYWORD, final int LIMIT, final int OFFSET) in SC3J.java
- Download/Play  
  See getMP3URLOf(final SCMusic result) in SC4J.java
- Uploads  
  See getUploads(final SCPublisher publisher, final int LIMIT, final int OFFSET) in SC4J.java
## Roadmap
- Playlist  
Program of parsing playlist is in progress.
- Login
- Recommendation
- Like/Repost/Follow
## How to use
- Create a class that inherits from SC4J and implement the getHTTP functions.
[Example](https://github.com/wevez/SC4J/blob/main/src/CustomSC4J.java)
- Create an instance of your custom SC4J and enjoy your scraping!
[Example](https://github.com/wevez/SC4J/blob/main/src/Main.java)
## This project contains following libraries.
- [gson](https://github.com/google/gson) Parser for .json file.
