package sample;

import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class Tank {
   	private int posX;
    private int posY;
    private int angle;
    private Rectangle tank;
    private Label tankIdLabel;
    private int idTank;
    private boolean show = true;
    private float health = 100;
    
    public Tank(int x, int y, int a, float h, int id, Rectangle t, boolean s)
    {
    	this.posX = x;
    	this.posY = y;
    	this.angle = a;
    	this.idTank = id;
    	this.tank = t;
    	this.show = s;
    	this.health = h;
    }
    
    public Label getTankIdLabel()
    {
    	return this.tankIdLabel;
    }
    
    public void setTankIdLabel(Label l)
    {
    	this.tankIdLabel = l;
    }
    
    public float getHealth()
    {
    	return this.health;
    }
    
    public void dmg(float value)
    {
    	this.health -= value;
    }
    
    public void setHealth(float value)
    {
    	this.health = value;
    }
    
    public void setTankAngle(int a)
    {
    	this.angle = a;
    }
    
    public boolean isShowing()
    {
    	return show;
    }
    
    public void hidden()
    {
    	show = false;
    }
    
    public void setX(int x)
    {
    	this.posX = x;
    }
    
    public void setY(int y)
    {
    	this.posY = y;
    }
    
    public int getIdTank()
    {
    	return idTank;
    }
    
    public void setNewModel(Rectangle t)
    {
    	this.tank = t;
    }
    
    public int getX()
    {
    	return posX;
    }
    
    public int getY()
    {
    	return posY;
    }
    
    public int getAngle()
    {
    	return angle;
    }
    
    public Rectangle getModel()
    {
    	return tank;
    }
}
