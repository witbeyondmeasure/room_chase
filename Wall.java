import java.awt.*;

public class Wall{
	private int type;
	private Point facing;
	public Wall(int type,Point facing){
		this.type = type;
		this.facing = facing;
	}

	public int getType(){
		return this.type;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getFacingX(){
		return (int)facing.getX();
	}

	public int getFacingY(){
		return (int)facing.getY();
	}

	public boolean open(){
		return true;
	}
}