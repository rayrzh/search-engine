
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Graph {
	HashMap<Integer, Integer> urlDoc = new HashMap<Integer, Integer>();
	HashMap<Integer, Node> graphNodes = new HashMap<Integer, Node>();

	public void loadUrlDoc() {
		File file = new File("url-doc.dat");

		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);

			urlDoc = (HashMap<Integer, Integer>) ois.readObject();

			ois.close();
			fis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void buildGraph() {
		String pagePath = System.getProperty("user.dir") + File.separator + "CrawledPages" + File.separator;
		File file = new File(pagePath);
		String[] fileNames = file.list();
		JSONParser parser = new JSONParser();
		int count = 0;

		// extract outlink for each json file
		for (String fileName : fileNames) {
			if (fileName.matches("^\\w+?(\\.json)$")) {
				try {
					FileReader reader = new FileReader(pagePath + fileName);
					Object obj = parser.parse(reader);
					JSONObject jsonObject = (JSONObject) obj;

					int docID = (Integer.valueOf(fileName.split("\\.")[0]));
					Node node = new Node(docID); 

					JSONArray ourgoingLinks = (JSONArray) jsonObject.get("links");
					Iterator<String> iterator = ourgoingLinks.iterator();
					while (iterator.hasNext()) {
						String url = iterator.next();

						int hashValue = BKDRHash(url);
						if (urlDoc.containsKey(hashValue)) { //outlink in domain
							Node outNode = null;
							int outDocID = urlDoc.get(hashValue); // find docID
							if (outDocID == docID)
								continue;
							if (graphNodes.containsKey(outDocID)) { 
								outNode = graphNodes.get(outDocID); 
								outNode.addInLink(node.getDocId());
							} else {
								outNode = new Node(outDocID); 
								outNode.addInLink(node.getDocId());
							}
							graphNodes.put(outDocID, outNode); 
							node.addOutLink(outNode.getDocId()); 
						}
					}
					// System.out.println("");
					graphNodes.put(docID, node);
					reader.close();
					count++;
					if (count % 1000 == 0)
						System.out.println(count);
				} catch (org.json.simple.parser.ParseException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Pause");
		try {
			FileOutputStream fos = new FileOutputStream(new File("link.dat"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(graphNodes);
			oos.writeObject(null);
			oos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("****************************");
//		File file1 = new File("link.dat");
//
//		try
//		{
//			PrintWriter pw = new PrintWriter("link.txt");
//			FileInputStream fis = new FileInputStream(file1);
//			ObjectInputStream ois = new ObjectInputStream(fis);
//
//			HashMap<Integer, Node> nodes = (HashMap<Integer, Node>) ois.readObject();
//			Iterator<Entry<Integer, Node>> iter = nodes.entrySet().iterator();
//			while (iter.hasNext())
//			{
//				Entry<Integer, Node> entry = (Entry<Integer, Node>) iter.next();
//				pw.println("DOCID: " + (Integer) entry.getKey());
//				pw.println("InLink: ");
//				pw.println(entry.getValue().getInLinks().toString());
//				pw.println("OutLink: ");
//				pw.println(entry.getValue().getOutLinks().toString());
//				pw.println();
//			}
//
//			pw.flush();
//			pw.close();
//			ois.close();
//			fis.close();
//
//		}
//		catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//		}
		
	}

	public static int BKDRHash(String str) {
		int seed = 131;
		int hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}
		return (hash & 0x7FFFFFFF);
	}

	public static void main(String[] args) {
		Graph g = new Graph();
		g.loadUrlDoc();
		g.buildGraph();
		System.out.println("Finished");

	}
}
