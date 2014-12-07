import java.awt.*;
import java.util.*;

public class Wall{
	private int type;
	private Point facing;
	private boolean open;
	private Riddle[] riddles;
	private int kind;
	public Wall(int type,Point facing){
		this.type = type;
		this.facing = facing;
		this.open = false;

		Random rand = new Random();
		kind = rand.nextInt(9);

		riddles = new Riddle[100];
		riddles[0] = new Riddle("<html>What tastes better than it smells?<br/>","Tongue");
		riddles[1] = new Riddle("<html>What gets wet when drying?<br/>", "Towel");
		riddles[2] = new Riddle("<html>A cloud was my mother, the wind is my father, <br/> my son is the cool stream, and my daughter is <br/>the fruit of the land. A rainbow is my bed, the earth my final resting place, <br/>and I'm the torment of man. <br/>", "Rain");
		riddles[3] = new Riddle("<html>It stands on one leg with its heart in its head. <br/>", "Cabbage");
		riddles[4] = new Riddle("<html>What belongs to you but others use it more than you do? <br/>", "Name");
		riddles[5] = new Riddle("<html>What is is that you will break even when you name it? <br/>", "Silence");
		riddles[6] = new Riddle("<html>What holds water yet is full of holes? <br/>", "Sponge");
		riddles[7] = new Riddle("<html>Lives without a body, hears without ears, speaks without a mouth, to which the air alone gives birth. <br/>", "Echo");
		riddles[8] = new Riddle("<html>When one does not know what it is, then it is something; <br/> but when one knows what it is, then it is nothing. <br/>", "Riddle");
	}
	public String getAnswer(){
		return riddles[kind].getAnswer();
	}

	public int getFacingX(){
		return (int)facing.getX();
	}

	public int getFacingY(){
		return (int)facing.getY();
	}

	public String getRiddle(){
		return riddles[kind].getQuestion();
	}

	public int getType(){
		return this.type;
	}

	public boolean checkOpen(){
		return open;
	}

	public void open(boolean v){
		this.open = v;
	}

	public void setType(int type){
		this.type = type;
	}
}