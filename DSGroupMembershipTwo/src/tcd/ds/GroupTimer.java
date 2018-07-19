package tcd.ds;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class GroupTimer implements Runnable {
	final int groupId;
	private final String memberId;
	private DSGroupManager manager;
	private MulticastSocket socket;
	final InetAddress groupAddress;
	private boolean timerOn = false;
	private boolean stopTimer;

	public GroupTimer(int groupId, DSGroupManager manager, String memberId) throws IOException {
		this.groupId = groupId;
		this.manager = manager;
		this.memberId = memberId;
		this.socket = new MulticastSocket(Config.dgPort);
//		this.socket.setReuseAddress(true);
		this.socket.setLoopbackMode(false);
		this.groupAddress = InetAddress.getByName("230.0.0." + groupId);
		this.socket.joinGroup(groupAddress);
	}

	@Override
	public void run() {
		timerOn = true;
		stopTimer = false;
		try {
			
			synchronized (manager) {
				while (!stopTimer) {
					manager.wait(30000);
					if(timerOn) {
						sendSyncView();
					} else {
						stopTimer();
//						stopTimer = true;
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopTimer() {
		this.timerOn = false;
		this.stopTimer = true;
	}
	
	public void startTimer() {
		this.timerOn = true;
		this.stopTimer = false;
	}
	
	public boolean isTimerON() {
		return this.timerOn;
	}

	private void sendSyncView() {
		// TODO Auto-generated method stub
		DSViewSimple tempView = manager.getView(groupId);
		tempView.setViewType(0);
		// tempView.getSpecificMember(this.memberId).setActive(true);
		tempView.getSpecificMember(this.memberId).setLastChangeTimeStamp(System.currentTimeMillis());
		byte[] sendBuffer = new byte[Config.BUFFER_SIZE];
		try {
			
			sendBuffer = Utils.serializeObject(tempView);
			DatagramPacket datagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, groupAddress,
					Config.dgPort);
			this.socket.send(datagramPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
