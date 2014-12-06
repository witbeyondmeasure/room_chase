import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class RoomChaseUI implements Constants{
	private static JFrame ui = new JFrame("Room Chase!!!");
	private static CardLayout cl = new CardLayout();
	private static JPanel panel = new JPanel(cl);
	private static JDialog[] jd = new JDialog[2];
	private CardLayout front = new CardLayout();
	private JPanel mainpanel = new JPanel();
	private JPanel splash = new JPanel();
	private JPanel chatbox = new JPanel();
	private JPanel waitWindow = new JPanel();
	private JPanel stageWindow = new JPanel(front);
	private JPanel mapPanel = new JPanel();
	private MapPanelGrid[][] mapPanelGrid = new MapPanelGrid[3][3];
	private RoomPanel[] roomPanel = new RoomPanel[4];
	private JLabel label[] = new JLabel[5];
	private JTextArea[] textarea = new JTextArea[2];
	private JScrollPane[] scrollpane = new JScrollPane[2];
	private JButton[] button = new JButton[3];
	private static JTextField textfield = new JTextField();

	public RoomChaseUI(RoomChase rc){
		//splash panel creation setup
		splash.setBackground(Color.black);
		splash.setLayout(null);
		label[0] = new JLabel("Leo Diabordo and Patrick Ursolino Presents . . .");
		label[0].setForeground(Color.white);
		label[0].setFont(label[0].getFont().deriveFont(30.0f));
		label[0].setBounds(100,300,800,100);
		splash.add(label[0]);


		label[1] = new JLabel("There is someone in a room next to you."); //this label will show if the game detects that someone is in a room next to player
		label[2] = new JLabel("Enter name: ");
		label[3] = new JLabel("Name is required!");
		

		waitWindow.setBackground(Color.black);
		waitWindow.setLayout(null);
		label[4] = new JLabel("Waiting for players. . . ");
		label[4].setForeground(Color.white);
		label[4].setFont(label[4].getFont().deriveFont(30.0f));
		waitWindow.add(label[4]);
		label[4].setBounds(100,300,800,100);

		textarea[0] = new JTextArea(30,30);
		textarea[1] = new JTextArea(9,30);
		textarea[0].setEditable(false);
		textarea[0].setLineWrap(true);
		textarea[1].setLineWrap(true);
		
		mainpanel.setLayout(null);
		mainpanel.setBackground(Color.black);
		
		scrollpane[0] = new JScrollPane(textarea[0],JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane[1] = new JScrollPane(textarea[1],JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		button[0] = new JButton("Send");
		button[1] = new JButton("OK");
		button[2] = new JButton("OK");

		jd[0] = new JDialog(ui,true);
		jd[1] = new JDialog(ui,true);

		jd[0].setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		Container[] c = new Container[2];
		JPanel[] p = new JPanel[2];

		c[0] = jd[0].getContentPane();
		p[0] = new JPanel();
		c[0].setLayout(new BorderLayout());

		c[0].add(label[2],BorderLayout.NORTH);
		c[0].add(textfield,BorderLayout.CENTER);
		p[0].setLayout(new FlowLayout());
		p[0].add(button[1]);
		c[0].add(p[0],BorderLayout.SOUTH);

		jd[0].pack();
		jd[0].setLocationRelativeTo(null);

		jd[1].setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		c[1] = jd[1].getContentPane();
		p[1] = new JPanel();
		c[1].setLayout(new BorderLayout());

		c[1].add(label[3],BorderLayout.CENTER);
		p[1].setLayout(new FlowLayout());
		p[1].add(button[2]);
		c[1].add(p[1],BorderLayout.SOUTH);

		jd[1].pack();
		jd[1].setLocationRelativeTo(null);

		chatbox.setLayout(null);
		chatbox.add(scrollpane[0]);
		chatbox.add(scrollpane[1]);
		chatbox.add(button[0]);

		scrollpane[0].setBounds(0,200,200,250);
		scrollpane[1].setBounds(0,460,200,150);
		button[0].setBounds(100,620,90,30);

		mainpanel.add(chatbox);
		chatbox.setBounds(820,0,200,670);

		mapPanel =  new JPanel(new GridLayout(3,3,5,5));
		initMap();
		chatbox.add(mapPanel);
		mapPanel.setBounds(5,0,190,200);


		panel.add(splash,SPLASH);
		panel.add(mainpanel,MAINPANEL);

		roomPanel[0] = new RoomPanel(new ImageIcon("Images/no_door.jpg").getImage());
		roomPanel[1] = new RoomPanel(new ImageIcon("Images/riddledoor.jpg").getImage());
		roomPanel[2] = new RoomPanel(new ImageIcon("Images/keydoor.jpg").getImage());
		roomPanel[3] = new RoomPanel(new ImageIcon("Images/weightdoor.jpg").getImage());
		stageWindow.add(waitWindow,WAITWINDOW);
		stageWindow.add(roomPanel[0], NODOOR);
		stageWindow.add(roomPanel[1], RIDDLEDOOR);
		stageWindow.add(roomPanel[2], KEYDOOR);
		stageWindow.add(roomPanel[3], WEIGHTDOOR);

		stageWindow.setFocusable(true);

		mainpanel.add(stageWindow);
		stageWindow.setBounds(0,0,800,670);

		ui.add(panel);

		ui.pack();
		ui.setVisible(true);
		ui.setSize(1024,700);
		ui.setLocationRelativeTo(null);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		rc.setUI(this);
		rc.addTextAreaKeyListener(textarea[1]);
		rc.addActionListenerToButtons(button);
		rc.addWindowListenerToJDialog(jd[0]);
	}

	private void initMap(){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				mapPanelGrid[i][j] = new MapPanelGrid();
				mapPanel.add(mapPanelGrid[i][j]);
			}
		}
	}

	public JPanel getStageWindow(){
		return stageWindow;
	}

	public RoomPanel[] getRoomPanel(){
		return roomPanel;
	}

	public void setGameWindowEnabled(boolean b){
		roomPanel[0].setEnabled(b);
		roomPanel[1].setEnabled(b);
	}

	public void switchWindow(String id){
		front.show(stageWindow,id);
	}

	public void showNameWarning(){
		jd[1].setVisible(true);
	}

	public void hideNameWarning(){
		jd[1].setVisible(false);
	}

	public String getInputName(){
		return textfield.getText();
	}

	public JButton[] getButtons(){
		return button;
	}

	public String getTextFromChat(){
		return textarea[1].getText();
	}

	public void setBlank(){
		textarea[1].setText("");
	}

	public void closeJDialog(){
		jd[0].setVisible(false);
	}

	public void setPlayerLocationInMap(int x, int y, String color, int face){
		switch(face){
			case 1:
				mapPanelGrid[x][y].setX(25);
				mapPanelGrid[x][y].setY(0);	
				break;
			case 2:
				mapPanelGrid[x][y].setX(45);
				mapPanelGrid[x][y].setY(25);	
				break;
			case 3:
				mapPanelGrid[x][y].setX(25);
				mapPanelGrid[x][y].setY(45);	
				break;
			case 4:
				mapPanelGrid[x][y].setX(0);
				mapPanelGrid[x][y].setY(20);	
				break;
		}
		mapPanelGrid[x][y].setColor(color);
		mapPanelGrid[x][y].repaint();
	}

	public void clearMapGrid(int x, int y){
		mapPanelGrid[x][y].setX(-1);
		mapPanelGrid[x][y].setY(-1);
		mapPanelGrid[x][y].repaint();
	}

	public void displayMessage(String msg){
		textarea[0].setEditable(true);
		textarea[0].setText(textarea[0].getText()+"\n"+msg);
		textarea[0].setEditable(false);
	}

	public static void main(String[] args){
		System.out.println(args[0]);
		RoomChase rc = new RoomChase(args[0]);
		RoomChaseUI ui = new RoomChaseUI(rc);
		try{
			Thread.sleep(3000);
			cl.show(panel,MAINPANEL);
			textfield.setText("Player");
			jd[0].setVisible(true);
		}
		catch(Exception e){

		}
	}
}