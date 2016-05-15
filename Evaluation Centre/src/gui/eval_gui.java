package gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import com.google.zxing.NotFoundException;

import core_modules.cryptography_module.Decryptor;
import core_modules.data_extractor_module.CSVFileInvalidException;
import core_modules.data_extractor_module.DataExtractor;
import core_modules.data_extractor_module.ExtractedData;
import core_modules.qr_code_module.QRScanner;
import core_modules.validation_module.Validator;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class eval_gui extends JFrame
{
	private JPanel contentPane;
	private JPanel panel_path_to_csv;
	private JTextField textfield_path_to_csv;
	private JButton btnBrowse_path_to_csv;
	private JTextField textfield_path_to_eqr_omr_image;
	private JPanel panel_path_to_eqr_omr_image;
	private JButton btnBrowse_path_to_eqr_omr_image;
	private JPanel panel_decryption_password;
	private JPasswordField pwdfield_decryption_password;
	private JButton btnGo;
	private JPanel panel_progress;
	private JLabel lbl_image_uploaded;
	private JLabel lbl_omr_data_extracted;
	private JLabel lbl_data_decrypted;
	private JLabel lbl_see_details_3;
	private JLabel lbl_see_details_2;
	private JLabel lbl_see_details_1;
	private JLabel lbl_see_details_4;
	private JLabel lbl_see_details_5;
	private JLabel lbl_validation;
	private JLabel lbl_qr_code_scanned;
	private JLabel lbl_a_digit;
	private JLabel lbl_an_uppercase_letter;
	private JLabel lbl_a_special_character;
	private JLabel lbl_min_8_characters;
	
	private String result = null;
	private JProgressBar progress_bar;
	private JMenuBar menu_bar;
	private JMenu menu_file;
	private JMenu menu_tools;
	private JMenuItem menu_item_exit;
	private JMenuItem menu_item_open_console;
	
	String cipher_text = null;
	String decrypted_text = null;
	ExtractedData vital_data_from_omr = null;
	ExtractedData vital_data_from_qr = null;
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
					eval_gui frame = new eval_gui();
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
	public eval_gui()
	{
		console = new console();
		
		setMinimumSize(new Dimension(500, 510));
		setTitle("Evaluation Centre");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 500, 510);
		setLocationRelativeTo(null);
		
		menu_bar = new JMenuBar();
		setJMenuBar(menu_bar);
		
		menu_file = new JMenu("File");
		menu_bar.add(menu_file);
		
		menu_item_exit = new JMenuItem("Exit");
		menu_item_exit.setToolTipText("Exit the application");
		menu_item_exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		menu_item_exit.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/power-icon_16.png")));
		menu_file.add(menu_item_exit);
		
		menu_tools = new JMenu("Tools");
		menu_bar.add(menu_tools);
		
		menu_item_open_console = new JMenuItem("Open Console");
		menu_item_open_console.setToolTipText("Open the console for verbose logging");
		menu_item_open_console.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				console.show_console(true);
			}
		});
		menu_item_open_console.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/console-icon_12.png")));
		menu_tools.add(menu_item_open_console);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		panel_path_to_csv = new JPanel();
		panel_path_to_csv.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PATH TO CSV FILE", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		textfield_path_to_csv = new JTextField();
		textfield_path_to_csv.setToolTipText("Select the .csv file output by Form Scanner");
		textfield_path_to_csv.setEditable(false);
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
				.addGap(0, 459, Short.MAX_VALUE)
				.addGroup(gl_panel_path_to_csv.createSequentialGroup()
					.addContainerGap()
					.addComponent(textfield_path_to_csv, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBrowse_path_to_csv)
					.addContainerGap())
		);
		gl_panel_path_to_csv.setVerticalGroup(
			gl_panel_path_to_csv.createParallelGroup(Alignment.LEADING)
				.addGap(0, 68, Short.MAX_VALUE)
				.addGroup(gl_panel_path_to_csv.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_path_to_csv.createParallelGroup(Alignment.BASELINE)
						.addComponent(textfield_path_to_csv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse_path_to_csv))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_path_to_csv.setLayout(gl_panel_path_to_csv);
		
		panel_path_to_eqr_omr_image = new JPanel();
		panel_path_to_eqr_omr_image.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PATH TO EQR OMR IMAGE", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		textfield_path_to_eqr_omr_image = new JTextField();
		textfield_path_to_eqr_omr_image.setToolTipText("Select your EQR-OMR image file");
		textfield_path_to_eqr_omr_image.setEditable(false);
		textfield_path_to_eqr_omr_image.setColumns(10);
		
		btnBrowse_path_to_eqr_omr_image = new JButton("Browse");
		btnBrowse_path_to_eqr_omr_image.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".png file", "png");
				chooser.setFileFilter(filter);
				
				File current_working_directory = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(current_working_directory);
				
				int returnVal = chooser.showOpenDialog(getParent());
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					textfield_path_to_eqr_omr_image.setText(chooser.getSelectedFile().getAbsolutePath());
				}
				
				check_inputs();
			}
		});
		GroupLayout gl_panel_path_to_eqr_omr_image = new GroupLayout(panel_path_to_eqr_omr_image);
		gl_panel_path_to_eqr_omr_image.setHorizontalGroup(
			gl_panel_path_to_eqr_omr_image.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 459, Short.MAX_VALUE)
				.addGroup(gl_panel_path_to_eqr_omr_image.createSequentialGroup()
					.addContainerGap()
					.addComponent(textfield_path_to_eqr_omr_image, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnBrowse_path_to_eqr_omr_image)
					.addContainerGap())
		);
		gl_panel_path_to_eqr_omr_image.setVerticalGroup(
			gl_panel_path_to_eqr_omr_image.createParallelGroup(Alignment.LEADING)
				.addGap(0, 68, Short.MAX_VALUE)
				.addGroup(gl_panel_path_to_eqr_omr_image.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_path_to_eqr_omr_image.createParallelGroup(Alignment.BASELINE)
						.addComponent(textfield_path_to_eqr_omr_image, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse_path_to_eqr_omr_image))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_path_to_eqr_omr_image.setLayout(gl_panel_path_to_eqr_omr_image);
		
		panel_decryption_password = new JPanel();
		panel_decryption_password.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "DECRYPTION PASSWORD", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		pwdfield_decryption_password = new JPasswordField();
		pwdfield_decryption_password.addFocusListener(new FocusAdapter()
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
		pwdfield_decryption_password.getDocument().addDocumentListener(new DocumentListener()
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
		pwdfield_decryption_password.setToolTipText("Specify the decryption password");
		
		btnGo = new JButton("Go");
		btnGo.setEnabled(false);
		btnGo.addActionListener(new ActionListener()
		{
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e)
			{
				if(textfield_path_to_csv.getText().length()>=8 
						&& textfield_path_to_eqr_omr_image.getText().length()>=8 
						&& pwdfield_decryption_password.getText().length()>=8)
				{
					SwingWorker<String, Integer> worker = new SwingWorker<String, Integer>()
					{
						@Override
						protected String doInBackground() throws InterruptedException
						{
							disable_input();
							
							exec_progress = 0;
							publish(exec_progress);//0
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//IMAGE UPLOADED
////////////////////////////////////////////////////////////////////////////////////////////
							publish(++exec_progress);//1
							Thread.sleep(200);
							
							if(!Files.exists(Paths.get(textfield_path_to_eqr_omr_image.getText())))
							{
								return "OMR Image not Found.";
							}
							
							publish(++exec_progress);//2
							Thread.sleep(200);

////////////////////////////////////////////////////////////////////////////////////////////
//QR CODE SCANNED
////////////////////////////////////////////////////////////////////////////////////////////
							publish(++exec_progress);//3
							Thread.sleep(200);
							
							try
							{
								cipher_text = new QRScanner().read_qr_code(textfield_path_to_eqr_omr_image.getText());
							}
							catch (FileNotFoundException e)
							{
								e.printStackTrace();
								return "The image was not found in path";
							}
							catch (NotFoundException e)
							{
								e.printStackTrace();
								return "The input image doesn't seem to contain a QR code. Please check again";								
							}
							catch (Exception e)
							{
								e.printStackTrace();
								return "Error in reading the EQR-OMR image";
							}
							
							publish(++exec_progress);//4
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//DATA DECRYPTED
////////////////////////////////////////////////////////////////////////////////////////////			
							publish(++exec_progress);//5
							Thread.sleep(200);
							
							try
							{
								decrypted_text = new Decryptor().decrypt(pwdfield_decryption_password.getText(), cipher_text);
							}
							catch (EncryptionOperationNotPossibleException e)
							{
								e.printStackTrace();
								return "Decryption Failed. Wrong Password!";
							}
							
							publish(++exec_progress);//6
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//OMR DATA EXTRACTED
////////////////////////////////////////////////////////////////////////////////////////////							
							DataExtractor de = new DataExtractor(textfield_path_to_csv.getText());
							publish(++exec_progress);//7
							Thread.sleep(200);
							
							try
							{
								vital_data_from_omr = de.extract();
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
							
							publish(++exec_progress);//8
							Thread.sleep(200);
////////////////////////////////////////////////////////////////////////////////////////////
//VALIDATION
////////////////////////////////////////////////////////////////////////////////////////////
							vital_data_from_qr = new ExtractedData(decrypted_text);
							publish(++exec_progress);//9
							Thread.sleep(200);
							
							boolean valid = Validator.verify(vital_data_from_omr,vital_data_from_qr);
							
							if(valid)
							{
								System.out.println("OMR is valid.");
								result = "OMR is valid.";
							}
							else
							{
								System.out.println("OMR file"+ textfield_path_to_eqr_omr_image.getText() +"is tampered");
								result = "OMR is tampered";
							}
							
							publish(++exec_progress);//10
							Thread.sleep(200);
								
							return result;
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
							if(status.equals("OMR is valid."))
							{
								JOptionPane.showMessageDialog(null, status, "Message", JOptionPane.INFORMATION_MESSAGE);
							}
							else
							{
								switch(exec_progress)
								{
								case 1:
								case 2: lbl_image_uploaded.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 3:
								case 4: lbl_qr_code_scanned.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 5:
								case 6: lbl_data_decrypted.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 7:
								case 8: lbl_omr_data_extracted.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
										break;
								case 9:
								case 10: lbl_validation.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/error-icon_12.png")));
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
								lbl_qr_code_scanned.setIcon(null);
								lbl_data_decrypted.setIcon(null);
								lbl_omr_data_extracted.setIcon(null);
								lbl_validation.setIcon(null);
								lbl_see_details_1.setEnabled(false);
								lbl_see_details_2.setEnabled(false);
								lbl_see_details_3.setEnabled(false);
								lbl_see_details_4.setEnabled(false);
								lbl_see_details_5.setEnabled(false);
								progress_bar.setValue(most_recent_value);
								break;
							case 1:
								lbl_image_uploaded.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 2:
								lbl_image_uploaded.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_1.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 3:
								lbl_qr_code_scanned.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 4:
								lbl_qr_code_scanned.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_2.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 5:
								lbl_data_decrypted.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 6:
								lbl_data_decrypted.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_3.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 7:
								lbl_omr_data_extracted.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 8:
								lbl_omr_data_extracted.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
								lbl_see_details_4.setEnabled(true);
								progress_bar.setValue(most_recent_value);
								break;
							case 9:
								lbl_validation.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/processing-icon_12.png")));
								progress_bar.setValue(most_recent_value);
								break;
							case 10:
								lbl_validation.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
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
		lbl_a_digit.setVisible(false);
		lbl_a_digit.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		
		lbl_an_uppercase_letter = new JLabel("An Uppercase Letter");
		lbl_an_uppercase_letter.setVisible(false);
		lbl_an_uppercase_letter.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		
		lbl_a_special_character = new JLabel("A Special Character");
		lbl_a_special_character.setVisible(false);
		lbl_a_special_character.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		
		lbl_min_8_characters = new JLabel("Min. 8 Characters");
		lbl_min_8_characters.setVisible(false);
		lbl_min_8_characters.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		GroupLayout gl_panel_decryption_password = new GroupLayout(panel_decryption_password);
		gl_panel_decryption_password.setHorizontalGroup(
			gl_panel_decryption_password.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_decryption_password.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_decryption_password.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_decryption_password.createSequentialGroup()
							.addComponent(pwdfield_decryption_password, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnGo))
						.addGroup(gl_panel_decryption_password.createSequentialGroup()
							.addGroup(gl_panel_decryption_password.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(lbl_a_digit, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbl_an_uppercase_letter, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_decryption_password.createParallelGroup(Alignment.LEADING)
								.addComponent(lbl_min_8_characters)
								.addComponent(lbl_a_special_character))))
					.addContainerGap())
		);
		gl_panel_decryption_password.setVerticalGroup(
			gl_panel_decryption_password.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_decryption_password.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_decryption_password.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnGo)
						.addComponent(pwdfield_decryption_password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_decryption_password.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_a_digit)
						.addComponent(lbl_a_special_character))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panel_decryption_password.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_an_uppercase_letter)
						.addComponent(lbl_min_8_characters))
					.addContainerGap())
		);
		panel_decryption_password.setLayout(gl_panel_decryption_password);
		
		panel_progress = new JPanel();
		panel_progress.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PROGRESS", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		lbl_image_uploaded = new JLabel("Image Uploaded");
		
		lbl_omr_data_extracted = new JLabel("OMR Data Extracted");
		
		lbl_data_decrypted = new JLabel("Data Decrypted");
		
		lbl_see_details_3 = new JLabel("See Details");
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
					JOptionPane.showMessageDialog(null, decrypted_text, "Data Decrypted", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		lbl_see_details_3.setForeground(Color.BLUE);
		lbl_see_details_3.setEnabled(false);
		
		lbl_see_details_2 = new JLabel("See Details");
		lbl_see_details_2.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if(lbl_see_details_2.isEnabled())
				{
					lbl_see_details_2.setText("<html><u>See Details</u></html>");
				}
			}
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
					JOptionPane.showMessageDialog(null, cipher_text, "QR Code Scanned", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		lbl_see_details_2.setForeground(Color.BLUE);
		lbl_see_details_2.setEnabled(false);
		
		lbl_see_details_1 = new JLabel("See Details");
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
			public void mouseClicked(MouseEvent arg0)
			{
				if(lbl_see_details_1.isEnabled())
				{
					new dialog_display_image(textfield_path_to_eqr_omr_image.getText(), "Image Uploaded");
				}
			}
		});
		
		lbl_see_details_1.setForeground(Color.BLUE);
		lbl_see_details_1.setEnabled(false);
		
		lbl_qr_code_scanned = new JLabel("QR Code Scanned");
		
		lbl_validation = new JLabel("Validation");
		
		lbl_see_details_4 = new JLabel("See Details");
		lbl_see_details_4.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				if(lbl_see_details_4.isEnabled())
				{
					lbl_see_details_4.setText("<html><u>See Details</u></html>");	
				}
			}
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
					JOptionPane.showMessageDialog(null, vital_data_from_omr, "OMR Data Extracted", JOptionPane.INFORMATION_MESSAGE);
					
				}
			}
		});
		lbl_see_details_4.setForeground(Color.BLUE);
		lbl_see_details_4.setEnabled(false);
		
		lbl_see_details_5 = new JLabel("See Details");
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
					JOptionPane.showMessageDialog(null, result, "Validation", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		lbl_see_details_5.setForeground(Color.BLUE);
		lbl_see_details_5.setEnabled(false);
		
		progress_bar = new JProgressBar();
		progress_bar.setStringPainted(true);
		progress_bar.setMaximum(10);
		GroupLayout gl_panel_progress = new GroupLayout(panel_progress);
		gl_panel_progress.setHorizontalGroup(
			gl_panel_progress.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_progress.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_progress.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_progress.createSequentialGroup()
							.addGroup(gl_panel_progress.createParallelGroup(Alignment.TRAILING)
								.addComponent(lbl_validation, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(lbl_data_decrypted, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(lbl_qr_code_scanned, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(lbl_image_uploaded, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
								.addComponent(lbl_omr_data_extracted, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
							.addGap(238)
							.addGroup(gl_panel_progress.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lbl_see_details_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbl_see_details_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbl_see_details_4, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbl_see_details_5, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lbl_see_details_1, Alignment.TRAILING)))
						.addComponent(progress_bar, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
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
							.addComponent(lbl_qr_code_scanned)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_data_decrypted)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_omr_data_extracted)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_validation))
						.addGroup(gl_panel_progress.createSequentialGroup()
							.addComponent(lbl_see_details_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_2)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_3)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_4)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_see_details_5)))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(progress_bar, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
		);
		panel_progress.setLayout(gl_panel_progress);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_progress, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
						.addComponent(panel_decryption_password, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
						.addComponent(panel_path_to_eqr_omr_image, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
						.addComponent(panel_path_to_csv, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_path_to_csv, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_path_to_eqr_omr_image, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_decryption_password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_progress, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(87, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	@SuppressWarnings("deprecation")
	private void check_inputs()
	{
		boolean fields=false;
		boolean password=false;
		
		if(textfield_path_to_csv.getText().length()>0
				&& textfield_path_to_eqr_omr_image.getText().length()>0)
		{
			fields=true;
		}
		else
			fields=false;
			
////////////////////////////////////////////////////////////////////////////////////////////			
		String pwd = pwdfield_decryption_password.getText();
		
		if(pwd.matches(".*[0-9].*"))
		{
			lbl_a_digit.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_a_digit.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
		if(pwd.matches(".*[A-Z].*"))
		{
			lbl_an_uppercase_letter.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_an_uppercase_letter.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
		if(pwd.matches(".*[^a-z-A-Z-0-9].*"))
		{
			lbl_a_special_character.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_a_special_character.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
		}
		if(pwd.length()>=8)
		{
			lbl_min_8_characters.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/success-icon_12.png")));
		}
		else
		{
			lbl_min_8_characters.setIcon(new ImageIcon(eval_gui.class.getResource("/gui/gui_resources/alert-icon_12.png")));
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
		btnBrowse_path_to_eqr_omr_image.setEnabled(false);
		pwdfield_decryption_password.setEnabled(false);
		btnGo.setEnabled(false);
	}
	
	private void enable_input()
	{
		btnBrowse_path_to_csv.setEnabled(true);
		btnBrowse_path_to_eqr_omr_image.setEnabled(true);
		pwdfield_decryption_password.setEnabled(true);
		btnGo.setEnabled(true);
	}
}
