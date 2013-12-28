package octopus.semantic.similarity.resource.graph.slibextend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import slib.sglib.io.conf.GDataConf;
import slib.sglib.io.loader.GraphLoader;
import slib.sglib.io.loader.bio.mesh.GraphLoader_MESH_XML;
import slib.sglib.model.graph.G;
import slib.sglib.model.graph.elements.E;
import slib.sglib.model.impl.graph.elements.Edge;
import slib.sglib.model.impl.repo.URIFactoryMemory;
import slib.sglib.model.repo.URIFactory;
import slib.sglib.model.voc.SLIBVOC;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;

public class MESHXMLGraphLoader implements GraphLoader {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    Map<String, MeshConcept> idToConcepts = new HashMap<String, MeshConcept>();
    Map<String, MeshConcept> conceptToId = new HashMap<String, MeshConcept>();
    Set<MeshConcept> concepts = new HashSet<MeshConcept>();
    G graph;
    URIFactory factory = URIFactoryMemory.getSingleton();
    /**
     *
     */
    public static final String ARG_PREFIX = "prefix";
    String default_namespace;

    /**
     * Return parent ID i.e. giving C10.228.140.300.275.500 will return
     * C10.228.140.300.275
     *
     * @return a String associated to the parent ID.
     */
    private String getParentId(String id) {

        String[] data = id.split("\\.");
        String idParent = null;

        for (int i = data.length - 2; i >= 0; i--) {
            if (idParent == null) {
                idParent = data[i];
            } else {
                idParent = data[i] + "." + idParent;
            }
        }
        return idParent;
    }

    void addConcept(MeshConcept concept) {
        for (String s : concept.treeNumberList) {
            idToConcepts.put(s, concept);
            conceptToId.put(concept.getDescriptorName().toLowerCase(), concept);
            for(String conceptName : concept.getConceptNamesList())
            	conceptToId.put(conceptName, concept);
        }
        concepts.add(concept);
    }


    @Override
    public void populate(GDataConf conf, G g) throws SLIB_Exception {

        this.graph = g;


        default_namespace = (String) conf.getParameter(ARG_PREFIX);

        if (default_namespace == null) {
            default_namespace = graph.getURI().getNamespace();
        }
        try {
            logger.info("-------------------------------------");
            logger.info("Loading Mesh XML");
            logger.info("-------------------------------------");
            idToConcepts = new HashMap<String, MeshConcept>();
            SAXParserFactory parserfactory = SAXParserFactory.newInstance();
            SAXParser saxParser;

            saxParser = parserfactory.newSAXParser();
            saxParser.parse(conf.getLoc(), new XMLHandler(this));


            logger.info("Number of descriptor loaded " + concepts.size());
            logger.info("Loading relationships ");

            // Create universal root if required
            URI universalRoot = SLIBVOC.THING_OWL;

            if (!graph.containsVertex(universalRoot)) {
                graph.addV(universalRoot);
            }

            // create relationships and roots of each tree
            for (Entry<String, MeshConcept> e : idToConcepts.entrySet()) {

                MeshConcept c = e.getValue();

                URI vConcept = getOrCreateVertex(c.descriptorUI);


                for (String treeNumber : c.treeNumberList) {

                    String parentId = getParentId(treeNumber);

                    if (parentId != null) {

                        MeshConcept parent = idToConcepts.get(parentId);

                        if (parent == null) {
                            throw new SLIB_Ex_Critic("Cannot locate parent identified by TreeNumber " + treeNumber);
                        } else {

                            //System.out.println("\t" + parentId + "\t" + parent.descriptorUI);
                            URI vParent = getOrCreateVertex(parent.descriptorUI);
                            E edge = new Edge(vConcept, RDFS.SUBCLASSOF, vParent);

                            g.addE(edge);
                        }
                    } else {
                        /* Those vertices are the inner roots of each trees, 
                         * i.e. Psychiatry and Psychology [F] tree has for inner roots:
                         *  - Behavior and Behavior Mechanisms [F01] 
                         *  - Psychological Phenomena and Processes [F02] 
                         *  - Mental Disorders [F03] 
                         *  - Behavioral Disciplines and Activities [F04] 
                         * A vertex has already been created for each inner root (e.g. F01, F02, F03, F04) 
                         * , we therefore create a vertex for the tree root (e.g. F).
                         * Finally all the tree roots are rooted by a global root which do not 
                         * correspond to a concept specified into the mesh.
                         * 
                         * More information about MeSH trees at http://www.nlm.nih.gov/mesh/trees.html
                         */

                        // we link the tree inner root to the root tree
                        char localNameTreeRoot = treeNumber.charAt(0); // id of the tree root
                        URI rootTree = getOrCreateVertex(localNameTreeRoot + ""); // e.g. F
                        E treeInnerRootToTreeRoot = new Edge(vConcept, RDFS.SUBCLASSOF, rootTree);
                        g.addE(treeInnerRootToTreeRoot);
//                        logger.debug("Creating Edge : " + treeInnerRootToTreeRoot);

                        // we link the tree root to the universal root
                        E treeRootToUniversalRoot = new Edge(rootTree, RDFS.SUBCLASSOF, universalRoot);
                        g.addE(treeRootToUniversalRoot);
//                        logger.debug("Creating Edge : " + treeRootToUniversalRoot);
                    }
                }
            }

        } catch (Exception ex) {
            throw new SLIB_Ex_Critic(ex.getMessage());
        }

        logger.info("MESH loader - process performed");
        logger.info("-------------------------------------");
    }

    private URI getOrCreateVertex(String descriptorUI) {

        String uriConceptAsString = default_namespace + descriptorUI;

        URI uriConcept = factory.createURI(uriConceptAsString);

        if (!graph.containsVertex(uriConcept)) {
            graph.addV(uriConcept);
        }
        return uriConcept;
    }
    
    public String getIDForConcept(String concept){
    	String uriID = null;
    	String conceptLowerCase = concept.toLowerCase();
    	MeshConcept mc = conceptToId.get(conceptLowerCase);
		if(mc!=null){
    		uriID = mc.getDescriptorUI();
    	}
    	return uriID;
    }
    
    public class MeshConcept{
        
        String descriptorUI;
        String descriptorName;
        Set<String> treeNumberList;
        Set<String> conceptNamesList;
        /**
         *
         */
        public MeshConcept(){
            treeNumberList = new HashSet<String>();
        }

        /**
         *
         * @return the descriptor
         */
        public String getDescriptorUI() {
            return descriptorUI;
        }

        /**
         *
         * @param descriptorUI
         */
        public void setDescriptorUI(String descriptorUI) {
            this.descriptorUI = descriptorUI;
        }

        /**
         *
         * @return the name of the descriptor.
         */
        public String getDescriptorName() {
            return descriptorName;
        }

        /**
         *
         * @param descriptorName
         */
        public void setDescriptorName(String descriptorName) {
            this.descriptorName = descriptorName;
        }
        
        
        /**
         *
         * @param treeNumber
         */
        public void addTreeNumber(String treeNumber){
            treeNumberList.add(treeNumber);
        }
        
        @Override
        public String toString(){
            String out = descriptorUI +"\n";
            out += "\t"+descriptorName+"\n";
            out += "\t"+treeNumberList+"\n";
            return out;
        }

		public Set<String> getConceptNamesList() {
			return conceptNamesList;
		}

		public void addConceptName(String name) {
			if(conceptNamesList==null) conceptNamesList = new HashSet<String>();
			conceptNamesList.add(name.toLowerCase());
		}
    }
    
    public class XMLHandler extends DefaultHandler {

        MESHXMLGraphLoader loader;
        public MeshConcept concept;
        boolean descriptorName = false;
        boolean descriptorUI = false;
        boolean treeNumber = false;
        boolean inConcept = false;
        boolean inTerm = false;
        boolean inString = false;
        final String DESCRIPTOR_RECORD = "DescriptorRecord";
        final String DESCRIPTOR_URI = "DescriptorUI";
        final String DESCRIPTOR_NAME = "DescriptorName";
        final String TREE_NUMBER = "TreeNumber";
        final String CONCEPT = "ConceptName";
        final String TERM = "Term";
        final String STRING = "String";
        

        /**
         * Create a XML handler for MeSH.
         *
         * @param loader the loader associated to the handler
         */
        public XMLHandler(MESHXMLGraphLoader loader) {
            this.loader = loader;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            if (qName.equals(DESCRIPTOR_RECORD)) {// start creation of a concept
                concept = new MeshConcept();
            }

            if (qName.equals(DESCRIPTOR_URI) && concept.descriptorUI == null) {
                descriptorUI = true;
            }

            if (qName.equals(DESCRIPTOR_NAME) && concept.descriptorName == null) { // define descriptor name
                descriptorName = true;
            }

            if (qName.equals(TREE_NUMBER)) {// define tree number
                treeNumber = true;
            }
            
            if (qName.equals(CONCEPT)) {// define concept
                inConcept = true;
            }
            
            if (qName.equals(TERM)) {// define concept
                inTerm = true;
            }
            
            if (qName.equals(STRING)) {// define concept
                inString = true;
            }
        }

        @Override
        public void endElement(String uri, String localName,
                String qName) throws SAXException {

            if (descriptorUI) {
                descriptorUI = false;
            } else if (descriptorName) {
                descriptorName = false;
            } else if (treeNumber) {
                treeNumber = false;
            } else if (inConcept && inString) {
            	inConcept = false;
            }else if (inTerm && inString) {
            	inTerm = false;
            }
            if(inString) inString=false;

            if (qName.equals(DESCRIPTOR_RECORD)) {
                loader.addConcept(concept);
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {

            if (descriptorUI) {
                concept.descriptorUI = new String(ch, start, length);
            } else if (descriptorName) {
                concept.descriptorName = new String(ch, start, length);
            } else if (treeNumber) {
                concept.addTreeNumber(new String(ch, start, length));
            } else if (inString&&(inConcept || inTerm)) {
                concept.addConceptName(new String(ch, start, length));
            }
        }
    }

}


