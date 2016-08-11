package org.schine.starmade.data.element;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.schine.starmade.common.StringTools;
import org.schine.starmade.data.element.annotation.Element;
import org.schine.starmade.data.element.exception.CannotAppendXMLException;
import org.schine.starmade.data.element.factory.BlockFactory;
import org.schine.starmade.data.element.factory.FactoryResource;
import org.schine.starmade.data.element.factory.FixedRecipe;
import org.schine.starmade.data.element.factory.FixedRecipeProduct;
import org.schine.starmade.data.element.factory.RecipeInterface;
import org.w3c.dom.Document;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;


public class ElementInformation implements Comparable<ElementInformation> {

	public static final String[] rDesc = new String[]{"ore", "plant", "basic", "Cubatom-Splittable", "manufactory", "advanced", "capsule"};
	public static final int FAC_NONE = 0;
	public static final int FAC_CAPSULE = 1;
	public static final int FAC_MICRO = 2;
	public static final int FAC_BASIC = 3;
	public static final int FAC_STANDARD = 4;
	public static final int FAC_ADVANCED = 5;
	public static final int RT_ORE = 0;
	public static final int RT_PLANT = 1;
	public static final int RT_BASIC = 2;
	public static final int RT_CUBATOM_SPLITTABLE = 3;
	public static final int RT_MANUFACTORY = 4;
	public static final int RT_ADVANCED = 5;
	public static final int RT_CAPSULE = 6;
	//not write as tag (its an attribute)
	@Element(tag = "ID", writeAsTag = false, canBulkChange = false)
	public final short id;
	
	@Element(tag = "Texture", writeAsTag = false, canBulkChange = false)
	private short[] textureId;	
	
	@Element(tag = "Consistence", consistence = true)
	public final ArrayList<FactoryResource> consistence = new ArrayList<FactoryResource>();
	@Element(tag = "CubatomConsistence", cubatomConsistence = true)
	public
	final ArrayList<FactoryResource> cubatomConsistence = new ArrayList<FactoryResource>();
	@Element(tag = "ControlledBy", collectionElementTag = "Element", collectionType = "blockTypes")
	public final Set<Short> controlledBy = new HashSet<Short>();
	@Element(tag = "Controlling", collectionElementTag = "Element", collectionType = "blockTypes")
	public final Set<Short> controlling = new HashSet<Short>();
	
	@Element(tag = "RecipeBuyResource", collectionElementTag = "Element", collectionType = "blockTypes")
	public final List<Short> recipeBuyResources = new ShortArrayList();
	public final ObjectOpenHashSet<String> parsed = new ObjectOpenHashSet<String>(2048);
	private final int[] textureLayerMapping = new int[6];
	private final int[] textureIndexLocalMapping = new int[6];
	private final int[] textureLayerMappingActive = new int[6];
	private final int[] textureIndexLocalMappingActive = new int[6];
	@Element(tag = "Name", writeAsTag = false, canBulkChange = false)
	public String name;
	public ElementCategory type;
	@Element(tag = "BuildIcon", writeAsTag = false, canBulkChange = false)
	public int buildIconNum = 62;
	@Element(tag = "FullName", canBulkChange = false)
	public String fullName = "";
	@Element(from = 0, to = Integer.MAX_VALUE, tag = "Price")
	public long price = 100;
	@Element(tag = "Description", textArea = true)
	public String description = "undefined description";
	@Element(tag = "BlockResourceType", states = {"0", "1", "2", "3", "4", "5", "6"}, stateDescs = {"ore", "plant", "basic", "Cubatom-Splittable", "manufactory", "advanced", "capsule"})
	public int blockResourceType = 2;
	@Element(tag = "ProducedInFactory", states = {"0", "1", "2", "3", "4", "5"}, stateDescs = {"none", "capsule refinery", "micro assembler", "basic factory", "standard factory", "advanced factory"})
	public int producedInFactory = 0;
	@Element(tag = "BasicResourceFactory", type = true)
	public short basicResourceFactory = 0;
	@Element(from = 0, to = 1000000, tag = "FactoryBakeTime")
	public float factoryBakeTime = 5;
	@Element(tag = "InventoryGroup", inventoryGroup = true)
	public String inventoryGroup = "";
	@Element(tag = "Factory", factory = true)
	public BlockFactory factory;
	@Element(tag = "Animated")
	public boolean animated;
	@Element(from = 0, to = 100, tag = "Armour")
	public float amour = 0;
	@Element(from = 0, to = Integer.MAX_VALUE, tag = "ArmorHPContribution")
	public int armourHP = 0;
	@Element(from = 0, to = Integer.MAX_VALUE, tag = "StructureHPContribution")
	public int structureHP = 0;
	@Element(tag = "Transparency")
	public boolean blended;
	@Element(tag = "InShop")
	public boolean shoppable = true;
	@Element(tag = "Orientation")
	public boolean orientatable;
	@Element(tag = "Slab", states = {"0", "1", "2", "3"}, stateDescs = {"full block", "3/4 block", "1/2 block", "1/4 block"})
	public int slab = 0;
	@Element(tag = "Enterable")
	public boolean enterable;
	@Element(tag = "Mass")
	public float mass = 0.1f;
	
	@Element(tag = "Volume")
	public float volume = -1.0f;
	
	@Element(from = 1, to = 255, tag = "Hitpoints")
	public short maxHitPoints = 100;
	@Element(tag = "Placable")
	public boolean placable = true;
	@Element(tag = "InRecipe")
	public boolean inRecipe = shoppable;
	@Element(tag = "CanActivate")
	public boolean canActivate;
	@Element(states = {"1", "3", "6"}, tag = "IndividualSides", updateTextures = true)
	public int individualSides = 1;
	@Element(tag = "SideTexturesPointToOrientation")
	public boolean sideTexturesPointToOrientation = false;
	@Element(tag = "HasActivationTexture")
	public boolean hasActivationTexure;
	@Element(tag = "MainCombinationController")
	public boolean mainCombinationController;
	@Element(tag = "SupportCombinationController")
	public boolean supportCombinationController;
	@Element(tag = "EffectCombinationController")
	public boolean effectCombinationController;
	@Element(tag = "Physical")
	public boolean physical = true;
	@Element(tag = "BlockStyle", states = {"0", "1", "2", "3", "4", "5", "6"}, stateDescs = {"block", "wedge", "corner", "sprite", "tetra", "penta", "24Normal"})
	public int blockStyle;
	@Element(tag = "LightSource")
	public boolean lightSource;

	@Element(tag = "Door")
	public boolean door;
	@Element(tag = "Deprecated")
	public boolean deprecated;
	@Element(tag = "CubatomCompound", cubatom = true)
	public Object[] cubatomCompound;
	public long dynamicPrice = -1;
	@Element(tag = "ResourceInjection", states = {"0", "1", "17"}, stateDescs = {"off", "ore", "flora"})
	public int resourceInjection = 0;
	@Element(from = 0, to = 100000, tag = "ExplosionAbsorbtion")
	public float explosionAbsorbtion;
	@Element(tag = "LightSourceColor", vector4f = true)
	public final Vector4f lightSourceColor = new Vector4f(1, 1, 1, 1);
	
	@Element(tag = "SlabIds", canBulkChange = false)
	public short[] slabIds = null;
	
	@Element(tag = "SlabReference", canBulkChange = false, editable = false)
	public int slabReference = 0;
	
	@Element(tag = "OnlyDrawnInBuildMode", editable = true, canBulkChange = true)
	public boolean drawOnlyInBuildMode;
	
	@Element(tag = "LodShape", editable = true, canBulkChange = true)
	public String lodShapeString = "";
	
	@Element(tag = "LodShapeFromFar", states = {"0", "1", "2"}, stateDescs = {"solid block", "sprite", "invisible"})
	public int lodShapeStyle = 0;

	
	@Element(tag = "LowHpSetting", editable = true, canBulkChange = true)
	public boolean lowHpSetting;
	
	private float armourPercent;
	private boolean solidBlockStyle;
	private boolean blendedBlockStyle;
	private FixedRecipe productionRecipe;
	private float maxHitpointsInverse;
	private boolean createdTX = false;
	private boolean signal;
	public String idName;
	private boolean specialBlock = true;
	
	

	public ElementInformation(ElementInformation v, short id, String name) {
		this.name = new String(name);
		this.type = v.type;
		this.setTextureId(v.textureId);
		this.id = id;

		Field[] fields = ElementInformation.class.getFields();
		for (Field f : fields) {
			try {
				if (f.get(v) == null || Modifier.isFinal(f.getModifiers()) || f.getName().equals("name")) {
					continue;
				}
				//				if(f.getType().isPrimitive()){
				//					System.err.println("SKDJHKSJAHSDKJH "+f.getName()+": "+f.get(v));
				//				}
				f.set(this, f.get(v));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		signal = calcIsSignal(id);
		assert (v.getBlockStyle() == this.getBlockStyle());
	}

	public ElementInformation(short id, String name, ElementCategory class1, short[] textureId) {
		this.name = name;
		this.type = class1;
		this.id = id;
		this.setTextureId(textureId);
		signal = calcIsSignal(id);
	}
	
	
	public static byte defaultActive(short type) {
		return (byte) (
				type == ElementKeyMap.WEAPON_ID ||
						type == ElementKeyMap.MISSILE_DUMB_ID ||
						type == ElementKeyMap.SHIELD_SUPPLY_MODULE_ID ||
						type == ElementKeyMap.SHIELD_DRAIN_MODULE_ID ||
						type == ElementKeyMap.REPAIR_ID ||
						type == ElementKeyMap.SALVAGE_ID ? 0 : 1);
	}

	public static String getKeyId(short id) {
		Set<Entry<Object, Object>> entrySet = ElementKeyMap.properties.entrySet();
		String key = null;
		for (Entry<Object, Object> e : entrySet) {
			if (e.getValue().equals(String.valueOf(id))) {
				key = e.getKey().toString();
				break;
			}
		}
		return key;
	}

	private static boolean calcIsSignal(short id) {
		return id == ElementKeyMap.ACTIVAION_BLOCK_ID ||
				id == ElementKeyMap.SIGNAL_AND_BLOCK_ID ||
				id == ElementKeyMap.SIGNAL_DELAY_BLOCK_ID ||
				id == ElementKeyMap.SIGNAL_NOT_BLOCK_ID ||
				id == ElementKeyMap.SIGNAL_TRIGGER_STEPON ||
				id == ElementKeyMap.SIGNAL_DELAY_NON_REPEATING_ID ||
				id == ElementKeyMap.LOGIC_BUTTON ||
				id == ElementKeyMap.LOGIC_FLIP_FLOP ||
				id == ElementKeyMap.LOGIC_WIRELESS ||
				id == ElementKeyMap.LOGIC_REMOTE_INNER ||
				id == ElementKeyMap.SIGNAL_OR_BLOCK_ID;
	}


	public static boolean isAlwaysPhysical(short type) {
		return !ElementKeyMap.isDoor(type);
	}



	public static boolean isBlendedSpecial(short type, boolean act) {
		return ElementKeyMap.isDoor(type) && !act;
	}

	public static boolean isVisException(ElementInformation info,
	                                     short containIndexType, boolean isActive) {
		return info.isDoor() && containIndexType < 0;
	}

	public static byte activateOnPlacement(short type) {
		return type > 0 && ElementKeyMap.getInfo(type).activateOnPlacement() ? (byte) 1 : (byte) 0;
	}

	/**
	 * only use for assertion. control is saved in the dynamic structures
	 * @param fromType
	 * @param toType
	 * @return
	 */
	public static boolean canBeControlled(short fromType, short toType) {
		if (!ElementKeyMap.isValidType(toType) || !ElementKeyMap.isValidType(fromType)) {
			return false;
		}
		return (fromType == ElementKeyMap.SHIPYARD_COMPUTER && toType == ElementKeyMap.SHIPYARD_CORE_POSITION) ||
				(fromType == ElementKeyMap.SHIPYARD_COMPUTER && toType == ElementKeyMap.STASH_ELEMENT) ||
				(fromType == ElementKeyMap.SHOP_BLOCK_ID && toType == ElementKeyMap.STASH_ELEMENT) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.SHOP_BLOCK_ID) ||
				(fromType == ElementKeyMap.SHOP_BLOCK_ID && toType == ElementKeyMap.CARGO_SPACE) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.SHIPYARD_COMPUTER) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.STASH_ELEMENT) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.SIGNAL_AND_BLOCK_ID) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.SIGNAL_OR_BLOCK_ID) ||
				(ElementKeyMap.getInfo(fromType).isRailRotator() && toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				(fromType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER && ElementKeyMap.getInfoFast(toType).isSignal()) ||
				(ElementKeyMap.getInfoFast(fromType).isSignal() && toType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && ElementKeyMap.getFactorykeyset().contains(toType)) ||
				(fromType == ElementKeyMap.SALVAGE_CONTROLLER_ID && toType == ElementKeyMap.STASH_ELEMENT) ||
				(ElementKeyMap.getFactorykeyset().contains(fromType) && toType == ElementKeyMap.STASH_ELEMENT) ||
				(fromType == ElementKeyMap.RAIL_RAIL_SPEED_CONTROLLER && ElementKeyMap.getInfo(toType).isRailTrack()) ||
				(fromType == ElementKeyMap.RAIL_RAIL_SPEED_CONTROLLER && toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				
				(fromType == ElementKeyMap.SIGNAL_TRIGGER_AREA_CONTROLLER && ElementKeyMap.getInfo(toType).isSignal()) ||
				(fromType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER && ElementKeyMap.getInfo(toType).isSignal()) ||
				
				(ElementKeyMap.getInfo(fromType).isSignal() && toType == ElementKeyMap.RAIL_BLOCK_DOCKER) ||
				(ElementKeyMap.getInfo(fromType).isSignal() && ElementKeyMap.getInfo(toType).canActivate()) ||
				(ElementKeyMap.getInfo(fromType).isSignal() && ElementKeyMap.getInfo(toType).isRailTrack()) ||
				ElementKeyMap.getInfo(fromType).isLightConnect(toType) ||
				(ElementKeyMap.getInfo(fromType).isMainCombinationControllerB() && ElementKeyMap.getInfo(toType).isMainCombinationControllerB()) ||
				(ElementKeyMap.getInfo(fromType).isSupportCombinationControllerB() && ElementKeyMap.getInfo(toType).isMainCombinationControllerB())
				; 
	}

	private static CharSequence getFactoryResourceString(ElementInformation info) {

		if (info.getFactory() == null) {
			return "CANNOT DISPLAY RESOURCES: NOT A FACTORY";
		}
		StringBuffer sb = new StringBuffer();
		if (info.getFactory().input != null) {
			sb.append("----------Factory Production--------------\n\n");
			assert (info.getFactory().input != null) : info;
			for (int i = 0; i < info.getFactory().input.length; i++) {
				sb.append("----------Product-<" + (i + 1) + ">--------------\n");
				sb.append("--- Required Resources:\n");

				for (FactoryResource r : info.getFactory().input[i]) {
					sb.append(r.count + "x " + ElementKeyMap.getInfo(r.type).getName() + "\n");
				}
				sb.append("\n\n--- Produces Resources:\n");
				for (FactoryResource r : info.getFactory().output[i]) {
					sb.append(r.count + "x " + ElementKeyMap.getInfo(r.type).getName() + "\n");
				}
				sb.append("\n");
			}
			sb.append("\n---------------------------------------------\n\n");
		}
		return sb.toString();
	}

	public static boolean allowsMultiConnect(short controlledType) {
		return
				ElementKeyMap.isValidType(controlledType) && ElementKeyMap.getInfo(controlledType).isMultiControlled();
	}

	public static boolean isMedical(short bId) {
		return bId == ElementKeyMap.MEDICAL_CABINET || bId == ElementKeyMap.MEDICAL_SUPPLIES;
	}

	public boolean isProducedIn(short factoryId) {
		return
				(factoryId == ElementKeyMap.FACTORY_CAPSULE_ASSEMBLER_ID && producedInFactory == FAC_CAPSULE) ||
						(factoryId == ElementKeyMap.FACTORY_MICRO_ASSEMBLER_ID && producedInFactory == FAC_MICRO) ||
						(factoryId == ElementKeyMap.FACTORY_BASIC_ID && producedInFactory == FAC_BASIC) ||
						(factoryId == ElementKeyMap.FACTORY_STANDARD_ID && producedInFactory == FAC_STANDARD) ||
						(factoryId == ElementKeyMap.FACTORY_ADVANCED_ID && producedInFactory == FAC_ADVANCED);
	}
	public short getProducedIn() {
		
		switch(producedInFactory){
		case FAC_CAPSULE: return ElementKeyMap.FACTORY_CAPSULE_ASSEMBLER_ID;
		case FAC_MICRO: return ElementKeyMap.FACTORY_MICRO_ASSEMBLER_ID;
		case FAC_BASIC: return ElementKeyMap.FACTORY_BASIC_ID;
		case FAC_STANDARD: return ElementKeyMap.FACTORY_STANDARD_ID;
		case FAC_ADVANCED: return ElementKeyMap.FACTORY_ADVANCED_ID;
		default: return 0;
		}
		
	}

	private String getDivString(float c) {

		for (float i = 0; i < 8; i++) {
			if (i * 0.125f == c) {
				return (int) i + "/8";
			}
		}
		if (c - Math.round(c) == 0) {
			return String.valueOf(c);
		}
		return StringTools.formatPointZeroZero(c);
	}


	

	

	private void addGraph(Int2IntOpenHashMap map, int col, int localrow, int w, int h, mxGraph graph, Object v1, Object parent, float count, float currentMult) {

		int r = map.get(col);
		String cS;
		String totCS;

		cS = getDivString(count);
		totCS = getDivString(currentMult);

		String label = getName() + "\n(x" + cS + ")\ntot(x" + totCS + ")";
		Object v2 = graph.insertVertex(parent, null, label, col * (w * 2 + 10), r * (h + 10), w, h);

		Object e1 = graph
				.insertEdge(
						parent,
						null,
						"",
						v2,
						v1);

		map.put(col, r + 1);

		int j = 0;
		if (isCapsule()) {
			System.err.println("CAPSULE " + this + "; Consistence " + consistence);
			if (consistence.size() > 0) {
				FactoryResource factoryResource = consistence.get(0);
				ElementInformation from = ElementKeyMap.getInfo(factoryResource.type);
				System.err.println("CAPSULE " + this + "; Consistence " + consistence + " split-> " + from.cubatomConsistence);
				for (FactoryResource c : from.cubatomConsistence) {
					if (c.type == getId()) {
						System.err.println("ADDING CUBATOM CONSISTENS FOR " + this + " -> " + ElementKeyMap.getInfo(c.type));
						from.addGraph(map, col + 1, j, w, h, graph, v2, parent, 1f / c.count, (1f / c.count) * currentMult);
					}
				}
			}
		} else {
			for (FactoryResource c : consistence) {
				System.err.println("ADD CONSISTENCE NORMAL " + c);
				ElementKeyMap.getInfo(c.type).addGraph(map, col + 1, j, w, h, graph, v2, parent, c.count, c.count * currentMult);
				j++;
			}
		}
	}

	public JPanel getGraph() {
		JPanel p = new JPanel(new GridBagLayout());
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		graph.setCellsEditable(false);
		graph.setConnectableEdges(false);
		graph.getModel().beginUpdate();
		int startX = 20;
		int w = 100;
		int h = 60;
		int col = 0;
		try {
			Object v1 = graph.insertVertex(parent, String.valueOf(getId()), getName(), startX, 20, w, h);
			int i = 0;

			Int2IntOpenHashMap map = new Int2IntOpenHashMap();
			if (isCapsule()) {
				System.err.println("CAPSULE " + this + "; Consistence " + consistence);
				if (consistence.size() > 0) {
					FactoryResource factoryResource = consistence.get(0);
					ElementInformation from = ElementKeyMap.getInfo(factoryResource.type);
					System.err.println("CAPSULE " + this + "; Consistence " + consistence + " split-> " + from.cubatomConsistence);
					for (FactoryResource c : from.cubatomConsistence) {
						if (c.type == getId()) {
							System.err.println("ADDING CUBATOM CONSISTENS FOR " + this + " -> " + ElementKeyMap.getInfo(c.type));
							from.addGraph(map, col + 1, i, w, h, graph, v1, parent, 1f / c.count, 1f / (c.count));
						}
					}
				}
			} else {
				for (FactoryResource c : consistence) {

					ElementKeyMap.getInfo(c.type).addGraph(map, col + 1, i, w, h, graph, v1, parent, c.count, c.count);
					map.put(0, 1);
					i++;
				}
			}

		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		GridBagConstraints gbc_btnGraph = new GridBagConstraints();
		gbc_btnGraph.anchor = GridBagConstraints.NORTHEAST;
		gbc_btnGraph.gridx = 0;
		gbc_btnGraph.gridy = 0;
		gbc_btnGraph.weightx = 1;
		gbc_btnGraph.weighty = 1;
		gbc_btnGraph.fill = GridBagConstraints.BOTH;
		p.add(graphComponent, gbc_btnGraph);

		return p;
	}

	public long calculateDynamicPrice() {
		if (dynamicPrice < 0) {
			//			System.err.println("CALC DYN PRICE FOR "+name);
			long price = 0;
			if (!consistence.isEmpty()) {
				for (FactoryResource c : consistence) {
					//					System.err.println("CALC DYN PRICE FOR "+name+" adding: "+ElementKeyMap.getInfo(c.type).name);

					price += c.count * ElementKeyMap.getInfo(c.type).calculateDynamicPrice();
				}
			} else {
				price = this.price;
			}
			dynamicPrice = price;
		}
		return dynamicPrice;
	}

	public boolean isCapsule() {
		return blockResourceType == RT_CAPSULE;
	}

	public boolean isOre() {
		return blockResourceType == RT_ORE;
	}

	public void appendXML(Document doc, org.w3c.dom.Element parent) throws CannotAppendXMLException {
		String tagName = "Block";//getName().replaceAll("[^a-zA-Z]+", "");

		org.w3c.dom.Element child = doc.createElement(tagName);

		//		<Gravity type='GRAVITY_ID' icon='0' textureId='192' name='Gravity Unit'>

		String key = getKeyId(getId());

		if (key == null) {
			throw new CannotAppendXMLException("Cannot find property key for Block ID " + getId() + "; Check your Block properties file");
		}
		child.setAttribute("type", key);
		child.setAttribute("icon", String.valueOf(getBuildIconNum()));

		child.setAttribute("textureId", String.valueOf(StringTools.getCommaSeperated(getTextureIds())));
		child.setAttribute("name", name);

		Field[] fields = ElementInformation.class.getFields();
		for (Field f : fields) {
			try {
				f.setAccessible(true);
				if (f.get(this) == null) {
					continue;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new CannotAppendXMLException(e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new CannotAppendXMLException(e.getMessage());
			}

			Element annotation = f.getAnnotation(Element.class);

			if (annotation != null && annotation.writeAsTag()) {

				org.w3c.dom.Element node = doc.createElement(annotation.tag());
				try {
					if (annotation.factory()) {
						if (getFactory().input == null) {
							node.setTextContent("INPUT");
						} else {

							for (int pid = 0; pid < getFactory().input.length; pid++) {
								org.w3c.dom.Element prodNode = doc.createElement("Product");

								org.w3c.dom.Element inputNode = doc.createElement("Input");
								org.w3c.dom.Element outputNode = doc.createElement("Output");

								for (int i = 0; i < getFactory().input[pid].length; i++) {
									FactoryResource factoryResource = getFactory().input[pid][i];

									inputNode.appendChild(factoryResource.getNode(doc));
								}

								for (int i = 0; i < getFactory().output[pid].length; i++) {
									FactoryResource factoryResource = getFactory().output[pid][i];

									outputNode.appendChild(factoryResource.getNode(doc));
								}

								prodNode.appendChild(inputNode);
								prodNode.appendChild(outputNode);
								node.appendChild(prodNode);
							}

						}

					} else if (annotation.type()) {
						short string = f.getShort(this);
						node.setTextContent(String.valueOf(string));
					} else if (annotation.cubatom()) {

					} else if (annotation.consistence()) {
						for (int j = 0; j < getConsistence().size(); j++) {

							FactoryResource factoryResource = getConsistence().get(j);
							node.appendChild(factoryResource.getNode(doc));

						}
					} else if (annotation.cubatomConsistence()) {
						for (int j = 0; j < cubatomConsistence.size(); j++) {
							FactoryResource factoryResource = cubatomConsistence.get(j);
							node.appendChild(factoryResource.getNode(doc));

						}
					} else if (annotation.vector3f()) {
						Vector3f v = (Vector3f) f.get(this);

						node.setTextContent(v.x + "," + v.y + "," + v.z);

					} else if (annotation.vector4f()) {
						Vector4f v = (Vector4f) f.get(this);

						node.setTextContent(v.x + "," + v.y + "," + v.z + "," + v.w);

					} else if (f.getType().equals(short[].class)) {
						short[] v = (short[]) f.get(this);
						StringBuffer d = new StringBuffer();
						for(int i = 0; i < v.length; i++){
							d.append(v[i]);
							if(i < v.length - 1){
								d.append(", ");
							}
						}
						node.setTextContent(d.toString());

					} else if (f.getType().equals(int[].class)) {
						int[] v = (int[]) f.get(this);
						StringBuffer d = new StringBuffer();
						for(int i = 0; i < v.length; i++){
							d.append(v[i]);
							if(i < v.length - 1){
								d.append(", ");
							}
						}
						node.setTextContent(d.toString());

					} else if (annotation.collectionType().equals("blockTypes")) {
						@SuppressWarnings("unchecked")
						Collection<Short> set = (Collection<Short>) f.get(this);
						if (set.isEmpty()) {
							continue;
						}
						for (Short s : set) {
							//							if(ElementKeyMap.getFactorykeyset().contains(getId()) && ElementKeyMap.getFactorykeyset().contains(s)){
							//								continue;
							//								//DO NOT WRITE FACTORIES. they are added automatically
							//							}
							org.w3c.dom.Element item = doc.createElement(annotation.collectionElementTag());
							String keyId = getKeyId(s);
							if (keyId == null) {
								throw new CannotAppendXMLException("[BlockSet] " + f.getName() + " Cannot find property key for Block ID " + s + "; Check your Block properties file");
							}
							item.setTextContent(keyId);
							node.appendChild(item);
						}

					} else {
						String string = f.get(this).toString();

						if (annotation.textArea()) {
							string = string.replace("\n", "\\n\\r");
						}
						if (string.length() == 0) {
							continue;
						}
						node.setTextContent(string);

					}
				} catch (Exception e1) {
					e1.printStackTrace();
					throw new CannotAppendXMLException(e1.getMessage());
				}

				child.appendChild(node);
			}
		}

		parent.appendChild(child);
	}

	public short[] getTextureIds() {
		return textureId;
	}

	public boolean canActivate() {
		return canActivate;
	}

	@Override
	public int compareTo(ElementInformation o) {
		return name.compareTo(o.name);
	}

	/**
	 * @return the amour
	 */
	public float getAmour() {
		return amour;
	}

	/**
	 * @param amour the amour to set
	 */
	public void setAmour(float amour) {
		this.amour = amour;
		this.armourPercent = amour / 100f;
	}

	/**
	 * @return the armourPercent
	 */
	public float getArmourPercent() {
		return armourPercent;
	}

	/**
	 * @return the blockStyle
	 */
	public int getBlockStyle() {
		return blockStyle;
	}

	/**
	 * @param blockStyle the blockStyle to set
	 */
	public void setBlockStyle(int blockStyle) {
		this.blockStyle = blockStyle;
		solidBlockStyle = blockStyle > 0 && blockStyle != 3 && blockStyle != 6;
		blendedBlockStyle = blockStyle == 3 ;
	}

	/**
	 * @return the buildIconNum
	 */
	public int getBuildIconNum() {
		return buildIconNum;
	}

	/**
	 * @param buildIconNum the buildIconNum to set
	 */
	public void setBuildIconNum(int buildIconNum) {
		this.buildIconNum = buildIconNum;
	}

	public Set<Short> getControlledBy() {
		return controlledBy;
	}

	public Set<Short> getControlling() {
		return controlling;
	}

	


	/**
	 * @return the description
	 */
	public String getDescription() {
		String n = ElementKeyMap.descriptionTranslations.get(id);
		if(n != null){
			return n;
		}
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public byte getExtraOrientation() {
		return (byte) 0;
	}

	public BlockFactory getFactory() {
		return this.factory;
	}

	public void setFactory(BlockFactory f) {
		this.factory = f;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		if (fullName == null) {
			return getName();
		}
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the id
	 */
	public short getId() {
		return id;
	}

	public int getIndividualSides() {
		return individualSides;
	}

	public void setIndividualSides(int individualSides) {
		this.individualSides = individualSides;
	}


	/**
	 * @return the lightSourceColor
	 */
	public Vector4f getLightSourceColor() {
		return lightSourceColor;
	}

	/**
	 * @return the maxHitPoints
	 */
	public short getMaxHitPoints() {
		return maxHitPoints;
	}

	/**
	 * @param maxHitPoints the maxHitPoints to set
	 */
	public void setMaxHitPoints(short maxHitPoints) {
		assert (maxHitPoints > 0 && maxHitPoints < 256) : maxHitPoints;
		this.maxHitPoints = maxHitPoints;
		this.maxHitpointsInverse = (1f / maxHitPoints);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		String n = ElementKeyMap.nameTranslations.get(id);
		if(n != null){
			return n;
		}
		return name;
	}

	/**
	 * @return the price
	 */
	public long getPrice(boolean dynamic) {
		if (!dynamic) {
			return price;
		} else {
			return dynamicPrice;
		}
	}

	public List<Short> getRecipeBuyResources() {
		return recipeBuyResources;
	}

	/**
	 * @return the textureId
	 */
	public short getTextureId(int side) {
		return textureId[side];
	}

	public ElementCategory getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getId();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return ((ElementInformation) obj).getId() == getId();
	}

	@Override
	public String toString() {
		return getName() + "(" + getId() + ")";
	}

	/**
	 * @return the animated
	 */
	public boolean isAnimated() {
		return animated;
	}

	/**
	 * @param animated the animated to set
	 */
	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	/**
	 * @return the blended
	 */
	public boolean isBlended() {
		return blended;
	}

	/**
	 * @param blended the blended to set
	 */
	public void setBlended(boolean blended) {
		this.blended = blended;
	}

	public boolean isController() {
		return !getControlling().isEmpty() || controlsAll();
	}

	public boolean isDockable() {
		return getId() == ElementKeyMap.TURRET_DOCK_ID || getId() == ElementKeyMap.FIXED_DOCK_ID;
	}

	/**
	 * @return the enterable
	 */
	public boolean isEnterable() {
		return enterable;
	}

	/**
	 * @param enterable the enterable to set
	 */
	public void setEnterable(boolean enterable) {
		this.enterable = enterable;
	}

	/**
	 * @return the inRecipe
	 */
	public boolean isInRecipe() {
		return inRecipe;
	}

	/**
	 * @param inRecipe the inRecipe to set
	 */
	public void setInRecipe(boolean inRecipe) {
		this.inRecipe = inRecipe;
	}

	

	public boolean isLightSource() {
		return lightSource;
	}

	public void setLightSource(boolean lightSource) {
		this.lightSource = lightSource;
	}

	public boolean isOrientatable() {
		return orientatable;
	}

	public void setOrientatable(boolean orientatable) {
		this.orientatable = orientatable;
	}

	/**
	 * @return the physical
	 */
	public boolean isPhysical() {

		return physical;
	}

	/**
	 * @param physical the physical to set
	 */
	public void setPhysical(boolean physical) {
		this.physical = physical;
	}

	/**
	 * @return the physical
	 */
	public boolean isPhysical(boolean active) {
		return isDoor() ? active : physical;
	}

	public boolean isPlacable() {
		return placable;
	}

	/**
	 * @param placable the placable to set
	 */
	public void setPlacable(boolean placable) {
		this.placable = placable;
	}

	/**
	 * @return the shoppable
	 */
	public boolean isShoppable() {
		return shoppable;
	}

	/**
	 * @param shoppable the shoppable to set
	 */
	public void setShoppable(boolean shoppable) {
		this.shoppable = shoppable;
	}

	/**
	 * @param canActivate the canActivate to set
	 */
	public void setCanActivate(boolean canActivate) {
		this.canActivate = canActivate;
	}


	/**
	 * @param price the price to set
	 */
	public void setPrice(long price) {
		this.price = price;
	}

	public boolean isSolidBlockStyle() {
		return solidBlockStyle;
	}

	



	public boolean usesActiveBitForExtraOrientation() {
		return getBlockStyle() == 2; //corner piece
	}

	public boolean isBlendBlockStyle() {
		return blendedBlockStyle || (hasLod() && lodShapeStyle == 1);
	}

	public boolean controlsAll() {
		return isSignal();
	}

	public boolean isSignal() {
		return signal;
	}

	/**
	 * @return the hasActivationTexure
	 */
	public boolean isHasActivationTexure() {
		return hasActivationTexure;
	}

	/**
	 * @param hasActivationTexure the hasActivationTexure to set
	 */
	public void setHasActivationTexure(boolean hasActivationTexure) {
		this.hasActivationTexure = hasActivationTexure;
	}


	/**
	 * @return the supportCombinationController
	 */
	public boolean isSupportCombinationControllerB() {
		return supportCombinationController;
	}

	/**
	 * @param supportCombinationController the supportCombinationController to set
	 */
	public void setSupportCombinationController(boolean supportCombinationController) {
		this.supportCombinationController = supportCombinationController;
	}

	/**
	 * @return the mainCombinationController
	 */
	public boolean isMainCombinationControllerB() {
		return mainCombinationController;
	}

	/**
	 * @param mainCombinationController the mainCombinationController to set
	 */
	public void setMainCombinationController(boolean mainCombinationController) {
		this.mainCombinationController = mainCombinationController;
	}

	/**
	 * ok, when: main->main or support->main
	 *
	 * @param to
	 * @return
	 */
	public boolean isCombiConnectSupport(short to) {
		if (ElementKeyMap.isValidType(to)) {
			ElementInformation info = ElementKeyMap.getInfo(to);
			if (isMainCombinationControllerB() && info.isMainCombinationControllerB()) {
				return true;
			}
			if (isSupportCombinationControllerB() && info.isMainCombinationControllerB()) {
				return true;
			}
		}

		return false;
	}

	public boolean isCombiConnectEffect(short to) {
		ElementInformation info = ElementKeyMap.getInfo(to);
		return isMainCombinationControllerB() && info.isEffectCombinationController();
	}

	/**
	 * @return the effectCombinationController
	 */
	public boolean isEffectCombinationController() {
		return effectCombinationController;
	}

	/**
	 * @param effectCombinationController the effectCombinationController to set
	 */
	public void setEffectCombinationController(boolean effectCombinationController) {
		this.effectCombinationController = effectCombinationController;
	}

	public boolean isCombiConnectAny(short to) {
		return isCombiConnectSupport(to) || isCombiConnectEffect(to) || isLightConnect(to);
	}

	public boolean isLightConnect(short to) {
		if (ElementKeyMap.isValidType(to)) {
			ElementInformation info = ElementKeyMap.getInfo(to);
			if (isMainCombinationControllerB() && info.isLightSource()) {
				return true;
			}
		}
		return false;
	}

	public boolean activateOnPlacement() {
		if (isSignal()) {
			return false;
		}
		if (ElementKeyMap.getFactorykeyset().contains(id) || isLightSource() || getBlockStyle() > 0 || isDoor() || getId() == ElementKeyMap.STASH_ELEMENT) {
			return true;
		}
		return false;
	}

	public String[] parseDescription() {
		String d = getDescription();

		String[] split = d.split("\\n");
		boolean del = false;
		for (int i = 0; i < split.length; i++) {
			
			split[i] = split[i].replace("$ACTIVATE", /*KeyboardMappings.ACTIVATE.getKeyChar()*/ "R");

			
			if (del || split[i].contains("Structural Stats") || split[i].contains("StructuralStats")) {
				if(!del){
					split[i] = split[i].substring(0, split[i].indexOf("Structural")-1);
				}else{
					split[i] = "";
				}
				del = true;
			}
			
			if (split[i].contains("$RESOURCES")) {
				split[i] = split[i].replace("$RESOURCES", getFactoryResourceString(this));
			}
			if (split[i].contains("$ARMOUR")) {
				split[i] = split[i].replace("$ARMOUR", String.valueOf(getAmour()));
			}
			if (split[i].contains("$HP")) {
				split[i] = split[i].replace("$HP", String.valueOf(getMaxHitPoints()));
			}

			if (split[i].contains("$EFFECT")) {
				split[i] = split[i].replace("$EFFECT", "");
//				ShipManagerContainer c = new ShipManagerContainer(new Ship(state));
//				EffectElementManager<?, ?, ?> effect = c.getEffect(getId());
//				if (effect != null) {
//					split[i] = split[i].replace("$EFFECT",
//							Lng.str("You can use this system to upgrade your weapons or to use defensively on your ship.\n\nTo link your Controller to its Modules, press %s on the Controller, then %s on the individual modules, \nor alternatively Shift + $CONNECT_MODULE to mass select grouped modules.\n\nAfterwards you can link it to a weapon by connecting the weapon controller you want to upgrade\nwith the effect controller you just made.\nYou can do this manually with $SELECT_MODULE and $CONNECT_MODULE or in the Weapons Menu.\n\n\nEffect:\n%s\n\nNote that for the full effect, \nyou need to connect 1:1 in size. \nThe amount of linked modules of your effect has to be the same amount of your weapon.\n\nIf not used linked to a weapon, \nthe system has a defensive effect you can enable\nPlace the computer on your hotbar in the Weapons Menu\nand activate it:\n\n",  KeyboardMappings.SELECT_MODULE.getKeyChar(),  KeyboardMappings.CONNECT_MODULE.getKeyChar(),  effect.getCombiDescription()) +
//									effect.getDefensiveEffectType().getShopDescription());
//				} else {
//					split[i] = split[i].replace("$EFFECT", "NO_EFFECT(invalid $ var)");
//				}
			}
			if (split[i].contains("$MAINCOMBI")) {

				split[i] = split[i].replace("$MAINCOMBI",
						String.format("This controller can be connected to other weapons\nand systems (cannon, beam, damage pulse, missile)\nto customize your weapon.\n\nTo link your Controller to its Modules, press %s on the Controller, then %s on the individual modules, \nor alternatively Shift + $CONNECT_MODULE to mass select grouped modules.\n\nAfterwards you can link it to another weapon by connecting the weapon controller you want to upgrade\nwith the weapon controller you just made.\nYou can do this manually with $SELECT_MODULE and $CONNECT_MODULE or in the Weapons Menu.\n\nNote that for the full effect, \nyou need to connect 1:1 in size.\n\n", 
								/*KeyboardMappings.SELECT_MODULE.getKeyChar()*/"C", 
								/*KeyboardMappings.CONNECT_MODULE.getKeyChar()*/"X")
				);

			}
		}
		return split;
	}

	/**
	 * @return the deprecated
	 */
	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean b) {
		this.deprecated = b;
	}

	/**
	 * @return the consistence
	 */
	public ArrayList<FactoryResource> getConsistence() {
		return consistence;
	}

	public RecipeInterface getProductionRecipe() {

		if (productionRecipe == null) {
			productionRecipe = new FixedRecipe();
			productionRecipe.costAmount = -1;
			productionRecipe.costType = -1;
			if (consistence.isEmpty()) {
				//empty recipe
				productionRecipe.recipeProducts = new FixedRecipeProduct[0];
			} else if (isCapsule()) {
				ElementInformation from = ElementKeyMap.getInfo(consistence.get(0).type);
				productionRecipe.recipeProducts = new FixedRecipeProduct[1];
				productionRecipe.recipeProducts[0] = new FixedRecipeProduct();

				productionRecipe.recipeProducts[0].input = new FactoryResource[1];
				productionRecipe.recipeProducts[0].input[0] = new FactoryResource(1, from.getId());

				productionRecipe.recipeProducts[0].output = new FactoryResource[from.cubatomConsistence.size()];
				int i = 0;
				for (FactoryResource c : from.cubatomConsistence) {
					productionRecipe.recipeProducts[0].output[i] = c;
					i++;
				}

			} else {
				productionRecipe.recipeProducts = new FixedRecipeProduct[1];
				productionRecipe.recipeProducts[0] = new FixedRecipeProduct();

				productionRecipe.recipeProducts[0].input = new FactoryResource[consistence.size()];
				int i = 0;
				for (FactoryResource c : consistence) {
					assert (c != null);
					productionRecipe.recipeProducts[0].input[i] = c;
					i++;
				}

				productionRecipe.recipeProducts[0].output = new FactoryResource[1];
				productionRecipe.recipeProducts[0].output[0] = new FactoryResource(1, getId());
			}
		}

		return productionRecipe;
	}

	/**
	 * @return the maxHitpointsInverse
	 */
	public float getMaxHitpointsInverse() {
		return maxHitpointsInverse;
	}

	/**
	 * @return the basicResourceFactory
	 */
	public short getBasicResourceFactory() {
		return basicResourceFactory;
	}

	/**
	 * @param basicResourceFactory the basicResourceFactory to set
	 */
	public void setBasicResourceFactory(short basicResourceFactory) {
		this.basicResourceFactory = basicResourceFactory;
	}

	/**
	 * @return the explosionAbsorbtion
	 */
	public float getExplosionAbsorbtion() {
		return explosionAbsorbtion;
	}

	/**
	 * @param explosionAbsorbtion the explosionAbsorbtion to set
	 */
	public void setExplosionAbsorbtion(float explosionAbsorbtion) {
		this.explosionAbsorbtion = explosionAbsorbtion;
	}

	/**
	 * @return the factoryBakeTime
	 */
	public float getFactoryBakeTime() {
		return factoryBakeTime;
	}

	/**
	 * @param factoryBakeTime the factoryBakeTime to set
	 */
	public void setFactoryBakeTime(float factoryBakeTime) {
		this.factoryBakeTime = factoryBakeTime;
	}

	public boolean isMultiControlled() {
		return
				isSignal() ||
						getId() == ElementKeyMap.STASH_ELEMENT ||
						isRailTrack() ||
						ElementKeyMap.getFactorykeyset().contains(getId());
	}

	public String createWikiStub() {
		StringBuffer s = new StringBuffer();

		s.append("{{infobox block" + "\n");
		s.append("  |type=" + getName() + "\n");
		s.append("	|hp=" + getMaxHitPoints() + "\n");
		s.append("	|armor=" + (int) (getArmourPercent() * 100) + "%" + "\n");
		s.append("	|light=" + (isLightSource() ? "yes" : "no") + "\n");
		if (isLightSource()) {
			s.append("	|lightColor=" + getLightSourceColor() + "\n");
		}
		s.append("	|dv=" + getId() + "\n");
		s.append("}}" + "\n\n");


		return s.toString();
	}

	public String getFilledDescription() {
		StringBuffer newB = new StringBuffer();
		String[] split = getDescription().split("\\n");
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].replace("$ACTIVATE", "R"/*KeyboardMappings.ACTIVATE.getKeyChar()*/);

			if (split[i].contains("$ARMOUR")) {
				split[i] = split[i].replace("$ARMOUR", String.valueOf(getAmour()));
			}
			if (split[i].contains("$HP")) {
				split[i] = split[i].replace("$HP", String.valueOf(getMaxHitPoints()));
			}

			if (split[i].contains("$EFFECT")) {
				//				ShipManagerContainer c = new ShipManagerContainer(new Ship(getState()));
				//				EffectElementManager<?, ?, ?> effect = c.getEffect(info.getId());
				//				if(effect != null){
				//					split[i] = split[i].replace("$EFFECT",
				//							"You can use this system\n"
				//									+ "to upgrade your weapons.\n"
				//									+ "Place it like you would a normal weapon.\n"
				//									+ "First the controller and then the module,\n"
				//									+ "so the module is connected to the controller.\n"
				//									+ "You can then select a weapon controller with "+KeyboardMappings.SELECT_MODULE.getKeyChar()+"\n"
				//									+ "and then press "+KeyboardMappings.CONNECT_MODULE.getKeyChar()+" to hook\n"
				//									+ "the effect up to the weapon\n\n"
				//									+ "Effect:\n"+effect.getCombiDescription()+"\n\n"
				//									+ "Note, that for the full effect, \nyou need to connect 1:1 in size.\n\n"
				//									+ "If not used for a weapon, \n"
				//									+ "the effect system has a defensive effect:\n\n"+
				//									effect.getDefensiveEffectType().getShopDescription());
				//				}else{
				//					split[i] = split[i].replace("$EFFECT", "NO_EFFECT(invalid $ var)");
				//				}
			}

			if (split[i].contains("$MAINCOMBI")) {

				split[i] = split[i].replace("$MAINCOMBI",
						"This controller can be connected to other weapons\n"
								+ "and systems (cannon, beam, pulse, missile)\n"
								+ "to customize your weapon\n\n"
								+ "First the controller and then the module,\n"
								+ "so the module is connected to the controller.\n"
								+ "You can then select another weapon controller with C\n"
								+ "and then press X to hook\n"
								+ "this weapon up to the other weapon\n\n"
								+ "Note, that for the full effect, \nyou need to connect 1:1 in size\n\n"
				);

			}
			newB.append(split[i] + "\n");

		}
		return newB.toString();
	}

	public boolean isDoor() {
		return door;
	}

	public void setDoor(boolean c) {
		this.door = c;
	}

	public int getProducedInFactory() {
		return producedInFactory;
	}

	public void setProducedInFactory(int producedInFactory) {
		this.producedInFactory = producedInFactory;
	}

	/**
	 * @return the inventoryGroup
	 */
	public String getInventoryGroup() {
		return inventoryGroup;
	}

	/**
	 * @param inventoryGroup the inventoryGroup to set
	 */
	public void setInventoryGroup(String inventoryGroup) {
		this.inventoryGroup = inventoryGroup.trim();
	}

	public boolean hasInventoryGroup() {
		return inventoryGroup.length() > 0;
	}

	public boolean isNormalBlockStyle() {
		return blockStyle == 0 || blockStyle == 6;
	}

	public boolean isRailTrack() {
		return blockStyle == 6 && !hasLod() && 
				id != ElementKeyMap.RAIL_BLOCK_DOCKER && 
				id != ElementKeyMap.RAIL_BLOCK_TURRET_Y_AXIS && 
				id != ElementKeyMap.SHIPYARD_CORE_POSITION && 
				id != ElementKeyMap.PICKUP_AREA && 
				id != ElementKeyMap.SHIPYARD_MODULE;
	}

	public boolean isRailShipyardCore() {
		return (id == ElementKeyMap.SHIPYARD_CORE_POSITION);
	}
	public boolean isRailRotator() {
		return (id == ElementKeyMap.RAIL_BLOCK_CW || id == ElementKeyMap.RAIL_BLOCK_CCW);
	}

	public boolean isRailTurret() {
		return id == ElementKeyMap.RAIL_BLOCK_TURRET_Y_AXIS;
	}

	public boolean isRailDockable() {
		return isRailTrack() || isRailRotator() || isRailTurret();
	}

	public void recreateTextureMapping() {
		for (byte i = 0; i < 6; i++) {
			textureLayerMapping[i] = calcTextureLayerCode(false, i);
			textureIndexLocalMapping[i] = calcTextureIndexLocalCode(false, i);

			textureLayerMappingActive[i] = calcTextureLayerCode(true, i);
			textureIndexLocalMappingActive[i] = calcTextureIndexLocalCode(true, i);
		}

		createdTX = true;

	}

	public byte getTextureLayer(boolean active, byte side) {
		assert (createdTX);
		return (byte) (active ? textureLayerMappingActive[side] : textureLayerMapping[side]);
	}

	public short getTextureIndexLocal(boolean active, byte side) {
		assert (createdTX);
		return (short) (active ? textureIndexLocalMappingActive[side] : textureIndexLocalMapping[side]);
	}

	/**
	 * used to cache
	 *
	 * @param active
	 * @param orientationCode
	 * @return the texture layer (number of texture file) the texture is on
	 */
	private byte calcTextureLayerCode(boolean active, byte orientationCode) {
		return (byte) (Math.abs(getTextureId(active, orientationCode)) / 256);
	}

	/**
	 * used to cache
	 *
	 * @param active
	 * @param orientationCode
	 * @return the texture index on its layer
	 */
	private short calcTextureIndexLocalCode(boolean active, byte orientationCode) {
		return (short) ((getTextureId(active, orientationCode)) % 256);
	}

	private short getTextureId(boolean active, int side) {
		assert (id != ElementKeyMap.ACTIVAION_BLOCK_ID || hasActivationTexure);

		if (hasActivationTexure && !active) {
			return (short) (textureId[side] + 1);
		}
		return textureId[side];
	}

	/**
	 * @param textureId the textureId to set
	 */
	public void setTextureId(short[] textureId) {
		this.textureId = Arrays.copyOf(textureId, textureId.length);
		createdTX = false;
	}

	public void setTextureId(int side, short tex) {
		textureId[side] = tex;
		createdTX = false;
	}


	public boolean isRailDocker() {
		return id == ElementKeyMap.RAIL_BLOCK_DOCKER;
	}

	public boolean isRailSpeedActivationConnect(short controlledType) {
		return id == ElementKeyMap.RAIL_RAIL_SPEED_CONTROLLER && controlledType == ElementKeyMap.ACTIVAION_BLOCK_ID;
	}

	public boolean isRailSpeedTrackConnect(short controlledType) {
		return id == ElementKeyMap.RAIL_RAIL_SPEED_CONTROLLER && ElementKeyMap.isValidType(controlledType) && ElementKeyMap.getInfo(controlledType).isRailTrack();
	}

	public boolean needsCoreConnectionToWorkOnHotbar() {

		return id != ElementKeyMap.RAIL_BLOCK_DOCKER
				&& id != ElementKeyMap.LOGIC_REMOTE_INNER;
	}

	/**
	 * @return mass of block
	 */
	public float getMass() {
		return mass;
	}


	public int getSlab() {
		return slab;
	}
	public int getSlab(int orientation) {
		if(id == ElementKeyMap.CARGO_SPACE){
			return orientation;
		}
		return slab;
	}

	public float getVolume() {
		return volume;
	}

	public boolean isInventory() {
		return 
				id == ElementKeyMap.STASH_ELEMENT || 
				id == ElementKeyMap.FACTORY_BASIC_ID ||
				id == ElementKeyMap.SHIPYARD_COMPUTER ||
				id == ElementKeyMap.FACTORY_MICRO_ASSEMBLER_ID ||
				id == ElementKeyMap.FACTORY_STANDARD_ID ||
				id == ElementKeyMap.FACTORY_ADVANCED_ID ||
				id == ElementKeyMap.SHOP_BLOCK_ID ||
				ElementKeyMap.factoryInfoArray[id];
	}

	public boolean isSpecialBlock() {
		return specialBlock;
	}

	public void setSpecialBlock(boolean specialBlock) {
		this.specialBlock = specialBlock;
	}


	public boolean isDrawnOnlyInBuildMode() {
		assert(!drawOnlyInBuildMode || blended);
		return drawOnlyInBuildMode;
	}

	public void setDrawOnlyInBuildMode(boolean drawOnlyInBuildMode) {
		this.drawOnlyInBuildMode = drawOnlyInBuildMode;
		
	}

	public boolean isInOctree() {
		return id != ElementKeyMap.PICKUP_AREA && id != ElementKeyMap.EXIT_SHOOT_RAIL && id != ElementKeyMap.PICKUP_RAIL;
	}

	

	public void onInit() {
		calculateDynamicPrice();	
	}

	

	public boolean hasLod(){
		return lodShapeString.length() > 0;
	}

	



}
