package octopus.semantic.similarity.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceConfigManager {
	private List<IMSRResource> resourceList = new ArrayList<IMSRResource>();

	public List<IMSRResource> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<IMSRResource> resourceList) {
		this.resourceList = resourceList;
	}
}
