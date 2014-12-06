import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class MapPanelGrid extends JPanel{
	private int x;
	private int y;
	private String color;

	public MapPanelGrid(){
		this.setBackground(Color.black);
		x=-1;
		y=-1;
		color = "BLACK";
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(x!=-1&&y!=-1){
			if(color.equals("RED")){
				g.setColor(Color.RED);
			}
			else if(color.equals("BLUE")){
				g.setColor(Color.BLUE);	
			}
			else if(color.equals("GREEN")){
				g.setColor(Color.GREEN);
			}
			else if(color.equals("PINK")){
				g.setColor(Color.PINK);
			}
			g.fillOval(x,y,10,10);
		}
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public void setColor(String color){
		this.color = color;
	}
}