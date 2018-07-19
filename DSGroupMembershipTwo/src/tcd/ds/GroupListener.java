package tcd.ds;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupListener implements Runnable {
	// private final int BUFFER_SIZE = 65535;
	final int groupId;
	private byte[] receiveBuffer;
	private MulticastSocket socket;
	private boolean listenerActive = true;

	public boolean isObligation() {
		return obligation;
	}

	public void setObligation(boolean obligation) {
		this.obligation = obligation;
	}

	private DSGroupManager manager;
	private static Logger log;
	private boolean obligation = false;
	private Map<String, Boolean> activeMembers;

	public GroupListener(int groupId, DSGroupManager manager) throws IOException {
		log = Logger.getLogger(GroupListener.class.getName());
		this.manager = manager;
		this.groupId = groupId;
		this.receiveBuffer = new byte[Config.BUFFER_SIZE];
		this.socket = new MulticastSocket(Config.dgPort);
		this.socket.setReuseAddress(true);
		this.socket.setLoopbackMode(false);
		this.activeMembers = new HashMap<String, Boolean>();
	}

	private void processPacket(DatagramPacket packet) throws ClassNotFoundException, IOException {
		final DSViewSimple receivedView = (DSViewSimple) Utils.deserializeObject(packet.getData());
		DSViewSimple myView = this.manager.getView(groupId);
		boolean viewChanged = false;
		int messageType = receivedView.getViewType();
		String gotViewFrom = receivedView.getMemberId();

		if (messageType == 0) {
			activeMembers.put(gotViewFrom, true);
			DSMember member = receivedView.getSpecificMember(gotViewFrom);
			if (myView.getAllKnownMembers().contains(member)) {
				myView.getSpecificMember(gotViewFrom).setActive(true);
				member.getLastChangeTimeStamp();
				myView.getSpecificMember(gotViewFrom).setLastChangeTimeStamp(member.getLastChangeTimeStamp());
				myView.getSpecificMember(gotViewFrom).getLastChangeTimeStamp();
			}
		}

		if (messageType == -1) {
			activeMembers.put(gotViewFrom, false);
			DSMember member = receivedView.getSpecificMember(gotViewFrom);
			if (myView.getAllKnownMembers().contains(member)) {
				myView.getSpecificMember(gotViewFrom).setActive(false);
				myView.getSpecificMember(gotViewFrom).setLastChangeTimeStamp(member.getLastChangeTimeStamp());
			}
		}
		if (messageType == 1) {
			activeMembers.put(gotViewFrom, true);
			DSMember member = receivedView.getSpecificMember(gotViewFrom);
			if (!myView.getAllKnownMembers().contains(member)) {
				myView.addNewMember(gotViewFrom);
				myView.getSpecificMember(gotViewFrom).setActive(true);
				myView.getSpecificMember(gotViewFrom).setLastChangeTimeStamp(member.getLastChangeTimeStamp());
			}
		} else {
			for (DSMember member : receivedView.getAllKnownMembers()) {
				if (myView.getAllKnownMembers().contains(member)) {
					String tempMemberId = member.getMemberId();
					DSMember temp = myView.getSpecificMember(tempMemberId);
					if (temp.getLastChangeTimeStamp() < member.getLastChangeTimeStamp()
							&& activeMembers.get(gotViewFrom)) {
						temp.setActive(member.getActivityState());
						temp.setLastChangeTimeStamp(member.getLastChangeTimeStamp());
						viewChanged = true;
					}
				} else {
					myView.addExistingMember(member);
					viewChanged = true;
				}
			}
		}

		if (viewChanged) {
			// send View
			this.setObligation(true);
		}

	}

	public Map<String, Boolean> getActiveMemberList() {
		return this.activeMembers;
	}

	public void stopListener() {
		this.listenerActive = false;
		// log.log(Level.INFO, "Stopping Listener");
	}

	public void startListener() {
		this.listenerActive = true;
	}

	public boolean getListenerStatus() {
		return this.listenerActive;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			DatagramPacket packet = new DatagramPacket(this.receiveBuffer, this.receiveBuffer.length);
			final InetAddress groupAddress = InetAddress.getByName("230.0.0." + groupId);
			this.socket.joinGroup(groupAddress);
			log.info(">> Actively Listening for group: " + this.groupId + " <<");
			while (listenerActive) {
				this.socket.receive(packet);
				// log.info(">> Packet Received on " + this.groupId + " <<");
				processPacket(packet);
			}
			this.socket.leaveGroup(groupAddress);
			log.log(Level.INFO, "Left Group :" + this.groupId);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
			// e.printStackTrace();
		}

	}

}
