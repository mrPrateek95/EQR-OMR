package core_modules.data_extractor_module;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class DataExtractor 
{
	private String path_to_csv;
	
	public DataExtractor(String csv_path)
	{
		path_to_csv = csv_path;
	}
	
	public ExtractedData extract() throws IOException, CSVFileInvalidException 
	{
		ExtractedData newData = new ExtractedData();
		String usn="";
		int num_of_responses=0;
		System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		System.out.println("Inside Data Extractor Module");
		System.out.println("Path to CSV given: " +path_to_csv);
		testPath(path_to_csv);
		String csvFilename = extractFilename(path_to_csv);
		String rawData = CsvtoString(path_to_csv,csvFilename);
		ArrayList<String> splitData = StringtoArrayList(rawData);
		for(int i=1; i<=10; i++) 
		{
			usn+= splitData.get(i);
		}
		
		num_of_responses = splitData.size()-11;
		
		newData.usn = usn;
		newData.num_of_responses = num_of_responses;
		System.out.println("Extracted Data (Data to be encrypted):");
		System.out.println(newData);
		System.out.println("Exiting Data Extractor Module");
		System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		return newData;
	}
	
	private static void testPath(String csvPath) throws FileNotFoundException
	{
		System.out.println("Testing if path is valid...");
		File csvFile = new File(csvPath);
		if(csvFile.exists())
		{
			System.out.println("CSV Path is Valid");
		}
		else
		{
			throw new FileNotFoundException();
		}
	}
	
	private static String extractFilename(String csvPath) 
	{
		System.out.println("Extracting filename...");
		Path csvFilePath = Paths.get(csvPath);
		String csvFilename = csvFilePath.getFileName().toString();
		csvFilename = csvFilename.substring(0, csvFilename.length()-4);
		System.out.println("Filename extracted: "+csvFilename);
		return csvFilename;
	}
	
	private static String CsvtoString(String path, String filename) throws IOException, CSVFileInvalidException 
	{
		BufferedReader csvBuffer;
		String csvstr = null, csvLine;
		System.out.println("Extracting CSV File Data...");
		csvBuffer = new BufferedReader(new FileReader(path));
		while ((csvLine = csvBuffer.readLine()) != null) 
		{
			csvstr+=csvLine;
		}
		csvBuffer.close();
		if(!csvstr.contains(filename))
		{
			throw new CSVFileInvalidException();
		}
		csvstr = csvstr.substring(csvstr.indexOf(filename));
		System.out.println("Raw Data extracted from CSV File:");
		System.out.println(csvstr);
		return csvstr;
	}
	
	private static ArrayList<String> StringtoArrayList(String rawdata) 
	{
		System.out.println("Converting Comma-Seperated-Values to ArrayList of Strings...");
		ArrayList<String> resultantArrayList = new ArrayList<String>();
		rawdata = rawdata.replace(";;", ";");
		
		String[] splitData = rawdata.split(";");
		for (int i = 0; i < splitData.length; i++) 
		{
			if (!(splitData[i] == null) || !(splitData[i].length() == 0))
			{
				resultantArrayList.add(splitData[i].trim());
			}
		}
		System.out.println("Resultant ArrayList:");
		System.out.println(resultantArrayList);
		return resultantArrayList;
	}
}

