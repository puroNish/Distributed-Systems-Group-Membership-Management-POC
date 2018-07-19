package tcd.ds;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

public class DSServer {
	private static Logger LOG;
	private final String memberId;
	private byte[] sendBuffer;
	private DSGroupManager groupManager;
	private MulticastSocket socket;
	static private Map<Integer, GroupListener> groupListeners;
	static private Map<Integer, GroupTimer> groupTimers;
	Timer timertwo;
	SystemDaemon sysDae;

	public DSServer(String memberId) throws IOException {
		LOG = Logger.getLogger(DSServer.class.getName());
		this.memberId = memberId;
		groupListeners = new HashMap<Integer, GroupListener>();
		groupTimers = new HashMap<Integer, GroupTimer>();
		this.sendBuffer = new byte[Config.BUFFER_SIZE];
		this.socket = new MulticastSocket(Config.dgPort);
		this.socket.setReuseAddress(true);
		this.socket.setLoopbackMode(false);
		this.groupManager = new DSGroupManager(this.memberId, this.socket);
		sysDae = new SystemDaemon(this.groupManager);
		new Thread(sysDae::run).start();
	}

	public void joinGroup(int groupId) {
		String group = "230.0.0." + groupId;
		try {
			final InetAddress groupAddress = InetAddress.getByName(group);
			if (!groupManager.hasGroup(groupId)) {
				groupManager.addNewGroup(groupId);
				this.socket.joinGroup(groupAddress);
				GroupListener listener = new GroupListener(groupId, groupManager);
				Thread t = new Thread(listener::run);
				t.start();
				groupListeners.put(groupId, listener);

				GroupTimer timer = new GroupTimer(groupId, groupManager, this.memberId);
				timer.startTimer();
				Thread t2 = new Thread(timer::run);
				t2.start();
				groupTimers.put(groupId, timer);

				// this.demo(groupId);
			} else if (groupListeners.containsKey(groupId)) {
				if (!groupListeners.get(groupId).getListenerStatus()) {
					groupListeners.get(groupId).startListener();
					Thread t = new Thread(groupListeners.get(groupId)::run);
					t.start();
					
					if(groupTimers.get(groupId).isTimerON()) {
						groupTimers.get(groupId).startTimer();
						Thread t2 = new Thread(groupTimers.get(groupId)::run);
						t2.start();
					}
					
				}
			}

			sendView(groupId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendView(int groupId) {
		if (groupListeners.get(groupId) != null) {
			DSViewSimple tempView = groupManager.getView(groupId);
			tempView.setViewType(1);
			if (tempView != null) {
				try {
					// LOG.info(partAddress+groupId);
					this.sendBuffer = Utils.serializeObject(tempView);
					final InetAddress groupAddress = InetAddress.getByName("230.0.0." + groupId);
					DatagramPacket datagramPacket = new DatagramPacket(this.sendBuffer, this.sendBuffer.length,
							groupAddress, Config.dgPort);
					this.socket.send(datagramPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
				LOG.info("Sent");
				// ("Sent");
			}
		}
	}

	public void leaveGroup(int groupId) {
		if (groupListeners.containsKey(groupId)) {
			GroupListener listener = groupListeners.get(groupId);
			listener.stopListener();
			// groupListeners.remove(groupId);

			this.groupManager.leaveGroup(groupId);
			sendLeaveMessage(groupId);
			GroupTimer timer = groupTimers.get(groupId);
			timer.stopTimer();
			// groupTimers.remove(groupId);
		}
	}

	private void sendLeaveMessage(int groupId) {
		DSViewSimple tempView = groupManager.getView(groupId);
		tempView.setViewType(-1);
		tempView.getSpecificMember(this.memberId).setActive(false);
		tempView.getSpecificMember(this.memberId).setLastChangeTimeStamp(System.currentTimeMillis());
		if (tempView != null) {
			try {
				// LOG.info(partAddress+groupId);
				this.sendBuffer = Utils.serializeObject(tempView);
				final InetAddress groupAddress = InetAddress.getByName("230.0.0." + groupId);
				DatagramPacket datagramPacket = new DatagramPacket(this.sendBuffer, this.sendBuffer.length,
						groupAddress, Config.dgPort);
				this.socket.send(datagramPacket);
			} catch (Exception e) {
				e.printStackTrace();
			}
			LOG.info("Sent");
			// ("Sent");
		}
	}

	public void showMyView(int groupId) {
		groupManager.showView(groupId);
	}

	public void sendStatusMessage(int groupId) {
		DSViewSimple tempView = groupManager.getView(groupId);
		tempView.setViewType(0);
		tempView.getSpecificMember(this.memberId).setActive(true);
		tempView.getSpecificMember(this.memberId).setLastChangeTimeStamp(System.currentTimeMillis());
		try {
			// LOG.info(partAddress+groupId);
			this.sendBuffer = Utils.serializeObject(tempView);
			final InetAddress groupAddress = InetAddress.getByName("230.0.0." + groupId);
			DatagramPacket datagramPacket = new DatagramPacket(this.sendBuffer, this.sendBuffer.length, groupAddress,
					Config.dgPort);
			this.socket.send(datagramPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMemberId() {
		return this.memberId;
	}

}
