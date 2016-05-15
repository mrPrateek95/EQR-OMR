package core_modules.validation_module;

import core_modules.data_extractor_module.ExtractedData;

public class Validator
{
	public static boolean verify(ExtractedData omr, ExtractedData qr)
	{
		if(omr.num_of_responses != qr.num_of_responses || !omr.usn.equals(qr.usn))
		{
			return false;
		}
		return true;
	}
}
