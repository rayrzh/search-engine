package duedue;

import java.io.Serializable;
import java.util.ArrayList;

public class TitleDoc implements Serializable{
	private int fre;
	private double tfIDF;
	private int docID;
	private int length;
	
	public TitleDoc(int docID)
	{
		fre=0;
		tfIDF = 0;
		this.docID = docID;
	}
	public int getId(){
		return docID;
	}
	public void add() 
	{
		fre++;
		return;
	}
	
	public int frequency()
	{
		return fre;
	}
	
	public double getTFIDF()
	{
		return tfIDF;
	}
	
	public void calculateTFIDF(int docFreq)
	{
		tfIDF = Math.log(1+fre)*Math.log10(63993/docFreq);
		return;
	}
	
	
	
//	public String toString()
//	{
//		String s = "\r\tDoc: " + this.docName;
//		s += "\r\t\t[pos: ";
//		s += position.toString();
//		s += String.format(", TF-IDF: %.4f]", (float) tfIDF);
//		return s;
//	}
	
	
}
