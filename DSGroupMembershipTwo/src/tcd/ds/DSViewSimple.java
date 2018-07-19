package tcd.ds;

import java.io.Serializable;
import java.util.ArrayList;

public class DSViewSimple implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private int version;
	private final String memberId;
	private ArrayList<DSMember> knownMembers;
	private int viewType;

	public int getViewType() {
		return viewType;
	}
	
	public String getMemberId() {
		return memberId;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public DSViewSimple(String memberId) {
		this.memberId = memberId;
//		this.version = 1;
		knownMembers = new ArrayList<DSMember>();
		addNewMember(this.memberId);
	}

	public void addNewMember(String memberId) {
		DSMember newMember = new DSMember(memberId);
		knownMembers.add(newMember);
	}
	
	public void addExistingMember(DSMember exMember){
		this.knownMembers.add(exMember);
	}
	
	public ArrayList<DSMember> getAllKnownMembers(){
		return this.knownMembers;
	}
	
	public DSMember getSpecificMember(String memberId){
		DSMember tempMember = new DSMember(memberId);
			if(this.knownMembers.contains(tempMember)){
				int index = this.knownMembers.indexOf(tempMember);
				tempMember = this.knownMembers.get(index);
			}
			return tempMember;
	}
//	
//	public void leaveGroup(){
//		DSMember myInstance = this.getSpecificMember(this.memberId);
//		myInstance.setActive(false);
//		myInstance.setLastChangeTimeStamp(System.currentTimeMillis());
//	}


}

