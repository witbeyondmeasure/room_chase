import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class GameServer implements Runnable,Constants{
	private int playernum;
	private DatagramSocket serverSocket = null;
	private ArrayList<Player> players = new ArrayList<Player>();
	private byte sendData[] = new byte[256];
	private int gameStage;
	private int[][] rooms = new int[3][3];
	private String doorPosition;

	public GameServer(){
		this.gameStage = WAITING_FOR_PLAYERS;
		this.playernum=0;
		try{
			serverSocket = new DatagramSocket(PORT);
			serverSocket.setSoTimeout(100);
		}catch(IOException e){
			System.err.println("Could not listen on port: "+PORT);
			System.exit(-1);
		}catch(Exception e){}
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				rooms[i][j]=-1;
			}
		}
		doorPosition = initDoors();
	}

	private void printLoc(){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				System.out.print(rooms[i][j]+" ");
			}
			System.out.print("\n");
		}
	}

	private void broadcast(String msg, String name, String color){
		msg = "MSG##"+name+":"+msg+"##"+color;
		for(int i=0;i<playernum;i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}
	}

	private void broadcastConnect(String name, Point p){
		String msg = "CONNECTED##"+name+" has connected to the server.##LOC##"+(int)p.getX()+";;"+(int)p.getY()+"##"+assignColor();
		for(int i=0;i<playernum;i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
				printLoc();
			}
			catch(Exception e){

			}
		}
	}

	private void broadcastFaceMovement(String msg){
		for(int i=0;i<playernum;i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}
	}

	private void broadcastRoomMovement(String msg){
		String[] temp = msg.split("##");
		int p = searchPlayerByName(temp[1]);
		String[] temp2 = temp[2].split(";;");

		int oldx = players.get(p).getLocationX();
		int oldy = players.get(p).getLocationY();

		msg = msg + "##" +oldx+";;"+oldy;

		players.get(p).setLocation(new Point(Integer.parseInt(temp2[0]),Integer.parseInt(temp2[1])));

		rooms[oldx][oldy] = -1;
		rooms[Integer.parseInt(temp2[0])][Integer.parseInt(temp2[1])] = players.get(p).getID();

		/*checkSides(Integer.parseInt(temp2[0]),Integer.parseInt(temp2[1]));*/

		for(int i=0;i<playernum;i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}
	}

	private void checkSides(int x, int y){
		String msg = "MSG##Server: There is someone in a room next to you";
		sendToPlayer(msg, players.get(rooms[x][y]-1).getIPAddress(),players.get(rooms[x][y]-1).getPort());
		if(x==0&&y==0){
			if(rooms[0][1]!=-1){
				sendToPlayer(msg, players.get(rooms[0][1]-1).getIPAddress(),players.get(rooms[0][1]-1).getPort());
			}
			else if(rooms[1][0]!=-1){
				sendToPlayer(msg, players.get(rooms[1][0]-1).getIPAddress(),players.get(rooms[1][0]-1).getPort());
			}
		}
		else if(x==0&&y==1){
			if(rooms[0][0]!=-1){
				sendToPlayer(msg, players.get(rooms[0][0]-1).getIPAddress(),players.get(rooms[0][0]-1).getPort());
			}
			else if(rooms[1][1]!=-1){
				sendToPlayer(msg, players.get(rooms[1][1]-1).getIPAddress(),players.get(rooms[1][1]-1).getPort());
			}
			else if(rooms[0][2]!=-1){
				sendToPlayer(msg, players.get(rooms[0][2]-1).getIPAddress(),players.get(rooms[0][2]-1).getPort());
			}
		}
		else if(x==0&&y==2){
			if(rooms[0][1]!=-1){
				sendToPlayer(msg, players.get(rooms[0][1]-1).getIPAddress(),players.get(rooms[0][1]-1).getPort());
			}
			else if(rooms[1][2]!=-1){
				sendToPlayer(msg, players.get(rooms[1][2]-1).getIPAddress(),players.get(rooms[1][2]-1).getPort());
			}
		}
		else if(x==1&&y==0){
			if(rooms[0][0]!=-1){
				sendToPlayer(msg, players.get(rooms[0][0]-1).getIPAddress(),players.get(rooms[0][0]-1).getPort());
			}
			else if(rooms[1][1]!=-1){
				sendToPlayer(msg, players.get(rooms[1][1]-1).getIPAddress(),players.get(rooms[1][1]-1).getPort());
			}
			else if(rooms[2][0]!=-1){
				sendToPlayer(msg, players.get(rooms[2][0]-1).getIPAddress(),players.get(rooms[2][0]-1).getPort());
			}
		}
		else if(x==1&&y==1){
			if(rooms[0][1]!=-1){
				sendToPlayer(msg, players.get(rooms[0][1]-1).getIPAddress(),players.get(rooms[0][1]-1).getPort());
			}
			else if(rooms[1][0]!=-1){
				sendToPlayer(msg, players.get(rooms[1][0]-1).getIPAddress(),players.get(rooms[1][0]-1).getPort());
			}
			else if(rooms[2][1]!=-1){
				sendToPlayer(msg, players.get(rooms[2][1]-1).getIPAddress(),players.get(rooms[2][1]-1).getPort());
			}
			else if(rooms[1][2]!=-1){
				sendToPlayer(msg, players.get(rooms[1][2]-1).getIPAddress(),players.get(rooms[1][2]-1).getPort());
			}
		}
		else if(x==1&&y==2){
			if(rooms[0][2]!=-1){
				sendToPlayer(msg, players.get(rooms[0][2]-1).getIPAddress(),players.get(rooms[0][2]-1).getPort());
			}
			else if(rooms[1][1]!=-1){
				sendToPlayer(msg, players.get(rooms[1][1]-1).getIPAddress(),players.get(rooms[1][1]-1).getPort());
			}
			else if(rooms[2][2]!=-1){
				sendToPlayer(msg, players.get(rooms[2][2]-1).getIPAddress(),players.get(rooms[2][2]-1).getPort());
			}
		}
		else if(x==2&&y==0){
			if(rooms[1][0]!=-1){
				sendToPlayer(msg, players.get(rooms[1][0]-1).getIPAddress(),players.get(rooms[1][0]-1).getPort());
			}
			else if(rooms[2][1]!=-1){
				sendToPlayer(msg, players.get(rooms[2][1]-1).getIPAddress(),players.get(rooms[2][1]-1).getPort());
			}
		}
		else if(x==2&&y==1){
			if(rooms[2][0]!=-1){
				sendToPlayer(msg, players.get(rooms[2][0]-1).getIPAddress(),players.get(rooms[2][0]-1).getPort());
			}
			else if(rooms[1][1]!=-1){
				sendToPlayer(msg, players.get(rooms[1][1]-1).getIPAddress(),players.get(rooms[1][1]-1).getPort());
			}
			else if(rooms[2][2]!=-1){
				sendToPlayer(msg, players.get(rooms[2][2]-1).getIPAddress(),players.get(rooms[2][2]-1).getPort());
			}
		}
		else if(x==2&&y==2){
			if(rooms[1][2]!=-1){
				sendToPlayer(msg, players.get(rooms[1][2]-1).getIPAddress(),players.get(rooms[1][2]-1).getPort());
			}
			else if(rooms[2][1]!=-1){
				sendToPlayer(msg, players.get(rooms[2][1]-1).getIPAddress(),players.get(rooms[2][1]-1).getPort());
			}
		}
	}

	private String assignColor(){
		switch (playernum){
			case 1:
				return "RED";
			case 2:
				return "BLUE";
			case 3:
				return "GREEN";
			case 4:
				return "PINK";
		}
		return "RED";
	}

	private void sendDoorPosition(){
		String msg = "DOOR##"+doorPosition;
		try{
			sendData = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(playernum-1).getIPAddress(), players.get(playernum-1).getPort());
			serverSocket.send(sendPacket);
		}
		catch(Exception e){

		}
	}

	private void sendOtherPlayers(){
		String msg = "PLAYER";
		for(int i=0;i<playernum-1;i++){
			msg = msg+"##"+players.get(i).getName()+"##"+players.get(i).getLocationX()+";;"+players.get(i).getLocationY()+"##"+players.get(i).getColor();
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(playernum-1).getIPAddress(), players.get(playernum-1).getPort());
				serverSocket.send(sendPacket);
				printLoc();
			}
			catch(Exception e){

			}
		}
	}

	private void startGame(){
		String msg = "START";
		for(int i=0;i<playernum;i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}
	}

	private void sendToPlayer(String msg, InetAddress ipa, int port){
		try{
			sendData = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipa, port);
			serverSocket.send(sendPacket);
		}
		catch(Exception e){

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

	private String searchPlayerColor(String name){
		for(int i=0;i<playernum;i++){
			if(players.get(i).getName().equals(name)){
				return players.get(i).getColor();
			}
		}
		return null;
	}

	public void run(){
		while(true){
			byte[] buf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
				serverSocket.receive(packet);
			}catch(Exception ioe){}

			String protocol = new String(buf);
			protocol=protocol.trim();

			switch(gameStage){
				case WAITING_FOR_PLAYERS:
					if(protocol.startsWith("CONNECT##")){
						if(playernum<2){
							String msg[] = protocol.split("##");
							playernum+=1;
							System.out.println(msg[0]);
							players.add(new Player(playernum, msg[1], packet.getAddress(), packet.getPort(), assignColor()));
							Point loc = new Point(generateRandomLocation());
							players.get(playernum-1).setLocation(loc);
							rooms[players.get(playernum-1).getLocationX()][players.get(playernum-1).getLocationY()]=playernum-1;
							broadcastConnect(msg[1],loc);
							sendDoorPosition();
							if(playernum>1){
								sendOtherPlayers();
							}
						}
						if(playernum==2){
							gameStage = GAME_START;
							startGame();
						}
					}
					else if(protocol.startsWith("SEND##")){
						String msg[] = protocol.split("##");
						broadcast(msg[1],msg[2],searchPlayerColor(msg[1]));
					}
					break;						
				case GAME_START:
					if(protocol.startsWith("SEND##")){
						String msg[] = protocol.split("##");
						broadcast(msg[1],msg[2],searchPlayerColor(msg[1]));
					}
					else if(protocol.startsWith("UPDATEFACE##")){
						broadcastFaceMovement(protocol);
					}
					else if(protocol.startsWith("UPDATEROOM##")){
						broadcastRoomMovement(protocol);
					}
					break;
				case IN_PROGRESS:
					break;
			}
		}
	}

	private Point generateRandomLocation(){
		Random rand = new Random();
		int x = rand.nextInt(3), y = rand.nextInt(3);
		while (collide(x,y)){
			x = rand.nextInt(3);
			y = rand.nextInt(3);
		}

		return new Point(x,y);
	}

	private boolean collide(int x, int y){
		for(int k=0; k<playernum;k++){
			if(players.get(k).getLocationX()==x&&players.get(k).getLocationY()==y){
				return true;
			}
		}
		return false;
	}

	public String initDoors(){
		int i, j, k, random;
		String init = "";	
		Random rand = new Random();


		for(i=0; i<3; i++){
			for(j=0; j<3; j++){
				for(k=0; k<4; k++){
					init+=rand.nextInt(104-101)+101;
					if(k!=3) init+=";;";
				}
				init+="##";
			}
		}
		System.out.println(init);
		return init;

	}

	public static void main(String args[]){
		Thread t = new Thread(new GameServer());
		t.start();
	}
}