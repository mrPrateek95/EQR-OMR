package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;
import javax.swing.JButton;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import core_modules.cryptography_module.Encryptor;
import core_modules.data_extractor_module.CSVFileInvalidException;
import core_modules.data_extractor_module.DataExtractor;
import core_modules.data_extractor_module.ExtractedData;
import core_modules.image_processor_module.EQROMRFileNotFoundException;
import core_modules.image_processor_module.ImageMerger;
import core_modules.image_processor_module.OMRFileNotFoundException;
import core_modules.qr_code_module.QRGenerator;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class exam_gui extends JFrame
{

	private JPanel contentPane;
	private JPanel panel_path_to_csv;
	private JTextField textfield_path_to_csv;
	private JButton btnBrowse_path_to_csv;
	private JPanel panel_path_to_omr_image;
	private JTextField textfield_path_to_omr_image;
	private JButton btnBrowse_path_to_omr_image;
	private JPanel panel_encryption_password;
	private JPasswordField pwdfield_encryption_password;
	private JButton btnGo;
	private JPanel panel_progress;
	private JLabel lbl_image_uploaded;
	private JLabel lbl_vital_data_extracted;
	private JLabel lbl_data_encrypted;
	private JLabel lbl_qr_code_generated;
	private JLabel lbl_omr_with_eqr;
	private JProgressBar progress_bar;
	private JLabel lbl_see_details_1;
	private JLabel lbl_see_details_2;
	private JLabel lbl_see_details_3;
	private JLabel lbl_see_details_4;
	private JLabel lbl_see_details_5;
	private JPanel panel_path_to_save_eqr_omr;
	private JTextField textfield_path_to_save_eqr_omr;
	private JButton btnBrowse_path_to_save_eqr_omr;
	private JLabel lbl_a_digit;
	private JLabel lbl_an_uppercase_letter;
	private JLabel lbl_a_special_character;
	private JLabel lbl_min_8_characters;	

	private JMenuBar menu_bar;
	private JMenu menu_file;
	private JMenu menu_tools;
	private JMenuItem menu_item_exit;
	private JMenuItem menu_item_open_console;
	
	ExtractedData vital_data = null;
	String cipher_text = null;
	BufferedImage qr_image = null;
	String path_to_qr_omr = null;
	int exec_progress;
	
	console console = null;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					exam_gui frame = new exam_gui();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public exam_gui()
	{
		console = new console();
		
		setMinimumSize(new Dimension(500, 585));
		setTitle("Examination Centre");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 500, 585);
		setLocationRelativeTo(null);
		
		menu_bar = new JMenuBar();
		setJMenuBar(menu_bar);
		
		menu_file = new JMenu("File");
		menu_bar.add(menu_file);
		
		menu_item_exit = new JMenuItem("Exit");
		menu_item_exit.setToolTipText("Exit the application");
		menu_item_exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});
		menu_item_exit.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/power-icon_16.png")));
		menu_file.add(menu_item_exit);
		
		menu_tools = new JMenu("Tools");
		menu_bar.add(menu_tools);
		
		menu_item_open_console = new JMenuItem("Open Console");
		menu_item_open_console.setToolTipText("Open the console for verbose logging");
		menu_item_open_console.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				console.show_console(true);
			}
		});
		menu_item_open_console.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/console-icon_12.png")));
		menu_tools.add(menu_item_open_console);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		panel_path_to_csv = new JPanel();
		panel_path_to_csv.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PATH TO CSV FILE", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panel_path_to_omr_image = new JPanel();
		panel_path_to_omr_image.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PATH TO OMR IMAGE", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panel_encryption_password = new JPanel();
		panel_encryption_password.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ENCRYPTION PASSWORD", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panel_progress = new JPanel();
		panel_progress.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PROGRESS", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panel_path_to_save_eqr_omr = new JPanel();
		panel_path_to_save_eqr_omr.setBorder(new TitledBorder(null, "PATH TO SAVE EQR-OMR", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		lbl_image_uploaded = new JLabel("Image Uploaded");
		
		lbl_vital_data_extracted = new JLabel("Vital Data Extracted");
		
		lbl_data_encrypted = new JLabel("Data Encrypted");
		
		lbl_qr_code_generated = new JLabel("QR Code Generated");
		
		lbl_omr_with_eqr = new JLabel("OMR With EQR");
		
		progress_bar = new JProgressBar();
		progress_bar.setStringPainted(true);
		progress_bar.setMaximum(10);
		
		lbl_see_details_1 = new JLabel("See Details");
		lbl_see_details_1.setEnabled(false);
		lbl_see_details_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_see_details_1.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if(lbl_see_details_1.isEnabled())
				{
					lbl_see_details_1.setText("<html><u>See Details</u></html>");
				}					
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(lbl_see_details_1.isEnabled())
				{
					lbl_see_details_1.setText("See Details");					
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if(lbl_see_details_1.isEnabled())
				{
					new dialog_display_image(textfield_path_to_omr_image.getText(), "Image Uploaded");
				}
			}
		});
		lbl_see_details_1.setForeground(Color.BLUE);
		
		lbl_see_details_2 = new JLabel("See Details");
		lbl_see_details_2.setEnabled(false);
		lbl_see_details_2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_see_details_2.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if(lbl_see_details_2.isEnabled())
				{
					lbl_see_details_2.setText("<html><u>See Details</u></html>");
				}
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(lbl_see_details_2.isEnabled())
				{
					lbl_see_details_2.setText("See Details");					
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if(lbl_see_details_2.isEnabled())
				{
					JOptionPane.showMessageDialog(null, vital_data, "Vital Data Extracted", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		lbl_see_details_2.setForeground(Color.BLUE);
		
		lbl_see_details_3 = new JLabel("See Details");
		lbl_see_details_3.setEnabled(false);
		lbl_see_details_3.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_see_details_3.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if(lbl_see_details_3.isEnabled())
				{
					lbl_see_details_3.setText("<html><u>See Details</u></html>");
				}
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(lbl_see_details_3.isEnabled())
				{
					lbl_see_details_3.setText("See Details");
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if(lbl_see_details_3.isEnabled())
				{
					JOptionPane.showMessageDialog(null, cipher_text, "Data Encrypted", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		lbl_see_details_3.setForeground(Color.BLUE);
		
		lbl_see_details_4 = new JLabel("See Details");
		lbl_see_details_4.setEnabled(false);
		lbl_see_details_4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_see_details_4.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if(lbl_see_details_4.isEnabled())
				{
					lbl_see_details_4.setText("<html><u>See Details</u></html>");
				}
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(lbl_see_details_4.isEnabled())
				{
					lbl_see_details_4.setText("See Details");
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if(lbl_see_details_4.isEnabled())
				{
					new dialog_qr_code_generated(qr_image);
					
				}
			}
		});
		lbl_see_details_4.setForeground(Color.BLUE);
		
		lbl_see_details_5 = new JLabel("See Details");
		lbl_see_details_5.setEnabled(false);
		lbl_see_details_5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lbl_see_details_5.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				if(lbl_see_details_5.isEnabled())
				{
					lbl_see_details_5.setText("<html><u>See Details</u></html>");
				}
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(lbl_see_details_5.isEnabled())
				{
					lbl_see_details_5.setText("See Details");
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if(lbl_see_details_5.isEnabled())
				{
					new dialog_display_image(path_to_qr_omr, "OMR With EQR");
				}
			}
		});
		lbl_see_details_5.setForeground(Color.BLUE);
		
		GroupLayout gl_panel_progress = new GroupLayout(panel_progress);
		gl_panel_progress.setHorizontalGroup(
			gl_panel_progress.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_progress.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_progress.createParallelGroup(Alignment.LEADING)
						.addComponent(progress_bar, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addGroup(gl_panel_progress.createSequentialGroup()
							.addGroup(gl_panel_progress.createParallelGroup(Alignment.LEADING)
								.addComponent(lbl_vital_data_extracted, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
								.addComponent(lbl_data_encrypted, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
								.addComponent(lbl_image_uploaded, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
								.addComponent(lbl_qr_code_generated, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
								.addComponent(lbl_omr_with_eqr, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
							.addGap(259)
							.addGroup(gl_panel_progress.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lbl_see_details_5, Alignment.TRAILING)
								.addComponent(lbl_see_details_4, Alignment.TRAILING)
								.addComponent(lbl_see_details_3)
								.addComponent(lbl_see_details_2, Alignment.TRAILING)
								.addComponent(lbl_see_details_1, Alignment.TRAILING))))
					.addContainerGap())
		);
		gl_panel_progress.setVerticalGroup(
			gl_panel_progress.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_progress.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_progress.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_progress.createSequentialGroup()
							.addComponent(lbl_image_uploaded)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_vital_data_extracted)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_data_encrypted)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_qr_code_generated)
							.addGap(7)
							.addComponent(lbl_omr_with_eqr))
						.addGroup(gl_panel_progress.createSequentialGroup()
							.addComponent(lbl_see_details_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_2)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_3)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_4)
							.addGap(7)
							.addComponent(lbl_see_details_5)))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(progress_bar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel_progress.setLayout(gl_panel_progress);
		
		pwdfield_encryption_password = new JPasswordField();
		pwdfield_encryption_password.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				lbl_a_digit.setVisible(true);
				lbl_an_uppercase_letter.setVisible(true);
				lbl_a_special_character.setVisible(true);
				lbl_min_8_characters.setVisible(true);
			}
			@Override
			public void focusLost(FocusEvent e)
			{
				lbl_a_digit.setVisible(false);
				lbl_an_uppercase_letter.setVisible(false);
				lbl_a_special_character.setVisible(false);
				lbl_min_8_characters.setVisible(false);
			}
		});
		pwdfield_encryption_password.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				check_inputs();
			}
			public void removeUpdate(DocumentEvent e)
			{
				check_inputs();
			}
			public void insertUpdate(DocumentEvent e)
			{
				check_inputs();
			}
		});

		pwdfield_encryption_password.setToolTipText("Select a strong password");
		
		btnGo = new JButton("Go");
		btnGo.setEnabled(false);
		btnGo.addActionListener(new ActionListener()
		{
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e)
			{
				if(textfield_path_to_csv.getText().length()>=8 
						&& textfield_path_to_omr_image.getText().length()>=8 
						&& pwdfield_encryption_password.getText().length()>=8 
						&& textfield_path_to_save_eqr_omr.getText().length()>=8)
				{
					disable_input();
					
					SwingWorker<String, Integer> worker = new SwingWorker<String, Integer>()
					{
						@Override
						protected String doInBackground() throws InterruptedException
						{ 
							exec_progress = 0;
							publish(exec_progress);//0
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//IMAGE UPLOADED
////////////////////////////////////////////////////////////////////////////////////////////
							publish(++exec_progress);//1
							Thread.sleep(200);
							
							if(!Files.exists(Paths.get(textfield_path_to_omr_image.getText())))
							{
								return "OMR Image not Found.";
							}
							
							publish(++exec_progress);//2
							Thread.sleep(200);
							
////////////////////////////////////////////////////////////////////////////////////////////
//DATA EXTRACTOR
////////////////////////////////////////////////////////////////////////////////////////////							
							DataExtractor de = new DataExtractor(textfield_path_to_csv.getText());
							publish(++exec_progress);//3
							Thread.sleep(200);
							
							try
							{
								vital_data = de.extract();
							}
							catch (CSVFileInvalidException e)
							{
								e.printStackTrace();
								return "The CSV File appears to be invalid. Please check again.";
							}
							catch (FileNotFoundException e)
							{
								e.printStackTrace();
								return "CSV File not found.";
							}
							catch (Exception e)
							{
								e.printStackTrace();
								return "Error in processing CSV File";
							}
							
							publish(++exec_progress);//4
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//ENCRYPTOR
////////////////////////////////////////////////////////////////////////////////////////////			
							publish(++exec_progress);//5
							Thread.sleep(200);
							try
							{
								cipher_text = new Encryptor().encrypt(pwdfield_encryption_password.getText(), vital_data.toString());
							}
							catch(EncryptionOperationNotPossibleException e)
							{
								e.printStackTrace();
								return "Please Install JCE Unlimited Strength Jurisdiction Policy Files";
							}
							catch(Exception e)
							{
								e.printStackTrace();
								return "Error in encrypting the data";
							}
							publish(++exec_progress);//6
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//QR CODE GENERATOR
////////////////////////////////////////////////////////////////////////////////////////////							
							qr_image = null;
							publish(++exec_progress);//7
							Thread.sleep(200);
							
							try
							{
								qr_image = new QRGenerator().write_qr_code(cipher_text, 200, 200);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								return "Error in generating QR Code";
							}
							
							publish(++exec_progress);//8
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//IMAGE SUPERIMPOSE
////////////////////////////////////////////////////////////////////////////////////////////
							path_to_qr_omr = textfield_path_to_save_eqr_omr.getText() + File.separator +"QR-OMR.png";
							ImageMerger image_superimposer = new ImageMerger();
							publish(++exec_progress);//9
							Thread.sleep(200);
							
							try
							{
								image_superimposer.merge(textfield_path_to_omr_image.getText(), qr_image, path_to_qr_omr);
							}
							catch (OMRFileNotFoundException e)
							{
								e.printStackTrace();
								return "OMR image not found.";
							}
							catch (EQROMRFileNotFoundException e)
							{
								e.printStackTrace();
								return "Save path is invalid.";
							}
							catch (Exception e)
							{
								e.printStackTrace();
								return "Error in superimposing QR image to OMR image.";
							}
							
							publish(++exec_progress);//10
							Thread.sleep(200);
														
							return "Operation Completed Successfully";
						}
						protected void done()
						{	
							enable_input();
							
////////////////////////////////////////////////////////////////////////////////////////////
//Retrieve the return value of doInBackground.
////////////////////////////////////////////////////////////////////////////////////////////							
							String status = null;

							try
							{
								status = get();
							}
							catch (InterruptedException | ExecutionException e)
							{
								e.printStackTrace();
							}
////////////////////////////////////////////////////////////////////////////////////////////
//What to do when SwingWorker thread completes?
////////////////////////////////////////////////////////////////////////////////////////////					
							if(status.equals("Operation Completed Successfully"))
							{
								JOptionPane.showMessageDialog(null, status, "Message", JOptionPane.INFORMATION_MESSAGE);
							}
							else
							{
								switch(exec_progress)
								{
								case 1:
								case 2: lbl_image_uploaded.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 3:
								case 4: lbl_vital_data_extracted.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 5:
								case 6: lbl_data_encrypted.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 7:
								case 8: lbl_qr_code_generated.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 9:
								case 10: lbl_omr_with_eqr.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								default:
									break;
								}
								
								JOptionPane.showMessageDialog(null, status, "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						@Override
						protected void process(List<Integer> chunks)
						{
							int most_recent_value = chunks.get(chunks.size()-1);
							
							switch(most_recent_value)
							{
							case 0:
								lbl_image_uploaded.setIcon(null);
								lbl_vital_data_extracted.setIcon(null);
								lbl_data_encrypted.setIcon(null);
								lbl_qr_code_generated.setIcon(null);
								lbl_omr_with_eqr.setIcon(null);
								lbl_see_details_1.setEnabled(false);
								lbl_see_details_2.setEnabled(false);
								lbl_see_details_3.setEnabled(false);
								lbl_see_details_4.setEnabled(false);
								lbl_see_details_5.setEnabled(false);
								progress_bar.setValue(most_recent_value);
								break;
							case 1:
								lbl_image_uploaded.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 2:
								lbl_image_uploaded.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_1.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 3:
								lbl_vital_data_extracted.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 4:
								lbl_vital_data_extracted.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_2.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 5:
								lbl_data_encrypted.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 6:
								lbl_data_encrypted.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_3.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 7:
								lbl_qr_code_generated.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 8:
								lbl_qr_code_generated.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_4.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 9:
								lbl_omr_with_eqr.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 10:
								lbl_omr_with_eqr.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_5.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							default:
								break;
							}
						    
						}						
					};
					worker.execute();
				}
				else
					JOptionPane.showMessageDialog(null, "Check Inputs");
			}		
		});
		
		lbl_a_digit = new JLabel("A Digit");
		lbl_a_digit.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		lbl_a_digit.setVisible(false);
		
		lbl_an_uppercase_letter = new JLabel("An Uppercase Letter");
		lbl_an_uppercase_letter.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		lbl_an_uppercase_letter.setVisible(false);
		
		lbl_a_special_character = new JLabel("A Special Character");
		lbl_a_special_character.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		lbl_a_special_character.setVisible(false);
		
		lbl_min_8_characters = new JLabel("Min. 8 Characters");
		lbl_min_8_characters.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		lbl_min_8_characters.setVisible(false);
		GroupLayout gl_panel_encryption_password = new GroupLayout(panel_encryption_password);
		gl_panel_encryption_password.setHorizontalGroup(
			gl_panel_encryption_password.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_encryption_password.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_encryption_password.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_encryption_password.createSequentialGroup()
							.addComponent(pwdfield_encryption_password, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnGo))
						.addGroup(gl_panel_encryption_password.createSequentialGroup()
							.addGroup(gl_panel_encryption_password.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lbl_a_digit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbl_an_uppercase_letter, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(39)
							.addGroup(gl_panel_encryption_password.createParallelGroup(Alignment.LEADING)
								.addComponent(lbl_min_8_characters)
								.addComponent(lbl_a_special_character))))
					.addContainerGap())
		);
		gl_panel_encryption_password.setVerticalGroup(
			gl_panel_encryption_password.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_encryption_password.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_encryption_password.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnGo)
						.addComponent(pwdfield_encryption_password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panel_encryption_password.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_a_digit)
						.addComponent(lbl_a_special_character))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_encryption_password.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_an_uppercase_letter)
						.addComponent(lbl_min_8_characters))
					.addContainerGap())
		);
		panel_encryption_password.setLayout(gl_panel_encryption_password);
		
		textfield_path_to_omr_image = new JTextField();
		textfield_path_to_omr_image.setEditable(false);
		textfield_path_to_omr_image.setToolTipText("Select your OMR image file");
		textfield_path_to_omr_image.setColumns(10);
		
		btnBrowse_path_to_omr_image = new JButton("Browse");
		btnBrowse_path_to_omr_image.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".png file", "png");
				chooser.setFileFilter(filter);
				
				File current_working_directory = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(current_working_directory);
				
				int returnVal = chooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					textfield_path_to_omr_image.setText(chooser.getSelectedFile().getAbsolutePath());
										
					textfield_path_to_save_eqr_omr.setText(chooser.getSelectedFile().getAbsolutePath().substring(0,chooser.getSelectedFile().getAbsolutePath().lastIndexOf(File.separator)));
				}
				
				check_inputs();
			}
		});
		GroupLayout gl_panel_path_to_omr_image = new GroupLayout(panel_path_to_omr_image);
		gl_panel_path_to_omr_image.setHorizontalGroup(
			gl_panel_path_to_omr_image.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_path_to_omr_image.createSequentialGroup()
					.addContainerGap()
					.addComponent(textfield_path_to_omr_image, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBrowse_path_to_omr_image)
					.addContainerGap())
		);
		gl_panel_path_to_omr_image.setVerticalGroup(
			gl_panel_path_to_omr_image.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_path_to_omr_image.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_path_to_omr_image.createParallelGroup(Alignment.BASELINE)
						.addComponent(textfield_path_to_omr_image, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse_path_to_omr_image))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_path_to_omr_image.setLayout(gl_panel_path_to_omr_image);
		
		textfield_path_to_csv = new JTextField();
		textfield_path_to_csv.setEditable(false);
		textfield_path_to_csv.setToolTipText("Select the .csv file output by Form Scanner");
		textfield_path_to_csv.setColumns(10);
		
		btnBrowse_path_to_csv = new JButton("Browse");
		btnBrowse_path_to_csv.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".csv file", "csv");
				chooser.setFileFilter(filter);
				
				File current_working_directory = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(current_working_directory);
				
				int returnVal = chooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					textfield_path_to_csv.setText(chooser.getSelectedFile().getAbsolutePath());
				}
				
				check_inputs();
			}
		});

		GroupLayout gl_panel_path_to_csv = new GroupLayout(panel_path_to_csv);
		gl_panel_path_to_csv.setHorizontalGroup(
			gl_panel_path_to_csv.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_path_to_csv.createSequentialGroup()
					.addContainerGap()
					.addComponent(textfield_path_to_csv, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBrowse_path_to_csv)
					.addContainerGap())
		);
		gl_panel_path_to_csv.setVerticalGroup(
			gl_panel_path_to_csv.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_path_to_csv.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_path_to_csv.createParallelGroup(Alignment.BASELINE)
						.addComponent(textfield_path_to_csv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse_path_to_csv))
					.addContainerGap(68, Short.MAX_VALUE))
		);
		panel_path_to_csv.setLayout(gl_panel_path_to_csv);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_progress, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_path_to_omr_image, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
								.addComponent(panel_path_to_save_eqr_omr, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
								.addComponent(panel_encryption_password, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)))
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_path_to_csv, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)))
					.addGap(5))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_path_to_csv, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_path_to_omr_image, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_path_to_save_eqr_omr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(8)
					.addComponent(panel_encryption_password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(panel_progress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(16))
		);
		
		textfield_path_to_save_eqr_omr = new JTextField();
		textfield_path_to_save_eqr_omr.setToolTipText("Select the path to save the EQR-OMR image");
		textfield_path_to_save_eqr_omr.setEditable(false);
		textfield_path_to_save_eqr_omr.setColumns(10);
		
		btnBrowse_path_to_save_eqr_omr = new JButton("Browse");
		btnBrowse_path_to_save_eqr_omr.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				File current_working_directory = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(current_working_directory);
				
				int returnVal = chooser.showSaveDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					textfield_path_to_save_eqr_omr.setText(chooser.getSelectedFile().getAbsolutePath());
				}
				
				check_inputs();
			}
		});
		GroupLayout gl_panel_path_to_save_eqr_omr = new GroupLayout(panel_path_to_save_eqr_omr);
		gl_panel_path_to_save_eqr_omr.setHorizontalGroup(
			gl_panel_path_to_save_eqr_omr.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_path_to_save_eqr_omr.createSequentialGroup()
					.addContainerGap()
					.addComponent(textfield_path_to_save_eqr_omr, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBrowse_path_to_save_eqr_omr)
					.addContainerGap())
		);
		gl_panel_path_to_save_eqr_omr.setVerticalGroup(
			gl_panel_path_to_save_eqr_omr.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_path_to_save_eqr_omr.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_path_to_save_eqr_omr.createParallelGroup(Alignment.BASELINE)
						.addComponent(textfield_path_to_save_eqr_omr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse_path_to_save_eqr_omr))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_path_to_save_eqr_omr.setLayout(gl_panel_path_to_save_eqr_omr);
		contentPane.setLayout(gl_contentPane);
	}
	
	@SuppressWarnings("deprecation")
	private void check_inputs()
	{
		boolean fields=false;
		boolean password=false;
		
		if(textfield_path_to_csv.getText().length()>0
				&& textfield_path_to_omr_image.getText().length()>0
				&& textfield_path_to_save_eqr_omr.getText().length()>0)
		{
			fields=true;
		}
		else
			fields=false;
			
////////////////////////////////////////////////////////////////////////////////////////////			
		String pwd = pwdfield_encryption_password.getText();
		
		if(pwd.matches(".*[0-9].*"))
		{
			lbl_a_digit.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_a_digit.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
		if(pwd.matches(".*[A-Z].*"))
		{
			lbl_an_uppercase_letter.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_an_uppercase_letter.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
		if(pwd.matches(".*[^a-z-A-Z-0-9].*"))
		{
			lbl_a_special_character.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_a_special_character.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
		if(pwd.length()>=8)
		{
			lbl_min_8_characters.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_min_8_characters.setIcon(new ImageIcon(exam_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
////////////////////////////////////////////////////////////////////////////////////////////		
		if(pwd.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[^A-Z-0-9]).{8,}$"))
		{
			password = true;
		}
		else
			password = false;
		
		if(fields && password)
		{
			btnGo.setEnabled(true);
		}
		else
			btnGo.setEnabled(false);

	}
	
	private void disable_input()
	{
		btnBrowse_path_to_csv.setEnabled(false);
		btnBrowse_path_to_omr_image.setEnabled(false);
		btnBrowse_path_to_save_eqr_omr.setEnabled(false);
		pwdfield_encryption_password.setEnabled(false);
		btnGo.setEnabled(false);
	}
	
	private void enable_input()
	{
		btnBrowse_path_to_csv.setEnabled(true);
		btnBrowse_path_to_omr_image.setEnabled(true);
		btnBrowse_path_to_save_eqr_omr.setEnabled(true);
		pwdfield_encryption_password.setEnabled(true);
		btnGo.setEnabled(true);
	}
}
