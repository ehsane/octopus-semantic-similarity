package octopus.semantic.similarity.msr;

import java.util.ArrayList;
import java.util.List;

public class MSRConfigManager {
	private List<IMSR> msrList = new ArrayList<IMSR>();

	public List<IMSR> getMsrList() {
		return msrList;
	}

	public void setMsrList(List<IMSR> msrList) {
		this.msrList = msrList;
	}
}
