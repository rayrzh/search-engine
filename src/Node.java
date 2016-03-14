
import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable
{

	private static final long serialVersionUID = 7306362059931039441L;
	private int docID;
	private ArrayList<Integer> outLink;
	private ArrayList<Integer> inLink;
	private double pageRanking;

	public Node(int dicID)
	{

		this.docID = dicID;
		outLink = new ArrayList<Integer>();
		inLink = new ArrayList<Integer>();
		pageRanking = 1.0;
	}

	public void addInLink(int docID)
	{
		inLink.add(docID);
	}

	public void addOutLink(int docID)
	{
		outLink.add(docID);
	}
	
	public void setPageRanking(double pageRanking)
	{
		this.pageRanking = pageRanking;
	}
	
	public double getPageRanking()
	{
		return pageRanking;
	}

	public ArrayList<Integer> getInLinks()
	{
		return inLink;
	}

	public ArrayList<Integer> getOutLinks()
	{
		return outLink;
	}

	public int getOutLinkSize()
	{
		return outLink.size();
	}

	public int getDocId()
	{
		return this.docID;
	}

	public int getInLinkSize()
	{
		return inLink.size();
	}
}
