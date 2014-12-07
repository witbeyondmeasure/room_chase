import java.io.*;
import java.net.*;
import java.awt.*;

public class ChatBox{
	private InetAddress ipa;
	private DatagramSocket clientSocket;
	private byte[] sendData = new byte[256];	
	private String sentence="";
	private RoomChase rc;
	private String name;

	public ChatBox(InetAddress ipa, DatagramSocket clientSocket, RoomChase rc) throws SocketException{
		this.ipa = ipa;
		this.clientSocket = clientSocket;
		this.rc = rc;
		ReceiverThread receiver = new ReceiverThread(clientSocket,this);
		receiver.start();
	}

	public void setName(String name){
		this.name=name;
	}

	public void displayMessage(String msg){
		rc.displayMessage(msg);
	}

	public void sendMessage(String msg){
		try{
			sendData = new byte[256];
			sentence = "SEND##"+msg+"##"+name;
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipa, 4445);
			clientSocket.send(sendPacket);
		}catch(Exception e){

		}
	}

	public void startGame(){
		rc.startGame();
	}

	public void addPlayer(String name, String msg, String color){
		String xy[] = msg.split(";;");
		if(name.equals(this.name)){
			rc.setLocation(Integer.parseInt(xy[0]),Integer.parseInt(xy[1]));
			rc.setColor(color);
		}
		else{
			rc.addPlayer(new Player(name, new Point(Integer.parseInt(xy[0]),Integer.parseInt(xy[1])), color));
		}
	}

	public void sendDoorPosition(String doorPosition){
		rc.setDoorPosition(doorPosition);
	}

	public void setPlayerNumInRoomChase(String msg){
		rc.setPlayerNum(Integer.parseInt(msg));
	}

	public void updateFaceInMap(String name, String loc, String color, String face){
		String[] temp = loc.split(";;");
		rc.updateFaceInMap(name, new Point(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])), color, Integer.parseInt(face));
	}

	public void updateRoomInMap(String name, String loc, String color, String face, String oldloc){
		String[] temp = loc.split(";;");
		String[] temp2 = oldloc.split(";;");
		rc.updateRoomInMap(name, new Point(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])), color, Integer.parseInt(face),new Point(Integer.parseInt(temp2[0]),Integer.parseInt(temp2[1])));
	}	

	public void stopKeys(){
		rc.stopKeys();
	}

	public void updatePlayer(String player1, String player2){
		rc.updatePlayer(Integer.parseInt(player1), Integer.parseInt(player2));
	}
}