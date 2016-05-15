package core_modules.image_processor_module;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class ImageMerger 
{
	public void merge(String path_to_omr, BufferedImage qr_image, String path_to_qr_omr) throws EQROMRFileNotFoundException, OMRFileNotFoundException, IOException
	{
		System.out.println("Inside Image Processor Module (Image Superimposition)");
		File omr_file = new File(path_to_omr);
		if(omr_file.exists())
		{
			BufferedImage omr_sheet = ImageIO.read(omr_file);
			int w = Math.max(omr_sheet.getWidth(), qr_image.getWidth());
			int h = Math.max(omr_sheet.getHeight(), qr_image.getHeight());
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			Graphics g = combined.getGraphics();
			g.drawImage(omr_sheet, 0, 0, null);
			g.drawImage(qr_image, 552, 73, null);
			
			if(Files.exists(Paths.get(path_to_qr_omr.substring(0,path_to_qr_omr.lastIndexOf(File.separatorChar)))))
			{
				ImageIO.write(combined, "PNG", new File(path_to_qr_omr));
				System.out.println("Resultant Image written to path: "+path_to_qr_omr);
				System.out.println("Exiting Image Processor Module (Image Superimposition)");
				System.out.println("_______________________________________________________________________________________________________________________________________________________________");
		
			}
			else
			{
				throw new EQROMRFileNotFoundException();
			}
		}
		else
		{
			throw new OMRFileNotFoundException();
		}
	}
}
