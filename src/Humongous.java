
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class Humongous {

	private JFrame frame;
	private JPanel panel;
	private Container searchPanel;
	private Container resultPanel;
	
	private final String SEARCH = "SearchPanel";
	private final String RESULT = "ResultPanel";
	private JButton backButton;
	private JButton searchButton;
	private JButton saveButton;
	private JTextField searchField;
	private JLabel searchLabel;
	private JLabel statusLabel;
	private JScrollPane scroll;
	private JTextPane resultPane;
	
	private Query query;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Humongous window = new Humongous();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Humongous() {
		query = new Query();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Search Engine");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(450, 300));
		frame.setMaximumSize(new Dimension(1350, 900));
		frame.getContentPane().setLayout(new BorderLayout());
		
		addComponentToPane(frame.getContentPane());
		
		frame.pack();
	}
	
	private void addComponentToPane(Container pane)
	{
		CardLayout cardLayout = new CardLayout();
		panel = new JPanel(cardLayout);
		
		initSearchPanel();
		initResultPanel();
		
		pane.add(panel, BorderLayout.CENTER);
	}
	
	private void transformToResultView()
	{
		String q = searchField.getText().toLowerCase();
		if(!q.isEmpty())
		{
			System.out.println(q);
			statusLabel.setVisible(true);
			frame.validate();
			frame.repaint();
			query.query(q);
			resultPane.setText(query.getResultDisplayString());
			statusLabel.setVisible(false);
			frame.validate();
			frame.repaint();
			frame.setBounds(100, 100, 900, 600);
			((CardLayout) panel.getLayout()).show(panel, RESULT);
		}
	}
	
	private void initSearchPanel()
	{
		searchPanel = new JPanel();
		SpringLayout springLayout = new SpringLayout();
		searchPanel.setLayout(springLayout);
		searchPanel.setBounds(100, 100, 450, 300);
		
		searchField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, searchField, 28, SpringLayout.WEST, searchPanel);
		springLayout.putConstraint(SpringLayout.EAST, searchField, -110, SpringLayout.EAST, searchPanel);
		searchField.setToolTipText("Enter search query");
		springLayout.putConstraint(SpringLayout.SOUTH, searchField, -86, SpringLayout.SOUTH, searchPanel);
		searchField.addKeyListener(new KeyListener() 
		{
			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER) 
				{
					transformToResultView();
				}
			}
			public void keyTyped(KeyEvent e){};
			public void keyPressed(KeyEvent e){};
		});
		searchPanel.add(searchField);
		searchField.setColumns(10);
		
		searchButton = new JButton("Go!");
		springLayout.putConstraint(SpringLayout.NORTH, searchButton, 1, SpringLayout.NORTH, searchField);
		springLayout.putConstraint(SpringLayout.WEST, searchButton, 13, SpringLayout.EAST, searchField);
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				transformToResultView();
//				String q = searchField.getText();
//				if(!q.isEmpty())
//				{
//					System.out.println(q);
//					
//					query.query(q);
//					resultPane.setText(query.getResultString());
//					frame.setBounds(100, 100, 900, 600);
//					((CardLayout) panel.getLayout()).show(panel, RESULT);
//				}
			}
		});
		searchPanel.add(searchButton);
		
		searchLabel = new JLabel("humongous!");
		searchLabel.setIcon(new ImageIcon(System.getProperty("user.dir") + File.separator + "src" + File.separator + "www.png"));
		springLayout.putConstraint(SpringLayout.NORTH, searchLabel, 43, SpringLayout.NORTH, searchPanel);
		springLayout.putConstraint(SpringLayout.WEST, searchLabel, 122, SpringLayout.WEST, searchPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, searchLabel, -45, SpringLayout.NORTH, searchField);
		springLayout.putConstraint(SpringLayout.EAST, searchLabel, 12, SpringLayout.EAST, searchField);
		springLayout.putConstraint(SpringLayout.NORTH, searchLabel, 40, SpringLayout.NORTH, searchPanel);
		searchLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		searchLabel.setHorizontalAlignment(SwingConstants.CENTER);
		searchPanel.add(searchLabel);
		
		panel.add(searchPanel, SEARCH);
		
		statusLabel = new JLabel("Fetching...");
		statusLabel.setBackground(new Color(240, 128, 128));
		springLayout.putConstraint(SpringLayout.NORTH, statusLabel, 23, SpringLayout.SOUTH, searchField);
		springLayout.putConstraint(SpringLayout.WEST, statusLabel, 0, SpringLayout.WEST, searchField);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVisible(false);
		searchPanel.add(statusLabel);
	}
	
	private void initResultPanel()
	{
		resultPanel = new JPanel();
		SpringLayout springLayout = new SpringLayout();
		resultPanel.setLayout(springLayout);
		searchPanel.setBounds(100, 100, 900, 600);
		scroll = new JScrollPane (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		springLayout.putConstraint(SpringLayout.WEST, scroll, 24, SpringLayout.WEST, resultPanel);
		springLayout.putConstraint(SpringLayout.NORTH, scroll, 42, SpringLayout.NORTH, resultPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, scroll, -20, SpringLayout.SOUTH, resultPanel);
		springLayout.putConstraint(SpringLayout.EAST, scroll, -24, SpringLayout.EAST, resultPanel);
		resultPanel.add(scroll);
		
		saveButton = new JButton("Save");
		springLayout.putConstraint(SpringLayout.SOUTH, saveButton, -6, SpringLayout.NORTH, scroll);
		springLayout.putConstraint(SpringLayout.EAST, saveButton, 0, SpringLayout.EAST, scroll);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					File file = new File(System.getProperty("user.dir") + File.separator + "humongous");
					if(!file.exists())
						file.mkdir();
					file = new File(System.getProperty("user.dir") + File.separator
							+ "humongous" + File.separator + searchField.getText() + ".txt");
					PrintWriter pw = new PrintWriter(file);
					pw.write(searchField.getText());
					pw.println();
					pw.println();
					pw.write(query.getResultString());
					pw.flush();
					pw.close();
				} 
				catch (FileNotFoundException e1) 
				{
					e1.printStackTrace();
				}
			}
		});
		resultPanel.add(saveButton);
		
		backButton = new JButton("Back");
		springLayout.putConstraint(SpringLayout.SOUTH, backButton, -6, SpringLayout.NORTH, scroll);
		
		resultPane = new JTextPane();
		resultPane.setContentType("text/html");
		resultPane.setEditable(false);
		resultPane.addHyperlinkListener(new HyperlinkListener() 
		{
		    public void hyperlinkUpdate(HyperlinkEvent e) 
		    {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
		        {
		        	if(Desktop.isDesktopSupported()) {
		        	    try
						{
							Desktop.getDesktop().browse(e.getURL().toURI());
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
						catch (URISyntaxException e1)
						{
							e1.printStackTrace();
						}
		        	}
		        }
		    }
		});
		scroll.setViewportView(resultPane);
		springLayout.putConstraint(SpringLayout.EAST, backButton, -6, SpringLayout.WEST, saveButton);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				searchField.setText("");
				query = new Query();
				frame.setBounds(100, 100, 450, 300);
				((CardLayout) panel.getLayout()).show(panel, SEARCH);
			}
		});
		resultPanel.add(backButton);
		
		panel.add(resultPanel, RESULT);
	}
	
	
	private void getText()
	{
	    try 
	    {
			Scanner scanner = new Scanner(new File("t.txt"));
			String s = new String();
			while(scanner.hasNextLine())
			    s += "<p>" + scanner.nextLine() + "</p>";
			scanner.close();
			resultPane.setText(s);
	    } 
	    catch (FileNotFoundException e) 
	    {
	    	e.printStackTrace();
	    }
	}
}
