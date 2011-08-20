import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
   
public class Grapher extends JPanel {  
    static double[] data; 
    int x, y, oldX, oldY;
    final int PAD = 40;  
   
    protected void paintComponent(Graphics g) {  
        super.paintComponent(g);  
        Graphics2D g2 = (Graphics2D)g;  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                            RenderingHints.VALUE_ANTIALIAS_ON);  
        int w = getWidth();  
        int h = getHeight();  
        g2.drawLine(PAD, PAD, PAD, h-PAD);  
        g2.drawLine(PAD, h-PAD, w-PAD, h-PAD);  
        double xScale = (w - 2*PAD)/(data.length + 1.0d);  
        double maxValue = 100.0;  
        double yScale = (h - 2*PAD)/maxValue;  
        // The origin location.  
        int x0 = PAD;  
        int y0 = h-PAD;  
        oldX = PAD;
        oldY = h-PAD;
        g2.setPaint(Color.red);  
        for(int j = 0; j < data.length; j++) {  
            x = x0 + (int)(xScale * (j+1));  
            y = y0 - (int)(yScale * data[j]);  
            g2.drawLine(oldX, oldY, x, y);
            oldX = x;
            oldY = y;
        }  
    }  
 
	public static float getPercentageDone(final float pSecondsElapsed, final float pDuration, final float pMinValue, final float pMaxValue) {
		return (float) (pMaxValue * pSecondsElapsed / pDuration + pMinValue + 4.0f * Math.sin(Math.PI * pSecondsElapsed * 10.0f/pDuration));
	}


    public static void main(String[] args) {  
    	data = new double[2000];
    	for (int i=0; i<2000; i++) {
    		data[i] = getPercentageDone(i, 2000.0f, 0.0f, 100.0f);
    	}
        JFrame f = new JFrame();  
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        f.getContentPane().add(new Grapher());  
        f.setSize(400,400);  
        f.setLocation(200,200);  
        f.setVisible(true);  
    }  
}  