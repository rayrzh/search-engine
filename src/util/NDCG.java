package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class NDCG
{
	
	private HashMap<String, int[]> mapRanking(String file, boolean ideal)
	{
		HashMap<String, int[]> ranking = new HashMap<String, int[]>();
		
		try
		{
			FileReader fileReader = new FileReader(new File(file));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int rank = 5;
			String line = bufferedReader.readLine();
			while(line != "" && rank > 0)
			{
				if(line.equals("@URL"))
				{
					line = bufferedReader.readLine();
					int[] ranks = new int[2];
					if(ideal)
					{
						ranks[0] = rank;
						ranks[1] = 0;
					}
					else
					{
						ranks[0] = 0;
						ranks[1] = rank;
					}
					ranking.put(line, ranks);
					rank--;
				}
				line = bufferedReader.readLine();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return ranking;
	}
	
	private HashMap<String, int[]> getRankingMap(String idealFile, String actualFile)
	{
		HashMap<String, int[]> rankingMap = new HashMap<String, int[]>();
		HashMap<String, int[]> idealRanking = mapRanking(idealFile, true);
		HashMap<String, int[]> acturalRanking = mapRanking(actualFile, false);
		
		rankingMap = idealRanking;
		Iterator iter = acturalRanking.entrySet().iterator();
		while(iter.hasNext())
		{
			Entry<String, int[]> entry = (Entry<String, int[]>) iter.next();
			String key = entry.getKey();
			if(rankingMap.containsKey(key))
			{
				rankingMap.get(key)[1] = entry.getValue()[1];
			}
			else
			{
				int[] ranks = {0, entry.getValue()[1]};
				rankingMap.put(key, ranks);
			}
		}
		
		rankingMap = (HashMap<String, int[]>) sortByValue(rankingMap);
		
		
		return rankingMap;
	}
	
	private <K, V extends Comparable<? super V>> Map<String, int[]> sortByValue(Map<String, int[]> map)
	{
		List<Map.Entry<String, int[]>> list = new LinkedList<Map.Entry<String, int[]>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, int[]>>() {
			public int compare(Map.Entry<String, int[]> o1, Map.Entry<String, int[]> o2)
			{
				return ((Integer) o2.getValue()[0]).compareTo((Integer) o1.getValue()[0]);
			}
		});

		Map<String, int[]> result = new LinkedHashMap<String, int[]>();
		for (Map.Entry<String, int[]> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	public double calculateNDCG(String idealFile, String actualFile)
	{
		HashMap<String, int[]> rankingMap = getRankingMap(idealFile, actualFile);
		
		Iterator iter = rankingMap.entrySet().iterator();
		int count = 1;
		double[] DCG = new double[] {0.0, 0.0};
		while(iter.hasNext() && count < 6)
		{
			Entry<String, int[]> entry = (Entry<String, int[]>) iter.next();
			double[] dg = new double[2];
			
			if(count == 1)
			{
				dg[0] = entry.getValue()[0];
				dg[1] = entry.getValue()[1];
			}
			else
			{
				dg[0] = (double)entry.getValue()[0]/(Math.log(count)/Math.log(2.0));
				dg[1] = (double)entry.getValue()[1]/(Math.log(count)/Math.log(2.0));
			}
			DCG[0] += dg[0];
			DCG[1] += dg[1];
			count++;
			System.out.printf("RK\t[%d, %d]\n", entry.getValue()[0], entry.getValue()[1]);
//			System.out.printf("DG\t[%f, %f]\n", dg[0], dg[1]);
			System.out.printf("DCG\t[%f, %f]\n", DCG[0], DCG[1]);
		}
		
		return DCG[1]/DCG[0];
	}
	
	
	public static void main(String[] args)
	{
		
		String[] queries = {"mondego", "machine learning", "software engineering", "security", "student affairs", 
				"graduate courses", "crista lopes", "rest", "computer games", "information retrieval"};
		String google = System.getProperty("user.dir") + File.separator + "google" + File.separator;
		String humongous = System.getProperty("user.dir") + File.separator + "humongous" + File.separator;
		
		
		NDCG ndcg = new NDCG();
		
		DocWriter dw = new DocWriter(System.getProperty("user.dir") + File.separator, "NDCG@5-optimized", "txt");
		
		for(String query : queries)
		{
//			HashMap<String, int[]> map = ndcg.getRankingMap(google + query + ".txt", humongous + query + ".txt");
//	
//			Iterator iter = map.entrySet().iterator();
//			while(iter.hasNext())
//			{
//				Entry<String, int[]> entry = (Entry<String, int[]>) iter.next();
//				System.out.println(entry.getKey());
//				System.out.printf("[%d, %d]\n", entry.getValue()[0], entry.getValue()[1]);
//			}
			
			double NDCG = ndcg.calculateNDCG(google + query + ".txt", humongous + query + ".txt");
			System.out.println(query+" NDCG:\t"+NDCG);
			System.out.println();
			dw.write(query + ": " + NDCG + "\n");
		}
		
		dw.close();
	}

}
