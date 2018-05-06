package org.schine.starmade.data.element.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.vecmath.Vector4f;

import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementInformation.ResourceInjectionType;
import org.schine.starmade.data.element.exception.ElementParserException;
import org.schine.starmade.data.element.factory.BlockFactory;
import org.schine.starmade.data.element.factory.FactoryResource;
import org.schine.starmade.effect.InterEffectHandler.InterEffectType;
import org.schine.starmade.parser.ElementParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public enum ElemType {
	CONSISTENCE("Consistence",new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException{

			FactoryResource[] parseResource = parseResource(node);

			
			for (int i = 0; i < parseResource.length; i++) {
				info.getConsistence().add(parseResource[i]);
			}

		}
	}), 
	CUBATON_CONSISTENCE("CubatomConsistence", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			FactoryResource[] parseResource = parseResource(node);

			for (int i = 0; i < parseResource.length; i++) {
				info.cubatomConsistence.add(parseResource[i]);
			}
		}
	}),

	CONTROLLED_BY("ControlledBy", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {

				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {

					if (!item.getNodeName().equals("Element")) {
						throw new ElementParserException("[controlledBy] All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}

					short typeId = 0;
					String typeProperty = ElementParser.properties.getProperty(item.getTextContent());
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
	}),

	CONTROLLING("Controlling", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {

				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {

					if (!item.getNodeName().equals("Element")) {
						throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}

					short typeId = 0;
					String typeProperty = ElementParser.properties.getProperty(item.getTextContent());
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
	}),

	RECIPE_BUY_RESOURCE("RecipeBuyResource", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {

				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {

					if (!item.getNodeName().equals("Element")) {
						throw new ElementParserException("All child nodes of " + node.getNodeName() + " have to be \"Element\" but is " + item.getNodeName() + " (" + node.getParentNode().getNodeName() + ")");
					}

					short typeId = 0;
					String typeProperty = ElementParser.properties.getProperty(item.getTextContent());
					if (typeProperty == null) {
						throw new ElementParserException("The value of " + item.getTextContent() + " has not been found");
					}
					try {
						typeId = (short) Integer.parseInt(typeProperty);
					} catch (NumberFormatException e) {
						throw new ElementParserException("The property " + typeProperty + " has to be an Integer value");
					}
					info.getRecipeBuyResources().add(typeId);

				}

			}
		}
	}),

	ARMOR_VALUE("ArmorValue", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setArmorValue(parseFloat(node));
		}
	}),

	NAME("Name", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			
		}
	}),

	BUILD_ICON("BuildIcon", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			
		}
	}),

	FULL_NAME("FullName", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			String desc = node.getTextContent();
			info.setFullName(desc);
		}
	}),

	PRICE("Price", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setPrice(parseInt(node));
			if (info.getPrice(false) < 0) {
				throw new ElementParserException("Price for " + node.getParentNode().getNodeName() + " has to be greater or equal zero");
			}
		}
	}),

	DESCRIPTION("Description", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			String desc = node.getTextContent();
			
			desc = desc.replaceAll("\\r\\n|\\r|\\n", "");
			desc = desc.replaceAll("\\\\n", "\n");
			desc = desc.replaceAll("\\\\r", "\r");
			desc = desc.replaceAll("\\\\t", "\t");
			desc = desc.replaceAll("\\r", "\r");
			desc = desc.replaceAll("\r","");
			//getting rid of -Structural Stats-*
			desc = desc.split("-Struct")[0];

			info.setDescription(desc);
		}
	}),

	BLOCK_RESOURCE_TYPE("BlockResourceType", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.blockResourceType = ((parseInt(node)));
		}
	}),

	PRODUCED_IN_FACTORY("ProducedInFactory", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setProducedInFactory(((parseInt(node))));
		}
	}),

	BASIC_RESOURCE_FACTORY("BasicResourceFactory", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			short typeId = 0;

			if (node.getTextContent().trim().length() == 0) {

			} else {
				String typeProperty = ElementParser.properties.getProperty(node.getTextContent());
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
	}),

	FACTORY_BAKE_TIME("FactoryBakeTime", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setFactoryBakeTime(parseFloat(node));
		}
	}),

	INVENTORY_GROUP("InventoryGroup", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setInventoryGroup(node.getTextContent().toLowerCase(Locale.ENGLISH));
		}
	}),

	FACTORY("Factory", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			List<FactoryResource[]> inputs = new ObjectArrayList<FactoryResource[]>();
			List<FactoryResource[]> outputs = new ObjectArrayList<FactoryResource[]>();
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
	}),

	ANIMATED("Animated", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setAnimated(parseBoolean(node));
		}
	}),

	STRUCTURE_HP("StructureHPContribution", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.structureHP = (parseInt(node));

			if (info.structureHP < 0) {
				throw new ElementParserException("StructureHP for " + node.getParentNode().getNodeName() + " has to be positive");
			}
		}
	}),

	TRANSPARENCY("Transparency", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setBlended(parseBoolean(node));
		}
	}),

	IN_SHOP("InShop", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setShoppable(parseBoolean(node));
			info.setInRecipe(info.isShoppable());
		}
	}),

	ORIENTATION("Orientation", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setOrientatable(parseBoolean(node));
		}
	}),

	BLOCK_COMPUTER_REFERENCE("BlockComputerReference", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.computerType = ((parseInt(node)));		
		}
	}),

	SLAB("Slab", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.slab = (parseInt(node));
		}
	}),
	SLAB_IDS("SlabIds", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
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
					throw new ElementParserException(ElementParser.currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
				}
			}else{
			}
		}
	}),

	STYLE_IDS("StyleIds", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			if(node.getTextContent() != null && node.getTextContent().trim().length() > 0){
				try {
					String[] split = node.getTextContent().split(",");
					
					short[] tex = new short[split.length];
					
					for (int i = 0; i < split.length; i++) {
						tex[i] = Short.parseShort(split[i].trim());
					}
					assert (tex[0] >= 0);
					
					info.styleIds = tex;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					throw new ElementParserException(ElementParser.currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
				}
			}else{
			}
		}
	}),

	WILDCARD_IDS("WildcardIds", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			short[] tex = parseShortArray(ElementParser.currentName, node, info);
			if(tex != null){
				assert (tex[0] >= 0);
				info.wildcardIds = tex;
			}
		}
	}),

	SLAB_REFERENCE("SlabReference", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setSourceReference(parseInt(node));
		}
	}),

	GENERAL_CHAMBER("GeneralChamber", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberGeneral = ((parseBoolean(node)));
		}
	}),

	EDIT_REACTOR("Edit Reactor", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			
		}
	}),

	CHAMBER_CAPACITY("ChamberCapacity", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberCapacity = parseFloat(node);
		}
	}),

	CHAMBER_ROOT("ChamberRoot", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberRoot = ((parseInt(node)));		
		}
	}),

	CHAMBER_PARENT("ChamberParent", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberParent = ((parseInt(node)));		
		}
	}),

	CHAMBER_UPGRADES_TO("ChamberUpgradesTo", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberUpgradesTo = ((parseInt(node)));		
		}
	}),

	CHAMBER_PREREQUISITES("ChamberPrerequisites", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			short[] tex = parseShortArray(ElementParser.currentName, node, info);
			if(tex != null){
				assert (tex[0] >= 0);
				for(short s : tex){
					info.chamberPrerequisites.add(s);
				}
			}
		}
	}),

	CHAMBER_MUTUALLY_EXCLUSIVE("ChamberMutuallyExclusive", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			short[] tex = parseShortArray(ElementParser.currentName, node, info);
			if(tex != null){
				assert (tex[0] >= 0);
				for(short s : tex){
					info.chamberMutuallyExclusive.add(s);
				}
			}
		}
	}),

	CHAMBER_CHILDREN("ChamberChildren", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			short[] tex = parseShortArray(ElementParser.currentName, node, info);
			if(tex != null){
				assert (tex[0] >= 0);
				for(short s : tex){
					info.chamberChildren.add(s);
				}
			}
		}
	}),

	CHAMBER_CONFIG_GROUPS("ChamberConfigGroups", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberConfigGroupsLowerCase.clear();
			List<String> parseList = parseList(node, "Element");
			for(String s : parseList){
				info.chamberConfigGroupsLowerCase.add(s.toLowerCase(Locale.ENGLISH));
			}
		}
	}),

	CHAMBER_APPLIES_TO("ChamberAppliesTo", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberAppliesTo = parseInt(node);
		}
	}),

	REACTOR_HP("ReactorHp", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.reactorHp = parseInt(node);
		}
	}),

	REACTOR_GENERAL_ICON_INDEX("ReactorGeneralIconIndex", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.reactorGeneralIconIndex = parseInt(node);
		}
	}),

	ENTERABLE("Enterable", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setEnterable(parseBoolean(node));
		}
	}),

	MASS("Mass", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.mass = (parseFloat(node));
		}
	}),

	HITPOINTS("Hitpoints", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			int hp =  parseInt(node);

			if (hp < 1) {
				try {
					throw new ElementParserException("Hitpoints for " + info.getName() + ": " + node.getParentNode().getNodeName() + " has to be more than 0");
				} catch (ElementParserException e) {
					e.printStackTrace();
					info.setMaxHitPointsE(100);
				}
			} else {
				info.setMaxHitPointsE(hp);
			}
		}
	}),

	PLACABLE("Placable", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setPlacable(parseBoolean(node));
		}
	}),

	IN_RECIPE("InRecipe", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setInRecipe((parseBoolean(node)));
		}
	}),

	CAN_ACTIVATE("CanActivate", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setCanActivate(parseBoolean(node));
		}
	}),

	INDIVIDUAL_SIDES("IndividualSides", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setIndividualSides(parseInt(node));
			if (info.getIndividualSides() == 1 || info.getIndividualSides() == 3 || info.getIndividualSides() == 6) {
			} else {
				throw new ElementParserException("Individual Sides for " + node.getParentNode().getNodeName() + " has to be either 1 (default), 3, or 6, but was: " + info.getIndividualSides());
			}
		}
	}),

	SIDE_TEXTURE_POINT_TO_ORIENTATION("SideTexturesPointToOrientation", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.sideTexturesPointToOrientation = (parseBoolean(node));
		}
	}),

	HAS_ACTIVE_TEXTURE("HasActivationTexture", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setHasActivationTexure(parseBoolean(node));
		}
	}),

	MAIN_COMBINATION_CONTROLLER("MainCombinationController", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setMainCombinationController(parseBoolean(node));
		}
	}),

	SUPPORT_COMBINATION_CONTROLLER("SupportCombinationController", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setSupportCombinationController(parseBoolean(node));
		}
	}),

	EFFECT_COMBINATION_CONTROLLER("EffectCombinationController", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setEffectCombinationController(parseBoolean(node));
		}
	}),

	PHYSICAL("Physical", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setPhysical((parseBoolean(node)));
		}
	}),

	BLOCK_STYLE("BlockStyle", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setBlockStyle((parseInt(node)));
		}
	}),

	LIGHT_SOURCE("LightSource", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setLightSource(parseBoolean(node));
		}
	}),

	DOOR("Door", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setDoor(parseBoolean(node));
		}
	}),

	SENSOR_INPUT("SensorInput", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.sensorInput = parseBoolean(node);
		}
	}),

	DEPRECATED("Deprecated", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setDeprecated((parseBoolean(node)));
		}
	}),

	RESOURCE_INJECTION("ResourceInjection", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.resourceInjection = (ResourceInjectionType.values()[parseInt(node)]);
		}
	}),

	LIGHT_SOURCE_COLOR("LightSourceColor", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.getLightSourceColor().set(parseVector4f(node));
		}
	}),

	EXTENDED_TEXTURE_4x4("ExtendedTexture4x4", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.extendedTexture = (parseBoolean(node));
		}
	}),

	ONLY_DRAW_IN_BUILD_MODE("OnlyDrawnInBuildMode", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setDrawOnlyInBuildMode(parseBoolean(node));
		}
	}),

	LOD_SHAPE("LodShape", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.lodShapeString = node.getTextContent().trim();
		}
	}),

	LOD_SHAPE_FROM_FAR("LodShapeFromFar", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.lodShapeStyle = parseInt(node);
		}
	}),

	LOW_HP_SETTING("LowHpSetting", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.lowHpSetting = (parseBoolean(node));
		}
	}),

	OLD_HITPOINTS("OldHitpoints", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setHpOldByte(parseShort(node));
		}
	}), 
	VOLUME("Volume", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.volume = (parseFloat(node));
		}
	}), 
	EXPLOSION_ABSOBTION("ExplosionAbsorbtion", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.setExplosionAbsorbtion(parseFloat(node));
		}
	}), 
	CHAMBER_PERMISSION("ChamberPermission", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			info.chamberPermission = parseInt(node);
		}
	}), 
	ID("ID", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
		}
	}),
	TEXTURE("Texture", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
		}
	}), 
	EFFECT_ARMOR("EffectArmor", new NodeSetting() {
		public void parse(Node node, ElementInformation info) throws ElementParserException {
			NodeList childs = node.getChildNodes();
			for(int i = 0; i < childs.getLength(); i++) {
				Node d = childs.item(i);
				if(d.getNodeType() == Node.ELEMENT_NODE) {
					String nm = d.getNodeName().toLowerCase(Locale.ENGLISH);
					for(InterEffectType e : InterEffectType.values()) {
						if(e.id.toLowerCase(Locale.ENGLISH).equals(nm)) {
							try {
								info.effectArmor.setStrength(e, Float.parseFloat(d.getTextContent()));
							}catch(NumberFormatException ex) {
								ex.printStackTrace();
								throw new ElementParserException("value has to be floating point. "+d.getNodeName()+"; "+node.getNodeName()+"; "+node.getParentNode().getNodeName()+"; "+ElementParser.currentName);
							}
						}
					}
				}
			}
		}
	}), 
	
	;
	private static short[] parseShortArray(String currentName, Node node, ElementInformation info){
		if(node.getTextContent() != null && node.getTextContent().trim().length() > 0){
			try {
				String s = node.getTextContent().replaceAll("\\{", "").replaceAll("\\}", "");
				if(s.length() > 0){
					String[] split = s.split(",");
				
					short[] tex = new short[split.length];
					
					for (int i = 0; i < split.length; i++) {
						tex[i] = Short.parseShort(split[i].trim());
					}
					return tex;
				}else{
					return null;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new ElementParserException(currentName + ": The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName());
			}
		}
		return null;
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
	
	private static boolean parseBoolean(Node node) throws ElementParserException {
		try {
			return Boolean.parseBoolean(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be an Boolean value for " + node.getParentNode().getNodeName()+" but was "+node.getTextContent());
		}
	}
	private static short parseShort(Node node) throws ElementParserException {
		try {
			return Short.parseShort(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Short value for " + node.getParentNode().getNodeName());
		}
	}
	public static int parseInt(Node node) throws ElementParserException {
		try {
			return Integer.parseInt(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be an Integer value for " + node.getParentNode().getNodeName()+" but was: "+node.getTextContent());
		}
	}
	private static Vector4f parseVector4f(Node node) throws ElementParserException {
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
	public static FactoryResource[] parseResource(Node n) {
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
				String typeProperty = ElementParser.properties.getProperty(cItem.getTextContent());
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
	private static float parseFloat(Node node) throws ElementParserException {
		try {
			return Float.parseFloat(node.getTextContent());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ElementParserException("The value of " + node.getNodeName() + " has to be a Float value for " + node.getParentNode().getNodeName());
		}
	}
	public static Node parseType(Node node, NamedNodeMap attributes, String name) throws ElementParserException {
		Node typeNode = attributes.getNamedItem(name);
		if (typeNode == null) {
			throw new ElementParserException("Obligatory attribute \"" + name + "\" not found in " + node.getNodeName());
		}
		return typeNode;
	}
	public final NodeSetting fac;
	public final String tag;

	private ElemType(String tag, NodeSetting s) {
		this.tag = tag;
		this.fac = s;
	}

}
