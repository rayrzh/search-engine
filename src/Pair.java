
import java.io.Serializable;
import java.util.ArrayList;

public class Pair implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String key="";
		private ArrayList<Document> array = new ArrayList<Document>();
		
		public Pair(String key, ArrayList<Document> array)
		{
			this.key = key;
			this.array = new ArrayList<Document>(array);
		}
		
		public String getKey(){
			return this.key;
		}
		public ArrayList<Document> getValue(){
			return this.array;
		}
	}
