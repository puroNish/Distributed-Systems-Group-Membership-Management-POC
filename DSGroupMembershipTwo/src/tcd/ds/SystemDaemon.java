package tcd.ds;

import java.util.Map;
import java.util.Map.Entry;

public class SystemDaemon implements Runnable{
	DSGroupManager manager ;
	
	public SystemDaemon(DSGroupManager groupManager) {
		// TODO Auto-generated constructor stub
		this.manager = groupManager;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			synchronized (manager) {
				while(true){
					manager.wait(60000);
					checkMembers();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void checkMembers() {
		Map<Integer, DSViewSimple> groupList= this.manager.getGroupList();
		for(Entry<Integer, DSViewSimple> group : groupList.entrySet()) {
			checkMembersInGroup(group.getValue());
		}
	}
	
	private void checkMembersInGroup(DSViewSimple groupView) {
		
		for(DSMember member : groupView.getAllKnownMembers()) {
			String memberId = member.getMemberId();
			long tsDiff = System.currentTimeMillis()-member.getLastChangeTimeStamp();
			if(tsDiff>60000) {
				groupView.getSpecificMember(memberId).setActive(false);
			}
		}
	}

}
