package duedue;

import java.io.Serializable;

public class Page implements Serializable
{
	
	private static final long serialVersionUID = 2990888688889159498L;
	private int docName;
	private int urlValue;
	
	public Page(int hashValue, int docID)
	{
		this.docName = docID;
		this.urlValue = hashValue;
	}
	
	public int getDocName()
	{
		return docName;
	}
	
	public int getUrlValue()
	{
		return urlValue;
	}

}
