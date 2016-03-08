package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DocWriter
{
	private BufferedWriter bufferedWriter;
	private FileWriter fileWriter;
	
	public DocWriter(String pathName, String docName, String extension)
	{
		try
		{
			File file = new File(pathName);
			if(!file.exists())
				file.mkdirs();
			file = new File(pathName + docName + "." + extension);
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
		}
		catch (IOException e)
		{
			fileWriter = null;
			bufferedWriter = null;
			e.printStackTrace();
		}
	}
	
	public void write(String s)
	{
		try
		{
			bufferedWriter.write(s);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		try
		{
			if(bufferedWriter != null)
			{
				bufferedWriter.flush();
				bufferedWriter.close();
			}
			if(fileWriter != null)
			{
				fileWriter.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
