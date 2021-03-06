package Indexer;

import java.io.Serializable;
import java.util.ArrayList;

public class Document implements Serializable{
	
	private static final long serialVersionUID = 491923165537575797L;
	private ArrayList<Integer> position;
	private double tfIDF;
	private String docName;
	private int length;
	
	public Document(String docName,int length)
	{
		position = new ArrayList<Integer>();
		tfIDF = 0;
		this.docName = docName;
		this.length = length;
	}
	
	public Document(String docName, ArrayList<Integer> pos, int length)
	{
		position = new ArrayList<Integer>(pos);
		tfIDF = 0;
		this.docName = docName;
		this.length = length;
	}

	public void addPosition(int pos) 
	{
		position.add(pos);
		return;
	}
	
	public ArrayList<Integer> getPositions()
	{
		return position;
	}
	
	public double getTFIDF()
	{
		return tfIDF;
	}
	
	public void calculateTFIDF(int docFreq, int totalDocCount)
	{
		tfIDF = Math.log(1+position.size())*Math.log10(totalDocCount/docFreq)/length;
		return;
	}
	
	public String getDocName()
	{
		return docName;
	}
	
	public String toString()
	{
		String s = "\r\tDoc: " + this.docName;
		s += "\r\t\t[pos: ";
		s += position.toString();
		s += String.format(", TF-IDF: %.4f]", (float) tfIDF);
		return s;
	}
	
}
