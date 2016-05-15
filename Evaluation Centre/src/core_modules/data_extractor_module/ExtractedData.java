package core_modules.data_extractor_module;

public class ExtractedData {
	public String usn;
	public int num_of_responses;
	
	public ExtractedData()
	{
		
	}
	
	public ExtractedData(String s)
	{
		String[] strarr = s.split(";");
		System.out.println("Inside ExtractedData(String) constructor");
		System.out.println(s);
		System.out.println("Exiting ExtractedData(String) constructor");
		System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		this.usn = strarr[0];
		this.num_of_responses = Integer.parseInt(strarr[1]);
	}
	public String toString()
	{
		return usn+";"+num_of_responses;
	}
}

