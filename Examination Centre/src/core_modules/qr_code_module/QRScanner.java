package core_modules.qr_code_module;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRScanner 
{
	static final String charset = "UTF-8"; // or alternatively "ISO-8859-1"
	
	public String read_qr_code(String path_to_qr_omr)
			throws FileNotFoundException, IOException, NotFoundException, ChecksumException, FormatException
	{
		System.out.println("Inside QRCode Module (Reading QR Code)");
		Map<DecodeHintType, Object> hintMap = new HashMap <DecodeHintType, Object>();
	    hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path_to_qr_omr)))));
		Result qrCodeResult = new QRCodeReader().decode(binaryBitmap,hintMap);
	    
		System.out.println("QR Code read from: "+path_to_qr_omr);
		System.out.println(qrCodeResult.getText());
		System.out.println("Exiting QRCode Module (Reading QR Code)");
		System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		
		return qrCodeResult.getText();
	}
}
