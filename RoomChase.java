/**
* RoomChase Class: All background processes of the game occur in this class
*/

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

	public void initRooms(){ //initializes rooms from the server
		String[] roomDoors = doorPosition.split("##");
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				String doorType[] = roomDoors[(i*3)+j].split(";;");
				rooms[i][j]= new Room(Integer.parseInt(doorType[0]),Integer.parseInt(doorType[1]),Integer.parseInt(doorType[2]),Integer.parseInt(doorType[3]), new Point(i,j));
			}
		}
	}

	public void startGame(){ //signals the start of the game
		addGameKeyListener(ui.getRoomPanel());
		addGameMouseListener(ui.getRoomPanel());
		switchView(ui.getRoomPanel());
		for(int i=0;i<playernum;i++){
			// System.out.println("name: "+players.get(i).getName());
			ui.setPlayerLocationInMap(players.get(i).getLocationX(),players.get(i).getLocationY(),players.get(i).getColor(),players.get(i).getFace());
		}
	}

	public void connect(String name){ //requests for connecting to the server
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

	private void updateFace(){ //sends updated wall where the player is facing to the server
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

	private void updateRoom(){ //sends player's updated location to the server
		int x=rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getFacingX();
		int y=rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getFacingY();
		players.get(0).setLocation(new Point(x,y));
		switchView(ui.getRoomPanel());
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

	public void updatePlayer(String player1, String player2){
		ui.updatePlayer(player1, player2);
	}

	public int searchPlayerByName(String name){ //searches player by name
		for(int i=0;i<playernum;i++){
			if(players.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}

	public void updateFaceInMap(String name, Point p, String color, int face){ //updates the direction where a player is facing to the map from the server
		int temp = searchPlayerByName(name);
		players.get(temp).setLocation(p);
		players.get(temp).setColor(color);
		players.get(temp).setFace(face);
		ui.clearMapGrid((int)p.getX(),(int)p.getY());
		ui.setPlayerLocationInMap(players.get(temp).getLocationX(),players.get(temp).getLocationY(),players.get(temp).getColor(),players.get(temp).getFace());
	}

	public void updateRoomInMap(String name, Point p, String color, int face, Point oldp){ //updates room location of a player from server to map
		int temp = searchPlayerByName(name);
		players.get(temp).setLocation(p);
		players.get(temp).setColor(color);
		players.get(temp).setFace(face);
		ui.clearMapGrid((int)oldp.getX(),(int)oldp.getY());
		ui.clearMapGrid((int)p.getX(),(int)p.getY());
		ui.setPlayerLocationInMap(players.get(temp).getLocationX(),players.get(temp).getLocationY(),players.get(temp).getColor(),players.get(temp).getFace());
	}

	public void addWindowListenerToJDialog(JDialog jd){ //ads window listener to jdialog
		jd.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				ui.showNameWarning();
			}
		});
	}

	public void addActionListenerToButtons(JButton[] btns){ //ads action listeners to buttons
		btns[0].addActionListener(this);
		btns[1].addActionListener(this);
		btns[2].addActionListener(this);
		btns[3].addActionListener(this);
		btns[4].addActionListener(this);
	}

	public void addTextAreaKeyListener(JTextArea ta){ //adds key listener to Jtextarea
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

	public void removeGameKeyListener(RoomPanel[] roompanel){
		for(int i=0;i<roompanel.length;i++){
			roompanel[i].removeKeyListener(this);
		}
	}

	public void removeGameMouseListener(RoomPanel[] roompanel){
		for(int i=0;i<roompanel.length;i++){
			roompanel[i].removeMouseListener(this);
		}
	}

	public void stopKeys(){
		removeGameKeyListener(ui.getRoomPanel());
		removeGameMouseListener(ui.getRoomPanel());
		ui.switchWindow(DEFEAT);
	}

	public void showVictory(){
		ui.switchWindow(WIN);
	}

	public void showDefeat(){
		ui.switchWindow(DEFEAT);
	}

	public void removeFromMap(){
		
	}

	public void switchView(RoomPanel[] rm){ //switches a player's view according to where the player is facing
		if(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getType()==NO_DOOR){
			ui.switchWindow(NODOOR);
			rm[0].requestFocus();
		}
		else if(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getType()==RIDDLE_DOOR){
			ui.switchWindow(RIDDLEDOOR);
			rm[1].requestFocus();
		}
		else if(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getType()==KEY_DOOR){
			ui.switchWindow(KEYDOOR);
			rm[2].requestFocus();
		}
		else if(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getType()==WEIGHT_DOOR){
			ui.switchWindow(WEIGHTDOOR);
			rm[3].requestFocus();
		}
	}

	public void keyTyped(KeyEvent e){

	}

	public void keyPressed(KeyEvent e){
		RoomPanel[] rm = ui.getRoomPanel();
		JFrame frame = ui.getFrame();
		JTextField answerBox = ui.getAnswerBox();
		JTextArea textarea = ui.getChatArea();
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			if(frame.getFocusOwner()==answerBox){
				if(ui.getAnswer().toLowerCase().equals(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getAnswer().toLowerCase())){
					int x = players.get(0).getLocationX(), y = players.get(0).getLocationY();
					rooms[x][y].getWall(face).open(true);
					ui.hidePuzzleJD();
					updateRoom();
					rooms[x][y].getWall(face).open(false);
					ui.clearAnswer();
				}
				else {
					ui.getRiddleLabel().setText(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getRiddle().replace("<html>","<html> INCORRECT!!! <br/>")+"</html>");
				}
			}
			else if (frame.getFocusOwner()==textarea){
				chatbox.sendMessage(ui.getTextFromChat());
				ui.setBlank();
			}
		}
		else if(e.getKeyCode()==37){ //if left is pressed, the player navigates the room in counter clockwise
			if(face==1)face=4;
			else face--;

			updateFace();
			switchView(rm);
			ui.clearAnswer();
			/*if(face%2==0){
				ui.switchWindow(KEYDOOR);
				rm[2].requestFocus();
			}
			else {
				ui.switchWindow(RIDDLEDOOR);
				rm[1].requestFocus();
			}*/
		}
		else if(e.getKeyCode()==39){ //if right is pressed, the player navigates clockwise
			if(face==4) face=1;
			else face++;

			updateFace();
			switchView(rm);
			ui.clearAnswer();
			/*if(face%2==0){
				ui.switchWindow(KEYDOOR);
				rm[2].requestFocus();
			}
			else {
				ui.switchWindow(RIDDLEDOOR);
				rm[1].requestFocus();
			}*/
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
				if(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].checkOpen(face)){
					updateRoom();
				}
				else{
					String str = setClue(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getAnswer());
					ui.getRiddleLabel().setText(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getRiddle()+str+"<br/></html>");
					ui.showPuzzleJD();
				}
			}
			else if((e.getX()>480&&e.getX()<735)&&(e.getY()>215&&e.getY()<530)){
				String str = setClue(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getAnswer());
				ui.getRiddleLabel().setText(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getRiddle()+str+"<br/></html>");
				ui.showPuzzleJD();
			}
		}
	}

	private String setClue(String ans){ //sets a clue to a riddle so that players will not have a hard time answering
		int cluenum = 0;
		Random rand = new Random();
		int[] clues = new int[2];
		initClues(clues);
		StringBuilder tempans = new StringBuilder(ans);
		if(ans.length()<=3){
			cluenum = 1;
		}
		else if(ans.length()>3&&ans.length()<6){
			cluenum = 2;
			clues = new int[cluenum];
			initClues(clues);
		}
		else if(ans.length()>=6&&ans.length()<10){
			cluenum = 3;
			clues = new int[cluenum];
			initClues(clues);
		}

		if(cluenum==1){

		}
		else{
			clues[0] = 0;
			for(int i = 1; i < cluenum; i++){
				int temp = rand.nextInt(ans.length());
				while(checkClue(clues,temp)){
					temp = rand.nextInt(ans.length());
				}
				clues[i]=temp;
			}
			for(int i=0;i<ans.length();i++){
				if(!ifClue(clues,i)){
					tempans.setCharAt(i,'_');
				}
			}
		}
		return tempans.toString().replace("_"," _");
	}

	private boolean ifClue(int[] clues, int temp){ //checks if temp is already a clue
		for(int i=0;i<clues.length;i++){
			if(clues[i]==temp){
				return true;
			}
		}

		return false;
	}

	private void initClues(int[] clues){ //initializes clues to -1
		for(int i=0; i<clues.length; i++){
			clues[i] = -1;
		}
	}

	private boolean checkClue(int[] clues, int temp){
		for(int i=1; i<clues.length; i++){
			if(clues[i]==temp) return true;
		}
		return false;
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
		else if(e.getSource()==btn[3]){ 
			ui.hideAlert();
		}
		else if(e.getSource()==btn[4]){
			if(ui.getAnswer().toLowerCase().equals(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getAnswer().toLowerCase())){
				int x = players.get(0).getLocationX(), y = players.get(0).getLocationY();
				rooms[x][y].getWall(face).open(true);
				ui.hidePuzzleJD();
				updateRoom();
				rooms[x][y].getWall(face).open(false);
				ui.clearAnswer();
			}
			else {
				ui.getRiddleLabel().setText(rooms[players.get(0).getLocationX()][players.get(0).getLocationY()].getWall(face).getRiddle().replace("<html>","<html> INCORRECT!!! <br/>"));
			}
		}
	}
}