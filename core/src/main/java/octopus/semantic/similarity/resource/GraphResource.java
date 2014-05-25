package octopus.semantic.similarity.resource;


import org.openrdf.model.URI;

import slib.sglib.io.conf.GDataConf;
import slib.sglib.io.loader.GraphLoaderGeneric;
import slib.sglib.io.util.GFormat;
import slib.sglib.model.graph.G;
import slib.sglib.model.impl.graph.memory.GraphMemory;
import slib.sglib.model.impl.repo.URIFactoryMemory;
import slib.sglib.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;


public abstract class GraphResource implements IMSRResource{
	final ResourceType resourceType = ResourceType.GRAPH;
	URIFactory factory = URIFactoryMemory.getSingleton();
	G graph;
	private SM_Engine engine = null;
    
	public G getGraph(){
		return graph;
	}
	protected void setGraph(G pGraph){
		graph = pGraph;
	}
	protected void loadGraph(String graphFile, GFormat format) throws SLIB_Exception{
		if(graph==null){
			URI graph_uri = factory.getURI("http://graph/"+getResourceName()+"/");
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
		String searchableContent = getSearchableContentForWord(word);
		return getURI(searchableContent);
	}
	
	public URI getURI(String conceptId) {
		return factory.getURI("http://graph/"+getResourceName()+"/"+conceptId);
	}
	@Override
	public String toString(){
		return getResourceName()+" - "+graph.toString();
	}
	
	@Override
	public String getSearchableContentForWord(String word) {
		return word;
	}
	
	public SM_Engine getSMEngine() {
		if(engine==null)
			try {
				engine  = new SM_Engine(graph);
			} catch (SLIB_Ex_Critic e) {
				e.printStackTrace();
			}
		return engine;
	}
	
}
