/**
*GameServer Class: Class for the server. One server must be running to play the game
*/

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

public class GameServer implements Runnable,Constants{
	private int expectednum;
	private int playernum;
	private DatagramSocket serverSocket = null;
	private ArrayList<Player> players = new ArrayList<Player>();
	private byte sendData[] = new byte[256];
	private int gameStage;
	private int[][] rooms = new int[3][3];
	private String doorPosition;

	public GameServer(int expectednum){
		this.gameStage = WAITING_FOR_PLAYERS;
		this.playernum=0;
		this.expectednum = expectednum;
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

	private void printLoc(){ //For printing each player's location
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				System.out.print(rooms[i][j]+" ");
			}
			System.out.print("\n");
		}
	}

	private void broadcast(String msg, String name, String color){ //method for broadcasting a protocol to all players
		msg = "MSG##"+name+":"+msg+"##"+color;
		for(int i=0;i<players.size();i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}
	}

	private void broadcastConnect(String name, Point p){ //method for broadcasting a player's connection
		String msg = "CONNECTED##"+name+" has connected to the server.##LOC##"+(int)p.getX()+";;"+(int)p.getY()+"##"+assignColor();
		for(int i=0;i<playernum;i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
				// printLoc();
			}
			catch(Exception e){

			}
		}
	}

	private void broadcastFaceMovement(String msg){ //method for broadcasting the update of where a player is facing
		for(int i=0;i<players.size();i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}
	}

	private void broadcastRoomMovement(String msg){ //method for broadcasting update of a player's location
		String[] temp = msg.split("##");
		int p = searchPlayerByName(temp[1]);
		String[] temp2 = temp[2].split(";;");

		int oldx = players.get(p).getLocationX();
		int oldy = players.get(p).getLocationY();
		int newx = Integer.parseInt(temp2[0]);
		int newy = Integer.parseInt(temp2[1]);

		msg = msg + "##" +oldx+";;"+oldy;

		/*if(collide(newx, newy)){
			for(int i=0;i<playernum;i++){
				if(players.get(i).getLocationX()==newx&&players.get(i).getLocationY()==newy&&p!=i){
					killPlayer(players.get(p).getName(), players.get(i).getName(), i);
					break;
				}
			}
		}*/

		players.get(p).setLocation(new Point(Integer.parseInt(temp2[0]),Integer.parseInt(temp2[1])));
		rooms[oldx][oldy] = -1;
		rooms[Integer.parseInt(temp2[0])][Integer.parseInt(temp2[1])] = players.get(p).getID()-1;
		checkSides(Integer.parseInt(temp2[0]),Integer.parseInt(temp2[1]));
		

		for(int i=0;i<players.size();i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}

		if(collide(newx, newy)){
			for(int i=0;i<players.size();i++){
				if(players.get(i).getLocationX()==newx&&players.get(i).getLocationY()==newy&&p!=i){
					killPlayer(players.get(p).getName(), players.get(i).getName(), i);
					break;
				}
			}
		}
	}

	private void killPlayer(String player0, String player1, int player2){ //method for killing a player
		//1 = int for killer
		//2 = int for victim
		String msg = "DEATH##"+player0+"##"+player1;
		String msg2 = "TERMINATE##";

		//broadcast the death
		for(int i=0;i<players.size();i++){
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}
		}

		//terminate inputs from player2
		try{
				sendData = msg2.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(player2).getIPAddress(), players.get(player2).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}

		//update player list and count
		int n = searchPlayerByName(player0);
		players.get(player2).setStatus(DEAD);
		rooms[players.get(player2).getLocationX()][players.get(player2).getLocationY()]=players.get(n).getID()-1;
		playernum--;

		if(playernum==1){
			int num = searchWinner();
			gameStage = GAME_END;
			try{
				sendData = msg2.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(num).getIPAddress(), players.get(num).getPort());
				serverSocket.send(sendPacket);
			}
			catch(Exception e){

			}

			String win = "VICTOR";
			String lose = "DEFEATED";

			for(int i=0;i<players.size();i++){
				if(players.get(i).getStatus()==DEAD){
					try{
						sendData = lose.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
						serverSocket.send(sendPacket);
					}
					catch(Exception e){

					}
				}
				else if(players.get(i).getStatus()==ALIVE){
					try{
						sendData = win.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(i).getIPAddress(), players.get(i).getPort());
						serverSocket.send(sendPacket);
					}
					catch(Exception e){

					}
				}
			}
		}
		// System.out.println(msg);
	}

	private int searchWinner(){ //Method for searching the winner
		for(int i=0;i<players.size();i++){
			if(players.get(i).getStatus()==ALIVE){
				return i;
			}
		}
		return -1;
	}

	private void checkSides(int x, int y){ //method for checking if two players are in side-by-side rooms
		String msg = "MSG##Server: There is someone in a room next to you";
		int temp, flag=0;
		for (int dx = (x > 0 ? -1 : 0); dx <= (x < 2 ? 1 : 0); ++dx){
			for (int dy = (y > 0 ? -1 : 0); dy <= (y < 2 ? 1 : 0); ++dy){
				if((dx==0&&dy==0)||(Math.abs(dx)==Math.abs(dy))){}
				else if(rooms[x+dx][y+dy]!=-1){
					temp = searchPlayerIndex(x+dx, y+dy);
					sendToPlayer(msg, players.get(temp).getIPAddress(),players.get(temp).getPort());
					flag=1;
				}
			}
		}
		
		if(flag==1) {
			temp = searchPlayerIndex(x, y);
			sendToPlayer(msg, players.get(temp).getIPAddress(),players.get(temp).getPort());
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

	private void sendDoorPosition(){ //method for sending door position
		String msg = "DOOR##"+doorPosition;
		try{
			sendData = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(playernum-1).getIPAddress(), players.get(playernum-1).getPort());
			serverSocket.send(sendPacket);
		}
		catch(Exception e){

		}
	}

	private void sendOtherPlayers(){ //method for sending other player's data to a player
		String msg = "PLAYER";
		for(int i=0;i<playernum-1;i++){
			msg = msg+"##"+players.get(i).getName()+"##"+players.get(i).getLocationX()+";;"+players.get(i).getLocationY()+"##"+players.get(i).getColor();
			try{
				sendData = msg.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, players.get(playernum-1).getIPAddress(), players.get(playernum-1).getPort());
				serverSocket.send(sendPacket);
				//printLoc();
			}
			catch(Exception e){

			}
			msg="PLAYER";
		}
	}

	private void startGame(){ //method for broadcasting the start of a game
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

	private void sendToPlayer(String msg, InetAddress ipa, int port){ //method that sends a protocol to one player
		try{
			sendData = msg.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipa, port);
			serverSocket.send(sendPacket);
		}
		catch(Exception e){

		}
	}

	public int searchPlayerByName(String name){ //method for searching a player by name
		for(int i=0;i<players.size();i++){
			if(players.get(i).getName().equals(name)){
				return i;
			}
		}
		return -1;
	}

	private String searchPlayerColor(String name){ //method for searching a player's color
		for(int i=0;i<players.size();i++){
			if(players.get(i).getName().equals(name)){
				return players.get(i).getColor();
			}
		}
		return null;
	}

	public int searchPlayerIndex(int x, int y){
		for(int i=0;i<players.size();i++){
			if(players.get(i).getLocationX()==x && players.get(i).getLocationY()==y){
				return i;
			}
		}
		return -1;
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
						if(playernum<expectednum){
							String msg[] = protocol.split("##");
							playernum++;
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
						if(playernum==expectednum){
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
						//printLoc();
					}
					break;
				case IN_PROGRESS:
					break;
				case GAME_END:
					if(protocol.startsWith("SEND##")){
						String msg[] = protocol.split("##");
						broadcast(msg[1],msg[2],searchPlayerColor(msg[1]));
					}
					break;
			}
		}
	}

	private Point generateRandomLocation(){ //method for generating random location of a player
		Random rand = new Random();
		int x = rand.nextInt(3), y = rand.nextInt(3);
		while (collide(x,y)){
			x = rand.nextInt(3);
			y = rand.nextInt(3);
		}

		return new Point(x,y);
	}

	private boolean collide(int x, int y){ //method for detecting if two players are in the same location
		for(int k=0; k<players.size();k++){
			if(players.get(k).getLocationX()==x&&players.get(k).getLocationY()==y){
				return true;
			}
		}
		return false;
	}

	public String initDoors(){ //method for initializing doors so that all players have the same doors in each room
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
		return init;

	}

	public static void main(String args[]){
		if(args.length==0){
			Thread t = new Thread(new GameServer(2));
			t.start();
		}
		else{
			int temp = Integer.parseInt(args[0]);
			if(temp<=2){
				Thread t = new Thread(new GameServer(2));
				t.start();
			}
			else if(temp>=3){
				Thread t = new Thread(new GameServer(3));
				t.start();
			}
		}
	}
}