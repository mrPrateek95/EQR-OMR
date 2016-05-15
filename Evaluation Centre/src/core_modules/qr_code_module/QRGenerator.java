package core_modules.qr_code_module;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRGenerator 
{
	static final String charset = "UTF-8"; // or alternatively "ISO-8859-1"
	
	public BufferedImage write_qr_code(String qrCodeData, int qrCodeheight, int qrCodewidth) throws UnsupportedEncodingException, WriterException
	{
		System.out.println("Inside QRCode Module (Writing QR Code)");
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
	    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	    
	    BitMatrix matrix = new QRCodeWriter().encode(new String(qrCodeData.getBytes(charset), charset),BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight);
	    //String image_format = path_to_qr.substring(path_to_qr.lastIndexOf('.') + 1);
	    
	    BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
	    // MatrixToImageWriter.writeToFile(matrix, image_format, new File(path_to_qr));
	    //System.out.println("QR Code written to: "+path_to_qr);
	    System.out.println("Exiting QRCode Module (Writing QR Code)");
	    System.out.println("_______________________________________________________________________________________________________________________________________________________________");
	    return image;
	}
}
