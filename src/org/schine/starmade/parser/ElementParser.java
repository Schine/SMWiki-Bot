package org.schine.starmade.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.vecmath.Vector4f;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.schine.starmade.data.element.ElementCategory;
import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;
import org.schine.starmade.data.element.exception.ElementParserException;
import org.schine.starmade.data.element.factory.BlockFactory;
import org.schine.starmade.data.element.factory.FactoryResource;
import org.schine.starmade.data.element.factory.FixedRecipe;
import org.schine.starmade.data.element.factory.FixedRecipeProduct;
import org.schine.starmade.data.element.factory.FixedRecipes;
import org.schine.starmade.mediawiki.MediawikiExport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parsing the Element Configuration
 *
 * @author Schema
 */
public class ElementParser {

	public static Properties properties;
	private final ArrayList<ElementInformation> infoElements = new ArrayList<ElementInformation>();
	private Document doc;
	private ElementCategory rootCategory;
	private FixedRecipes fixedRecipes;
	private boolean thrown;
	private String currentName;

	public static String[] getTypeStringArray() {
		return new String[]{"General", "Terrain", "Ship", "Element", "SpaceStation", "General", "DeathStar", "Factory", "Manufacturing", "Mineral", "Hulls", "Docking", "Doors", "Light", "Effect", "Logic", "Decoration"};
	}

	public static void childMerge(NodeList childNodes, Document target) {
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {

				NodeList cs = item.getChildNodes();
				childMerge(cs, target);
			}
		}
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

		Node typeNode = parseType(node, attributes, "type");

		Node iconNode = parseType(node, attributes, "icon");

		Node textureIdNode = parseType(node, attributes, "textureId");

		Node nameNode = parseType(node, attributes, "name");

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

		info.setBuildIconNum(parseInt(iconNode));
		if (info.getBuildIconNum() < 0 || info.getBuildIconNum() >= 256 * 8) {
			throw new ElementParserException("Icon has to be between [0, " + (256 * 8) + "[ for " + node.getNodeName() + " but was " + info.getBuildIconNum());
		}

		parseInfoNode(node, info);

		for (int i = 0; i < 6; i++) {
			assert (info.getTextureId(i) >= 0);
		}
		info.recreateTextureMapping();
		if(info.getBlockStyle() != 0){
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

		Node typeNode = parseType(node, attributes, "costType");
		Node typeAmountNode = parseType(node, attributes, "costAmount");
		Node typeNameNode = parseType(node, attributes, "name");

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
							input = parseResource(item);
						}
						if (item.getNodeName().toLowerCase(Locale.ENGLISH).equals("output")) {
							output = parseResource(item);
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
		read(custom, zipped, properties == null ? MediawikiExport.properties.getProperty("blockconfigpath")+"BlockTypes.properties" : properties, appendImport);
		parse();
	}

	public void loadAndParseDefault(File appendImport) throws SAXException, IOException, ParserConfigurationException, ElementParserException {
		try {
			read(new File(
					MediawikiExport.properties.getProperty("blockconfigpath")+"BlockConfig.xml"), false,
					MediawikiExport.properties.getProperty("blockconfigpath")+"BlockTypes.properties", appendImport);
		} catch (ElementParserException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
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
//		System.err.println("PRINTING CAT");
//		rootCategory.print();
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

	private void parseAnimated(Node node, ElementInformation info) throws ElementParserException {
		info.setAnimated(parseBoolean(node));
	}

	private void parseHasBlockTexture(Node node, ElementInformation info) throws ElementParserException {
		info.setHasActivationTexure(parseBoolean(node));
	}

	private void parseIsMainController(Node node, ElementInformation info) throws ElementParserException {
		info.setMainCombinationController(parseBoolean(node));
	}

	private void parseIsEffectController(Node node, ElementInformation info) throws ElementParserException {
		info.setEffectCombinationController(parseBoolean(node));
	}

	private void parseOnlyInBuildModeBackeTime(Node node,
			ElementInformation info) {
		info.setDrawOnlyInBuildMode(parseBoolean(node));
	}
	private void parseIsSuppoerController(Node node, ElementInformation info) throws ElementParserException {
		info.setSupportCombinationController(parseBoolean(node));
	}
	private void parseLodShape(Node node, ElementInformation info) {
		info.lodShapeString = node.getTextContent().trim();
	}
	private void parseArmour(Node node, ElementInformation info) throws ElementParserException {
		info.setAmour(parseFloat(node));

		if (info.getAmour() < 0f || info.getAmour() > 100f) {
			throw new ElementParserException("Armour for " + node.getParentNode().getNodeName() + " has to be between 0% and 100%");
		}
	}

	private void parseArmourHP(Node node, ElementInformation info) throws ElementParserException {
		info.armourHP = (parseInt(node));

		if (info.armourHP < 0) {
			throw new ElementParserException("ArmourHP for " + node.getParentNode().getNodeName() + " has to be positive");
		}
	}

	private void parseStructureHP(Node node, ElementInformation info) throws ElementParserException {
		info.structureHP = (parseInt(node));

		if (info.structureHP < 0) {
			throw new ElementParserException("StructureHP for " + node.getParentNode().getNodeName() + " has to be positive");
		}
	}

	private void parseBlockStyle(Node node, ElementInformation info) {
		info.setBlockStyle((parseInt(node)));
	}

	private void parseBlockResourceStyle(Node node, ElementInformation info) {
		info.blockResourceType = ((parseInt(node)));
	}
	private void parseSlab(Node node, ElementInformation info) {
		info.slab = (parseInt(node));
	}

	private void parseProducedInFactory(Node node, ElementInformation info) {
		info.setProducedInFactory(((parseInt(node))));
	}

	private void parseResourceInj(Node node, ElementInformation info) {
		info.resourceInjection = ((parseInt(node)));
	}

	private boolean parseBoolean(Node node) throws ElementParserException {
		try {
			return Boolean.parseBoolean(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be an Boolean value for " + node.getParentNode().getNodeName());
		}
	}

	private void parseCanActivate(Node node, ElementInformation info) throws ElementParserException {
		info.setCanActivate(parseBoolean(node));
	}

	private void parseControlledBy(Node node, ElementInformation info) throws ElementParserException {

		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {

			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {

				if (!item.getNodeName().equals("Element")) {
					throw new ElementParserException("[controlledBy] All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
				}

				short typeId = 0;
				String typeProperty = properties.getProperty(item.getTextContent());
				if (typeProperty == null) {
					throw new ElementParserException("[controlledBy] The value of " + item.getTextContent() + " has not been found");
				}
				try {
					
					typeId = (short) Integer.parseInt(typeProperty);
				} catch (NumberFormatException e) {
					throw new ElementParserException("[controlledBy] The property " + typeProperty + " has to be an Integer value");
				}
				info.getControlledBy().add(typeId);

			}

		}
	}

	private void parseControlling(Node node, ElementInformation info) throws ElementParserException {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {

			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {

				if (!item.getNodeName().equals("Element")) {
					throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
				}

				short typeId = 0;
				String typeProperty = properties.getProperty(item.getTextContent());
				if (typeProperty == null) {
					throw new ElementParserException("[controlling] The value of " + item.getTextContent() + " has not been found");
				}
				try {
					typeId = (short) Integer.parseInt(typeProperty);
				} catch (NumberFormatException e) {
					throw new ElementParserException("[controlling] The property " + typeProperty + " has to be an Integer value");
				}
				//				System.err.println("ADDING CONTROLLING "+s);
				info.getControlling().add(typeId);

			}

		}
	}


	private void parseConsistence(Node node, ElementInformation info) {

		FactoryResource[] parseResource = parseResource(node);

		for (int i = 0; i < parseResource.length; i++) {
			info.getConsistence().add(parseResource[i]);
		}

	}

	private void parseCubatomConsistence(Node node, ElementInformation info) {

		FactoryResource[] parseResource = parseResource(node);

		for (int i = 0; i < parseResource.length; i++) {
			info.cubatomConsistence.add(parseResource[i]);
		}

	}

	private void parseCubatomCompound(Node node, ElementInformation info) {
		//deprecated
	}

//	private void parseCubatomRecipe(Node node, ElementInformation info) {
//		CubatomFlavor[] compound = parseCubatomCompound(node);
//		info.setCubatomRecipy(new CubatomCompound(compound));
//	}

	private void parseDescription(Node node, ElementInformation info) throws ElementParserException {

		String desc = node.getTextContent();
		desc = desc.replaceAll("\\r\\n|\\r|\\n", "");
		desc = desc.replaceAll("\\\\n", "\n");
		desc = desc.replaceAll("\\\\r", "\r");
		desc = desc.replaceAll("\\\\t", "\t");
		
		//		desc = desc.replace(System.getProperty("line.separator"), "");
		info.setDescription(desc);
	}

	private void parseEnterable(Node node, ElementInformation info) throws ElementParserException {
		info.setEnterable(parseBoolean(node));
	}

	private void parseFactory(Node node, ElementInformation info) throws ElementParserException {
		ArrayList<FactoryResource[]> inputs = new ArrayList<FactoryResource[]>();
		ArrayList<FactoryResource[]> outputs = new ArrayList<FactoryResource[]>();
		BlockFactory f = new BlockFactory();
		info.setFactory(f);
		if (node.getTextContent().toLowerCase(Locale.ENGLISH).equals("input")) {
			//nothing to do: input factory
			return;
		}
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
							input = parseResource(item);
						}
						if (item.getNodeName().toLowerCase(Locale.ENGLISH).equals("output")) {
							output = parseResource(item);
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

		f.input = new FactoryResource[inputs.size()][];
		f.output = new FactoryResource[outputs.size()][];

		for (int i = 0; i < f.input.length; i++) {
			f.input[i] = inputs.get(i);
			f.output[i] = outputs.get(i);
		}

		if (inputs.size() == 0 && outputs.size() == 0) {
			info.setFactory(null);
		}
	}

	private float parseFloat(Node node) throws ElementParserException {
		try {
			return Float.parseFloat(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Float value for " + node.getParentNode().getNodeName());
		}
	}

	private void parseFullName(Node node, ElementInformation info) throws ElementParserException {

		String desc = node.getTextContent();

		//		desc = desc.replace(System.getProperty("line.separator"), "");
		info.setFullName(desc);
	}

	private void parseInventoryGroup(Node node, ElementInformation info) throws ElementParserException {
		info.setInventoryGroup(node.getTextContent().toLowerCase(Locale.ENGLISH));
	}

	private void parseHitpoints(Node node, ElementInformation info) throws ElementParserException {
		short hp = (short) parseInt(node);

		if (hp < 1 || hp >= 256) {
			try {
				throw new ElementParserException("Hitpoints for " + info.getName() + ": " + node.getParentNode().getNodeName() + " has to be between [1, " + 256 + "[");
			} catch (ElementParserException e) {
				e.printStackTrace();
				if (!thrown) {
					thrown = true;
				}
				info.setMaxHitPoints((short) 255);
			}
		} else {
			info.setMaxHitPoints(hp);
		}
	}

	private void parseIndividualSides(Node node, ElementInformation info) throws ElementParserException {
		info.setIndividualSides(parseInt(node));
		if (info.getIndividualSides() == 1 || info.getIndividualSides() == 3 || info.getIndividualSides() == 6) {
		} else {
			throw new ElementParserException("Individual Sides for " + node.getParentNode().getNodeName() + " has to be either 1 (default), 3, or 6, but was: " + info.getIndividualSides());
		}
	}

	private void parseInfoNode(Node pNode, ElementInformation info) throws ElementParserException {
		NodeList childNodes = pNode.getChildNodes();
		boolean parsedArmorHP = false;
		boolean parsedStructureHP = false;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				info.parsed.add(node.getNodeName());

				if (node.getNodeName().equals("InShop")) {
					parseInShop(node, info);
				} else if (node.getNodeName().equals("Placable")) {
					parsePlacable(node, info);
				} else if (node.getNodeName().equals("SideTexturesPointToOrientation")) {
					parseSideTexturePointToOrientation(node, info);
				} else if (node.getNodeName().equals("LowHpSetting")) {
					parseLowHpSetting(node, info);
				} else if (node.getNodeName().equals("InRecipe")) {
					parseInRecipe(node, info);
				} else if (node.getNodeName().equals("RecipeBuyResource")) {
					parseRecipeBuyResource(node, info);
				} else if (node.getNodeName().equals("BasicResourceFactory")) {
					parseBasicResourceFactory(node, info);
				} else if (node.getNodeName().equals("Proje")) {
					parseBasicResourceFactory(node, info);
				} else if (node.getNodeName().equals("ProjectionTo")) {
					//deprecated so do not parse
				} else if (node.getNodeName().equals("ControlledBy")) {
					parseControlledBy(node, info);
				} else if (node.getNodeName().equals("Controlling")) {
					parseControlling(node, info);
				} else if (node.getNodeName().equals("Level")) {
					parseLevel(node, info);
				} else if (node.getNodeName().equals("Consistence")) {
					parseConsistence(node, info);
				} else if (node.getNodeName().equals("CubatomConsistence")) {
					parseCubatomConsistence(node, info);
				} else if (node.getNodeName().equals("CubatomCompound")) {
					parseCubatomCompound(node, info);
				} else if (node.getNodeName().equals("CubatomRecipe")) {
//					parseCubatomRecipe(node, info);
				} else if (node.getNodeName().equals("Enterable")) {
					parseEnterable(node, info);
				} else if (node.getNodeName().equals("CanActivate")) {
					parseCanActivate(node, info);
				} else if (node.getNodeName().equals("LightSource")) {
					parseLightSource(node, info);
				} else if (node.getNodeName().equals("Door")) {
					parseDoor(node, info);
				} else if (node.getNodeName().equals("Physical")) {
					parsePhysical(node, info);
				} else if (node.getNodeName().equals("BlockStyle")) {
					parseBlockStyle(node, info);
				} else if (node.getNodeName().equals("BlockResourceType")) {
					parseBlockResourceStyle(node, info);
				} else if (node.getNodeName().equals("Slab")) {
					parseSlab(node, info);
				} else if (node.getNodeName().equals("ProducedInFactory")) {
					parseProducedInFactory(node, info);
				} else if (node.getNodeName().equals("LightSourceColor")) {
					parseLightSourceColor(node, info);
				} else if (node.getNodeName().equals("Hitpoints")) {
					parseHitpoints(node, info);
				} else if (node.getNodeName().equals("Armour")) {
					parseArmour(node, info);

				} else if (node.getNodeName().equals("ArmorHPContribution")) {
					parseArmourHP(node, info);
					parsedArmorHP = true;
				} else if (node.getNodeName().equals("StructureHPContribution")) {
					parseStructureHP(node, info);
					parsedStructureHP = true;
				} else if (node.getNodeName().equals("Transparency")) {
					parseTransparency(node, info);
				} else if (node.getNodeName().equals("Description")) {
					parseDescription(node, info);
				} else if (node.getNodeName().equals("Factory")) {
					parseFactory(node, info);
				} else if (node.getNodeName().equals("FullName")) {
					parseFullName(node, info);
				} else if (node.getNodeName().equals("InventoryGroup")) {
					parseInventoryGroup(node, info);
				} else if (node.getNodeName().equals("Deprecated")) {
					parseDeprecated(node, info);
				} else if (node.getNodeName().equals("Animated")) {
					parseAnimated(node, info);
				} else if (node.getNodeName().equals("HasActivationTexture")) {
					parseHasBlockTexture(node, info);
				} else if (node.getNodeName().equals("MainCombinationController")) {
					parseIsMainController(node, info);
				} else if (node.getNodeName().equals("EffectCombinationController")) {
					parseIsEffectController(node, info);
				} else if (node.getNodeName().equals("SupportCombinationController")) {
					parseIsSuppoerController(node, info);
				} else if (node.getNodeName().equals("Price")) {
					parsePrice(node, info);
				} else if (node.getNodeName().equals("InShop")) {
					parseInShop(node, info);
				} else if (node.getNodeName().equals("Orientation")) {
					parseOrientation(node, info);
				} else if (node.getNodeName().equals("IndividualSides")) {
					parseIndividualSides(node, info);
				} else if (node.getNodeName().equals("ResourceInjection")) {
					parseResourceInj(node, info);
				} else if (node.getNodeName().equals("ExplosionAbsorbtion")) {
					parseExplosionAbsorbtion(node, info);
				} else if (node.getNodeName().equals("Mass")) {
					parseMass(node, info);
				} else if (node.getNodeName().equals("Volume")) {
					parseVolume(node, info);
				}  else if (node.getNodeName().equals("SlabReference")) {
					parseSlabReference(node, info);
				} else if (node.getNodeName().equals("SlabIds")) {
					parseSlabIds(node, info);
				} else if (node.getNodeName().equals("FactoryBakeTime")) {
					parseFactoryBackeTime(node, info);
				} else if (node.getNodeName().equals("OnlyDrawnInBuildMode")) {
					parseOnlyInBuildModeBackeTime(node, info);
				} else if (node.getNodeName().equals("LodShape")) {
					parseLodShape(node, info);
				} else if (node.getNodeName().equals("LodShapeFromFar")) {
					parseLodShapeFromFar(node, info);
				} else {
					throw new ElementParserException(currentName + ": Element Info " + node.getNodeName() + " not found. location: " + pNode.getNodeName());
				}
			}
		}

		if(info.volume < 0f){
			info.volume = info.mass;
		}
		if (!parsedArmorHP) {
			info.armourHP = Math.round(info.getMaxHitPoints() * info.getArmourPercent());
		}
		if (!parsedStructureHP) {
			info.structureHP = info.getMaxHitPoints();
		}
	}



	

	private void parseLodShapeFromFar(Node node, ElementInformation info) {
		info.lodShapeStyle = parseInt(node);
	}

	private void parseSlabReference(Node node, ElementInformation info) {
		info.slabReference = parseInt(node);
	}
	private void parseSlabIds(Node node, ElementInformation info) {
		if(node.getTextContent() != null && node.getTextContent().trim().length() > 0){
			try {
				String[] split = node.getTextContent().split(",");
	
				short[] tex = new short[]{0, 0, 0};
	
				for (int i = 0; i < split.length; i++) {
					tex[i] = Short.parseShort(split[i].trim());
				}
				assert (tex[0] >= 0);
				
				info.slabIds = tex;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new ElementParserException(currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
			}
		}else{
		}
	}

	private void parseDeprecated(Node node, ElementInformation info) {
		info.setDeprecated((parseBoolean(node)));
	}

	private void parseInRecipe(Node node, ElementInformation info) throws ElementParserException {
		info.setInRecipe((parseBoolean(node)));
	}

	private void parseInShop(Node node, ElementInformation info) throws ElementParserException {
		info.setShoppable(parseBoolean(node));
		info.setInRecipe(info.isShoppable());
	}

	private void parseExplosionAbsorbtion(Node node, ElementInformation info) throws ElementParserException {
		info.setExplosionAbsorbtion(parseFloat(node));
	}

	private void parseMass(Node node, ElementInformation info) throws ElementParserException {
		info.mass = (parseFloat(node));
	}
	private void parseVolume(Node node, ElementInformation info) throws ElementParserException {
		info.volume = (parseFloat(node));
	}

	private void parseFactoryBackeTime(Node node, ElementInformation info) throws ElementParserException {
		info.setFactoryBakeTime(parseFloat(node));
	}

	private int parseInt(Node node) throws ElementParserException {
		try {
			return Integer.parseInt(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException(currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
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

//	private int parseIntAttribute(Node node) throws ElementParserException {
//		try {
//			return Integer.parseInt(node.getNodeValue());
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//			throw new ElementParserException(currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
//		}
//	}

	private void parseLevel(Node node, ElementInformation info) {
		NodeList childNodes = node.getChildNodes();
		boolean idPresent = false;
		boolean lvlPresent = false;
		short lvlId = 0;
		int lvl = 0;
		for (int i = 0; i < childNodes.getLength(); i++) {

			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {

				if (item.getNodeName().equals("Id")) {
					if (idPresent) {
						throw new ElementParserException(currentName + ": [LEVEL] Multiple IDs for level in " + node.getParentNode().getNodeName());
					}

					String typeProperty = properties.getProperty(item.getTextContent());
					if (typeProperty == null) {
						throw new ElementParserException("[LEVEL] The value of " + item.getTextContent() + " has not been found in " + node.getParentNode().getNodeName());
					}
					try {
						lvlId = (short) Integer.parseInt(typeProperty);
						idPresent = true;
					} catch (NumberFormatException e) {
						throw new ElementParserException("[LEVEL] The property " + typeProperty + " has to be an Integer value in " + node.getParentNode().getNodeName());
					}
				}

				if (item.getNodeName().equals("Nr")) {
					try {
						lvl = Integer.parseInt(item.getTextContent());
						lvlPresent = true;
					} catch (NumberFormatException e) {
						throw new ElementParserException("[LEVEL] Nr Value '" + item.getTextContent() + "' is not an Integer value in " + node.getParentNode().getNodeName());
					}
				}

			}

		}
		if (!idPresent) {
			throw new ElementParserException("[LEVEL] No level id in " + node.getParentNode().getNodeName());
		}
		if (!lvlPresent) {
			throw new ElementParserException("[LEVEL] No level nr in " + node.getParentNode().getNodeName());
		}

	}

	private void parseLightSource(Node node, ElementInformation info) throws ElementParserException {
		info.setLightSource(parseBoolean(node));
	}

	private void parseDoor(Node node, ElementInformation info) throws ElementParserException {
		info.setDoor(parseBoolean(node));
	}

	private void parseLightSourceColor(Node node, ElementInformation info) throws ElementParserException {
		info.getLightSourceColor().set(parseVector4f(node));
	}

	private void parseOrientation(Node node, ElementInformation info) throws ElementParserException {
		info.setOrientatable(parseBoolean(node));
	}

	private void parsePhysical(Node node, ElementInformation info) throws ElementParserException {
		info.setPhysical((parseBoolean(node)));

	}

	private void parsePlacable(Node node, ElementInformation info) throws ElementParserException {
		info.setPlacable(parseBoolean(node));

	}

	private void parseSideTexturePointToOrientation(Node node, ElementInformation info) throws ElementParserException {
		info.sideTexturesPointToOrientation = (parseBoolean(node));

	}
	private void parseLowHpSetting(Node node, ElementInformation info) throws ElementParserException {
		info.lowHpSetting = (parseBoolean(node));
		
	}

	private void parsePrice(Node node, ElementInformation info) throws ElementParserException {
		info.setPrice(parseInt(node));
		if (info.getPrice(false) < 0) {
			throw new ElementParserException("Price for " + node.getParentNode().getNodeName() + " has to be greater or equal zero");
		}
	}

	private void parseBasicResourceFactory(Node node, ElementInformation info) throws ElementParserException {
		short typeId = 0;

		if (node.getTextContent().trim().length() == 0) {

		} else {
			String typeProperty = properties.getProperty(node.getTextContent());
			if (typeProperty == null) {
				info.setBasicResourceFactory(parseShort(node));
				return;
			}
			try {
				typeId = (short) Integer.parseInt(typeProperty);
			} catch (NumberFormatException e) {
				throw new ElementParserException("The property " + typeProperty + " has to be an Integer value");
			}
			info.setBasicResourceFactory(typeId);
			//			System.err.println("PROJECTION PARSED: "+info.getProjectionTo());
		}

	}

	private void parseRecipeBuyResource(Node node, ElementInformation info) throws ElementParserException {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {

			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {

				if (!item.getNodeName().equals("Element")) {
					throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
				}

				short typeId = 0;
				String typeProperty = properties.getProperty(item.getTextContent());
				if (typeProperty == null) {
					throw new ElementParserException("The value of " + item.getTextContent() + " has not been found");
				}
				try {
					typeId = (short) Integer.parseInt(typeProperty);
				} catch (NumberFormatException e) {
					throw new ElementParserException("The property " + typeProperty + " has to be an Integer value");
				}
				//				System.err.println("ADDING CONTROLLING "+s);
				info.getRecipeBuyResources().add(typeId);

			}

		}
	}

	private FactoryResource[] parseResource(Node n) {

		ArrayList<FactoryResource> r = new ArrayList<FactoryResource>();
		NodeList cNodes = n.getChildNodes();
		for (int j = 0; j < cNodes.getLength(); j++) {
			Node cItem = cNodes.item(j);
			if (cItem.getNodeType() == Node.ELEMENT_NODE) {
				if (!cItem.getNodeName().toLowerCase(Locale.ENGLISH).equals("item")) {
					throw new ElementParserException("All child nodes of " + n.getNodeName() + " have to be \"item\" but is " + n.getParentNode().getNodeName() + " (" + n.getParentNode().getParentNode().getNodeName() + ")");
				}

				NamedNodeMap attributes = cItem.getAttributes();
				if (attributes != null && attributes.getLength() != 1) {
					throw new ElementParserException("Element has wrong attribute count (" + attributes.getLength() + ", but should be 4)");
				}

				Node typeNode = parseType(cItem, attributes, "count");
				int count = 0;
				try {
					count = Integer.parseInt(typeNode.getNodeValue());
				} catch (NumberFormatException e) {
					throw new ElementParserException("Cant parse count in " + cItem.getNodeName() + ", in " + n.getParentNode().getNodeName() + " (" + n.getParentNode().getParentNode().getNodeName() + ")");
				}

				short typeId = 0;
				String typeProperty = properties.getProperty(cItem.getTextContent());
				if (typeProperty == null) {
					throw new ElementParserException(n.getParentNode().getParentNode().getParentNode().getNodeName() + " -> " + n.getParentNode().getNodeName() + " -> " + n.getNodeName() + " The value of \"" + cItem.getTextContent() + "\" has not been found");
				}
				try {
					typeId = (short) Integer.parseInt(typeProperty);
				} catch (NumberFormatException e) {
					throw new ElementParserException("The property " + typeProperty + " has to be an Integer value");
				}
				r.add(new FactoryResource(count, typeId));

			}
		}
		FactoryResource[] a = new FactoryResource[r.size()];
		r.toArray(a);
		return a;
	}

	private short parseShort(Node node) throws ElementParserException {
		try {
			return Short.parseShort(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Short value for " + node.getParentNode().getNodeName());
		}
	}

	private void parseTransparency(Node node, ElementInformation info) throws ElementParserException {
		info.setBlended(parseBoolean(node));
	}

	private Node parseType(Node node, NamedNodeMap attributes, String name) throws ElementParserException {
		Node typeNode = attributes.getNamedItem(name);
		if (typeNode == null) {
			throw new ElementParserException("Obligatory attribute \"" + name + "\" not found in " + node.getNodeName());
		}
		return typeNode;
	}

//	private Vector3f parseVector3f(Node node) throws ElementParserException {
//		Vector3f v = new Vector3f();
//
//		String[] split = node.getTextContent().split(",");
//
//		if (split.length != 3) {
//			throw new ElementParserException("The value of " + node.getNodeName() + " has to be 3 Float values seperated by commas");
//		}
//		try {
//			v.set(Float.parseFloat(split[0].trim()), Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()));
//		} catch (NumberFormatException e) {
//			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Float value");
//		}
//		return v;
//	}

	private Vector4f parseVector4f(Node node) throws ElementParserException {
		Vector4f v = new Vector4f();

		String[] split = node.getTextContent().split(",");

		if (split.length != 4) {
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be 4 Float values seperated by commas");
		}
		try {
			v.set(Float.parseFloat(split[0].trim()), Float.parseFloat(split[1].trim()), Float.parseFloat(split[2].trim()), Float.parseFloat(split[3].trim()));
		} catch (NumberFormatException e) {
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Float value");
		}
		return v;
	}

	public void merge(Document from, Document target, File config) {
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

							if (annotation != null && annotation.tag().equals(parsed)) {
								try {

									System.err.println(normInfo.getName() + "(" + normInfo.getId() + ") buildIcon " + normInfo.getBuildIconNum() + " MERGING " + field.getName());
									field.set(normInfo, field.get(otherInfo));
									System.err.println(normInfo.getName() + "(" + normInfo.getId() + ") buildIcon " + normInfo.getBuildIconNum() + " AFTER MERGING " + field.getName());
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
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

		System.err.println("[CONFIG] Writing merged config");
		ElementKeyMap.writeDocument(config, to.rootCategory, to.fixedRecipes);
	}

	public void read(File config, boolean zipped, String propertiesPath, File append) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		if (!zipped) {
			doc = dBuilder.parse(new BufferedInputStream(new FileInputStream(config), 4096));
		} else {
			GZIPInputStream str = new GZIPInputStream(new BufferedInputStream(new FileInputStream(config), 4096));
			doc = dBuilder.parse(str);
			str.close();
		}

		// Read properties file.
		properties = new Properties();
		BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(propertiesPath));
		properties.load(fileInputStream);
		fileInputStream.close();

		ElementKeyMap.properties = properties;
		ElementKeyMap.propertiesPath = propertiesPath;
		if (append != null && append.exists()) {
			System.err.println("[CONFIG] CONFIG FILE IMPORT FOUND: " + append.getAbsolutePath());
			Document docMerge = dBuilder.parse(append);
			merge(docMerge, doc, config);
			System.err.println("[CONFIG] CONFIG FILE IMPORT DONE AND MERGED");

			//read again
			if (!zipped) {
				doc = dBuilder.parse(new BufferedInputStream(new FileInputStream(config), 4096));
			} else {
				GZIPInputStream str = new GZIPInputStream(new BufferedInputStream(new FileInputStream(config), 4096));
				doc = dBuilder.parse(str);
				str.close();
			}
		}
	}

}
