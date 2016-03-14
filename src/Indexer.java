package Indexer;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Indexer {

	private static final String DIR_PATH = System.getProperty("user.dir") + File.separator;
	private static final String PAGE_PATH = DIR_PATH + "CrawledPages" + File.separator;
	private static final String INDEX_PATH = DIR_PATH + "indexes" + File.separator;
	private static final String UPDATED_INDEX_PATH = DIR_PATH + "updated-indexes" + File.separator;
	private static final String TXT_PATH = DIR_PATH + "txt" + File.separator;
	private int totalDocCount;
	private int wordCount;
	private TreeMap<String, ArrayList<Document>> termTreeMap;

	public Indexer() {
		totalDocCount = 0;
		wordCount = 0;
		termTreeMap = new TreeMap<String, ArrayList<Document>>();

		File file = new File(TXT_PATH);
		if (!file.exists())
			file.mkdir();

		file = new File(INDEX_PATH);
		if (!file.exists())
			file.mkdir();
	}
	/*
	private void datToText(String fileName, int fileCount) throws ParseException, FileNotFoundException, IOException {
		String s = "";
		ArrayList<Document> docList = new ArrayList<Document>();

		try {
			FileInputStream fis = new FileInputStream(new File(INDEX_PATH + fileName));
			ObjectInputStream ois = new ObjectInputStream(fis);
			PrintWriter pw = new PrintWriter(new File(TXT_PATH + fileCount + ".txt"));

			int count = 0;
			Pair pair = null;
			while ((pair = (Pair) ois.readObject()) != null) {

				s = (String) pair.getKey() + "";
				docList = (ArrayList<Document>) pair.getValue();

				s += "\n{";
				for (int i = 0; i < docList.size(); i++) {
					Document doc = docList.get(i);
					s += doc.toString();
					if (i < docList.size() - 1) {
						s += ",";
						s += "\t\t";
					}
				}
				s = s.substring(0, s.length());
				s += "\n}";
				pw.println(s);
				pw.println();
				count++;
				if (count % 1000 == 0)
					System.out.println("term count " + count);
				if (count % 10000 == 0)
					System.gc();
			}
			pw.flush();
			pw.close();

			ois.close();
			fis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	*/
	private void saveIndex() throws ClassNotFoundException, IOException {
		// save index and statistics to file
		File file = new File(DIR_PATH + "Final.dat");
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);

		String s = "";
		ArrayList<Document> docList = new ArrayList<Document>();
		PrintWriter pw = new PrintWriter(new File(TXT_PATH + "Final.txt"));
		
		try {
			int count = 0;
			Pair pair = null;
			while ((pair = (Pair) ois.readObject()) != null) {

				s = (String) pair.getKey() + "";
				wordCount++;
				docList = (ArrayList<Document>) pair.getValue();
				s += "\n{";
				for (int i = 0; i < docList.size(); i++) {
					Document doc = docList.get(i);
					s += doc.toString();
					if (i < docList.size() - 1) {
						s += ",";
						s += "\t\t";
					}
				}
				s = s.substring(0, s.length());
				s += "\n}";
				pw.println(s);
				pw.println();
				count++;
				if (count % 1000 == 0)
					System.out.println("term count " + count);
				if (count % 10000 == 0)
					System.gc();
			}
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		}

	}

	private void saveStats(int wordCount) {
		try {
			PrintWriter pw = new PrintWriter(new File(DIR_PATH + "wordCount.txt"));
			// pw.printf("Number of documents:\t%d\r", totalDocCount);
			pw.printf("Number of unique words:\t%d", wordCount);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void buildIndex(String fileName) {
		String docName = fileName.substring(0, fileName.indexOf('.'));
		JSONParser parser = new JSONParser();
		Object obj = null;
		FileReader fr;
		try {
			fr = new FileReader(PAGE_PATH + fileName);
			obj = parser.parse(fr);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		totalDocCount++;
		JSONObject jsonObject = (JSONObject) obj;
		String text = (String) jsonObject.get("text");
		if (fr != null) {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		text = text.replaceAll("([a-z0-9_]{1})([A-Z]{1})", "$1 $2");
		text = text.replaceAll("[^a-zA-Z0-9']", " ");

		String pattern = " \t\n\r\f.," + "~!@#$%^&*()_+-=`\"'" + "{}|[]\\;\"" + "<>?/";
		StringTokenizer st = new StringTokenizer(text, pattern, false);
		int length = st.countTokens();
		String token = "";
		int position = -1;

		// construct Map<term, list<position>>
		Map<String, ArrayList<Integer>> termMap = new HashMap<String, ArrayList<Integer>>();
		while (st.hasMoreTokens()) {
			position++;
			token = st.nextToken().toLowerCase();
			if (termMap.containsKey(token)) {
				termMap.get(token).add(position);
			} else {
				termMap.put(token, new ArrayList<Integer>());
				termMap.get(token).add(position);
			}
		}

		// construct Map<term, list<Document>>
		Iterator<Entry<String, ArrayList<Integer>>> it = termMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry termPos = (Map.Entry) it.next();
			Document doc = new Document(docName, (ArrayList<Integer>) termPos.getValue(),length);
			if (termTreeMap.containsKey(termPos.getKey())) {
				termTreeMap.get(termPos.getKey()).add(doc);
			} else {
				termTreeMap.put((String) termPos.getKey(), new ArrayList<Document>());
				termTreeMap.get(termPos.getKey()).add(doc);
			}
		}
	}

	private void writeObject(TreeMap<String, ArrayList<Document>> termTreeMap, int count) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(INDEX_PATH + count + ".dat"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			Iterator<Entry<String, ArrayList<Document>>> iter = termTreeMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = iter.next();
				Pair pair = new Pair((String) entry.getKey(), (ArrayList<Document>) entry.getValue());
				oos.writeObject(pair);
			}
			oos.writeObject(null);

			oos.flush();
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory())
					deleteFolder(f);
				else
					f.delete();
			}
		}
		folder.delete();
	}

	public void index() throws ParseException, FileNotFoundException, IOException {
		File file = new File(PAGE_PATH);
		String[] fileNames = file.list();

		int count = 0;
		int fileCount = 0;
		for (String fileName : fileNames) {

			buildIndex(fileName);
			count++;
			if (count % 10000 == 0 || count == fileNames.length) {
				fileCount++;
				writeObject(termTreeMap, fileCount);
				if (count != fileNames.length)
					termTreeMap = new TreeMap<String, ArrayList<Document>>();
			}
			if (totalDocCount % 1000 == 0)
				System.out.println("file count " + totalDocCount);
		}

		PrintWriter pw = new PrintWriter(new File(DIR_PATH + "DocCount.txt"));
		pw.printf("Number of documents:\t%d\r", totalDocCount);
		pw.flush();
		pw.close();

//		file = new File(INDEX_PATH);
//		fileNames = file.list();
//		fileCount = 0;
//		for (String fileName : fileNames) {
//			if (fileName.contains(".dat")) {
//				fileCount++;
//				System.out.println("processing: " + fileName);
//				datToText(fileName, fileCount);
//			}
//		}

//		deleteFolder(file); //index file

		external();
		try {
			saveIndex();
//			File f = new File(DIR_PATH + "final.dat");
//			if (f.exists())
//				file.delete();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		saveStats(wordCount);
	}

	public void external() {
		try {
			// String pagePath = System.getProperty("user.dir") + File.separator
			// + "indices" + File.separator;
			File file = new File(INDEX_PATH);
			ExternalSort.mergeSortedFiles(file.list(), INDEX_PATH, totalDocCount);
//			file.delete();
			System.out.println("Merge Complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] arg)
			throws FileNotFoundException, ParseException, IOException, org.json.simple.parser.ParseException {
		Indexer i = new Indexer();
		i.index();
	}
}
