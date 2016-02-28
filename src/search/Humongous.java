package search;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;
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
	private JScrollPane scroll;
	private JTextPane resultPane;

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
		searchPanel.add(searchField);
		searchField.setColumns(10);
		
		searchButton = new JButton("Go!");
		springLayout.putConstraint(SpringLayout.NORTH, searchButton, 1, SpringLayout.NORTH, searchField);
		springLayout.putConstraint(SpringLayout.WEST, searchButton, 13, SpringLayout.EAST, searchField);
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				String query = searchField.getText();
				if(!query.isEmpty())
				{
					System.out.println(searchField.getText());
					getText(); 
					frame.setBounds(100, 100, 900, 600);
					((CardLayout) panel.getLayout()).show(panel, RESULT);
				}
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
					PrintWriter pw = new PrintWriter(new File(System.getProperty("user.dir") + File.separator
									+ searchField.getText() + ".txt"));
					pw.write(resultPane.getText());
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
		scroll.setViewportView(resultPane);
		springLayout.putConstraint(SpringLayout.EAST, backButton, -6, SpringLayout.WEST, saveButton);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				searchField.setText("");
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
