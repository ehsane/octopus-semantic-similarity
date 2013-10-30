package octopus.semantic.similarity.resource;


import org.openrdf.model.URI;

import slib.sglib.io.conf.GDataConf;
import slib.sglib.io.loader.GraphLoaderGeneric;
import slib.sglib.io.util.GFormat;
import slib.sglib.model.graph.G;
import slib.sglib.model.impl.graph.memory.GraphMemory;
import slib.sglib.model.impl.repo.URIFactoryMemory;
import slib.sglib.model.repo.URIFactory;
import slib.utils.ex.SLIB_Exception;


public abstract class GraphResource implements IMSRResource{
	final ResourceType resourceType = ResourceType.GRAPH;
	URIFactory factory = URIFactoryMemory.getSingleton();
	G graph;
    
	public G getGraph(){
		return graph;
	}
	protected void loadGraph(String graphFile, GFormat format) throws SLIB_Exception{
		if(graph==null){
			URI graph_uri = factory.createURI("http://graph/"+getResourceName()+"/");
			graph  = new GraphMemory(graph_uri);
		}
		GDataConf graphconf = new GDataConf(format, graphFile);
		GraphLoaderGeneric.populate(graphconf, graph);
	}
	public GraphResource(){
	}

	public ResourceType getResourceType() {
		return resourceType;
	}
	public URI getWordURI(String word) {
		return factory.createURI("http://graph/"+getResourceName()+"/"+word);
	}
	
	@Override
	public String toString(){
		return getResourceName()+" - "+graph.toString();
	}
	
}
