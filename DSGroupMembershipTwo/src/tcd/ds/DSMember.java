package tcd.ds;

import java.io.Serializable;

public class DSMember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String memberId;
	private boolean active;
	private Long lastChangeTS;

	public DSMember(String memberId) {
		
		this.memberId = memberId;
		this.active = true;
		this.lastChangeTS = System.currentTimeMillis();
	}

	public boolean getActivityState() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getLastChangeTimeStamp() {
		return lastChangeTS;
	}

	public void setLastChangeTimeStamp(Long lastChangeTimeStamp) {
		this.lastChangeTS = lastChangeTimeStamp;
	}

	public String getMemberId() {
		return memberId;
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DSMember other = (DSMember) obj;
		if (memberId == null) {
			if (other.memberId != null)
				return false;
		} else if (!memberId.equals(other.memberId))
			return false;
		return true;
	}

}
