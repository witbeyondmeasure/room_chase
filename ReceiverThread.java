import java.io.*;
import java.net.*;

class ReceiverThread extends Thread{
	private byte[] receiveData;
	String sentence="";
	DatagramSocket clientSocket;
	ChatBox cb;

	public ReceiverThread(DatagramSocket clientSocket, ChatBox cb){
		this.clientSocket = clientSocket;
		this.cb = cb;
	}

	public void run(){
		while(true){
			try{
				receiveData = new byte[256];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				sentence = new String(receivePacket.getData());
				sentence = sentence.trim();
				if(sentence.startsWith("MSG##")){
					String[] msg = sentence.split("##");
					cb.displayMessage(msg[1]);
				}
				else if(sentence.startsWith("CONNECTED##")){
					String[] msg = sentence.split("##");
					String[] temp = msg[1].split(" ");
					cb.displayMessage(msg[1]);
					cb.addPlayer(temp[0], msg[3], msg[4]);
				}
				else if(sentence.startsWith("DOOR##")){
					String[] msg = sentence.split("DOOR##");
					cb.sendDoorPosition(msg[1]);
				}
				else if(sentence.startsWith("PLAYER##")){
					String[] msg = sentence.split("##");
					cb.addPlayer(msg[1], msg[2], msg[3]);
				}
				else if(sentence.startsWith("UPDATEFACE##")){
					String[] msg = sentence.split("##");
					cb.updateFaceInMap(msg[1],msg[2],msg[3],msg[4]);
				}
				else if(sentence.startsWith("UPDATEROOM##")){
					String[] msg = sentence.split("##");
					cb.updateRoomInMap(msg[1],msg[2],msg[3],msg[4],msg[5]);
				}
				else if(sentence.equals("START")){
					cb.startGame();
				}
			}catch(Exception e){
				
			}
		}
	}
}