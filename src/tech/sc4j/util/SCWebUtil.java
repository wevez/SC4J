package tech.sc4j.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SCWebUtil {
	
	private static final String agent1 = "User-Agent",
            agent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 OPR/91.0.4516.72 (Edition GX-CN)";

    private static String clientId;

    public static List<String> visitSiteThreaded(final String urly) {
    	final ArrayList<String> lines = new ArrayList<String>();
		URL url;
        try {
            String line;
            url = new URL(urly);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty(agent1, agent2);
            final InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "Shift_JIS");
            final BufferedReader in = new BufferedReader(reader);
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return lines;
	}
    
    public static String visitSiteThreaded2(final String urly) {
    	final StringBuffer buffer = new StringBuffer();
		URL url;
        try {
            String line;
            url = new URL(urly);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty(agent1, agent2);
            final InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "Shift_JIS");
            final BufferedReader in = new BufferedReader(reader);
            while ((line = in.readLine()) != null) {
            	buffer.append(line);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return buffer.toString();
	}
    
    public static String clip(String target, String first, String last) {
    	final int startIndex = target.indexOf(first) + first.length();
    	return target.substring(startIndex, target.indexOf(last, startIndex));
    }
    
    public static String titleToURL(final String title, int limit, int offset) {
        if (clientId == null) {
            final String a = visitSiteThreaded2("https://soundcloud.com/").
                    split("<script crossorigin src=\"https://a-v2.sndcdn.com/assets/")[5];
            clientId = clip(visitSiteThreaded2(String.format("https://a-v2.sndcdn.com/assets/%s", a.substring(0, a.indexOf("\"")))),
            "client_id=",
            "\"");
        }
    	return String.format("https://api-v2.soundcloud.com/search?q=%s&client_id=%s&limit=%d&offset=%d",
    			formatTitle(title),
                clientId,
    			limit,
    			offset);
    }
    
    private static String formatTitle(final String title) {
    	return title.replaceAll(" ", "%20");
    }

}
