
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

			while (true) {
				Page pair = (Page) ois.readObject();
				if (pair == null)
					break;
				int urlValue = (Integer) pair.getUrlValue();
				int docID = (Integer) pair.getDocName();
				urlDoc.put(urlValue, docID);
			}

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
			try {
				FileReader fr = new FileReader(pagePath + fileName);
				Object obj = parser.parse(fr);
				JSONObject jsonObject = (JSONObject) obj;

				int docID = (Integer.valueOf(fileName.split("\\.")[0]));
				Node node = new Node(docID); // create node for each json file;

				JSONArray ourgoingLinks = (JSONArray) jsonObject.get("links");
				fr.close();
				Iterator<String> iterator = ourgoingLinks.iterator();
				while (iterator.hasNext()) {
					String url = iterator.next();

					int hashValue = BKDRHash(url);
					if (urlDoc.containsKey(hashValue)) { // if outgoing link
															// belongs to
															// uci.edu.ics

						Node outNode = null;
						int outDocID = urlDoc.get(hashValue); // find docID for
																// given url
						// System.out.print(outDocID+" ");
						if (outDocID == docID)
							continue;
						if (graphNodes.containsKey(outDocID)) { // if nodes
																// exist in
																// graph
							outNode = graphNodes.get(outDocID); // add inlink to
																// node
							outNode.addInLink(node);
						} else {
							outNode = new Node(outDocID); // if not exist, then
															// create a new node
							outNode.addInLink(node);
						}
						graphNodes.put(outDocID, outNode); // add new or replace
						node.addOutLink(outNode); // add outlink to current node
					}
				}
				// System.out.println("");
				graphNodes.put(docID, node);
				
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
		try {
			FileOutputStream fos = new FileOutputStream(new File("link.dat"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

//			Iterator iter = graphNodes.entrySet().iterator();
//			while (iter.hasNext()) {
//				Map.Entry entry = (Map.Entry) iter.next();
//				// System.out.println((Integer) key + ":");
//				oos.writeObject((Node) entry.getValue());
//			}
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
//		try {
//			PrintWriter pw = new PrintWriter("link.txt");
//			FileInputStream fis = new FileInputStream(file1);
//			ObjectInputStream ois = new ObjectInputStream(fis);
//
//			while (true) {
//				Node node = (Node) ois.readObject();
//				if (node == null)
//					break;
//				pw.println("DOCID: "+(Integer) node.docID);
//				pw.println("InLink: ");
//				for(Node n: node.inLink){
//					pw.print(n.docID+" ");
//				}
//				pw.println();
//				pw.println("OutLink: ");
//				for(Node n: node.outLink){
//					pw.print(n.docID+" ");
//				}
//				pw.println();
//			}
//			pw.flush();
//			pw.close();
//			ois.close();
//			fis.close();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		
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
		System.out.println("end");

	}
}
