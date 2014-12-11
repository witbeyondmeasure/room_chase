import java.io.*;
import java.net.*;

/**
*ReceiverThread Class: a Thread that receives protocols from the Server. 
*/

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
				if(sentence.startsWith("MSG##")){ //For displaying messaged into the chatbox
					String[] msg = sentence.split("##");
					cb.displayMessage(msg[1]);
				}
				else if(sentence.startsWith("CONNECTED##")){ //for notifying player that s/he has successfully connected to the server
					String[] msg = sentence.split("##");
					String[] temp = msg[1].split(" ");
					cb.displayMessage(msg[1]);
					cb.addPlayer(temp[0], msg[3], msg[4]);
				}
				else if(sentence.startsWith("DOOR##")){ //for initializing the doors in each player
					String[] msg = sentence.split("DOOR##");
					cb.sendDoorPosition(msg[1]);
				}
				else if(sentence.startsWith("PLAYER##")){ //for updating the number of players in a player that connects
					String[] msg = sentence.split("##");
					cb.addPlayer(msg[1], msg[2], msg[3]);
				}
				else if(sentence.startsWith("UPDATEFACE##")){ //for updating where the face of a player in the map is
					String[] msg = sentence.split("##");
					cb.updateFaceInMap(msg[1],msg[2],msg[3],msg[4]);
				}
				else if(sentence.startsWith("UPDATEROOM##")){ //for updating a player's location in the map
					String[] msg = sentence.split("##");
					cb.updateRoomInMap(msg[1],msg[2],msg[3],msg[4],msg[5]);
				}
				else if(sentence.startsWith("DEATH##")){ //killing a player
					String[] msg = sentence.split("##");
					cb.updatePlayer(msg[1],msg[2]);
				}
				else if(sentence.startsWith("TERMINATE##")){ //for disabling key listeners of a killed player
					cb.stopKeys();
				}
				else if(sentence.equals("START")){ //for starting the game
					cb.startGame();
				}
				else if(sentence.equals("VICTOR")){ //for notifying a player's victory
					cb.showVictory();
				}
				else if(sentence.equals("DEFEATED")){ //for notifying a player's defeat
					cb.showDefeat();
				}
			}catch(Exception e){
				
			}
		}
	}
}