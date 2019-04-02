package sample;


public class Bomb {
	private int ownerID;
	private int direct;
	private int startX;
	private int startY;
	private int steps = 0;
	
	public Bomb(int id, int dir, int x, int y)
	{
		this.ownerID = id;
		this.direct = dir;
		this.startX = x;
		this.startY = y;
	}
	
	public int getX()
	{
		return startX;
	}
	
	public int getY()
	{
		return startY;
	}
	
	public int getSteps()
	{
		return steps;
	}
	
	public void setSteps(int value)
	{
		this.steps = value;
	}
	
	public int getOwner()
	{
		return ownerID;
	}
	
	public int getDir()
	{
		return direct;
	}
	
	public void moveBomb()
	{
		switch(direct)
		{
		case 0:	startY -= Controller.getGameSpeed()*2; 	break;
		case 1: startY += Controller.getGameSpeed()*2;	break;
		case 2:	startX += Controller.getGameSpeed()*2;	break;
		case 3:	startX -= Controller.getGameSpeed()*2;	break;
		}
		
		steps++;
	}
}
