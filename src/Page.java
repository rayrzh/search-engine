import java.io.Serializable;

public class Page implements Serializable
{
	
	private static final long serialVersionUID = 2990888688889159498L;
	private String docName;
	private String url;
	
	public Page(String docName, String url)
	{
		this.docName = docName;
		this.url = url;
	}
	
	public String getDocName()
	{
		return docName;
	}
	
	public String getUrl()
	{
		return url;
	}

}
