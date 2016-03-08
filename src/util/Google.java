package util;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Google
{
	private final String GOOGLE = "http://www.google.com/search?q=";
	private final String USER_AGENT = "UCI CS 221 Class Project (+http://www.ics.uci.edu/~lopes/teaching/cs221W16/)"; // Change this to your company's name and bot homepage!
	private final String CHARSET = "UTF-8";
	String search = "stackoverflow";
	
	public Google()
	{
		// Constructor
	}
	
	public void search(String query, String domain)
	{
		Elements links;
		try
		{
			links = Jsoup.connect(GOOGLE + URLEncoder.encode(query+" "+domain, CHARSET)).userAgent(USER_AGENT).get().select(".g>.r>a");
			int count = 0;
			Pattern FILTERS = Pattern.compile(".*(\\.(css|js|c|o|h|php|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi"
		            + "|lisp|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz|ppt|ppts|data|dat))$");
			DocWriter dw = new DocWriter(System.getProperty("user.dir") + File.separator + "google" + File.separator, query, "txt");
			dw.write(query + System.getProperty("line.separator"));
			dw.write(System.getProperty("line.separator"));
			for (Element link : links) 
			{
				String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
			    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
			    if (!url.startsWith("http") && FILTERS.matcher(url).matches())
			        continue;
			    
			    Document doc = Jsoup.parse(link.html());
			    String title = doc.text();
//			    String text = doc.text();
			    count++;
			    if(count > 10)
			    	return;
			    System.out.println("Title: " + title);
			    dw.write("@TITLE"+ System.getProperty("line.separator") + title + System.getProperty("line.separator"));
			    System.out.println("URL: " + url);
			    dw.write("@URL"+ System.getProperty("line.separator") + url + System.getProperty("line.separator"));
//			    dw.write("@TEXT" + System.getProperty("line.separator") + text + System.getProperty("line.separator"));
			    dw.write(System.getProperty("line.separator"));
			}
			dw.close();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	public static void main(String[] args)
	{
		final String SEARCH_DOMAIN = "site:ics.uci.edu";
		Google google = new Google();
		Scanner input = new Scanner(System.in);
		System.out.println("enter query");
		String search = input.nextLine();
		while(!search.equalsIgnoreCase("1111"))
		{
			google.search(search, SEARCH_DOMAIN);
			System.out.println();
			System.out.println("Enter search query:");
			search = input.nextLine();
		}
		input.close();
	}
}
