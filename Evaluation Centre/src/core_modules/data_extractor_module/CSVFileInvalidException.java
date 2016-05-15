package core_modules.data_extractor_module;

@SuppressWarnings("serial")
public class CSVFileInvalidException extends Exception
{
	public CSVFileInvalidException()
	{
		super();
	}
	public CSVFileInvalidException(String message)
	{
	    super(message);
	}
}
