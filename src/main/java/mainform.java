import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pro.beam.api.BeamAPI;
import pro.beam.interactive.net.packet.Protocol;
import pro.beam.interactive.net.packet.Protocol.Report.TactileInfo;
import pro.beam.interactive.robot.RobotBuilder;
import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ListSelectionEvent;
import java.awt.Font;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.Box;
import javax.swing.JTextArea;

public class mainform extends JFrame {

	private JPanel contentPane;
	private JPasswordField txtPassword;
	private ArrayList<String> requests;
	private JList listRequests;
	private DefaultListModel activeRequests;
	private webserver web;
	private JLabel lblPasswordrequired;
	private JTextArea txtPromo;
	private Box horizontalBox;
	private JPanel pnlPromoBtns;
	private JButton btnViewOnGithub;
	private JButton btnDonate;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainform frame = new mainform();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void goToURL(String url)
	{
		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		if( desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
			java.net.URI uri;
			try {
				uri = new java.net.URL(url).toURI();
				desktop.browse( uri );
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * Create the frame.
	 * @throws AWTException 
	 */
	public mainform() throws AWTException {
		setTitle("Request Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 424);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		requests = new ArrayList<String>(64);
		
		
		activeRequests = new DefaultListModel();
		web = new webserver(activeRequests);
		Thread t = new Thread(web);
		
		JPanel pnlConnect = new JPanel();
		pnlConnect.setBackground(Color.WHITE);
		contentPane.add(pnlConnect, BorderLayout.NORTH);
		
		lblPasswordrequired = new JLabel("Password:");
		pnlConnect.add(lblPasswordrequired);
		
		txtPassword = new JPasswordField();
		txtPassword.setToolTipText("Password");
		txtPassword.setColumns(10);
		pnlConnect.add(txtPassword);
		
		listRequests = new JList(activeRequests);
		DefaultListCellRenderer renderer =  (DefaultListCellRenderer)listRequests.getCellRenderer();  
		renderer.setHorizontalAlignment(JLabel.RIGHT);
		listRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listRequests.setFont(new Font("SansSerif", Font.PLAIN, 30));
		listRequests.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if(listRequests.getComponentCount() > 0)
				{
					listRequests.setSelectedIndex(0);
				}
				else
				{
					listRequests.setSelectedIndex(-1);
				}
			}
		});
		contentPane.add(listRequests, BorderLayout.CENTER);
		
		txtPromo = new JTextArea();
		txtPromo.setFont(new Font("SansSerif", Font.PLAIN, 13));
		txtPromo.setWrapStyleWord(true);
		txtPromo.setText("Request Manager by CoryZ40 from GitHub (AKA WhoIsWORM)\nFollow me on Twitter @CoryZ40\n"
				+ "Like my software? Consider contributing on GitHub or donating!\nI'd really appreciate it!");//promo
		contentPane.add(txtPromo, BorderLayout.WEST);
		
		//Open file and connect
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			/**
			 * Connect to Beam with provided username and password.
			 */
			public void actionPerformed(ActionEvent arg0) {
				BeamAPI beam = new BeamAPI();
		        try {
		        	//open appdata file
		        	btnConnect.setEnabled(false);
		        	
		        	File f = new File("default.ini");
		        	boolean notauto = (!f.exists() || f.isDirectory());
		        	String username = null;
		        	String chanID = null;
		        	JFileChooser chooser = new JFileChooser();
		        	if(notauto) {
			            chooser.setCurrentDirectory(new java.io.File("."));
			            chooser.setSelectedFile(new File(""));
			            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			            FileNameExtensionFilter defaultext = new FileNameExtensionFilter("Application Data (.ini)","ini");
			            chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text Document (.txt)","txt"));
			            chooser.setFileFilter(defaultext);
		        	}
		            if (!notauto || chooser.showOpenDialog(contentPane.getParent()) == JFileChooser.OPEN_DIALOG) {
		            	if(notauto)
		            		f = chooser.getSelectedFile();
		            	FileInputStream fstream = new FileInputStream(f);
		            	DataInputStream in = new DataInputStream(fstream);
		            	BufferedReader br = new BufferedReader(new InputStreamReader(in));
		            	String strLine;
		            	while ((strLine = br.readLine()) != null){
		            		if(strLine.length() > 0 && strLine.toCharArray()[0] != '#')
		            			if(username == null)
		            				username = strLine;
		            			else if(chanID == null)
		            				chanID = strLine;
		            			else
		            				requests.add(strLine);
		            	}
		            	
		            	//connect to Beam
		            	Robot controller = new Robot();
			            pro.beam.interactive.robot.Robot robot = new RobotBuilder()
			                    .username(username)
			                    .password(new String(txtPassword.getPassword()))
			                    .channel(Integer.parseInt(chanID)).build(beam).get();

			            robot.on(Protocol.Report.class, report -> {
			            	int check = report.getTactileCount();
			            	if(check > 0)
			            	{
			            		List<TactileInfo> l = report.getTactileList();
			            		for(int i=0;i<check;i++)
			            		{
			            			for(int t=0;t<l.get(i).getPressFrequency();t++)
			            			{
			            				System.out.println(l.get(i).getId());
			            				if(l.get(i).getId() < requests.size())
			            				{
			            					activeRequests.addElement(requests.get(l.get(i).getId()));
			            					listRequests.setSelectedIndex(0);
			            				}
			            			}
			            		}
			            	}
			            });
			            pnlConnect.setVisible(false);
			            txtPromo.setVisible(false);
			            pnlPromoBtns.setVisible(false);
			            contentPane.remove(pnlConnect);
			            contentPane.remove(txtPromo);
			            contentPane.remove(pnlPromoBtns);
			            t.start();
		            } else {
		                // do when cancel
		            }
		        } catch (InterruptedException | ExecutionException | AWTException | IOException e) {
		            e.printStackTrace();
		        }
		        finally{
		        	btnConnect.setEnabled(true);
		        }
			}
		});
		pnlConnect.add(btnConnect);
		
		pnlPromoBtns = new JPanel();
		pnlPromoBtns.setBackground(Color.WHITE);
		contentPane.add(pnlPromoBtns, BorderLayout.SOUTH);
		
		btnViewOnGithub = new JButton("View on GitHub");
		btnViewOnGithub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goToURL("https://github.com/CoryZ40");
			}
		});
		pnlPromoBtns.add(btnViewOnGithub);
		
		btnDonate = new JButton("Donate");
		btnDonate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToURL("https://www.paypal.me/Heckie");
			}
		});
		pnlPromoBtns.add(btnDonate);
		
		
		
		
	}

}