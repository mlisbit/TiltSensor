import java.awt.*;
import java.util.Random;
import javax.swing.*;
import java.awt.font.*;
import java.text.*;

/**
@author MACIEJ LIS 
*/
public class TiltSensor4 extends GUIComponent 
{
	private double[] Htilt;
	private double[] Vtilt;
	
	public static final Color BACKGROUND_COLOR = Color.yellow;
	public static final Color TEXT_COLOR = Color.GREEN;

	public static final double MAX_HEIGHT = 200;
	public static final double MAX_WIDTH = 200;

	//preffered even numbers and MAX_HEIGHT % NOVD =0
	public static final int NUMBER_OF_VERTICAL_DIVIDERS = 10 ; 
	public static final int RATE_OF_CHANGE_VERTICAL = 20;
	public static final int LENGTH_OF_TILT_LINE = (int)(100);
	public static final boolean DRAW_PERPENDICULAR = true;
	public static final int LENGTH_OF_PERPENDICULAR = (int)(10);
	public static final boolean DRAW_CONNECTING_LINES = true;
	public static final boolean DEBUGGING = true; 
	public static final boolean DRAW_ROTATING_FEILD_LINES = true; 

	public static final int VERTICALTILT_THRESHOLD = 90;
	public static final int HORIZONTALTILT_THRESHOLD = 80;
	

	private UpdateTiltFunction updateTilt;
	private JLabel TiltLabel;

	public TiltSensor4(double[] Htilt, double[] Vtilt) 
	{
		
		super(0,0, (int)MAX_WIDTH, (int)MAX_HEIGHT);
		this.Htilt = Htilt;
		this.Vtilt = Vtilt;
		TiltLabel = new JLabel();
		updateDisplay(this.Htilt[0], this.Vtilt[0]);

		this.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.add(TiltLabel);
		
		this.setBackground(BACKGROUND_COLOR);
		updateTilt = new UpdateTiltFunction(this.Htilt, this.Vtilt);
		this.addUpdateThreadFunction(updateTilt);
		this.startUpdate();
	}

	//when display is updated, just repaint 
	public void updateDisplay(double H, double V)
	{
		repaint();
	}

	//Draws all moving lines 
	public void paint(Graphics g)  
	{  
		super.paint(g); 
		
		int counter = (int)(((NUMBER_OF_VERTICAL_DIVIDERS/2) - 1) * RATE_OF_CHANGE_VERTICAL);
		
		for (int i = (int)MAX_HEIGHT/NUMBER_OF_VERTICAL_DIVIDERS ; 
			i <= (int)MAX_HEIGHT; i = i + (int)(MAX_HEIGHT/NUMBER_OF_VERTICAL_DIVIDERS))
		{	
			if ( Math.abs(Vtilt[0]) >= VERTICALTILT_THRESHOLD)
				g.setColor(Color.RED);			
			else
				g.setColor(Color.pink);

			g.drawLine((0+Math.abs(counter)),i,((int)MAX_WIDTH-Math.abs(counter)),i);
			counter = counter - RATE_OF_CHANGE_VERTICAL;
		}


		g.setColor(Color.MAGENTA);
		//#DRAWS ROTATING MAIN LINE
		g.drawLine((int)(MAX_WIDTH/2 - (LENGTH_OF_TILT_LINE/2)*Math.cos(Math.toRadians(Htilt[0]))),
			(int)(MAX_HEIGHT/2 - (LENGTH_OF_TILT_LINE/2)*Math.sin(Math.toRadians(Htilt[0]))), 
			(int)((LENGTH_OF_TILT_LINE/2)*Math.cos(Math.toRadians(Htilt[0])) + MAX_WIDTH/2),
			(int)((LENGTH_OF_TILT_LINE/2)*Math.sin(Math.toRadians(Htilt[0])) + MAX_HEIGHT/2));


		//Displays Horizontal and VEritcal Tilt Values on GUI 
		if (DEBUGGING == true)
		{
			//Purpose: change the font. 
			TiltLabel.setFont(new Font("monospaced", 1, 9));
			TiltLabel.setText("<html> V-Tilt: " + Vtilt[0] + "<br> H-Tilt: " + Htilt[0] + "<html>");
		}
		
		g.setColor(Color.BLACK); //color of main lines.
		g.drawLine(0, (int)(MAX_HEIGHT/2), (int)MAX_WIDTH, (int)(MAX_HEIGHT/2)); // horizontal line across center.
		g.drawLine((int)MAX_WIDTH/2, 0, (int)MAX_WIDTH/2, (int)(MAX_HEIGHT)); //vertical line down center.
		

		int Vdegree = 2; //scalability constant 
		
		if (DRAW_PERPENDICULAR == true)
		{
			g.setColor(Color.DARK_GRAY);
			g.drawLine((int)(MAX_WIDTH/2 + Vtilt[0]*Math.sin(Math.toRadians(Htilt[0]))), 
				(int)(MAX_HEIGHT/2 - Vtilt[0]*Math.cos(Math.toRadians(Htilt[0]))), 
				(int)MAX_WIDTH/2, (int)MAX_HEIGHT/2);


			g.setColor(Color.WHITE);
			g.drawLine((int)MAX_WIDTH/2, (int)MAX_HEIGHT/2, 
				(int)(MAX_WIDTH/2 - (Vtilt[0]/Vdegree)*Math.sin(Math.toRadians(Htilt[0]))),
				(int)(MAX_HEIGHT/2 + (Vtilt[0]/Vdegree)*Math.cos(Math.toRadians(Htilt[0]))));

		}
		
		if (DRAW_CONNECTING_LINES == true)
		{
			g.setColor(Color.BLUE);
			g.drawLine((int)(MAX_WIDTH/2 - (Vtilt[0]/Vdegree)*Math.sin(Math.toRadians(Htilt[0]))), 
				(int)(MAX_HEIGHT/2 + (Vtilt[0]/Vdegree)*Math.cos(Math.toRadians(Htilt[0]))),
				(int)((LENGTH_OF_TILT_LINE/2)*Math.cos(Math.toRadians(Htilt[0])) + MAX_WIDTH/2),
				(int)((LENGTH_OF_TILT_LINE/2)*Math.sin(Math.toRadians(Htilt[0])) + MAX_HEIGHT/2));

			g.drawLine((int)(MAX_WIDTH/2 - (Vtilt[0]/Vdegree)*Math.sin(Math.toRadians(Htilt[0]))), 
				(int)(MAX_HEIGHT/2 + (Vtilt[0]/Vdegree)*Math.cos(Math.toRadians(Htilt[0]))),
				(int)(MAX_WIDTH/2 - (LENGTH_OF_TILT_LINE/2)*Math.cos(Math.toRadians(Htilt[0]))),
				(int)(MAX_HEIGHT/2 - (LENGTH_OF_TILT_LINE/2)*Math.sin(Math.toRadians(Htilt[0]))));
				
		} //if
	} //paint

	private class UpdateTiltFunction extends UpdateFunction 
	{
		private double[] Hinput;
		private double[] Vinput;
		
		private double oldH;
		private double oldV;

		public UpdateTiltFunction(double[] Hinput, double[] Vinput)
		{
			this.Hinput = Hinput; 
			this.oldH = this.Hinput[0];
			
			this.Vinput  = Vinput; 
			this.oldV = this.Vinput[0];
		}

		@Override 
		public boolean checkValues()
		{
			if ((oldH == Hinput[0]) && (oldV == Vinput[0]))
			{
			 	return false;
			}
			return true;	
		}

		public void doUpdate()
		{
			oldH = Hinput[0];
			oldV = Vinput[0];
			
			updateDisplay(oldH, oldV); 
		} //doUpdate 
	}// UpdateTiltFunction 

	public static void main(String [] args)
	{
		boolean v1 = true; //set to for testing 
      
		JFrame frame = new JFrame(); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("TiltSensor");
		frame.setSize((int)MAX_WIDTH*2, (int)MAX_HEIGHT*2);
    
    	//########TEST TILT FRAME IN MAIN FRAME############
		JPanel fpanel = new JPanel();
		fpanel.setBackground(Color.black);
		fpanel.setOpaque(true);
		frame.add(fpanel);
		fpanel.setLayout(null);
		
		double[] horizontalTilt = new double[1];
		double[] verticalTilt = new double[1];
		
		//initial values of tilt sensor 
		horizontalTilt[0] = 0;
		verticalTilt[0] = 0;

		
		TiltSensor4 test = new TiltSensor4(horizontalTilt, verticalTilt);
		fpanel.add(test);
		
    	frame.add(fpanel);
    	frame.setVisible(true);

		//Cycle through random values, to display all possible values. 
    	do
    	{
			if (horizontalTilt[0] >= 0 && horizontalTilt[0] < 120) 
				horizontalTilt[0]++;
			else if (horizontalTilt[0] >= 120)
				horizontalTilt[0]-=125;
			else if ((horizontalTilt[0] <= 0) && (horizontalTilt[0] >= -120)) 
				horizontalTilt[0]--;
			else 
				horizontalTilt[0]+=121;

			if (verticalTilt[0] >= 0 && verticalTilt[0] < 100) 
				verticalTilt[0]+=2; 
			else 
				verticalTilt[0] -=50;
					
			try 
			{	
				Thread.sleep(250);
			} 
			catch (InterruptedException e) 	
			{
				e.printStackTrace();
			}
    				
    	} while (v1=true);		//end of do-while loop. 

	}	//end of main

}
