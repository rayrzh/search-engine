
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.StringTokenizer;

public class Query {

	private static final String DIR_PATH = System.getProperty("user.dir") + File.separator;
	private static final String PAGE_PATH = DIR_PATH + "CrawledPages" + File.separator;
	private static final String INDEX_PATH = DIR_PATH + "index" + File.separator;
	HashMap<String, ArrayList<Document>> qMap = new HashMap<String, ArrayList<Document>>(); //
	HashMap<String, Double> dList = new HashMap<String, Double>(); // document
	HashMap<String, Double> query = new HashMap<String, Double>();

	ArrayList<ArrayList<String>> qTerms = new ArrayList<ArrayList<String>>();

	public void query(String q) {

		qTerms.add(new ArrayList<String>()); // "0-9"
		qTerms.add(new ArrayList<String>()); // "a-f"
		qTerms.add(new ArrayList<String>()); // "g-l"
		qTerms.add(new ArrayList<String>()); // "m-r"
		qTerms.add(new ArrayList<String>()); // "s-z"

		StringTokenizer st = new StringTokenizer(q, " "); // more detail later
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			char c = token.charAt(0);
			if (c >= '0' && c <= '9') {
				if (!qTerms.get(0).contains(token))
					qTerms.get(0).add(token);
			} else if (c >= 'a' && c <= 'f') {
				if (!qTerms.get(1).contains(token))
					qTerms.get(1).add(token);
			} else if (c >= 'g' && c <= 'l') {
				if (!qTerms.get(2).contains(token))
					qTerms.get(2).add(token);
			} else if (c >= 'm' && c <= 'r') {
				if (!qTerms.get(3).contains(token))
					qTerms.get(3).add(token);
			} else if (c >= 's' && c <= 'z') {
				if (!qTerms.get(4).contains(token))
					qTerms.get(4).add(token);
			}

			if (!qMap.containsKey(token)) {
				qMap.put(token, new ArrayList<Document>());
				query.put(token, 1.0);
			} else {
				double val = query.get(token);
				query.put(token, ++val);
			}
		}
		// have to add log
		Iterator iter = query.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Double> entry = (Entry<String, Double>) iter.next();
			String key = entry.getKey();
			query.put(key, 1 + Math.log10(query.get(key)));
		}
		try {
			System.out.println(qTerms.toString());
			kDocument();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void kDocument() throws ClassNotFoundException, IOException {
		// save index and statistics to file
		String[] query = { "0-9", "a-f", "g-l", "m-r", "s-z" };
		for (int i = 0; i < qTerms.size(); i++) {
			if (qTerms.get(i).isEmpty())
				continue;
			File file = new File(INDEX_PATH + query[i] + ".dat");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);

			ArrayList<Document> docList = new ArrayList<Document>();
			
			try {
				Pair pair = null;
				while ((pair = (Pair) ois.readObject()) != null) {
					if (qTerms.get(i).contains(pair.getKey())) {
						System.out.println(pair.getKey());
						if (qMap.containsKey((String) pair.getKey())) {
							docList = (ArrayList<Document>) pair.getValue();
							for (int t = 0; t < Math.min(20, docList.size()); t++) {
								System.out.print(docList.get(t).getDocName()+" ");
								qMap.get((String) pair.getKey()).add(docList.get(t));
							}
							System.out.println("found");
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException e) {
				e.printStackTrace();
			}
			ois.close();
			fis.close();
		}
//		Iterator iter = qMap.entrySet().iterator();
//		while (iter.hasNext()) {
//			Map.Entry entry = (Map.Entry) iter.next();
//			Object key = entry.getKey();
//			System.out.println((String) key + ":");
//			ArrayList<Document> val = (ArrayList<Document>) entry.getValue();
//			for (int i = 0; i < val.size(); i++) {
//				System.out.println(val.get(i).getDocName());
//			}
//		}
	}

	private ArrayList<Entry<String, Double>> getResult() {

		int size = qMap.size();
		double score = 0.0;

		Iterator iter = qMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			ArrayList<Document> doclist = (ArrayList<Document>) entry.getValue();
			for (int i = 0; i < doclist.size(); i++) {
				String name = doclist.get(i).getDocName();
				int length = doclist.get(i).getPositions().size();
				if (!dList.containsKey(name)) {
					score = doclist.get(i).getTFIDF() * query.get(key) / length;
					dList.put(name, score);
				} else {
					score = dList.get(name);
					score += doclist.get(i).getTFIDF() * query.get(key) / length;
					dList.put(name, score);
				}
			}
		}

		ArrayList<Map.Entry<String, Double>> t = new ArrayList<Map.Entry<String, Double>>(dList.entrySet());
		Collections.sort(t, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
				return (entry2.getValue().compareTo(entry1.getValue()));
			}
		});

		Iterator<Entry<String, Double>> it = t.iterator();
		int i = 0;
		ArrayList<Entry<String, Double>> ranking = new ArrayList<Entry<String, Double>>();
		while (it.hasNext() && i < 5) {
			Entry<String, Double> entry = (Entry<String, Double>) it.next();
			ranking.add(entry);
			// String key = entry.getKey();
			// Double val = entry.getValue();
			// System.out.println(key+" "+val);
			 i++;
		}
		return ranking;
	}

	public String getResultDisplayString() {
		ArrayList<Entry<String, Double>> ranking = getResult();
		String resultString = "";

		for (Entry<String, Double> result : ranking) {
			String fileName = result.getKey();

			JSONParser parser = new JSONParser();
			Object obj = null;
			FileReader fr = null;
			try {
				fr = new FileReader(PAGE_PATH + fileName + ".json");
				obj = parser.parse(fr);
				JSONObject jsonObject = (JSONObject) obj;
				String title = ((String) jsonObject.get("title"));
				String text = ((String) jsonObject.get("text"));
				String url = (String) jsonObject.get("url");
				fr.close();

				resultString += "<p>" +title + "</p>";
				resultString += "<p>" + getText(text) + "</p>";
				resultString += "<p><a href=\"" + url + "\">" + url + "</a></p>";
				resultString += "<p>Score: " + result.getValue() + "</p>";
				resultString += "<p></p>";

			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return resultString;
	}
	
	public String getResultString()
	{
		ArrayList<Entry<String, Double>> ranking = getResult();
		String resultString = "";
		
		for (Entry<String, Double> result : ranking)
		{
			String fileName = result.getKey();

			JSONParser parser = new JSONParser();
			Object obj = null;
			FileReader fr = null;
			try
			{
				fr = new FileReader(PAGE_PATH + fileName + ".json");
				obj = parser.parse(fr);
				JSONObject jsonObject = (JSONObject) obj;
				String title = ((String) jsonObject.get("title"));
				String text = ((String) jsonObject.get("text"));
				String url = (String) jsonObject.get("url");
				fr.close();
				
				resultString += "@TITLE\n";
				resultString += title + "\n";
				resultString += "@URL\n";
				resultString += url + "\n";
				resultString += "\n";

			}
			catch (org.json.simple.parser.ParseException e)
			{
				e.printStackTrace();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		
		return resultString;
	}
	
/*
	private String getText(String text)
	{
		String s = "";
		ArrayList<String> tokens = token(text);
		int tokensLength = tokens.size();
		boolean first = true;
		loop: for(ArrayList<String> list : qTerms)
		{
			if(list.isEmpty())
				continue loop;
			for(int i = 0; i < list.size(); i++)
			{
				String term = list.get(i);
				if(term == "")
					continue;
				int index = tokens.indexOf(term);
//				System.out.println(index);
				if(first)
				{
					if(index-1 >= 0 && index+1 <= tokensLength-1)
						s += "..." + tokens.get(index - 1) + " " + term + " " + tokens.get(index + 1) + "...";
					else if(index-1 == 0 && index+1 <= tokensLength-1)
						s += "..." + term + " " + tokens.get(index + 1) + "...";
					else if(index-1 >= 0 && index <= tokensLength-1)
						s += "..." + tokens.get(index - 1) + " " + term + "...";
					else
						s += "..." + term + "...";
					first = false;
				}
				else
				{
					if(index-1 > 0 && index+1 <= tokensLength-1)
						s += tokens.get(index - 1) + " " + term + " " + tokens.get(index + 1) + "...";
					else if(index-1 == 0 && index+1 <= tokensLength-1)
						s += term + " " + tokens.get(index + 1) + "...";
					else if(index-1 >= 0 && index <= tokensLength-1)
						s += tokens.get(index - 1) + " " + term + "...";
					else
						s += term + "...";
				}
			}
		}
		return s;
	}
*/
	
	private String getText(String text)
	{
		String s = "";
		Scanner scanner = new Scanner(text);
		String line = "";
		boolean match;
		while(scanner.hasNextLine())
		{
			line = scanner.nextLine();
			loop: for(ArrayList<String> list : qTerms)
			{
				match = false;
				if(list.isEmpty())
					continue loop;
				for(int i = 0; i < list.size(); i++)
				{
					if(line.matches("(?i).*"+list.get(i)+".*"))
					{
						line = line.replaceAll("(?i)"+list.get(i).trim(), "<strong>"+list.get(i).trim()+"</strong>");
						if(!match)
							match = true;
					}
				}
				if(match)
					s += line + "...";
			}
		}
		return s;
	}
	
	/*
	private ArrayList<String> token(String text) {

		String toStr = "";
		ArrayList<String> words = new ArrayList<String>();

		String pattern = " \t\n\r\f.," + "~!@#$%^&*()_+-=`" + "{}|[]\\;':\"" + "<>?/";
		StringTokenizer st = new StringTokenizer(text, pattern, false);
		while (st.hasMoreTokens()) {
			toStr = st.nextToken().trim().toLowerCase();
			words.add(toStr);
		}
		return words;
	}
	*/
	
	/*
	public static void main(String[] args) {
		Query q = new Query();
		String qeury = "these theory time"; // just for test
		q.query(qeury);
		System.out.println(q.getResultString());
	}
	 */
}