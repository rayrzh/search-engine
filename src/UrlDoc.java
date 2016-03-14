
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UrlDoc {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String pagePath = System.getProperty("user.dir") + File.separator + "CrawledPages" + File.separator;
		File file = new File(pagePath);
		String[] fileNames = file.list();
		JSONParser parser = new JSONParser();
		int count=0;

		HashMap<Integer, Integer> urlMap = new HashMap<Integer,Integer>();
		FileOutputStream fos = new FileOutputStream(new File("url-doc.dat"));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
			for (String fileName : fileNames) {
				try {
					FileReader fr = new FileReader(pagePath + fileName);
					Object obj = parser.parse(fr);
					JSONObject jsonObject = (JSONObject) obj;
	
					String url = (String) jsonObject.get("url");
					fr.close();
					// System.out.println("url: " + url);
	
					int docID = (Integer.valueOf(fileName.split("\\.")[0]));
	
					int hashValue = BKDRHash(url);
					urlMap.put(hashValue, docID);
					count++;
					if(count%1000==0) System.out.println(count);
					if(count>10690&&count<10700) System.out.println(count);
				} catch (org.json.simple.parser.ParseException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			oos.writeObject(urlMap);
			oos.writeObject(null);
			oos.close();
	}

	public static int BKDRHash(String str) {
		int seed = 131;
		int hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}

		return (hash & 0x7FFFFFFF);
	}
}
