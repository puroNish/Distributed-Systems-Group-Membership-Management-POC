package tcd.ds;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class TimedTask {
	Timer timer;
	final int groupId;
	final String memberId;
	boolean startTimer = true;
	public static void main(String[] args){
		new TimedTask(5, 1, "nish");
	}
	
	public TimedTask(int seconds, int groupId, String memberId) {
		this.groupId = groupId;
		this.memberId = memberId;
		timer = new Timer();
		timer.schedule(new RemindTask(groupId, memberId), seconds * 1000);
	}

	class RemindTask extends TimerTask {
		int cmd=0;
		public RemindTask(int groupId, String memberId) {
			// TODO Auto-generated constructor stub
			try {
				DSServer server = new DSServer(memberId);
				server.sendStatusMessage(groupId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void startTimer(){
			if(startTimer){
				new TimedTask(5, 1, "nish");
			}
		}

		public void run() {
			System.out.println("Time's up!");
			// Terminate the timer thread
			timer.cancel();
			startTimer();
		}
	}
}
