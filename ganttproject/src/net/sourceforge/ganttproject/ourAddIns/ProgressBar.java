package net.sourceforge.ganttproject.ourAddIns;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class ProgressBar extends JComponent {
	
	private int progress = 0;
	private int size = 0;
	private Color color = Color.GREEN;
	
	
	
	public ProgressBar(int size, Color c, int progress){
		this.size = size;
		this.color = c;
		this.setBackground(new Color(100,100,100));
		this.UpdateProgress(progress);
	}
	public ProgressBar(int size, Color c){
		this.size = size;
		this.color = c;
		this.setBackground(new Color(100,100,100));
	}
	public ProgressBar(int size){
		this.size = size;
		this.setBackground(new Color(100,100,100));
	}
	
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(this.getWidth()/2, this.getHeight()/2);
		g2.rotate(Math.toRadians(270));
		Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);
		Ellipse2D circle = new Ellipse2D.Float(0,0,size-10,
				size-10);
		if(size < 60){
			arc.setFrameFromCenter(new Point(0,0),new Point(size-6,size-6));
		}else{
			arc.setFrameFromCenter(new Point(0,0),new Point(size,size));
		}
		
		circle.setFrameFromCenter(new Point(0,0),new Point(size-10,
				size-10));
		arc.setAngleStart(1);
		arc.setAngleExtent(-progress*3.6);
		GradientPaint gp = new GradientPaint(0, 0, color, 10, size-5, Color.white);
		g2.setPaint(gp);
		//g2.setColor(color);
		g2.draw(arc);
		g2.fill(arc);
		
	
		GradientPaint gp2 = new GradientPaint(0, 50, new Color(70,70,70), 90, size-5, Color.white);
		g2.setPaint(gp2);
		//g2.setColor(Color.white);
		g2.draw(circle);
		g2.fill(circle);
		
		g2.setColor(color);
		g2.rotate(Math.toRadians(90));
		g2.setFont(new Font("Verdana",Font.PLAIN,size/3));
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(progress+"%",
				g);
		int x = (0 - ((int)r.getWidth())/2);
		int y = (0 - (int)r.getHeight()/2+fm.getAscent());
		g2.drawString(progress+"%", x, y);
		
	}
	
	public void UpdateProgress(int p){
		
		new Thread(new Runnable(){
			public void run(){
				progressLogic(p);
			}
		}).start();
	}
	
	private synchronized void progressLogic(int p){
		if(this.progress > p ){
			
			int original = this.progress;
			int updateAmount = this.progress - p;
			
			while(true){
				
				if(this.progress == 0 || (this.progress == original - updateAmount)){
					break;
				}
				this.progress -= 1;
				this.repaint();
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}else{
			int original = this.progress;
			int updateAmount = p - this.progress;
			
			while(true){
				
				if(this.progress == 100 || (this.progress == updateAmount + original)){
					break;
				}
				this.progress += 1;
				this.repaint();
				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
