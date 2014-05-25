package octopus.semantic.similarity.resource.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import octopus.semantic.similarity.resource.GraphResource;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;

import rainbownlp.util.ConfigurationUtil;
import slib.sglib.algo.graph.validator.dag.ValidatorDAG;
import slib.sglib.io.conf.GDataConf;
import slib.sglib.io.loader.GraphLoaderGeneric;
import slib.sglib.io.util.GFormat;
import slib.sglib.model.graph.G;
import slib.sglib.model.graph.elements.E;
import slib.sglib.model.graph.utils.Direction;
import slib.sglib.model.impl.graph.memory.GraphMemory;
import slib.sglib.model.impl.repo.URIFactoryMemory;
import slib.sglib.model.repo.URIFactory;
import slib.utils.ex.SLIB_Ex_Critic;

public class Mesh extends GraphResource{
	HashMap<String, List<String>> wordSenseIdMap = new HashMap<String, List<String>>();
	G meshGraph;
     
	public static void main(String[] args) throws Exception{
		ConfigurationUtil.init("config.properties");
    	Mesh mesh = new Mesh();
		System.out.println(mesh);
		

	}
	
	public Mesh() throws Exception{
	    URI meshURI = getURI("");

        meshGraph = new GraphMemory(meshURI);
        String meshFilePath = ConfigurationUtil.getValue("meshPath");
       GDataConf dataMeshXML = new GDataConf(GFormat.MESH_XML, meshFilePath); // the DTD must be located in the same directory
       GraphLoaderGeneric.populate(dataMeshXML, meshGraph);

        System.out.println(meshGraph);

        /*
         * We remove the cycles of the graph in order to obtain 
         * a rooted directed acyclic graph (DAG) and therefore be able to 
         * use most of semantic similarity measures.
         * see http://semantic-measures-library.org/sml/index.php?q=doc&page=mesh
         */

        // We check the graph is a DAG: answer NO
        ValidatorDAG validatorDAG = new ValidatorDAG();
        boolean isDAG = validatorDAG.containsTaxonomicDag(meshGraph);

        System.out.println("MeSH Graph is a DAG: " + isDAG);

        // We remove the cycles
        removeMeshCycles(meshGraph);

        isDAG = validatorDAG.containsTaxonomicDag(meshGraph);

        // We check the graph is a DAG: answer Yes
        System.out.println("MeSH Graph is a DAG: " + isDAG);
        
        setGraph(meshGraph);
	}
	/**
     * Remove the cycles from the MeSH Graph see
     * http://semantic-measures-library.org/sml/index.php?q=doc&page=mesh 
     * for more information
     *
     * @param meshGraph the graph associated to the MeSH
     *
     * @throws SLIB_Ex_Critic
     */
    public void removeMeshCycles(G meshGraph) throws SLIB_Ex_Critic {
        URIFactory factory = URIFactoryMemory.getSingleton();

        // We remove the edges creating cycles
        URI ethicsURI = getURI("D004989");
        URI moralsURI = getURI("D009014");

        // We retrieve the direct subsumers of the concept (D009014)
        Set<E> moralsEdges = meshGraph.getE(RDFS.SUBCLASSOF, moralsURI, Direction.OUT);
        for (E e : moralsEdges) {

            System.out.println("\t" + e);
            if (e.getTarget().equals(ethicsURI)) {
                System.out.println("\t*** Removing edge " + e);
                meshGraph.removeE(e);
            }
        }

        ValidatorDAG validatorDAG = new ValidatorDAG();
        boolean isDAG = validatorDAG.containsTaxonomicDag(meshGraph);

        System.out.println("MeSH Graph is a DAG: " + isDAG);

        // We remove the edges creating cycles
        // see http://semantic-measures-library.org/sml/index.php?q=doc&page=mesh

        URI hydroxybutyratesURI = getURI("D006885");
        URI hydroxybutyricAcidURI = getURI("D020155");

        // We retrieve the direct subsumers of the concept (D009014)
        Set<E> hydroxybutyricAcidEdges = meshGraph.getE(RDFS.SUBCLASSOF, hydroxybutyricAcidURI, Direction.OUT);
        for (E e : hydroxybutyricAcidEdges) {

            System.out.println("\t" + e);
            if (e.getTarget().equals(hydroxybutyratesURI)) {
                System.out.println("\t*** Removing edge " + e);
                meshGraph.removeE(e);
            }
        }

    }

	@Override
	public String getResourceName() {
		return "MESH";
	}
	
	@Override
	public String getSearchableContentForWord(String word) {
		String searchableContent = "TODO:COMPLETE";//meshGraph.getIDForConcept(word);
//		if(searchableContent == null) searchableContent = word.toLowerCase();
		System.out.println(word+" mapped to-> "+searchableContent);
		return searchableContent;
	}

}
