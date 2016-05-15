package gui;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
public class dialog_qr_code_generated extends JDialog
{

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
					dialog_qr_code_generated dialog = new dialog_qr_code_generated(null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public dialog_qr_code_generated(BufferedImage qr_image)
	{
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
			
		setResizable(false);
		setTitle("QR Code Generated");
		setBounds(100, 100, 300, 300);
		setLocationRelativeTo(null);

		JLabel lblQr = new JLabel("");
		lblQr.setIcon(new ImageIcon(qr_image));
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(43)
					.addComponent(lblQr)
					.addContainerGap(51, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(108)
					.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(112, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(28)
					.addComponent(lblQr)
					.addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnOk)
					.addGap(9))
		);
		getContentPane().setLayout(groupLayout);
		
		setVisible(true);
	}
}
