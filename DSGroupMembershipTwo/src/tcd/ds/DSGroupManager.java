package tcd.ds;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DSGroupManager {
	final String memberId;
	private Map<Integer, DSViewSimple> groupList;

	public DSGroupManager(String memberId, MulticastSocket socket) {

		this.memberId = memberId;
		this.groupList = new HashMap<Integer, DSViewSimple>();

	}

	public void addNewGroup(int groupId) {
		if (!groupList.containsKey(groupId)) {
			DSViewSimple newGroupView = new DSViewSimple(memberId);
			groupList.put(groupId, newGroupView);
		}
	}

	public void addMemberToGroup(String memberId, int groupId) {
		if (groupList.containsKey(groupId)) {
			DSViewSimple groupView = groupList.get(groupId);
			groupView.addNewMember(memberId);
		} else {
			System.out.println("Group does not Exist!!");
		}
	}

	public void showView(int groupId) {

		if (groupList.containsKey(groupId)) {
			System.out.println("Groups ID: " + groupId);
			DSViewSimple groupView = groupList.get(groupId);
//			System.out.println("My View version :: " + groupView.getViewId());
			ArrayList<DSMember> groupMembers = groupView.getAllKnownMembers();
			System.out.println("Member ID | LastActivityTimeStamp");
			for (DSMember groupMember : groupMembers) {
				if(groupMember.getActivityState()){
					System.out.println(groupMember.getMemberId() + " | " + groupMember.getLastChangeTimeStamp());
				}
				
			}
		}
	}


	public DSViewSimple getView(int groupId) {
		if (!groupList.containsKey(groupId)) {
			this.addNewGroup(groupId);
//			return null;
		}
		return groupList.get(groupId);
	}

	public boolean hasGroup(int groupId) {
		return groupList.containsKey(groupId);
	}

	public void leaveGroup(int groupId) {
		if (groupList.containsKey(groupId)) {
			groupList.remove(groupId);
		}
	}

	public Map<Integer, DSViewSimple> getGroupList() {
		// TODO Auto-generated method stub
		return groupList;
	}

}
