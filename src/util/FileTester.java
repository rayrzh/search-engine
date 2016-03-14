package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class FileTester {

	public static void main(String[] args) throws IOException, ClassNotFoundException 
	{
		
		JSONParser parser = new JSONParser();  
		String pagePath = System.getProperty("user.dir") + File.separator + "Crawled-Pages" + File.separator;
		
		try 
		{  
		  
			Object obj = parser.parse(new FileReader(pagePath + "1.json"));  
			  
			JSONObject jsonObject = (JSONObject) obj;  
			  
			String url = (String) jsonObject.get("url");  
			System.out.println("url: "+url);  
			  
			String title = (String) jsonObject.get("title");
			System.out.println("title: "+title);  
			
//			String text = (String) jsonObject.get("text");  
//			System.out.println("text: "+text);  
			  
			System.out.println("Outgoing urls:");  
			JSONArray ourgoingLinks = (JSONArray) jsonObject.get("links");  
			Iterator<String> iterator = ourgoingLinks.iterator();
			while (iterator.hasNext()) 
			{  
				System.out.println(iterator.next());  
			}  
			
		}
		catch (FileNotFoundException e) 
		{  
			e.printStackTrace();  
		}
		catch (IOException e) 
		{  
			e.printStackTrace();  
		}
		catch (ParseException e) 
		{  
			e.printStackTrace();
		}

	}
}