
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7306362059931039441L;
	private int docID;
	private double pr;
	private ArrayList<Node> outLink;
	private ArrayList<Node> inLink;
	
	public Node(int dicID){
		
		this.docID=dicID;
		outLink=new ArrayList<Node>();
		inLink = new ArrayList<Node>();
		pr=1.0;
	}
	public void addInLink(Node n){
		inLink.add(n);
	}
	public void addOutLink(Node n){
		outLink.add(n);
	}
	public void setPageRanking(double pr)
	{
		this.pr = pr;
	}
	public int getOutLinkSize(){
		return outLink.size();
	}
	public int getInLinkSize(){
		return inLink.size();
	}
	public int getDocID()
	{
		return docID;
	}
	public double getPR(){
		return this.pr;
	}
}
