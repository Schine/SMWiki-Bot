package org.schine.starmade.data.element;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.schine.starmade.ElementCountMap;
import org.schine.starmade.ElementReactorChange;
import org.schine.starmade.data.element.annotation.ElemType;
import org.schine.starmade.data.element.annotation.Element;
import org.schine.starmade.data.element.exception.ElementParserException;
import org.schine.starmade.data.element.factory.BlockFactory;
import org.schine.starmade.data.element.factory.FactoryResource;
import org.schine.starmade.data.element.factory.FixedRecipe;
import org.schine.starmade.data.element.factory.FixedRecipeProduct;
import org.schine.starmade.data.element.factory.RecipeInterface;
import org.schine.starmade.effect.InterEffectSet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
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
	public static final int CHAMBER_APPLIES_TO_SELF = 0;
	public static final int CHAMBER_APPLIES_TO_SECTOR = 1;
	
	//not write as tag (its an attribute)
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.ID)
	public final short id;
	
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.TEXTURE)
	private short[] textureId;	
	
	
	@Element(consistence = true, parser = ElemType.CONSISTENCE)
	public final List<FactoryResource> consistence = new ObjectArrayList<FactoryResource>();
	
	@Element(cubatomConsistence = true, parser = ElemType.CUBATON_CONSISTENCE)
	public final List<FactoryResource> cubatomConsistence = new ObjectArrayList<FactoryResource>();
	
	@Element( collectionElementTag = "Element", elementSet = true, collectionType = "blockTypes", parser = ElemType.CONTROLLED_BY)
	public final ShortSet controlledBy = new ShortOpenHashSet();
	
	@Element(collectionElementTag = "Element", elementSet = true, collectionType = "blockTypes", parser = ElemType.CONTROLLING)
	public final ShortSet controlling = new ShortOpenHashSet();
	
	@Element(collectionElementTag = "Element", elementSet = true, collectionType = "blockTypes", parser = ElemType.RECIPE_BUY_RESOURCE)
	public final ShortList recipeBuyResources = new ShortArrayList();

	public final ObjectOpenHashSet<String> parsed = new ObjectOpenHashSet<String>(2048);
	
	private final int[] textureLayerMapping = new int[6];
	
	private final int[] textureIndexLocalMapping = new int[6];
	
	private final int[] textureLayerMappingActive = new int[6];
	
	private final int[] textureIndexLocalMappingActive = new int[6];
	
	
	@Element(from = 0, to = 10000000, parser = ElemType.ARMOR_VALUE)
	public float armorValue;
	
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.NAME)
	public String name;
	
	public ElementCategory type;
	
	@Element(parser = ElemType.EFFECT_ARMOR, canBulkChange = true)
	public InterEffectSet effectArmor = new InterEffectSet();
	
	@Element(writeAsTag = false, canBulkChange = false, parser = ElemType.BUILD_ICON)
	public int buildIconNum = 62;
	
	@Element( canBulkChange = false, parser = ElemType.FULL_NAME)
	public String fullName = "";
	
	@Element(from = 0, to = Integer.MAX_VALUE,  parser = ElemType.PRICE)
	public long price = 100;
	
	@Element( textArea = true, parser = ElemType.DESCRIPTION)
	public String description = "undefined description";
	
	@Element( states = {"0", "1", "2", "3", "4", "5", "6"}, stateDescs = {"ore", "plant", "basic", "Cubatom-Splittable", "manufactory", "advanced", "capsule"}, parser = ElemType.BLOCK_RESOURCE_TYPE)
	public int blockResourceType = 2;
	
	@Element( states = {"0", "1", "2", "3", "4", "5"}, stateDescs = {"none", "capsule refinery", "micro assembler", "basic factory", "standard factory", "advanced factory"}, parser = ElemType.PRODUCED_IN_FACTORY)
	public int producedInFactory = 0;
	
	@Element( type = true, parser = ElemType.BASIC_RESOURCE_FACTORY)
	public short basicResourceFactory = 0;
	
	@Element(from = 0, to = 1000000, parser = ElemType.FACTORY_BAKE_TIME)
	public float factoryBakeTime = 5;
	
	@Element( inventoryGroup = true, parser = ElemType.INVENTORY_GROUP)
	public String inventoryGroup = "";
	
	@Element( factory = true, parser = ElemType.FACTORY)
	public BlockFactory factory;
	
	@Element( parser = ElemType.ANIMATED)
	public boolean animated;
	
	@Element(from = 0, to = Integer.MAX_VALUE,  parser = ElemType.STRUCTURE_HP)
	public int structureHP = 0;
	
	@Element( parser = ElemType.TRANSPARENCY)
	public boolean blended;
	
	@Element( parser = ElemType.IN_SHOP)
	public boolean shoppable = true;
	
	@Element( parser = ElemType.ORIENTATION)
	public boolean orientatable;
	
	@Element( selectBlock = true, parser = ElemType.BLOCK_COMPUTER_REFERENCE)
	public int computerType;
	
	@Element( states = {"0", "1", "2", "3"}, stateDescs = {"full block", "3/4 block", "1/2 block", "1/4 block"}, parser = ElemType.SLAB)
	public int slab = 0;
	
	@Element( canBulkChange = false, parser = ElemType.SLAB_IDS)
	public short[] slabIds = null;
	
	@Element( canBulkChange = false, parser = ElemType.STYLE_IDS)
	public short[] styleIds = null;
	
	@Element( canBulkChange = false, parser = ElemType.WILDCARD_IDS)
	public short[] wildcardIds = null;
	
	public short[] blocktypeIds;
	
	
	@Element( canBulkChange = false, editable = false, parser = ElemType.SLAB_REFERENCE)
	public int sourceReference = 0;
	
	/**
	 * GeneralChamber: true if the block is a general chamber block that can be later specified (e.g. Jump Chamber (general) -> Jump Distance (specified))
	 */
	@Element( parser = ElemType.GENERAL_CHAMBER)
	public boolean chamberGeneral;
	
	@Element(writeAsTag = false, parser = ElemType.EDIT_REACTOR)
	public ElementReactorChange change;
	
	
	@Element( parser = ElemType.CHAMBER_CAPACITY)
	public float chamberCapacity = 0.0f;
	
	/**
	 * ChamberRoot: the top level chamber (jump distance 0)
	 */
	@Element( editable = false, selectBlock = true, parser = ElemType.CHAMBER_ROOT)
	public int chamberRoot;
	
	/**
	 * ChamberParent: the parent of a chamber (jump distance 1 has jump distance 0 as parent)
	 */
	@Element( editable = false, selectBlock = true, parser = ElemType.CHAMBER_PARENT)
	public int chamberParent;
	
	@Element( editable = false, selectBlock = true, parser = ElemType.CHAMBER_UPGRADES_TO)
	public int chamberUpgradesTo;
	
	public static final int CHAMBER_PERMISSION_ANY = 0;
	public static final int CHAMBER_PERMISSION_SHIP = 1;
	public static final int CHAMBER_PERMISSION_STATION = 2;
	public static final int CHAMBER_PERMISSION_PLANET = 4;
	@Element( states = {"0", "1", "6", "2", "4"}, stateDescs = {"Any", "Ship Only", "Station/Planet Only", "Station Only", "Planet Only"}, parser = ElemType.CHAMBER_PERMISSION)
	public int chamberPermission = 0;
	
	/**
	 * ChamberPrerequisites: chambers needed to specify this one
	 */
	@Element( editable = false, canBulkChange = false, shortSet = true, parser = ElemType.CHAMBER_PREREQUISITES)
	public final ShortSet chamberPrerequisites = new ShortOpenHashSet();
	
	
	/**
	 * ChamberMutuallyExclusive: chamber trees that are mutually exclusive and cannot be built on the same entity
	 */
	@Element(  editable = true, canBulkChange = true, shortSet = true, parser = ElemType.CHAMBER_MUTUALLY_EXCLUSIVE)
	public final ShortSet chamberMutuallyExclusive = new ShortOpenHashSet();
	
	/**
	 * ChamberChildren: what branches off this chamber
	 */
	@Element( canBulkChange = false, editable = false, shortSet = true, parser = ElemType.CHAMBER_CHILDREN)
	public final ShortSet chamberChildren = new ShortOpenHashSet();
	
	@Element( collectionElementTag = "Element", configGroupSet = true, collectionType = "String", stringSet= true, parser = ElemType.CHAMBER_CONFIG_GROUPS)
	public List<String> chamberConfigGroupsLowerCase = new ObjectArrayList<String>();
	
	@Element( states = {"0", "1"}, stateDescs = {"self", "sector"}, parser = ElemType.CHAMBER_APPLIES_TO)
	public int chamberAppliesTo = 0;
	
	@Element( editable = true, canBulkChange = true, parser = ElemType.REACTOR_HP)
	public int reactorHp;
	
	@Element( editable = true, canBulkChange = true, parser = ElemType.REACTOR_GENERAL_ICON_INDEX)
	public int reactorGeneralIconIndex;
	
	@Element( parser = ElemType.ENTERABLE)
	public boolean enterable;
	
	@Element( parser = ElemType.MASS)
	public float mass = 0.1f;
	
	@Element( parser = ElemType.VOLUME)
	public float volume = -1.0f;
	
	@Element(from = 1, to = Integer.MAX_VALUE,  parser = ElemType.HITPOINTS)
	public int maxHitPointsFull = 100;
	
	@Element( parser = ElemType.PLACABLE)
	public boolean placable = true;
	
	@Element( parser = ElemType.IN_RECIPE)
	public boolean inRecipe = shoppable;
	
	@Element( parser = ElemType.CAN_ACTIVATE)
	public boolean canActivate;
	
	@Element(states = {"1", "3", "6"},  updateTextures = true, parser = ElemType.INDIVIDUAL_SIDES)
	public int individualSides = 1;
	
	@Element( parser = ElemType.SIDE_TEXTURE_POINT_TO_ORIENTATION)
	public boolean sideTexturesPointToOrientation = false;
	
	@Element( parser = ElemType.HAS_ACTIVE_TEXTURE)
	public boolean hasActivationTexure;
	
	@Element( parser = ElemType.MAIN_COMBINATION_CONTROLLER)
	public boolean mainCombinationController;
	
	@Element( parser = ElemType.SUPPORT_COMBINATION_CONTROLLER)
	public boolean supportCombinationController;
	
	@Element( parser = ElemType.EFFECT_COMBINATION_CONTROLLER)
	public boolean effectCombinationController;
	
	@Element( parser = ElemType.PHYSICAL)
	public boolean physical = true;
	
	@Element( parser = ElemType.BLOCK_STYLE)
	public BlockStyle blockStyle = BlockStyle.NORMAL;
	
	@Element( parser = ElemType.LIGHT_SOURCE)
	public boolean lightSource;

	@Element( parser = ElemType.DOOR)
	public boolean door;
	
	@Element( parser = ElemType.SENSOR_INPUT)
	public boolean sensorInput;
	
	@Element( parser = ElemType.DEPRECATED)
	public boolean deprecated;
	
	public long dynamicPrice = -1;
	
	@Element( parser = ElemType.RESOURCE_INJECTION)
	public ResourceInjectionType resourceInjection = ResourceInjectionType.OFF;
	
	@Element(from = 0, to = 100000,  parser = ElemType.EXPLOSION_ABSOBTION)
	public float explosionAbsorbtion;
	
	@Element( vector4f = true, parser = ElemType.LIGHT_SOURCE_COLOR)
	public final Vector4f lightSourceColor = new Vector4f(1, 1, 1, 1);
	
	@Element( editable = true, canBulkChange = true, parser = ElemType.EXTENDED_TEXTURE_4x4)
	public boolean extendedTexture;
	
	@Element( editable = true, canBulkChange = true, parser = ElemType.ONLY_DRAW_IN_BUILD_MODE)
	public boolean drawOnlyInBuildMode;
	
	@Element( editable = true, canBulkChange = true, parser = ElemType.LOD_SHAPE)
	public String lodShapeString = "";
	
	@Element( states = {"0", "1", "2"}, stateDescs = {"solid block", "sprite", "invisible"}, parser = ElemType.LOD_SHAPE_FROM_FAR)
	public int lodShapeStyle = 0;

	
	@Element( editable = true, canBulkChange = true, parser = ElemType.LOW_HP_SETTING)
	public boolean lowHpSetting;
	
	@Element(from = 1, to = 127,  editable = false, parser = ElemType.OLD_HITPOINTS)
	public short oldHitpoints;
	
	
	
	
	//wilcard index assigned after initialization
	public int wildcardIndex;
	
	
	private FixedRecipe productionRecipe;
	private double maxHitpointsInverse;
	private double maxHitpointsByteToFull;
	private double maxHitpointsFullToByte;
	private boolean createdTX = false;
	private boolean signal;
	public String idName;
	private boolean specialBlock = true;
	
	private final List<FactoryResource> rawConsistence = new ObjectArrayList<FactoryResource>();
	private final List<FactoryResource> totalConsistence = new ObjectArrayList<FactoryResource>();
	private final ElementCountMap rawBlocks = new ElementCountMap();
	
	

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
	public enum ResourceInjectionType{
		OFF(0),
		ORE(1),
		FLORA(17);
		
		public final int index;
		private ResourceInjectionType(int index){
			this.index = index;
		}
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
				id == ElementKeyMap.SIGNAL_RANDOM ||
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
		return (type > 0 && ElementKeyMap.getInfo(type).activateOnPlacement()) ? (byte) 1 : (byte) 0;
	}
	public static boolean canBeControlledByAny(short toType) {
		if (!ElementKeyMap.isValidType(toType)) {
			return false;
		}
		return ElementKeyMap.getInfoFast(toType).getControlledBy().size() > 0 || 
				(toType == ElementKeyMap.SHIPYARD_CORE_POSITION) ||
				(toType == ElementKeyMap.CARGO_SPACE) ||
				(toType == ElementKeyMap.REACTOR_STABILIZER_STREAM_NODE) ||
				(ElementKeyMap.getInfoFast(toType).isLightSource()) ||
				(ElementKeyMap.getInfoFast(toType).isInventory()) ||
				(ElementKeyMap.getInfoFast(toType).isInventory()) ||
				(ElementKeyMap.getInfoFast(toType).isInventory()) ||
				(ElementKeyMap.getInfoFast(toType).isSensorInput()) ||
				(toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				(toType == ElementKeyMap.SIGNAL_AND_BLOCK_ID) ||
				(toType == ElementKeyMap.SIGNAL_OR_BLOCK_ID) ||
				(toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				(ElementKeyMap.getInfoFast(toType).isSignal()) ||
				(toType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER) ||
				(toType == ElementKeyMap.STASH_ELEMENT) ||
				(toType == ElementKeyMap.STASH_ELEMENT) ||
				(ElementKeyMap.getInfo(toType).isRailTrack()) ||
				(toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				
				(ElementKeyMap.getInfo(toType).isSignal()) ||
				(ElementKeyMap.getInfo(toType).isSignal()) ||

				(ElementKeyMap.getInfoFast(toType).isSignal()) ||
				
				(toType == ElementKeyMap.RAIL_BLOCK_DOCKER) ||
				(ElementKeyMap.getInfoFast(toType).canActivate()) ||
				(ElementKeyMap.getInfoFast(toType).isRailTrack()) ||
				isLightConnectAny(toType) ||
				(ElementKeyMap.getInfoFast(toType).isMainCombinationControllerB()) ||
				(ElementKeyMap.getInfoFast(toType).isMainCombinationControllerB())
				; 
	}

	public static boolean canBeControlled(short fromType, short toType) {
		if (!ElementKeyMap.isValidType(toType) || !ElementKeyMap.isValidType(fromType)) {
			return false;
		}
		return (fromType == ElementKeyMap.SHIPYARD_COMPUTER && toType == ElementKeyMap.SHIPYARD_CORE_POSITION) ||
				(fromType == ElementKeyMap.SHOP_BLOCK_ID && toType == ElementKeyMap.CARGO_SPACE) ||
				(fromType == ElementKeyMap.REACTOR_STABILIZER_STREAM_NODE && toType == ElementKeyMap.REACTOR_STABILIZER_STREAM_NODE) ||
				(fromType == ElementKeyMap.CORE_ID && ElementKeyMap.getInfoFast(toType).isLightSource()) ||
				(ElementKeyMap.isToStashConnectable(fromType) && ElementKeyMap.getInfoFast(toType).isInventory()) ||
				(ElementKeyMap.getInfoFast(fromType).isInventory() && ElementKeyMap.getInfoFast(toType).isInventory()) ||
				(ElementKeyMap.getInfoFast(fromType).isRailTrack() && ElementKeyMap.getInfoFast(toType).isInventory()) ||
				(fromType == ElementKeyMap.SIGNAL_SENSOR && ElementKeyMap.getInfoFast(toType).isSensorInput()) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.SIGNAL_AND_BLOCK_ID) ||
				(fromType == ElementKeyMap.STASH_ELEMENT && toType == ElementKeyMap.SIGNAL_OR_BLOCK_ID) ||
				(ElementKeyMap.getInfo(fromType).isRailRotator() && toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				(fromType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER && ElementKeyMap.getInfoFast(toType).isSignal()) ||
				(ElementKeyMap.getInfoFast(fromType).isSignal() && toType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER) ||
				(fromType == ElementKeyMap.SALVAGE_CONTROLLER_ID && toType == ElementKeyMap.STASH_ELEMENT) ||
				(ElementKeyMap.getFactorykeyset().contains(fromType) && toType == ElementKeyMap.STASH_ELEMENT) ||
				(fromType == ElementKeyMap.RAIL_RAIL_SPEED_CONTROLLER && ElementKeyMap.getInfo(toType).isRailTrack()) ||
				(fromType == ElementKeyMap.RAIL_RAIL_SPEED_CONTROLLER && toType == ElementKeyMap.ACTIVAION_BLOCK_ID) ||
				
				(fromType == ElementKeyMap.SIGNAL_TRIGGER_AREA_CONTROLLER && ElementKeyMap.getInfo(toType).isSignal()) ||
				(fromType == ElementKeyMap.ACTIVATION_GATE_CONTROLLER && ElementKeyMap.getInfo(toType).isSignal()) ||

				(ElementKeyMap.getInfoFast(fromType).isRailDockable() && ElementKeyMap.getInfoFast(toType).isSignal()) ||
				
				(ElementKeyMap.getInfoFast(fromType).isSignal() && toType == ElementKeyMap.RAIL_BLOCK_DOCKER) ||
				(ElementKeyMap.getInfoFast(fromType).isSignal() && ElementKeyMap.getInfoFast(toType).canActivate()) ||
				(ElementKeyMap.getInfoFast(fromType).isSignal() && ElementKeyMap.getInfoFast(toType).isRailTrack()) ||
				ElementKeyMap.getInfoFast(fromType).isLightConnect(toType) ||
				(ElementKeyMap.getInfoFast(fromType).isMainCombinationControllerB() && ElementKeyMap.getInfoFast(toType).isMainCombinationControllerB()) ||
				(ElementKeyMap.getInfoFast(fromType).isSupportCombinationControllerB() && ElementKeyMap.getInfoFast(toType).isMainCombinationControllerB())
				; 
	}

	private boolean isSensorInput() {
		return isInventory() || isDoor() || sensorInput;
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
//		assert(controlledType != ElementKeyMap.CARGO_SPACE || !ElementKeyMap.getInfo(controlledType).isMultiControlled());
		return
				ElementKeyMap.isValidType(controlledType) && 
				ElementKeyMap.getInfoFast(controlledType).isMultiControlled() &&
				!ElementKeyMap.getInfoFast(controlledType).isRestrictedMultiControlled();
	}

	public static boolean isMedical(short bId) {
		return bId == ElementKeyMap.MEDICAL_CABINET || bId == ElementKeyMap.MEDICAL_SUPPLIES;
	}

	public boolean isProducedIn(short factoryId) {
		return !deprecated && (
				(factoryId == ElementKeyMap.FACTORY_CAPSULE_ASSEMBLER_ID && producedInFactory == FAC_CAPSULE) ||
						(factoryId == ElementKeyMap.FACTORY_MICRO_ASSEMBLER_ID && producedInFactory == FAC_MICRO) ||
						(factoryId == ElementKeyMap.FACTORY_BASIC_ID && producedInFactory == FAC_BASIC) ||
						(factoryId == ElementKeyMap.FACTORY_STANDARD_ID && producedInFactory == FAC_STANDARD) ||
						(factoryId == ElementKeyMap.FACTORY_ADVANCED_ID && producedInFactory == FAC_ADVANCED));
	}
	public short getProducedInFactoryType(){
		switch(producedInFactory){
			case(FAC_CAPSULE): return ElementKeyMap.FACTORY_CAPSULE_ASSEMBLER_ID;
			case(FAC_MICRO): return ElementKeyMap.FACTORY_MICRO_ASSEMBLER_ID;
			case(FAC_BASIC): return ElementKeyMap.FACTORY_BASIC_ID;
			case(FAC_STANDARD): return ElementKeyMap.FACTORY_STANDARD_ID;
			case(FAC_ADVANCED): return ElementKeyMap.FACTORY_ADVANCED_ID;
		
			default: return 0;
		}
	}

	
	
	public long calculateDynamicPrice() {
		
		return dynamicPrice;
	}

	public boolean isCapsule() {
		return blockResourceType == RT_CAPSULE;
	}

	public boolean isOre() {
		return blockResourceType == RT_ORE;
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
	 * @return the blockStyle
	 */
	public BlockStyle getBlockStyle() {
		return blockStyle;
	}

	/**
	 * @param blockStyle the blockStyle to set
	 * @throws ParseException 
	 */
	public void setBlockStyle(int blockStyle) throws ElementParserException {
		this.blockStyle = BlockStyle.getById(blockStyle);
//		solidBlockStyle = blockStyle > 0 && blockStyle != 3 && blockStyle != 6;
//		blendedBlockStyle = blockStyle == 3 ;
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
	public int getMaxHitPointsFull() {
		return maxHitPointsFull;
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
	public String getNameUntranslated() {
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
		return shoppable 
				;
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


	public int getDefaultOrientation() {
		
//		if (getId() == ElementKeyMap.CARGO_SPACE) {
			return 0;
//		}
//		if (getId() == ElementKeyMap.TRANSPORTER_MODULE) {
//			return org.schema.game.common.data.element.Element.TOP;
//		}
//		if (getBlockStyle() == BlockStyle.SPRITE || getIndividualSides() == 3) {
//			return org.schema.game.common.data.element.Element.TOP;
//		}
//		
//		if (getBlockStyle() == BlockStyle.NORMAL24) {
//			return 14; //top front
//		}
//		return getId() == ElementKeyMap.GRAVITY_ID ? org.schema.game.common.data.element.Element.BOTTOM : org.schema.game.common.data.element.Element.FRONT;
	}




	public boolean isBlendBlockStyle() {
		return getBlockStyle().blendedBlockStyle || (hasLod() && lodShapeStyle == 1);
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

	public static boolean isLightConnectAny(short to) {
		if (ElementKeyMap.isValidType(to)) {
			ElementInformation info = ElementKeyMap.getInfo(to);
			if (info.isLightSource()) {
				return true;
			}
		}
		return false;
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
		return !isSignal() && (ElementKeyMap.getFactorykeyset().contains(id) || isLightSource() || isDoor() || 
				getId() == ElementKeyMap.STASH_ELEMENT || getId() == ElementKeyMap.PICKUP_AREA);
	}

	public String[] parseDescription() {
		String d = getDescription();

		d = String.format("Block-Armor: ") + "<?>" + " \n\n" + d;
		d = String.format("Block-HP: ") + getMaxHitPointsFull() + " \n" + d;
		String ahp = String.format("Armor-HP  for Structure: ");
		d = String.format("System-HP for Structure: ") + structureHP + " \n" + d;
		d = String.format("Mass: ") + getMass() + " \n" + d;

		String[] split = d.split("\\n");
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].replace("$ACTIVATE", "Activate Key");

			if (split[i].contains("$RESOURCES")) {
				split[i] = split[i].replace("$RESOURCES", getFactoryResourceString(this));
			}
			if (split[i].contains("$ARMOUR")) {
				split[i] = split[i].replace("$ARMOUR", ""/*String.valueOf(getArmor())*/);
			}
			if (split[i].contains("$HP")) {
				split[i] = split[i].replace("$HP", String.valueOf(getMaxHitPointsFull()));
			}

			if (split[i].contains("$EFFECT")) {
				split[i] = split[i].replace("$EFFECT", "NO_EFFECT(not parsed)");
			}
			if (split[i].contains("$MAINCOMBI")) {

				split[i] = split[i].replace("$MAINCOMBI",
						String.format("This controller can be connected to other weapons\nand systems (cannon, beam, damage pulse, missile)\nto customize your weapon.\n\nTo link your Controller to its Modules, press %s on the Controller, then %s on the individual modules, \nor alternatively Shift + $CONNECT_MODULE to mass select grouped modules.\n\nAfterwards you can link it to another weapon by connecting the weapon controller you want to upgrade\nwith the weapon controller you just made.\nYou can do this manually with $SELECT_MODULE and $CONNECT_MODULE or in the Weapons Menu.\n\nNote that for the full effect, \nyou need to connect 1:1 in size.\n\n", 
								"Select Button", 
								"Connect Button")
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
	public List<FactoryResource> getConsistence() {
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
	public double getMaxHitpointsFullInverse() {
		return maxHitpointsInverse;
	}
	public double getMaxHitpointsByteToFull() {
		return maxHitpointsByteToFull;
	}
	public double getMaxHitpointsFullToByte() {
		return maxHitpointsFullToByte;
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
						isInventory() ||
						isRailTrack() ||
						isSensorInput() ||
						getId() == ElementKeyMap.TEXT_BOX;
	}
	public boolean isRestrictedMultiControlled() {
		return getId() == ElementKeyMap.CARGO_SPACE;
	}
	/**
	 * 
	 * @return all types that can't have more than one connection to this
	 *	e.g. a cago block should only ever be assigned to one inventory
	 */
	public ShortArrayList getRestrictedMultiControlled() {
		if(getId() == ElementKeyMap.CARGO_SPACE){
			return ElementKeyMap.inventoryTypes;
		}else{
			assert(false):this;
			return null;
		}
	}

	public String createWikiStub() {
		StringBuffer s = new StringBuffer();

		s.append("{{infobox block" + "\n");
		s.append("  |type=" + getName() + "\n");
		s.append("	|hp=" + getMaxHitPointsFull() + "\n");
		s.append("	|armor=" + "-" + "\n");
		s.append("	|light=" + (isLightSource() ? "yes" : "no") + "\n");
		if (isLightSource()) {
			s.append("	|lightColor=" + getLightSourceColor() + "\n");
		}
		s.append("	|dv=" + getId() + "\n");
		s.append("}}" + "\n\n");

//		s.append("==Description=="+"\n");
//		s.append(getFilledDescription()+"\n");
//		s.append("-----\n");

		return s.toString();
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
		return blockStyle.cube;
	}

	public boolean isRailTrack() {
		return blockStyle == BlockStyle.NORMAL24 && !hasLod() && 
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
				&& id != ElementKeyMap.LOGIC_REMOTE_INNER && id != ElementKeyMap.POWER_BATTERY;
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
				getFactory() != null;
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


	private void recalcRawConsistenceRec(FactoryResource cs, int count) {
		ElementInformation info = ElementKeyMap.getInfoFast(cs.type);	
		if(info.getConsistence().isEmpty()){
			rawConsistence.add(cs);
			rawBlocks.inc(cs.type, count);
			//System.out.println("RAW CONSISTENCE: Base " + ElementKeyMap.getInfo(cs.type) + " " + count);
		}else{
			for(FactoryResource c : info.consistence){
				recalcRawConsistenceRec(c, c.count * count);
			}
		}
	}
	private void recalcTotalConsistenceRec(FactoryResource cs) {
		ElementInformation info = ElementKeyMap.getInfoFast(cs.type);
		getTotalConsistence().add(cs);
		for(FactoryResource c : info.consistence){
			recalcTotalConsistenceRec(c);
		}
		
	}
	public void recalcTotalConsistence() {
		rawBlocks.checkArraySize();
		rawConsistence.clear();
		getTotalConsistence().clear();
		//System.out.println("RAW CONSISTENCE: " + this.name);
		//Take source block for crafting consistence, probably temporary as consistence itself should already account for this
		List<FactoryResource> sourceConsistence = consistence;
		if(getSourceReference() != 0 && ElementKeyMap.isValidType(getSourceReference())){
			sourceConsistence = ElementKeyMap.getInfo(getSourceReference()).getConsistence();
		}
		
		for(FactoryResource c : sourceConsistence){
			recalcRawConsistenceRec(c, c.count);
		}
		//System.out.println("RAW CONSISTENCE: ----------");
		for(FactoryResource c : sourceConsistence){
			recalcTotalConsistenceRec(c);
		}
	}

	public ElementCountMap getRawBlocks() {
		return rawBlocks;
	}

	public List<FactoryResource> getRawConsistence() {
		return rawConsistence;
	}

	public List<FactoryResource> getTotalConsistence() {
		return totalConsistence;
	}

	public boolean isExtendedTexture() {
		return extendedTexture;
	}

	public boolean isReactorChamberAny() {
		return isReactorChamberGeneral() || isReactorChamberSpecific();
	}
	public boolean isReactorChamberGeneral() {
		return chamberGeneral;
	}
	public boolean isReactorChamberSpecific() {
		return chamberRoot != 0;
	}
	public short getComputer(){
		return (short) computerType;
	}
	public boolean needsComputer() {
		return ElementKeyMap.isValidType(computerType);
	}
	public ShortSet getChamberChildrenOnLevel(ShortSet out) {
		out.addAll(chamberChildren);
		if(chamberParent != 0){
			ElementInformation info = ElementKeyMap.getInfo(chamberParent);
			if(info.chamberUpgradesTo == id){
				info.getChamberChildrenOnLevel(out);
				out.remove((short)info.chamberUpgradesTo);
			}
		}
		return out;
	}
	public short getChamberUpgradedRoot() {
		if(chamberParent != 0){
			ElementInformation parent = ElementKeyMap.getInfo(chamberParent);
			if(parent.chamberUpgradesTo == id){
				return parent.getChamberUpgradedRoot();
			}else{
				return id;
			}
		}else{
			return id;
		}
	}
	public boolean isChamberChildrenUpgradableContains(short type) {
		if(chamberChildren.contains(type)){
			return true;
		}
		if(chamberParent != 0){
			ElementInformation parent = ElementKeyMap.getInfo(chamberParent);
			if(parent.chamberUpgradesTo == id){
				if(parent.isChamberChildrenUpgradableContains(type)){
					return true;
				}
			}
		}
		return false;
	}
	public boolean isChamberUpgraded() {
		return ElementKeyMap.isValidType(chamberParent) && ElementKeyMap.getInfo(chamberParent).chamberUpgradesTo == id;
	}
	public void sanatizeReactorValues() {
		if(chamberParent != 0 && (!ElementKeyMap.isValidType(chamberParent)|| !ElementKeyMap.isChamber((short)chamberParent))){
			System.err.println("SANATIZED REACTOR chamberParent "+this.getName()+" -> "+ElementKeyMap.toString(chamberParent));
			chamberParent = 0;
		}
		if(chamberRoot != 0 && (!ElementKeyMap.isValidType(chamberRoot)|| !ElementKeyMap.isChamber((short)chamberRoot))){
			System.err.println("SANATIZED REACTOR chamberRoot "+this.getName()+" -> "+ElementKeyMap.toString(chamberRoot));
			chamberRoot = 0;
		}
		if(chamberUpgradesTo != 0 && (!ElementKeyMap.isValidType(chamberUpgradesTo) || !ElementKeyMap.isChamber((short)chamberUpgradesTo))){
			System.err.println("SANATIZED REACTOR chamberUpgradesTo "+this.getName()+" -> "+ElementKeyMap.toString(chamberUpgradesTo));
			chamberUpgradesTo = 0;
		}
		
		ShortIterator iterator = chamberPrerequisites.iterator();
		while(iterator.hasNext()){
			short s = iterator.nextShort();
			if(s != 0 && (!ElementKeyMap.isValidType(s)|| !ElementKeyMap.isChamber(s))){
				System.err.println("SANATIZED REACTOR chamberPrereq "+this.getName()+" -> "+ElementKeyMap.toString(s));
				iterator.remove();
			}
		}
		iterator = chamberChildren.iterator();
		while(iterator.hasNext()){
			short s = iterator.nextShort();
			if(s != 0 && (!ElementKeyMap.isValidType(s)|| !ElementKeyMap.isChamber(s))){
				System.err.println("SANATIZED REACTOR chamberChildren "+this.getName()+" -> "+ElementKeyMap.toString(s));
				iterator.remove();
			}
		}
		if(isReactorChamberSpecific() || getId() == ElementKeyMap.REACTOR_MAIN || getId() == ElementKeyMap.REACTOR_CONDUIT || getId() == ElementKeyMap.REACTOR_STABILIZER){
			if(this.reactorHp == 0){
				this.reactorHp = 10;
			}
		}else{
			this.reactorHp = 0;
		}
	}
//	public String getChamberEffectInfo(ConfigPool pool) {
//		if(chamberConfigGroupsLowerCase.isEmpty()){
//			return String.format("No Effect");
//		}else{
//			StringBuffer sb = new StringBuffer();
//			for(String s : chamberConfigGroupsLowerCase){
//				ConfigGroup configGroup = pool.poolMapLowerCase.get(s);
//				if(configGroup != null){
//					sb.append(configGroup.getEffectDescription());
//				}
//			}
//			return sb.toString().trim();
//		}
//	}
	private float getChamberCapacityBranchRec(float rec) {
		if(ElementKeyMap.isValidType(chamberParent)){
			ElementInformation m = ElementKeyMap.getInfoFast(chamberParent);
			return m.getChamberCapacityBranchRec(rec + m.chamberCapacity);
		}
		return rec;
	}
	public float getChamberCapacityBranch() {
		return getChamberCapacityBranchRec(chamberCapacity);
	}
	public int getSourceReference() {
		if(chamberRoot != 0){
			return chamberRoot;
		}
		return sourceReference;
	}
	public void setSourceReference(int sourceReference) {
		this.sourceReference = sourceReference;
	}
	public float getChamberCapacityWithUpgrades() {
		float chamUp = this.chamberCapacity;
		if(isChamberUpgraded()){
			if(ElementKeyMap.isValidType(chamberParent)){
				chamUp += ElementKeyMap.getInfo(chamberParent).getChamberCapacityWithUpgrades();
			}
		}
		return chamUp;
	}

//	public boolean isChamberPermitted(EntityType t){
//		if(chamberPermission == CHAMBER_PERMISSION_ANY){
//			return true;
//		}
//		switch(t){
//		case PLANET_CORE:
//		case PLANET_ICO:
//		case PLANET_SEGMENT:
//			return (chamberPermission & CHAMBER_PERMISSION_PLANET) == CHAMBER_PERMISSION_PLANET; 
//		case SHIP:
//			return (chamberPermission & CHAMBER_PERMISSION_SHIP) == CHAMBER_PERMISSION_SHIP;
//		case SHOP:
//		case SPACE_STATION:
//			return (chamberPermission & CHAMBER_PERMISSION_STATION) == CHAMBER_PERMISSION_STATION;
//		default:
//			return false;
//		
//		}
//	}
	public String getDescriptionIncludingChamberUpgraded() {
		if(isChamberUpgraded()){
			return ElementKeyMap.getInfo(chamberParent).getDescription();
		}
		return getDescription();
	}
	public boolean isThisOrParentChamberMutuallyExclusive(short type) {
		if(chamberMutuallyExclusive.contains(type)){
			return true;
		}
		if(ElementKeyMap.isValidType(chamberParent)){
			return ElementKeyMap.getInfoFast(chamberParent).isThisOrParentChamberMutuallyExclusive(type);
		}
		return false;
	}
	public boolean isArmor() {
		return armorValue > 0;
	}
	private static final float AB = 1f / 127f;
	public float getMaxHitPointsOneDivByByte() {
		return AB;
	}
	public byte getMaxHitPointsByte() {
		return 127;
	}
	/**
	 * @param maxHitPoints the maxHitPoints to set
	 */
	public void setMaxHitPointsE(int maxHitPoints) {
		assert(maxHitPoints > 0);
		this.maxHitPointsFull = maxHitPoints;
		this.maxHitpointsInverse = (1d / (double)maxHitPoints);
		this.maxHitpointsFullToByte = 127d / (double)maxHitPoints ;
		this.maxHitpointsByteToFull = (double)maxHitPoints / 127d;
	}
	public short convertToByteHp(int hpFull) {
//		System.err.println("CONVERT: "+hpFull+"; "+maxHitpointsFullToByte+"; "+getMaxHitPointsFull());
		return (short)Math.max(0, Math.min(ElementKeyMap.MAX_HITPOINTS, Math.round(((double)hpFull * maxHitpointsFullToByte)))) ; //hp * (127 / maxHitPoints)
	}
	public int convertToFullHp(short hpByte) {
		return (int) (hpByte * maxHitpointsByteToFull); //hp * (maxHitPoints / 127)
	}
	public void setHpOldByte(short oldHitpoints) {
		this.oldHitpoints = oldHitpoints;
	}
	public short getHpOldByte() {
		return oldHitpoints;
	}
	public float getArmorValue() {
		return armorValue;
	}
	public void setArmorValue(float armorValue) {
		this.armorValue = armorValue;
	}
	public boolean isMineAddOn() {
		return id == ElementKeyMap.MINE_MOD_FRIENDS||
				id == ElementKeyMap.MINE_MOD_PERSONAL||
				id == ElementKeyMap.MINE_MOD_STEALTH||
				id == ElementKeyMap.MINE_MOD_STRENGTH;
	}
	public boolean isMineType() {
		return id == ElementKeyMap.MINE_TYPE_CANNON ||
				id == ElementKeyMap.MINE_TYPE_MISSILE||
				id == ElementKeyMap.MINE_TYPE_PROXIMITY;
	}
	


}
