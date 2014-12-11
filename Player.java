import java.io.*;
import java.net.*;
import java.awt.*;
/**
*Player class: Stored by the server and each player to keep track of each player's location and status
*/
public class Player implements Constants{
	private String name;
	private InetAddress ipa;
	private int port; 
	private int id;
	private Point location = new Point(-1,-1);
	private String color;
	private int status;
	private int face = 1;

	public Player(int id, String name, InetAddress ipa, int port, String color){ //constructor for Server
		this.name = name;
		this.ipa = ipa;
		this.port = port;
		this.id = id;
		this.color = color;
		this.status = ALIVE;
	}

	public Player(String name, Point location, String color){ //constructor for each client
		this.name = name;
		this.location = location;
		this. color = color;
	}

	public Player(String name){ //constructor for each client 
		this.name = name;
	}

	public int getID(){
		return this.id;
	}

	public InetAddress getIPAddress(){
		return this.ipa;
	}

	public String getName(){
		return this.name;
	}

	public String getColor(){
		return this.color;
	}

	public int getPort(){
		return this.port;
	}

	public int getLocationX(){
		return (int)this.location.getX();
	}

	public int getLocationY(){
		return (int)this.location.getY();
	}

	public int getFace(){
		return this.face;
	}

	public int getStatus(){
		return this.status;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public void setFace(int face){
		this.face=face;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setLocation(Point p){
		this.location = p;
	}

	public void setColor(String color){
		this.color = color;
	}
}