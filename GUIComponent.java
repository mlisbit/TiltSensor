import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * Base Component for the GUI. All classes to be used by the GUI must extend
 * this class.
 * 
 * @version 1.0
 * @author Dennis Liu, YURT 2012
 */
public class GUIComponent extends JPanel {
	/** Running status for the thread */
	public static final String THREAD_STARTED = "started";
	/** Paused status for the thread */
	public static final String THREAD_PAUSED = "paused";
	/** Stopped status for the thread */
	public static final String THREAD_STOPPED = "stopped";
	//-------------------------------------		Variables			-------------------------------------//
	/** Thread that does the updates to the display of the panel. */
	private UpdateThread updater;
	/** Functions to run inside update thread. */
	private ArrayList<UpdateFunction> threadFuncts;
	/** Status of the thread. */
	private String threadStatus;
	//-----------------------w--------------		Constructor		-------------------------------------//
	/**
	 * Constructs a JPanel with an update thread running in the background. Add
	 * functions to it by using addUpdateFunction(). Note that thread needs to be
	 * started manually. It does not start on construction. 
	 * 
	 * *several things to note: 
	 * - Layout is defaulted to null.
	 * - preferred size is set to width/height.
	 * - bounds is set to parameters.
	 * - opacity is turned on.
	 * 
	 * @param x x position of the top left of the panel. 
	 * @param y y position of the top left of the panel. 
	 * @param width width of the panel.
	 * @param height height of the panel.
	 */
	public GUIComponent(int x, int y, int width, int height) {
		super();//construct panel
		this.setLayout(null);//ensure no layout 
		this.setPreferredSize(new Dimension(width, height));
		this.setBounds(x,y, this.getPreferredSize().width, this.getPreferredSize().height);
		this.setOpaque(true);
		updater = null;
		threadFuncts = new ArrayList<UpdateFunction>();
		threadStatus = THREAD_STOPPED;
	}
	//---------------------------------		    Accessors   		-------------------------------------//
	/**
	 * Returns the status of the thread.
	 * There are three (3) possible statuses that can be returned:
	 * 1. THREAD_STARTED = "started"
	 * 2. THREAD_PAUSED = "paused"
	 * 3. THREAD_STOPPED = "stopped"
	 * 
	 * @return status of the thread.
	 */
	public String getThreadStatus() {
		return threadStatus;
	}
	//---------------------------------		Thread Functions		-------------------------------------//
	/**
	 * Adds a function to the update thread. Functions can be passed multiple times.
	 * @param threadFunct function to run in the update thread.
	 * @return if function was added.
	 */
	public boolean addUpdateThreadFunction(UpdateFunction threadFunct) {
			if(threadFunct != null) {
				threadFuncts.add(threadFunct);
				return true;
			}
		return false;
	}
	/**
	 * Removes a function from the update thread, 
	 * @param threadFunct function to remove
	 * @return if function was removed.
	 */
	public boolean removeUpdateThreadFunction(UpdateFunction threadFunct) {
			if(threadFunct != null) return threadFuncts.remove(threadFunct);
		return false;
	}
	/**
	 * Get a function running in the update thread.
	 * @param index index of the function running in the update thread.
	 * @return the function. Returns Null if function not found.
	 */
	public UpdateFunction getUpdateThreadFunction(int index) {
		if(!threadFuncts.isEmpty()) return threadFuncts.get(index);
		return null;
	}
	//------------------------------- 		Thread Functions		-------------------------------------//
	/**
	 * Starts the update thread. Value of getThreadStatus() will be changed to:
	 * 1. THREAD_STARTED = "started"
	 * 
	 * @return if thread started.
	 */
	public boolean startUpdate() {
		if(updater == null) {
			updater = new UpdateThread();
			updater.start();
		}
		if(updater.pause) {//if thread just started
			updater.pause = false;
			threadStatus = THREAD_STARTED;
			return true;
		}
		return false;
	}
	/**
	 * Pauses the update thread. Thread will pause if it is started; pausing a 
	 * paused thread will still return true.
	 * Value of getThreadStatus() will be changed to:
	 * 2. THREAD_PAUSED = "paused"
	 * 
	 * 
	 * @return if thread paused.
	 */
	public boolean pauseUpdate() {
		if(updater != null) {
			updater.pause = true;
			threadStatus = THREAD_PAUSED;
			return true;
		}
		return false;
	}
	/**
	 * Stops the update thread. Thread will stop if it has been started. Stopping
	 * a stopped thread will return false. 
	 * Value of getThreadStatus() will be changed to:
	 * 3. THREAD_STOPPED = "stopped"
	 * @return if thread was stopped.
	 */
	public boolean stopUpdate() {
		if(updater != null) {
			updater.pause = false;
			updater.isRunning = false;
			updater = null;
			threadStatus = THREAD_STOPPED;
			return true;
		}
		return false;
	}
	//-------------------------------------		Thread		-------------------------------------//
	/**
	 * Thread that updates the component display.
	 */
	private class UpdateThread extends Thread {
		public boolean isRunning = true;//default: thread is paused.
		public boolean pause = true;
		@Override
		public void run() {
		while(isRunning) {
			//pause; note that pause is a sleep, not busy waiting.
			while(pause) { try{Thread.sleep(1); } catch(Exception e) {} }//lock thread
			//run functions
			if(!threadFuncts.isEmpty()) {
				for(int i = 0; i < threadFuncts.size(); i++)
				threadFuncts.get(i).update();
			} else try{Thread.sleep(1); } catch(Exception e) {}
		}}
	}//thread class
}//class
