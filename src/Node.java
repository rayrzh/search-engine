
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7306362059931039441L;
	private int docID;
	private double pr;
	private ArrayList<Integer> outLink;
	private ArrayList<Integer> inLink;
	
	public Node(int dicID){
		
		this.docID=dicID;
		outLink=new ArrayList<Integer>();
		inLink = new ArrayList<Integer>();
		pr=1.0;
	}
	public void addInLink(int docID){
		inLink.add(docID);
	}
	public void addOutLink(int docID){
		outLink.add(docID);
	}
	public void setPageRanking(double pr)
	{
		this.pr = pr;
	}
	public ArrayList<Integer> getInLinks()
	{
		return inLink;
	}
	public ArrayList<Integer> getOutLinks()
	{
		return outLink;
	}
	public int getOutLinkSize(){
		return outLink.size();
	}
	public int getDocId(){
		return this.docID;
	}
	public int getInLinkSize(){
		return inLink.size();
	}
	public double getPR(){
		return this.pr;
	}
}
