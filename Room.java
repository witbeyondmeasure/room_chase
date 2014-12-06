import java.awt.*;

public class Room implements Constants{
	private String contains;
	private Wall[] wall = new Wall[4];
	private Point location;
	public Room(int north, int east, int south, int west, Point p){
		contains = null;
		location = new Point(p);
		int x,y;
		if(location.getX()==0&&location.getY()==0){
			wall[0] = new Wall(100, null);
			wall[1] = new Wall(east, new Point(0,1));
			wall[2] = new Wall(south, new Point(1,0));
			wall[3] = new Wall(100, null);
		}
		else if(location.getX()==0&&location.getY()==1){
			wall[0] = new Wall(100, null);
			wall[1] = new Wall(east, new Point(0,2));
			wall[2] = new Wall(south, new Point(1,1));
			wall[3] = new Wall(west, new Point(0,0));
		}
		else if(location.getX()==0&&location.getY()==2){
			wall[0] = new Wall(100, null);
			wall[1] = new Wall(100, null);
			wall[2] = new Wall(south, new Point(1,2));
			wall[3] = new Wall(west, new Point(0,1));
		}
		else if(location.getX()==1&&location.getY()==0){
			wall[0] = new Wall(north, new Point(0,0));
			wall[1] = new Wall(east, new Point(1,1));
			wall[2] = new Wall(south, new Point(2,0));
			wall[3] = new Wall(100, null);
		}
		else if(location.getX()==1&&location.getY()==1){
			wall[0] = new Wall(north, new Point(0,1));
			wall[1] = new Wall(east, new Point(1,2));
			wall[2] = new Wall(south, new Point(2,1));
			wall[3] = new Wall(west, new Point(1,0));
		}
		else if(location.getX()==1&&location.getY()==2){
			wall[0] = new Wall(north, new Point(0,2));
			wall[1] = new Wall(100, null);
			wall[2] = new Wall(south, new Point(2,2));
			wall[3] = new Wall(west, new Point(1,1));
		}
		else if(location.getX()==2&&location.getY()==0){
			wall[0] = new Wall(north, new Point(1,0));
			wall[1] = new Wall(east, new Point(2,1));
			wall[2] = new Wall(100, null);
			wall[3] = new Wall(100, null);
		}
		else if(location.getX()==2&&location.getY()==1){
			wall[0] = new Wall(north, new Point(1,1));
			wall[1] = new Wall(east, new Point(2,2));
			wall[2] = new Wall(100, null);
			wall[3] = new Wall(west, new Point(2,0));
		}
		else if(location.getX()==2&&location.getY()==2){
			wall[0] = new Wall(north, new Point(1,2));
			wall[1] = new Wall(100, null);
			wall[2] = new Wall(100, null);
			wall[3] = new Wall(west, new Point(2,1));
		}
	}
	public void setContains(String contains){
		this.contains = contains;
	}

	public String getContains(){
		return contains;
	}

	public Wall getWall(int face){
		return wall[face-1];
	}

	public boolean checkOpen(int face){
		return getWall(face).open();
	}
}