package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Title {

	// HashMap<Integer, ArrayList<String>> titleMap = new HashMap<Integer,
	// ArrayList<String>>();
	HashMap<String, ArrayList<TitleDoc>> titleMap = new HashMap<String, ArrayList<TitleDoc>>();
	HashMap<String, Double> query = new HashMap<String, Double>();
	public Set<String> stops = new HashSet<String>();
	HashMap<String, ArrayList<TitleDoc>> qMap = new HashMap<String, ArrayList<TitleDoc>>(); //
	HashMap<Integer, Double> dList = new HashMap<Integer, Double>();

	public void titleRank() throws IOException {
		String pagePath = System.getProperty("user.dir") + File.separator + "CrawledPages" + File.separator;
		File file = new File(pagePath);
		String[] fileNames = file.list();
		JSONParser parser = new JSONParser();
		int count = 0;

		HashMap<Integer, Integer> urlMap = new HashMap<Integer, Integer>();
		FileOutputStream fos = new FileOutputStream(new File("doc-title.dat"));
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		for (String fileName : fileNames) {
			try {
				Object obj = parser.parse(new FileReader(pagePath + fileName));
				JSONObject jsonObject = (JSONObject) obj;

				String title = (String) jsonObject.get("title");
				if (title == null)
					continue;
				ArrayList<String> titleList = new ArrayList<String>();
				String pattern = " \t\n\r\f.," + "~!@#$%^&*()_+-=`" + "{}|[]\\;':\"" + "<>?/";
				StringTokenizer st = new StringTokenizer(title, pattern, false);
				int length = st.countTokens();
				String term = "";
				int docID = (Integer.valueOf(fileName.split("\\.")[0]));
				while (st.hasMoreTokens()) {
					length++;
					term = st.nextToken().trim();
					if (!stops.contains(term.toLowerCase())) {
						if (titleMap.containsKey(term)) {
							if (titleMap.get(term).contains(docID)) {
								int index = titleMap.get(term).indexOf(docID);
								titleMap.get(term).get(index).add();
							} else {
								titleMap.get(term).add(new TitleDoc(docID,length));
							}
						} else {
							titleMap.put(term, new ArrayList<TitleDoc>());
							titleMap.get(term).add(new TitleDoc(docID,length));
						}
					}
				}
				// System.out.println("url: " + url);
				count++;

				if (count % 1000 == 0)
					System.out.println(count);

				if (count > 10690 && count < 10700)
					System.out.println(count);
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Iterator iter = titleMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, ArrayList<TitleDoc>> term = (Entry<String, ArrayList<TitleDoc>>) iter.next();
			for (TitleDoc t : term.getValue()) {
				int s = term.getValue().size();
//				System.out.println(t.getLength());
				t.calculateTFIDF(term.getValue().size());
			}
		}

		oos.writeObject(titleMap);
		oos.writeObject(null);
		oos.close();

		try {
			PrintWriter pw = new PrintWriter("title.txt");

			for (Entry<String, ArrayList<TitleDoc>> title : titleMap.entrySet()) {
				pw.println("term: " + title.getKey());
				pw.println("DocNo: " + title.getValue().size());
				pw.println();
			}

			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadStops() {

		FileInputStream fileIn = null;
		InputStreamReader reader = null;
		BufferedReader bufferIn = null;

		try {
			String str = "";
			fileIn = new FileInputStream("stopwords.txt");
			reader = new InputStreamReader(fileIn);
			bufferIn = new BufferedReader(reader);

			while ((str = bufferIn.readLine()) != null) {
				stops.add(str);
			}

		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException");
		} catch (IOException e) {
			System.out.println("IOException");
		} finally {
			try {
				bufferIn.close();
				reader.close();
				fileIn.close();

			} catch (IOException e) {
				System.out.println("IOException");
			}
		}
	}

	private ArrayList<Entry<Integer, Double>> getResultTitle() {

		int size = qMap.size();
		double score = 0.0;

		// qMap, contains query term and documents list whose title contains
		// query term
		Iterator iter = qMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			ArrayList<TitleDoc> doclist = (ArrayList<TitleDoc>) entry.getValue();
			for (int i = 0; i < doclist.size(); i++) {
				int docID = doclist.get(i).getId();

				if (!dList.containsKey(docID)) {
					score = doclist.get(i).getTFIDF() * query.get(key);
					dList.put(docID, score);
				} else {
					score = dList.get(docID);
					score += doclist.get(i).getTFIDF() * query.get(key);
					dList.put(docID, score);
				}
			}
		}

		ArrayList<Map.Entry<Integer, Double>> t = new ArrayList<Map.Entry<Integer, Double>>(dList.entrySet());
		Collections.sort(t, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> entry1, Map.Entry<Integer, Double> entry2) {
				return (entry2.getValue().compareTo(entry1.getValue()));
			}
		});

		Iterator<Entry<Integer, Double>> it = t.iterator();
		int i = 0;
		ArrayList<Entry<Integer, Double>> ranking = new ArrayList<Entry<Integer, Double>>();
		while (it.hasNext() && i < 5) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) it.next();
			ranking.add(entry);
			i++;
		}
		return ranking;
	}

	// get document whose title contains qTerms
	private void kDocument(String q) throws ClassNotFoundException, IOException {

		// calculate query's value for each term;
		StringTokenizer st = new StringTokenizer(q, " "); // more detail later
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (query.containsKey(token)) {
				double val = query.get(token);
				query.put(token, ++val);
			} else {
				query.put(token, 1.0);
			}
		}
		Iterator iter = query.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Double> entry = (Entry<String, Double>) iter.next();
			String key = entry.getKey();
			query.put(key, 1 + Math.log10(query.get(key)));
		}

		File file = new File("doc-title.dat");
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);

		ArrayList<TitleDoc> docList = new ArrayList<TitleDoc>();
		HashMap<String, ArrayList<TitleDoc>> titleMap1 = (HashMap<String, ArrayList<TitleDoc>>) ois.readObject();
		Iterator iter1 = titleMap1.entrySet().iterator();
		while (iter1.hasNext()) {
			Entry<String, ArrayList<TitleDoc>> pair = (Entry<String, ArrayList<TitleDoc>>) iter1.next();
			if (query.containsKey(pair.getKey())) {
				System.out.println(pair.getKey());
				if (qMap.containsKey((String) pair.getKey())) {
					docList = (ArrayList<TitleDoc>) pair.getValue();
					qMap.get((String) pair.getKey()).addAll(docList);
					System.out.println(docList.size() + " found");
				} else {
					docList = (ArrayList<TitleDoc>) pair.getValue();
					qMap.put((String) pair.getKey(), docList);
					System.out.println(docList.size() + " found");
				}
			}
			ois.close();
			fis.close();
		}
	}

	public static void main(String[] args) {
		Title t = new Title();
//		t.loadStops();
//		try {
//			t.titleRank();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		 try {
		 t.kDocument("machine learning");
		 } catch (ClassNotFoundException e) {
		 e.printStackTrace();
		 } catch (IOException e) {
		 e.printStackTrace();
		 }
		 ArrayList<Entry<Integer, Double>> a = t.getResultTitle();
		 for (Entry<Integer, Double> b : a) {
		 System.out.println(b.toString());
		 }

	}
}
