import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class RoomChase implements ActionListener, KeyListener, MouseListener, Constants{
	private DatagramSocket clientSocket;
	private ArrayList<Player> players = new ArrayList<Player>();
	private RoomChaseUI ui;
	private InetAddress ipa;
	private byte[] sendData = new byte[256];
	private String sentence="";
	private ChatBox chatbox;
	private int playernum=0;
	private String name;
	private int gameStage;
	private Room[][] rooms = new Room[3][3];
	private int face=1;
	private String doorPosition;
	public RoomChase(String serverIP){
		gameStage = WAITING_FOR_PLAYERS;
		try{
			clientSocket = new DatagramSocket();
			ipa = InetAddress.getByName(serverIP.trim());
			chatbox = new ChatBox(ipa,clientSocket,this);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

	public void initRooms(){
		String[] roomDoors = doorPosition.split("##");
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				String doorType[] = roomDoors[(i*3)+j].split(";;");
				rooms[i][j]= new Room(Integer.parseInt(doorType[0]),Integer.parseInt(doorType[1]),Integer.parseInt(doorType[2]),Integer.parseInt(doorType[3]), new Point(i,j));
			}
		}
	}

	public void startGame(){
		addGameKeyListener(ui.getRoomPanel());
		addGameMouseListener(ui.getRoomPanel());
		ui.switchWindow(RIDDLEDOOR);
		for(int i=0;i<playernum;i++){
			ui.setPlayerLocationInMap(players.get(i).getLocationX(),players.get(i).getLocationY(),players.get(i).getColor(),players.get(i).getFace());
		}
	}

	public void connect(String name){
		try{
			sentence += "CONNECT##"+name;
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipa, 4445);
			clientSocket.send(sendPacket);
			sentence = "";
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateFace(){
		try{
			sentence += "UPDATEFACE##"+players.get(0).getName()+"##"+players.get(0).getLocationX()+";;"+players.get(0).getLocationY()+"##"+players.get(0).getColor()+"##"+face;
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipa, 4445);
			clientSocket.send(sendPacket);
			sentence = "";
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateRoom(){
		if(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].checkOpen(face)){
			int x=rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getFacingX();
			int y=rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getFacingY();
			players.get(0).setLocation(new Point(x,y));
			try{
				sentence += "UPDATEROOM##"+players.get(0).getName()+"##"+players.get(0).getLocationX()+";;"+players.get(0).getLocationY()+"##"+players.get(0).getColor()+"##"+face;
				sendData = sentence.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipa, 4445);
				clientSocket.send(sendPacket);
				sentence = "";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public int searchPlayerByName(String name){
		for(int i=0;i<playernum;i++){
			if(players.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}

	public void updateFaceInMap(String name, Point p, String color, int face){
		int temp = searchPlayerByName(name);
		players.get(temp).setLocation(p);
		players.get(temp).setColor(color);
		players.get(temp).setFace(face);
		ui.clearMapGrid((int)p.getX(),(int)p.getY());
		ui.setPlayerLocationInMap(players.get(temp).getLocationX(),players.get(temp).getLocationY(),players.get(temp).getColor(),players.get(temp).getFace());
	}

	public void updateRoomInMap(String name, Point p, String color, int face, Point oldp){
		int temp = searchPlayerByName(name);
		players.get(temp).setLocation(p);
		players.get(temp).setColor(color);
		players.get(temp).setFace(face);
		ui.clearMapGrid((int)oldp.getX(),(int)oldp.getY());
		ui.clearMapGrid((int)p.getX(),(int)p.getY());
		ui.setPlayerLocationInMap(players.get(temp).getLocationX(),players.get(temp).getLocationY(),players.get(temp).getColor(),players.get(temp).getFace());
	}

	public void addWindowListenerToJDialog(JDialog jd){
		jd.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				ui.showNameWarning();
			}
		});
	}

	public void addActionListenerToButtons(JButton[] btns){
		btns[0].addActionListener(this);
		btns[1].addActionListener(this);
		btns[2].addActionListener(this);
	}

	public void addTextAreaKeyListener(JTextArea ta){
		ta.addKeyListener(this);
	}

	public int getPlayerNum(){
		return playernum;
	}

	public void setDoorPosition(String doorPosition){
		this.doorPosition = doorPosition;
		initRooms();
	}

	public void setPlayerNum(int playernum){
		this.playernum = playernum;
	}

	public void setUI(RoomChaseUI ui){
		this.ui=ui;
	}

	public void displayMessage(String msg){
		ui.displayMessage(msg);
	}

	public void setLocation(int x, int y){
		players.get(0).setLocation(new Point(x,y));
		
	}

	public void setColor(String color){
		players.get(0).setColor(color);
	}

	public void setName(String name){
		this.name = name;
	}

	public void addPlayer(Player p){
		players.add(p);
		playernum = players.size();
		
	}

	public void addGameKeyListener(RoomPanel[] roompanel){
		for(int i=0;i<roompanel.length;i++){
			roompanel[i].addKeyListener(this);
		}
	}

	public void addGameMouseListener(RoomPanel[] roompanel){
		for(int i=0;i<roompanel.length;i++){
			roompanel[i].addMouseListener(this);
		}
	}

	public void keyTyped(KeyEvent e){

	}

	public void keyPressed(KeyEvent e){
		RoomPanel[] rm = ui.getRoomPanel();
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			chatbox.sendMessage(ui.getTextFromChat());
			ui.setBlank();
		}
		else if(e.getKeyCode()==37){
			if(face==1)face=4;
			else face--;

			updateFace();

			if(face%2==0){
				ui.switchWindow(KEYDOOR);
				rm[2].requestFocus();
			}
			else {
				ui.switchWindow(RIDDLEDOOR);
				rm[1].requestFocus();
			}
		}
		else if(e.getKeyCode()==39){
			if(face==4) face=1;
			else face++;

			updateFace();

			if(face%2==0){
				ui.switchWindow(KEYDOOR);
				rm[2].requestFocus();
			}
			else {
				ui.switchWindow(RIDDLEDOOR);
				rm[1].requestFocus();
			}
		}
	}

	public void keyReleased(KeyEvent e){
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		RoomPanel[] rm = ui.getRoomPanel();
		if(e.getComponent()==rm[0]){
			rm[0].requestFocus();
		}
		else if(e.getComponent()==rm[1]){
			rm[1].requestFocus();
		}
		else if(e.getComponent()==rm[2]){
			rm[2].requestFocus();
		}
		
	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {
		RoomPanel[] rm = ui.getRoomPanel();
		if(e.getComponent()!=rm[0]){
			if((e.getX()>100&&e.getX()<420)&&(e.getY()>90&&e.getY()<670)){
				System.out.println("door");
				updateRoom();
			}
			else if((e.getX()>480&&e.getX()<735)&&(e.getY()>215&&e.getY()<530)){
				System.out.println("puzzle");
			}
		}
	}

	public void actionPerformed(ActionEvent e){
		JButton[] btn = ui.getButtons();

		if(e.getSource()==btn[0]){
			chatbox.sendMessage(ui.getTextFromChat());
			ui.setBlank();
		}
		else if(e.getSource()==btn[1]){ //will only connect to server after the name is entered
			if(ui.getInputName().equals("")){
				ui.showNameWarning();
			}
			else {
				name = ui.getInputName();
				chatbox.setName(name);
				players.add(new Player(name));
				playernum = players.size();
				connect(name);
				ui.closeJDialog();
			}
		}
		else if(e.getSource()==btn[2]){ 
			ui.hideNameWarning();
		}
	}
}