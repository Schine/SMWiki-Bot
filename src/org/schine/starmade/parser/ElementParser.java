package org.schine.starmade.parser;

import java.io.BufferedInputStream;

import org.schema.schine.resource.FileExt;
import org.schine.starmade.data.element.BlockStyle;
import org.schine.starmade.data.element.ElementCategory;
import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;
import org.schine.starmade.data.element.annotation.ElemType;
import org.schine.starmade.data.element.exception.ElementParserException;
import org.schine.starmade.data.element.factory.FactoryResource;
import org.schine.starmade.data.element.factory.FixedRecipe;
import org.schine.starmade.data.element.factory.FixedRecipeProduct;
import org.schine.starmade.data.element.factory.FixedRecipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.vecmath.Vector4f;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Parsing the Element Configuration
 *
 * @author Schema
 */
public class ElementParser {

	public static Properties properties;
	private final ArrayList<ElementInformation> infoElements = new ArrayList<ElementInformation>();

	
	
	private static final Object2ObjectOpenHashMap<String, ElemType> elemTypeLowerCase = new Object2ObjectOpenHashMap<String, ElemType>();
	static {
		ElemType[] vals = ElemType.values();
		for(ElemType t : vals)
		elemTypeLowerCase.put(t.tag.toLowerCase(Locale.ENGLISH), t);
	}
	private ElementCategory rootCategory;
	private FixedRecipes fixedRecipes;
	public static String currentName;
	private Document doc;

	public static String[] getTypeStringArray() {
		return new String[]{"General", "Power", "Terrain", "Ship", "Element", "SpaceStation", "General", "DeathStar", "Factory", "Manufacturing", "Mineral", "Hulls", "Docking", "Doors", "Light", "Effect", "Logic", "Decoration"};
	}

	/**
	 * @return the fixedRecipes
	 */
	public FixedRecipes getFixedRecipes() {
		return fixedRecipes;
	}

	/**
	 * @param fixedRecipes the fixedRecipes to set
	 */
	public void setFixedRecipes(FixedRecipes fixedRecipes) {
		this.fixedRecipes = fixedRecipes;
	}

	/**
	 * @return the infoElements
	 */
	public ArrayList<ElementInformation> getInfoElements() {
		return infoElements;
	}

	/**
	 * @return the rootCategory
	 */
	public ElementCategory getRootCategory() {
		return rootCategory;
	}

	private void handleBaicElementNode(Node node, ElementCategory cat) throws ElementParserException {
		if (!node.hasAttributes()) {
			handleTypeNodeRead(node, cat);
		} else {
			handleElementNode(node, cat);
		}
	}

	private void handleElementNode(Node node, ElementCategory cat) throws ElementParserException {
		String type = node.getParentNode().getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null && attributes.getLength() != 4) {
			throw new ElementParserException("Element has wrong attribute count (" + attributes.getLength() + ", but should be 4)");
		}

		Node typeNode = ElemType.parseType(node, attributes, "type");

		Node iconNode = ElemType.parseType(node, attributes, "icon");

		Node textureIdNode = ElemType.parseType(node, attributes, "textureId");

		Node nameNode = ElemType.parseType(node, attributes, "name");

		short typeId = 0;
		String typeProperty = properties.getProperty(typeNode.getNodeValue());
		if (typeProperty == null) {
			throw new ElementParserException("The value of \"" + typeNode.getNodeName() + "\" has not been found in " + nameNode.getNodeValue() + " (" + typeNode.getNodeValue() + ")");
		}
		try {
			typeId = (short) Integer.parseInt(typeProperty);
		} catch (NumberFormatException e) {
			throw new ElementParserException("The property " + typeProperty + " has to be an Integer value in " + node.getNodeName() + "<-" + node.getParentNode().getNodeName() + "; node value: " + typeNode.getNodeValue());
		}
		currentName = nameNode.getNodeValue();
		ElementInformation info = new ElementInformation(typeId, nameNode.getNodeValue(), cat, parseTextureIdAttribute(textureIdNode));

		info.idName = typeNode.getNodeValue().trim();
		info.parsed.add("BuildIcon");
		info.parsed.add("Texture");
		info.parsed.add("Name");

		if (info.getId() < 0 || info.getId() >= 2048) {
			throw new ElementParserException("Element type has to be between [0, 2048[ for " + node.getNodeName());
		}
		if (info.getTextureId(0) < 0 || info.getTextureId(0) >= 256 * 8) {
			throw new ElementParserException("Texture Id has to be between [0, " + (256 * 8) + "[ for " + node.getNodeName() + "; but was: " + info.getTextureId(0));
		}

		info.setBuildIconNum(ElemType.parseInt(iconNode));
		if (info.getBuildIconNum() < 0 || info.getBuildIconNum() >= 256 * 8) {
			throw new ElementParserException("Icon has to be between [0, " + (256 * 8) + "[ for " + node.getNodeName() + " but was " + info.getBuildIconNum());
		}

		parseInfoNode(node, info);

		assert(info.getBlockStyle() != null):info;
		for (int i = 0; i < 6; i++) {
			if (info.getTextureId(i) < 0) {
				break;
			}
		}
		for (int i = 0; i < 6; i++) {
			assert (info.getTextureId(i) >= 0);
		}
		info.recreateTextureMapping();
		if(info.getBlockStyle() != BlockStyle.NORMAL){
			info.setOrientatable(false);
		}
		infoElements.add(info);

		cat.getInfoElements().add(info);
	}

	private void handleRecipeProductNode(Node node, FixedRecipe r) throws ElementParserException {
		ArrayList<FactoryResource[]> inputs = new ArrayList<FactoryResource[]>();
		ArrayList<FactoryResource[]> outputs = new ArrayList<FactoryResource[]>();
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null && attributes.getLength() != 3) {
			throw new ElementParserException(node.getNodeName() + ": Recipe has wrong attribute count (" + attributes.getLength() + ", but should be 2)");
		}

		Node typeNode = ElemType.parseType(node, attributes, "costType");
		Node typeAmountNode = ElemType.parseType(node, attributes, "costAmount");
		Node typeNameNode = ElemType.parseType(node, attributes, "name");

		short typeId = 0;

		if (typeNode.getNodeValue().toLowerCase(Locale.ENGLISH).equals("credits")) {
			typeId = -1; //buy with credits
		} else {
			String typeProperty = properties.getProperty(typeNode.getNodeValue());
			if (typeProperty == null) {
				throw new ElementParserException("The value of \"" + typeNode.getNodeName() + "\" has not been found. was: " + typeNode.getNodeValue());
			}
			try {
				typeId = (short) Integer.parseInt(typeProperty);
			} catch (NumberFormatException e) {
				throw new ElementParserException("The property " + typeProperty + " has to be an Integer value");
			}
		}

		int amount = 0;
		try {
			amount = Integer.parseInt(typeAmountNode.getNodeValue());
		} catch (NumberFormatException e) {
			throw new ElementParserException("The property 'amount' " + typeAmountNode.getParentNode().getNodeName() + " has to be an Integer value");
		}

		r.costAmount = amount;
		r.costType = typeId;
		r.name = typeNameNode.getNodeValue();

		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node product = childNodes.item(i);
			if (product.getNodeType() == Node.ELEMENT_NODE) {
				if (!product.getNodeName().toLowerCase(Locale.ENGLISH).equals("product")) {
					throw new ElementParserException("All child nodes of " + product.getNodeName() + " have to be \"product\" but is " + product.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
				}
				NodeList productChilds = product.getChildNodes();
				FactoryResource[] input = null;
				FactoryResource[] output = null;
				for (int g = 0; g < productChilds.getLength(); g++) {
					Node item = productChilds.item(g);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						if (!item.getNodeName().toLowerCase(Locale.ENGLISH).equals("output")
								&& !item.getNodeName().toLowerCase(Locale.ENGLISH).equals("input")) {
							throw new ElementParserException("All child nodes of " +
									node.getNodeName() + " have to be \"output\" or \"input\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
						}
						if (item.getNodeName().toLowerCase(Locale.ENGLISH).equals("input")) {
							input = ElemType.parseResource(item);
						}
						if (item.getNodeName().toLowerCase(Locale.ENGLISH).equals("output")) {
							output = ElemType.parseResource(item);
						}

					}
				}
				if (input == null) {
					throw new ElementParserException("No input defined for " + node.getNodeName() + " in (" + node.getParentNode().getNodeName() + ")");
				}
				if (output == null) {
					throw new ElementParserException("No output defined for " + node.getNodeName() + " in (" + node.getParentNode().getNodeName() + ")");
				}

				inputs.add(input);
				outputs.add(output);
			}
		}
		if (inputs.size() != outputs.size()) {
			throw new ElementParserException("Factory Parsing failed for " + node.getNodeName() + " in (" + node.getParentNode().getNodeName() + ")");
		}

		r.recipeProducts = new FixedRecipeProduct[inputs.size()];

		for (int i = 0; i < r.recipeProducts.length; i++) {
			r.recipeProducts[i] = new FixedRecipeProduct();
			r.recipeProducts[i].input = inputs.get(i);
			r.recipeProducts[i].output = outputs.get(i);
		}

	}

	private void handleRecipesNode(Node node, FixedRecipes fr) throws ElementParserException {

		if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().toLowerCase(Locale.ENGLISH).equals("recipe")) {
			NodeList childNodes = node.getChildNodes();
			FixedRecipe r = new FixedRecipe();
			handleRecipeProductNode(node, r);
			fr.recipes.add(r);

		} else {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				throw new ElementParserException("only 'recipe' nodes allowed in 'recipes'. this was: " + node.getNodeName().toLowerCase(Locale.ENGLISH));
			}
		}

	}

	private void handleTypeNode(Node node, ElementCategory cat) throws ElementParserException {
//		System.err.println("TYPE NODE: "+node.getNodeName());
		if (node.getNodeType() == Node.ELEMENT_NODE) {
//			System.err.println("TYPE NODE: "+node.getNodeName());
			ElementCategory childCat = new ElementCategory(node.getNodeName(), cat);

			cat.getChildren().add(childCat);
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE && !childNodes.item(i).hasAttributes()) {
					handleTypeNode(childNodes.item(i), childCat);
				}
			}
		}
	}

	private void handleTypeNodeRead(Node node, ElementCategory cat) throws ElementParserException {
		if (node.getNodeType() == Node.ELEMENT_NODE) {

			ElementCategory childCat = cat.getChild(node.getNodeName());

			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					handleBaicElementNode(childNodes.item(i), childCat);
				}
			}
		}
	}

	public void loadAndParseCustomXML(File custom, boolean zipped, String properties, File appendImport) throws SAXException, IOException, ParserConfigurationException, ElementParserException {
		read(custom, zipped, properties == null ? "./BlockTypes.properties" : properties, appendImport);
		parse();
	}

	public void loadAndParseDefault(File appendImport) throws SAXException, IOException, ParserConfigurationException, ElementParserException {
		try {
			read(new FileExt("./BlockConfig.xml"), false, "./BlockTypes.properties", appendImport);
		} catch (ElementParserException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			read(new FileExt("./BlockConfig.xml"), true, "./BlockTypes.properties", appendImport);
		}
		parse();
	}

	private void parseHierarchy(Element root) {
		rootCategory = new ElementCategory("Element", null);

		if (root.getNodeName().equals("Config")) {
			NodeList childNodes = root.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node configChild = childNodes.item(i);

				if (configChild.getNodeName().equals("Element")) {
					NodeList elementChildNodes = configChild.getChildNodes();

//					System.err.println("## TYPE NODE: "+elementChildNodes.getLength());
					for (int j = 0; j < elementChildNodes.getLength(); j++) {
//						System.err.println("## TYPE NODE: "+elementChildNodes.item(j).getNodeName()+"; "+elementChildNodes.item(j).hasAttributes());
						if (elementChildNodes.item(j).getNodeType() == Node.ELEMENT_NODE && !elementChildNodes.item(j).hasAttributes()) {
							handleTypeNode(elementChildNodes.item(j), rootCategory);
						}
					}
				}

			}
		} else {
			throw new ElementParserException("No correct root element found");
		}
	}

	public void parse() throws ElementParserException {

		fixedRecipes = new FixedRecipes();
		Element root = doc.getDocumentElement();
		parseHierarchy(root);
		if (root.getNodeName().equals("Element")) {

			NodeList childNodes = root.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				handleBaicElementNode(childNodes.item(i), rootCategory);
			}
		} else if (root.getNodeName().equals("Config")) {
			NodeList childNodes = root.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node rootChild = childNodes.item(i);

				if (rootChild.getNodeName().equals("Element")) {
					NodeList rootChildNodes = rootChild.getChildNodes();
					for (int j = 0; j < rootChildNodes.getLength(); j++) {
						handleBaicElementNode(rootChildNodes.item(j), rootCategory);
					}
				}
				if (rootChild.getNodeName().equals("Recipes")) {
					NodeList rootChildNodes = rootChild.getChildNodes();
					for (int j = 0; j < rootChildNodes.getLength(); j++) {
						Node item = rootChildNodes.item(j);
						handleRecipesNode(item, this.fixedRecipes);
					}
				}
			}
		} else {
			throw new ElementParserException("No correct root element found");
		}
	}









	

	private static List<String> parseList(Node node, String elemName) throws ElementParserException {
		List<String> l = new ObjectArrayList<String>();
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				
				if (!item.getNodeName().equals(elemName)) {
					throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \""+elemName+"\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
				}
				
				l.add(item.getTextContent());
				
			}
			
		}
		return l;
	}






	private void parseInfoNode(Node pNode, ElementInformation info) throws ElementParserException {
		NodeList childNodes = pNode.getChildNodes();
		boolean parsedArmorHP = false;
		boolean parsedStructureHP = false;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				info.parsed.add(node.getNodeName());
				
				ElemType type = elemTypeLowerCase.get(node.getNodeName().toLowerCase(Locale.ENGLISH));
				

				if(type != null) {
					type.fac.parse(node, info);
				}else {
					try {
					throw new ElementParserException(currentName + ": Element Info <" + node.getNodeName() + "> not found. location: <" + pNode.getNodeName()+">");
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		if(info.volume < 0f){
			info.volume = info.mass;
		}
//		if (!parsedArmorHP) {
//			info.armorHP = 0;
//		}
		if (!parsedStructureHP) {
			info.structureHP = 0;
		}
	}



	private short[] parseTextureIdAttribute(Node node) throws ElementParserException {
		try {
			String[] split = node.getNodeValue().split(",");

			short[] tex = new short[]{-1, -1, -1, -1, -1, -1};

			for (int i = 0; i < split.length; i++) {
				tex[i] = Short.parseShort(split[i].trim());
			}
			assert (tex[0] >= 0);

			return tex;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException(currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
		}
	}


	
	public static void mergeApplyFromTargetField(Document from, Document target, File config, String ... onField) {
		ElementParser other = new ElementParser();
		other.doc = from;
		other.parse();

		ElementParser to = new ElementParser();
		to.doc = target;
		to.parse();

		for (int i = 0; i < other.infoElements.size(); i++) {
			boolean found = false;
			ElementInformation otherInfo = other.infoElements.get(i);
			for (int j = 0; j < to.infoElements.size(); j++) {
				ElementInformation normInfo = to.infoElements.get(j);

				if (otherInfo.getId() == normInfo.getId()) {
					for (String parsed : otherInfo.parsed) {
						
						Field[] fields = ElementInformation.class.getFields();

						for (int k = 0; k < fields.length; k++) {
							Field field = fields[k];
							field.setAccessible(true);
							org.schine.starmade.data.element.annotation.Element annotation = field.getAnnotation(org.schine.starmade.data.element.annotation.Element.class);
//							if(annotation != null){
//								System.err.println("SETTING FIELD: "+Arrays.toString(onField)+" _ "+annotationparser().tag);
//							}
							for(String s : onField){
								if (annotation != null && 
										annotation.parser().tag.toLowerCase(Locale.ENGLISH).equals(s.toLowerCase(Locale.ENGLISH)) && 
										annotation.parser().tag.toLowerCase(Locale.ENGLISH).equals(parsed.toLowerCase(Locale.ENGLISH))) {
									try {
										System.err.println("SETTING FIELD: "+annotation.parser().tag);
										field.set(normInfo, field.get(otherInfo));
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}

					found = true;
					break;
				}

			}

			if (!found) {
				to.infoElements.add(otherInfo);
			}
		}

		System.err.println("[CONFIG] Merging Categories");
		to.rootCategory.merge(other.rootCategory);

		System.err.println("[CONFIG] Writing merged config "+config.getAbsolutePath());
		ElementKeyMap.writeDocument(config, to.rootCategory, to.fixedRecipes);
	}
	public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException{
		ElementParser p = new ElementParser();
		File merge = new File("./Merge.xml");
		p.mergeOnField("Animated", new FileExt("./data/config/BlockConfig.xml"), false, "./data/config/BlockTypes.properties", merge);
	}
	private void mergeOnField(String field, File config, boolean zipped, String propertiesPath, File append) throws IOException, ParserConfigurationException, SAXException{
		properties = new Properties();
		BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(propertiesPath));
		properties.load(fileInputStream);
		fileInputStream.close();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		ElementKeyMap.properties = properties;
		ElementKeyMap.propertiesPath = propertiesPath;
		if (append != null && append.exists()) {
			
			
			if (!zipped) {
				doc = dBuilder.parse(new BufferedInputStream(new FileInputStream(config), 4096));
			} else {
				GZIPInputStream str = new GZIPInputStream(new BufferedInputStream(new FileInputStream(config), 4096));
				doc = dBuilder.parse(str);
				str.close();
			}
			
			System.err.println("[CONFIG] CONFIG FILE IMPORT FOUND: " + append.getAbsolutePath());
			Document docMerge = dBuilder.parse(append);
			mergeApplyFromTargetField(docMerge, doc, config, "Animated");
			System.err.println("[CONFIG] CONFIG FILE IMPORT DONE AND MERGED");

			
		}
		long t = System.currentTimeMillis();
		//read again
		if (!zipped) {
			doc = dBuilder.parse(new BufferedInputStream(new FileInputStream(config), 4096));
		} else {
			GZIPInputStream str = new GZIPInputStream(new BufferedInputStream(new FileInputStream(config), 4096));
			doc = dBuilder.parse(str);
			str.close();
		}
		long taken = System.currentTimeMillis() - t;
		System.err.println("[BLOCKCONFIG] READING TOOK "+taken+"ms");
	}
	public void read(File config, boolean zipped, String propertiesPath, File append) throws SAXException, IOException, ParserConfigurationException {
		

		// Read properties file.
		properties = new Properties();
		BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(propertiesPath));
		properties.load(fileInputStream);
		fileInputStream.close();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		ElementKeyMap.properties = properties;
		ElementKeyMap.propertiesPath = propertiesPath;
		ElementKeyMap.configFile = config;
		if (append != null && append.exists()) {
			
			
			if (!zipped) {
				doc = dBuilder.parse(new BufferedInputStream(new FileInputStream(config), 4096));
			} else {
				GZIPInputStream str = new GZIPInputStream(new BufferedInputStream(new FileInputStream(config), 4096));
				doc = dBuilder.parse(str);
				str.close();
			}
			
			System.err.println("[CONFIG] CONFIG FILE IMPORT FOUND: " + append.getAbsolutePath());
			Document docMerge = dBuilder.parse(append);
//			merge(docMerge, doc, config);
			System.err.println("[CONFIG] CONFIG FILE IMPORT DONE AND MERGED");

			
		}
		long t = System.currentTimeMillis();
		//read again
		if (!zipped) {
			doc = dBuilder.parse(new BufferedInputStream(new FileInputStream(config), 4096));
		} else {
			GZIPInputStream str = new GZIPInputStream(new BufferedInputStream(new FileInputStream(config), 4096));
			doc = dBuilder.parse(str);
			str.close();
		}
		long taken = System.currentTimeMillis() - t;
		System.err.println("[BLOCKCONFIG] READING TOOK "+taken+"ms");
	}

}
