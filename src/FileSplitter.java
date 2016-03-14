
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileSplitter
{
	private static final String DIR_PATH = System.getProperty("user.dir") + File.separator;
	private static final String INDEX_PATH = DIR_PATH + "index" + File.separator;
	private final String[] indexName = {"0-9", "a-c", "d-f", "g-i", "j-l", "m-o", "p-r", "s-u", "v-x", "y-z"};
	
	public FileSplitter()
	{
		File file = new File(INDEX_PATH);
		if (!file.exists())
			file.mkdir();
	}
	
	public void split()
	{
		File file = new File(DIR_PATH + "Final.dat");	
		try
		{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			int fileCount = 0;
			FileOutputStream fos = new FileOutputStream(new File(INDEX_PATH + indexName[fileCount] + ".dat"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			Pair pair = (Pair) ois.readObject();
			String s = (String) pair.getKey() + "";
			char c = s.charAt(0);
			int count = 0;
			
			while (pair != null && Character.isDigit(c))
			{
				oos.writeObject(pair);
				count++;
				
				if(count%100 == 0)
					System.out.println(count + "\t" + c);
				if(count%10000 == 0)
					System.gc();
				
				pair = (Pair) ois.readObject();
				s = (String) pair.getKey() + "";
				c = s.charAt(0);
			}
			oos.writeObject(null);
			oos.close();
			fos.close();
			System.out.println("Closing numbers");
			
			char ch = 'a';
			while(ch <= 'z')
			{
				fileCount++;
				fos = new FileOutputStream(new File(INDEX_PATH + indexName[fileCount] + ".dat"));
				oos = new ObjectOutputStream(fos);
				while (pair != null && c <= ch+2)
				{
					oos.writeObject(pair);
					count++;
					if(count%100 == 0)
						System.out.println(count + "\t" + c);
					if(count%10000 == 0)
						System.gc();
					
					pair = (Pair) ois.readObject();
					if(pair != null)
					{	
						s = (String) pair.getKey() + "";
						c = s.charAt(0);
					}
				}
				oos.writeObject(null);
				oos.close();
				System.out.println("Closing "+ indexName[fileCount]);
				ch += 3;
				System.gc();
			}
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args)
	{
		FileSplitter fs = new FileSplitter();
		fs.split();
	}

}
