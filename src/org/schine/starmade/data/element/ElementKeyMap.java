package org.schine.starmade.data.element;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.schine.starmade.data.element.exception.CannotAppendXMLException;
import org.schine.starmade.data.element.exception.ElementParserException;
import org.schine.starmade.data.element.exception.ParseException;
import org.schine.starmade.data.element.factory.FixedRecipe;
import org.schine.starmade.data.element.factory.FixedRecipes;
import org.schine.starmade.mediawiki.MediawikiExport;
import org.schine.starmade.parser.ElementParser;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * has to maps for the keys to the elements
 * we can afford that, because there only is a limited number of element types
 *
 * @author Schema
 */
public class ElementKeyMap {
	public final static Short2ObjectOpenHashMap<String> nameTranslations = new Short2ObjectOpenHashMap<String>();
	public final static Short2ObjectOpenHashMap<String> descriptionTranslations = new Short2ObjectOpenHashMap<String>();
	
	public static final short WEAPON_CONTROLLER_ID = 6;
	public static final short WEAPON_ID = 16;
	public static final short CORE_ID = 1;
	public static final short DEATHSTAR_CORE_ID = 65;
	public static final short HULL_ID = 5;
	public static final short GLASS_ID = 63;
	public static final short THRUSTER_ID = 8;
	public static final short TURRET_DOCK_ID = 7;
	public static final short TURRET_DOCK_ENHANCE_ID = 88;
	public static final short POWER_ID = 2;
	public static final short POWER_CAP_ID = 331;
	public static final short SHIELD_CAP_ID = 3;
	public static final short SHIELD_REGEN_ID = 478;
	public static final short EXPLOSIVE_ID = 14;
	public static final short RADAR_JAMMING_ID = 15;
	public static final short CLOAKING_ID = 22;
	public static final short SALVAGE_ID = 24;
	public static final short MISSILE_DUMB_CONTROLLER_ID = 38;
	public static final short MISSILE_DUMB_ID = 32;
	public static final short SHIELD_DRAIN_CONTROLLER_ID = 46;
	public static final short SHIELD_DRAIN_MODULE_ID = 40;
	public static final short SHIELD_SUPPLY_CONTROLLER_ID = 54;
	public static final short SHIELD_SUPPLY_MODULE_ID = 48;
	public static final short SALVAGE_CONTROLLER_ID = 4;
	public static final short GRAVITY_ID = 56;
	public static final short REPAIR_ID = 30;
	public static final short REPAIR_CONTROLLER_ID = 39;
	public static final short COCKPIT_ID = 47;
	public static final short LIGHT_ID = 55;
	public static final short LIGHT_BEACON_ID = 62;
	public static final short TERRAIN_ICE_ID = 64;
	public static final short HULL_COLOR_PURPLE_ID = 69;
	public static final short HULL_COLOR_BROWN_ID = 70;
	public static final short HULL_COLOR_BLACK_ID = 75;
	public static final short HULL_COLOR_RED_ID = 76;
	public static final short HULL_COLOR_BLUE_ID = 77;
	public static final short HULL_COLOR_GREEN_ID = 78;
	public static final short HULL_COLOR_YELLOW_ID = 79;
	public static final short HULL_COLOR_WHITE_ID = 81;
	public static final short LANDING_ELEMENT = 112;
	public static final short LIFT_ELEMENT = 113;
	public static final short RECYCLER_ELEMENT = 114;
	public static final short STASH_ELEMENT = 120;
	public static final short AI_ELEMENT = 121;
	public static final short DOOR_ELEMENT = 122;
	public static final short BUILD_BLOCK_ID = 123;
	public static final short TERRAIN_LAVA_ID = 80;
	public static final short TERRAIN_GOLD_ID = 128 + 0;
	public static final short TERRAIN_IRIDIUM_ID = 128 + 1;
	public static final short TERRAIN_MERCURY_ID = 128 + 2;
	public static final short TERRAIN_PALLADIUM_ID = 128 + 3;
	public static final short TERRAIN_PLATINUM_ID = 128 + 4;
	public static final short TERRAIN_LITHIUM_ID = 128 + 5;
	public static final short TERRAIN_MAGNESIUM_ID = 128 + 6;
	public static final short TERRAIN_TITANIUM_ID = 128 + 7;
	public static final short TERRAIN_URANIUM_ID = 128 + 8;
	public static final short TERRAIN_POLONIUM_ID = 128 + 9;
	public static final short TERRAIN_EXTRANIUM_ID = 72;
	public static final short TERRAIN_INSANIUNM_ID = 210;
	public static final short TERRAIN_METATE_ID = 209;
	public static final short TERRAIN_NEGAGATE_ID = 208;
	public static final short TERRAIN_QUANTACIDE_ID = 207;
	public static final short TERRAIN_NEGACIDE_ID = 206;
	public static final short TERRAIN_MARS_TOP = 128 + 10;
	public static final short TERRAIN_MARS_DIRT = 128 + 12;
	public static final short TERRAIN_ROCK_NORMAL = 73;
	public static final short TERRAIN_ROCK_MARS = 139;
	public static final short TERRAIN_ROCK_BLUE = 143;
	public static final short TERRAIN_ROCK_ORANGE = 151;
	public static final short TERRAIN_ROCK_YELLOW = 155;
	public static final short TERRAIN_ROCK_WHITE = 159;
	public static final short TERRAIN_ROCK_PURPLE = 163;
	public static final short TERRAIN_ROCK_RED = 171;
	public static final short TERRAIN_ROCK_GREEN = 179;
	public static final short TERRAIN_ROCK_BLACK = 203;
	public static final short TERRAIN_SAND_ID = 74;
	public static final short TERRAIN_EARTH_TOP_DIRT = 82;
	public static final short TERRAIN_EARTH_TOP_ROCK = 83;
	public static final short TERRAIN_TREE_TRUNK_ID = 84;
	public static final short TERRAIN_TREE_LEAF_ID = 85;

	public static final short TERRAIN_WATER = 86;
	public static final short TERRAIN_DIRT_ID = 87;
	public static final short TERRAIN_VINES_ID = TERRAIN_TREE_LEAF_ID;
	public static final short TERRAIN_CACTUS_ID = 89;
	public static final short TERRAIN_PURPLE_ALIEN_TOP = 90;
	public static final short TERRAIN_PURPLE_ALIEN_ROCK = 91;
	public static final short TERRAIN_PURPLE_ALIEN_VINE = 92;
	public static final short WATER = 86;
	public static final short PLAYER_SPAWN_MODULE = 94;
	public static final short LIGHT_BULB_YELLOW = 340;
	//earth like sprite stuff
	public static final short TERRAIN_GRASS_SPRITE = 93;
	public static final short TERRAIN_GRASSFLOWERS_SPRITE = 98;
	public static final short TERRAIN_TALLGRASSFLOWERS_SPRITE = 102;
	public static final short TERRAIN_TALLFLOWERS_SPRITE = 106;
	//desert sprite stuff
	public static final short TERRAIN_BROWNWEED_SPRITE = 95;
	public static final short TERRAIN_MINICACTUS_SPRITE = 103;
	public static final short TERRAIN_LONGWEED_SPRITE = 99;
	public static final short TERRAIN_ROCK_SPRITE = 107;
	//mars sprite stuff
	public static final short TERRAIN_MARSTENTACLES_SPRITE = 96;
	public static final short TERRAIN_REDSHROOM_SPRITE = 104;
	public static final short TERRAIN_TALLSHROOM_SPRITE = 100;
	public static final short TERRAIN_ALIENFLOWERS_SPRITE = 108;
	//columny sprite stuff
	public static final short TERRAIN_ALIENVINE_SPRITE = 97;
	public static final short TERRAIN_PURSPIRE_SPRITE = 101;
	public static final short TERRAIN_PURPTACLES_SPRITE = 105;
	public static final short TERRAIN_YHOLE_SPRITE = 109;
	//FACTORY
	public static final short FACTORY_BASIC_ID = 211;
	public static final short FACTORY_STANDARD_ID = 217;
	public static final short FACTORY_ADVANCED_ID = 259;
	public static final short FACTORY_INPUT_ENH_ID = 212;
	public static final short FACTORY_CAPSULE_ASSEMBLER_ID = 213;
	public static final short FACTORY_ENH_UNUSED_ID = 214;
	public static final short FACTORY_MICRO_ASSEMBLER_ID = 215;
	public static final short FACTORY_POWER_COIL_ENH_ID = 216;
	public static final short FACTORY_POWER_BLOCK_ENH_ID = 218;
	public static final short TERRAIN_ICEPLANET_SURFACE = 274;
	public static final short TERRAIN_ICEPLANET_ROCK = 275;
	public static final short TERRAIN_ICEPLANET_WOOD = 276;
	public static final short TERRAIN_ICEPLANET_LEAVES = 277;
	public static final short TERRAIN_ICEPLANET_SPIKE_SPRITE = 278;
	public static final short TERRAIN_ICEPLANET_ICECRAG_SPRITE = 279;
	public static final short TERRAIN_ICEPLANET_ICECORAL_SPRITE = 280;
	
	public static final short TERRAIN_ICEPLANET_ICEGRASS_SPRITE = 281;
	
	public static final short LIGHT_RED = 282;
	public static final short LIGHT_BLUE = 283;
	public static final short LIGHT_GREEN = 284;
	public static final short LIGHT_YELLOW = 285;
	public static final short TERRAIN_ICEPLANET_CRYSTAL = 286;
	public static final short TERRAIN_REDWOOD = 287;
	public static final short TERRAIN_REDWOOD_LEAVES = 288;
	public static final short FIXED_DOCK_ID = 289;
	public static final short FIXED_DOCK_ID_ENHANCER = 290;
	public static final short FACTION_BLOCK = 291;
	public static final short FACTION_HUB_BLOCK = 292;
	public static final short DECORATIVE_PANEL_1 = 336;
	public static final short DECORATIVE_PANEL_2 = 337;
	public static final short DECORATIVE_PANEL_3 = 338;
	public static final short DECORATIVE_PANEL_4 = 339;
	//FACTORY manufracture
	public static final short POWER_CELL = 219;
	public static final short POWER_COIL = 220;
	public static final short POWER_DRAIN_BEAM_COMPUTER = 332;
	public static final short POWER_DRAIN_BEAM_MODULE = 333;
	public static final short POWER_SUPPLY_BEAM_COMPUTER = 334;
	public static final short POWER_SUPPLY_BEAM_MODULE = 335;
	public static final short PUSH_PULSE_CONTROLLER_ID = 344;
	public static final short PUSH_PULSE_ID = 345;
	public static final short FACTION_PUBLIC_EXCEPTION_ID = 346;
	public static final short FACTION_FACTION_EXCEPTION_ID = 936;
	public static final short SHOP_BLOCK_ID = 347;
	public static final short ACTIVAION_BLOCK_ID = 405;
	public static final short SIGNAL_DELAY_NON_REPEATING_ID = 406;
	public static final short SIGNAL_DELAY_BLOCK_ID = 407;
	public static final short SIGNAL_AND_BLOCK_ID = 408;
	public static final short SIGNAL_OR_BLOCK_ID = 409;
	public static final short SIGNAL_NOT_BLOCK_ID = 410;
	public static final short SIGNAL_TRIGGER_AREA = 411;
	public static final short SIGNAL_TRIGGER_STEPON = 412;
	public static final short SIGNAL_TRIGGER_AREA_CONTROLLER = 413;
	public static final short DAMAGE_BEAM_COMPUTER = 414;
	public static final short DAMAGE_BEAM_MODULE = 415;
	public static final short DAMAGE_PULSE_COMPUTER = 416;
	public static final short DAMAGE_PULSE_MODULE = 417;
	public static final short EFFECT_PIERCING_COMPUTER = 418;
	public static final short EFFECT_PIERCING_MODULE = 419;
	public static final short EFFECT_EXPLOSIVE_COMPUTER = 420;
	public static final short EFFECT_EXPLOSIVE_MODULE = 421;
	public static final short EFFECT_PUNCHTHROUGH_COMPUTER = 422;
	public static final short EFFECT_PUNCHTHROUGH_MODULE = 423;
	public static final short EFFECT_EMP_COMPUTER = 424;
	public static final short EFFECT_EMP_MODULE = 425;
	public static final short EFFECT_STOP_COMPUTER = 460;
	public static final short EFFECT_STOP_MODULE = 461;
	public static final short EFFECT_PUSH_COMPUTER = 462;
	public static final short EFFECT_PUSH_MODULE = 463;
	public static final short EFFECT_PULL_COMPUTER = 464;
	public static final short EFFECT_PULL_MODULE = 465;
	public static final short EFFECT_ION_COMPUTER = 466;
	public static final short EFFECT_ION_MODULE = 467;
	public static final short EFFECT_OVERDRIVE_COMPUTER = 476;
	public static final short EFFECT_OVERDRIVE_MODULE = 477;
	public static final short TEXT_BOX = 479;
	public static final short WARP_GATE_CONTROLLER = 542;
	public static final short WARP_GATE_MODULE = 543;
	public static final short JUMP_DRIVE_CONTROLLER = 544;
	public static final short JUMP_DRIVE_MODULE = 545;
	public static final short MEDICAL_SUPPLIES = 445;
	public static final short MEDICAL_CABINET = 446;
	public static final short SCRAP_ALLOYS = 546;
	public static final short SCRAP_COMPOSITE = 547;
	public static final short SCANNER_COMPUTER = 654;
	public static final short SCANNER_MODULE = 655;
	public static final short METAL_MESH = 440;
	public static final short CRYSTAL_CRIRCUITS = 220;
	public static final short LOGIC_BUTTON = 666;
	public static final short LOGIC_FLIP_FLOP = 667;
	public static final short LOGIC_WIRELESS = 668;
	public static final short SHIPYARD_COMPUTER = 677;
	public static final short SHIPYARD_MODULE = 678;
	public static final short SHIPYARD_CORE_POSITION = 679;

	
	
	public static final short JUMP_INHIBITOR_COMPUTER = 681;
	public static final short JUMP_PROHIBITER_MODULE = 682;
	public static final int Hattel = 1;
	public static final int Sintyr = 2;
	public static final int Mattise = 3;
	public static final int Rammet = 4;
	public static final int Varat = 5;

	//ORIENTATION MAPPING
	public static final int Bastyn = 6;
	public static final int Parsen = 7;
	public static final int Nocx = 8;
	public static final int Threns = 9;
	public static final int Jisper = 10;
	public static final int Zercaner = 11;
	public static final int Sertise = 12;
	public static final int Hital = 13;
	public static final int Fertikeen = 14;
	public static final int Parstun = 15;
	//16 doesnt have one, as it has orientation 0
	public static final int Nacht = 17;
	public static final short RESS_CRYS_HATTEL = 480;
	public static final short RESS_CRYS_SINTYR = 481;
	public static final short RESS_CRYS_MATTISE = 482;
	public static final short RESS_CRYS_RAMMET = 483;
	public static final short RESS_CRYS_VARAT = 484;
	public static final short RESS_CRYS_BASTYN = 485;
	public static final short RESS_CRYS_PARSEN = 486;
	public static final short RESS_CRYS_NOCX = 487;
	public static final short RESS_ORE_THRENS = 488;
	public static final short RESS_ORE_JISPER = 489;
	public static final short RESS_ORE_ZERCANER = 490;
	public static final short RESS_ORE_SERTISE = 491;
	public static final short RESS_ORE_HITAL = 492;
	public static final short RESS_ORE_FERTIKEEN = 493;
	public static final short RESS_ORE_PARSTUN = 494;
	public static final short RESS_ORE_NACHT = 495;
	public static final short RAIL_BLOCK_BASIC = 662;
	public static final short RAIL_BLOCK_DOCKER = 663;
	public static final short RAIL_BLOCK_CW = 664;
	public static final short RAIL_BLOCK_CCW = 669;
	public static final short RAIL_BLOCK_TURRET_Y_AXIS = 665;
	public static final short RAIL_RAIL_SPEED_CONTROLLER = 672;
	public static final short RAIL_MASS_ENHANCER = 671;
	public static final short LOGIC_REMOTE_INNER = 670;
	
	public static final short RACE_GATE_CONTROLLER = 683;
	public static final short RACE_GATE_MODULE = 684;
	public static final short ACTIVATION_GATE_CONTROLLER = 685;
	public static final short ACTIVATION_GATE_MODULE = 686;
	public static final short TRANSPORTER_CONTROLLER = 687;
	public static final short TRANSPORTER_MODULE = 688;

	public static final short CARGO_SPACE = 689;
	
	
	public static final short PICKUP_AREA=937;
	public static final short PICKUP_RAIL=938;
	public static final short EXIT_SHOOT_RAIL=939;
	
	public static final short[] orientationToResIDMapping = new short[32];
	public static final byte[] resIDToOrientationMapping = new byte[2048];
	/**
	 * corresponding texture position (starting with 1) from block orientation
	 */
	public static final int[] orientationToResOverlayMapping = new int[32];
	public static final ShortOpenHashSet keySet = new ShortOpenHashSet(256);
	public static final ShortArrayList doorTypes = new ShortArrayList();
	public static final ShortArrayList inventoryTypes = new ShortArrayList();
	private static final Map<Short, ElementInformation> informationKeyMap = new HashMap<Short, ElementInformation>();
	private static final ShortOpenHashSet factoryKeySet = new ShortOpenHashSet(256);
	private static final ShortOpenHashSet leveldKeySet = new ShortOpenHashSet(256);
	private static final Short2ObjectOpenHashMap<ElementInformation> projected = new Short2ObjectOpenHashMap<ElementInformation>();
	public static int highestType = 0;
	public static ElementInformation[] infoArray;
	public static boolean[] factoryInfoArray;
	public static boolean[] validArray;
	public static boolean[] lodShapeArray;
	public static short[] signalArray;
	public static boolean initialized;
	public static Properties properties;
	public static FixedRecipes fixedRecipes;
	public static FixedRecipe capsuleRecipe;
	public static FixedRecipe microAssemblerRecipe;
	public static FixedRecipe macroBlockRecipe;
	public static ObjectArrayList<ElementInformation> sortedByName;
	public static FixedRecipe personalCapsuleRecipe;
	private static short[] keyArray;
	private static ElementCategory categoryHirarchy;
	public static String propertiesPath;
	
	
	public static final short[] HULL_HELPER = new short[]{
		 HULL_COLOR_PURPLE_ID,
		 HULL_COLOR_BROWN_ID,
		 HULL_COLOR_BLACK_ID,
		 HULL_COLOR_RED_ID,
		 HULL_COLOR_BLUE_ID,
		 HULL_COLOR_GREEN_ID,
		 HULL_COLOR_YELLOW_ID,
		 HULL_COLOR_WHITE_ID
	};
	

	static {
		orientationToResIDMapping[Hattel] = RESS_CRYS_HATTEL;
		orientationToResIDMapping[Sintyr] = RESS_CRYS_SINTYR;
		orientationToResIDMapping[Mattise] = RESS_CRYS_MATTISE;
		orientationToResIDMapping[Rammet] = RESS_CRYS_RAMMET;
		orientationToResIDMapping[Varat] = RESS_CRYS_VARAT;
		orientationToResIDMapping[Bastyn] = RESS_CRYS_BASTYN;
		orientationToResIDMapping[Parsen] = RESS_CRYS_PARSEN;
		orientationToResIDMapping[Nocx] = RESS_CRYS_NOCX;

		orientationToResIDMapping[Threns] = RESS_ORE_THRENS;
		orientationToResIDMapping[Jisper] = RESS_ORE_JISPER;
		orientationToResIDMapping[Zercaner] = RESS_ORE_ZERCANER;
		orientationToResIDMapping[Sertise] = RESS_ORE_SERTISE;
		orientationToResIDMapping[Hital] = RESS_ORE_HITAL;
		orientationToResIDMapping[Fertikeen] = RESS_ORE_FERTIKEEN;
		orientationToResIDMapping[Parstun] = RESS_ORE_PARSTUN;
		orientationToResIDMapping[Nacht] = RESS_ORE_NACHT;
	}

	static {
		resIDToOrientationMapping[RESS_CRYS_HATTEL] = Hattel;
		resIDToOrientationMapping[RESS_CRYS_SINTYR] = Sintyr;
		resIDToOrientationMapping[RESS_CRYS_MATTISE] = Mattise;
		resIDToOrientationMapping[RESS_CRYS_RAMMET] = Rammet;
		resIDToOrientationMapping[RESS_CRYS_VARAT] = Varat;
		resIDToOrientationMapping[RESS_CRYS_BASTYN] = Bastyn;
		resIDToOrientationMapping[RESS_CRYS_PARSEN] = Parsen;
		resIDToOrientationMapping[RESS_CRYS_NOCX] = Nocx;

		resIDToOrientationMapping[RESS_ORE_THRENS] = Threns;
		resIDToOrientationMapping[RESS_ORE_JISPER] = Jisper;
		resIDToOrientationMapping[RESS_ORE_ZERCANER] = Zercaner;
		resIDToOrientationMapping[RESS_ORE_SERTISE] = Sertise;
		resIDToOrientationMapping[RESS_ORE_HITAL] = Hital;
		resIDToOrientationMapping[RESS_ORE_FERTIKEEN] = Fertikeen;
		resIDToOrientationMapping[RESS_ORE_PARSTUN] = Parstun;
		resIDToOrientationMapping[RESS_ORE_NACHT] = Nacht;
	}

	//never use orientation 0 for anything, else old planets suddenly get minerals
	static {
		orientationToResOverlayMapping[Hattel] = 1;
		orientationToResOverlayMapping[Sintyr] = 2;
		orientationToResOverlayMapping[Mattise] = 3;
		orientationToResOverlayMapping[Rammet] = 4;
		orientationToResOverlayMapping[Varat] = 5;
		orientationToResOverlayMapping[Bastyn] = 6;
		orientationToResOverlayMapping[Parsen] = 7;
		orientationToResOverlayMapping[Nocx] = 8;

		orientationToResOverlayMapping[Threns] = 9;
		orientationToResOverlayMapping[Jisper] = 10;
		orientationToResOverlayMapping[Zercaner] = 11;
		orientationToResOverlayMapping[Sertise] = 12;
		orientationToResOverlayMapping[Hital] = 13;
		orientationToResOverlayMapping[Fertikeen] = 14;
		orientationToResOverlayMapping[Parstun] = 15;
		orientationToResOverlayMapping[Nacht] = 16;

	}

	public static boolean isShard(short type) {
		return
				type == RESS_CRYS_HATTEL ||
						type == RESS_CRYS_SINTYR ||
						type == RESS_CRYS_MATTISE ||
						type == RESS_CRYS_RAMMET ||
						type == RESS_CRYS_VARAT ||
						type == RESS_CRYS_BASTYN ||
						type == RESS_CRYS_PARSEN ||
						type == RESS_CRYS_NOCX;
	}

	public static boolean isOre(short type) {
		return
				type == RESS_ORE_THRENS ||
						type == RESS_ORE_JISPER ||
						type == RESS_ORE_ZERCANER ||
						type == RESS_ORE_SERTISE ||
						type == RESS_ORE_HITAL ||
						type == RESS_ORE_FERTIKEEN ||
						type == RESS_ORE_PARSTUN ||
						type == RESS_ORE_NACHT;
	}

	public static boolean hasResourceInjected(short type, byte orientation) {
		return isValidType(type) && getInfo(type).resourceInjection > 0 && orientation > 0 && orientation != 16 && orientation <= 17;
	}

	private static void add(short key, ElementInformation information) throws ParserConfigurationException {
		if (keySet.contains(key)) {
			throw new ParserConfigurationException("Duplicate Block ID " + key + " (" + information.getName() + " and " + informationKeyMap.get(key).getName() + ")");
		}
		keySet.add(key);
		informationKeyMap.put(key, information);
		highestType = Math.max(highestType, key);

		if (information.getFactory() != null) {
			factoryKeySet.add(key);
		}

		

	}

	public static void addInformationToExisting(ElementInformation info) throws ParserConfigurationException {

		categoryHirarchy.insertRecusrive(info);
		add(info.getId(), info);
		infoArray = new ElementInformation[highestType + 1];
		factoryInfoArray = new boolean[highestType + 1];
		validArray = new boolean[highestType + 1];
		lodShapeArray = new boolean[highestType + 1];

		for (Entry<Short, ElementInformation> e : informationKeyMap.entrySet()) {

			infoArray[e.getKey()] = e.getValue();
			validArray[e.getKey()] = true;
			
		}
		if (factoryKeySet.contains(info.getId())) {
			factoryInfoArray[info.getId()] = true;
			info.getFactory().enhancer = FACTORY_INPUT_ENH_ID;
		}
		if(info.hasLod()){
			lodShapeArray[info.getId()] = true;
		}
	}

	public static void clear() {
		informationKeyMap.clear();
		infoArray = null;
		highestType = 0;
		factoryKeySet.clear();
		keySet.clear();
		projected.clear();
		leveldKeySet.clear();
		categoryHirarchy.clear();
		factoryInfoArray = null;
		validArray = null;
		signalArray = null;
		lodShapeArray = null;
	}

	public static boolean exists(int q) {
		return q > 0 && (q < infoArray.length && infoArray[q] != null);
	}

	public static String formatDescString(String input) {
		StringBuffer b = new StringBuffer(input);
		int c = 0;
		int max = 50;
		for (int i = 0; i < b.length() - 1; i++) {
			if (b.charAt(i) == '\n') {
				c = 0;
				i++;
			}

			if (c > max) {
				while (i > 0 && b.charAt(i) != ' ') {
					i--;
				}
				b.deleteCharAt(i);
				b.insert(i, "\n");
				i++;
				c = 0;

			}

			c++;
		}
		String r = b.toString();
		return r;
	}

	/**
	 * @return the categoryHirarchy
	 */
	public static ElementCategory getCategoryHirarchy() {
		return categoryHirarchy;
	}

	/**
	 * @return the factorykeyset
	 */
	public static ShortOpenHashSet getFactorykeyset() {
		return factoryKeySet;
	}

	public static ElementInformation getInfoFast(short type) {
		return infoArray[type];
	}

	public static ElementInformation getInfoFast(int type) {
		return infoArray[type];
	}

	public static ElementInformation getInfo(short type) {
		assert (type > 0 && ((type < infoArray.length && infoArray[type] != null))) : "type " + type + " unknown, please check the properties and the xml ";
		ElementInformation elementInformation;
		if (type < 0) {
			throw new NullPointerException("Exception: REQUESTED TYPE " + type + " IS NULL");
		}
		elementInformation = infoArray[type];
		if (elementInformation == null) {
			throw new NullPointerException("Exception: REQUESTED TYPE " + type + " IS NULL");
		}

		return elementInformation;
	}

	public static ElementInformation getInfo(int type) {
		assert (type > 0 && ((type < infoArray.length && infoArray[type] != null))) : "type " + type + " unknown, please check the properties and the xml ";
		ElementInformation elementInformation;
		if (type < 0) {
			throw new NullPointerException("Exception: REQUESTED TYPE " + type + " IS NULL");
		}
		elementInformation = infoArray[type];
		if (elementInformation == null) {
			throw new NullPointerException("Exception: REQUESTED TYPE " + type + " IS NULL");
		}

		return elementInformation;
	}

	

	/**
	 * @return the leveldkeyset
	 */
	public static ShortOpenHashSet getLeveldkeyset() {
		return leveldKeySet;
	}

	public static String getNameSave(short type) {
		return exists(type) ? getInfo(type).getName() : "unknown(" + type + ")";
	}

	private static void initElements(ArrayList<ElementInformation> arrayList, ElementCategory load) throws ParserConfigurationException {
		for (ElementInformation e : arrayList) {
			add(e.getId(), e);
		}
		categoryHirarchy = load;

		infoArray = new ElementInformation[highestType + 1];
		factoryInfoArray = new boolean[highestType + 1];
		validArray = new boolean[highestType + 1];
		lodShapeArray = new boolean[highestType + 1];

		for (Entry<Short, ElementInformation> e : informationKeyMap.entrySet()) {
			infoArray[e.getKey()] = e.getValue();
			validArray[e.getKey()] = true;


		}
		ShortArrayList signal = new ShortArrayList();
		for (Entry<Short, ElementInformation> e : informationKeyMap.entrySet()) {
			e.getValue().onInit();
			if(e.getValue().isSignal()){
				signal.add(e.getKey());
			}
			lodShapeArray[e.getKey()] = e.getValue().hasLod();
		}
		signal.toArray((signalArray = new short[signal.size()]));
		if (projected.size() > 0) {
			projected.trim();
		}
		for (short s : factoryKeySet) {
			factoryInfoArray[s] = true;
			getInfo(s).getFactory().enhancer = FACTORY_INPUT_ENH_ID;
		}
		sortedByName = new ObjectArrayList<ElementInformation>(informationKeyMap.values());

		Collections.sort(sortedByName, new Comparator<ElementInformation>() {
			@Override
			public int compare(ElementInformation o1, ElementInformation o2) {
				return o1.getName().toLowerCase(Locale.ENGLISH).compareTo(o2.getName().toLowerCase(Locale.ENGLISH));
			}
		});
		initialized = true;

		for (Entry<Short, ElementInformation> e : informationKeyMap.entrySet()) {
			if (!(e.getValue().resourceInjection == 0 || e.getValue().getIndividualSides() == 1)) {
				try {
					throw new ParseException("BlockConfig.xml Error: " + e.getValue() + " cannot have resource injection (resOverlay) and multiple sides");
				} catch (ParseException e1) {
					throw new RuntimeException(e1);
				}
			}
			if (!(e.getValue().resourceInjection == 0 || !e.getValue().orientatable)) {
				try {
					throw new ParseException("BlockConfig.xml Error: " + e.getValue() + " cannot have resource injection (resOverlay) and be orientatable");
				} catch (ParseException e1) {
					throw new RuntimeException(e1);
				}
			}
		}
		
		for(int i : informationKeyMap.keySet()){
			if(getInfo(i).getType().hasParent("Terrain")){
				getInfo(i).setSpecialBlock(false);
			}
		}

	}

	private static void initFixedRecipes(FixedRecipes fixedRecipes) {
		ElementKeyMap.fixedRecipes = fixedRecipes;

		for (int i = 0; i < fixedRecipes.recipes.size(); i++) {
			if (fixedRecipes.recipes.get(i).name.equals("Make Macro Factory Block")) {
				ElementKeyMap.macroBlockRecipe = fixedRecipes.recipes.get(i);
			}
			if (fixedRecipes.recipes.get(i).name.equals("Micro Assembler")) {
				ElementKeyMap.microAssemblerRecipe = fixedRecipes.recipes.get(i);
			}
			if (fixedRecipes.recipes.get(i).name.equals("Capsule Refinery")) {
				ElementKeyMap.capsuleRecipe = fixedRecipes.recipes.get(i);
			}
			if (fixedRecipes.recipes.get(i).name.equals("Personal Capsule Refinery")) {
				ElementKeyMap.personalCapsuleRecipe = fixedRecipes.recipes.get(i);
			}
		}
	}

	public static void initializeData(File appendImport) {
		initializeData(null, false, null, appendImport);
	}

	public static void initializeData(File custom, boolean zipped, String properties, File appendImport) {
		if (initialized) {
			return;
		}

		ElementParser load;
		try {
			load = load(custom, zipped, properties, appendImport);
			initElements(load.getInfoElements(), load.getRootCategory());
			initFixedRecipes(load.getFixedRecipes());
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}


		keyArray = new short[keySet.size()];
		int i = 0;
		for (short s : keySet) {
			keyArray[i] = s;
			i++;
		}
		doorTypes.clear();
		for (ElementInformation in : infoArray) {
			if (in != null && in.isDoor()) {
				doorTypes.add(in.id);
			}
		}
		doorTypes.trim();

		
		inventoryTypes.clear();
		for (ElementInformation in : infoArray) {
			if (in != null && in.isInventory()) {
				inventoryTypes.add(in.id);
			}
		}
		inventoryTypes.trim();
		assert (checkConflicts());

	}

	private static boolean checkConflicts() {
		for (short s : keySet) {
			for (short s0 : keySet) {
				if (s != s0 && getInfo(s).getSlab() == 0 && getInfo(s0).getSlab() == 0 && getInfo(s).getBuildIconNum() == getInfo(s0).getBuildIconNum()) {
					System.err.println("[INFO] BuildIconConflict: " + toString(s) + " --- " + toString(s0) + "; " + getInfo(s).getBuildIconNum() + "; " + getInfo(s0).getBuildIconNum());
					return false;
				}
			}
		}
		return true;
	}


	public static boolean isValidType(short type) {
		return type >= 0 && type < infoArray.length && infoArray[type] != null;
	}

	public static boolean isValidType(int type) {
		return type >= 0 && type < infoArray.length && infoArray[type] != null;
	}

	public static String list() {
		return keySet.toString();
	}

	private static ElementParser load(File custom, boolean zipped, String properties, File appendImport) throws SAXException, IOException, ParserConfigurationException, ElementParserException {
		if (custom == null) {
			ElementParser parser = new ElementParser();
			parser.loadAndParseDefault(appendImport);

			return parser;
		} else {
			ElementParser parser = new ElementParser();
			parser.loadAndParseCustomXML(custom, zipped, properties, appendImport);

			return parser;
		}
	}

	public static void reinitializeData(File custom, boolean zipped, String properties, File appendInput) {
		initialized = false;
		categoryHirarchy = null;
		factoryKeySet.clear();
		projected.clear();
		keySet.clear();
		leveldKeySet.clear();
		highestType = 0;
		informationKeyMap.clear();
		fixedRecipes = null;
		infoArray = null;
		factoryInfoArray = null;
		validArray = null;
		signalArray = null;
		lodShapeArray = null;
		keyArray = null;
		initializeData(custom, zipped, properties, appendInput);

	}

	public static void removeFromExisting(ElementInformation info) {

		keySet.remove(info.getId());
		informationKeyMap.remove(info.getId());
		highestType = 0;
		for (short s : keySet) {
			highestType = Math.max(highestType, s);
		}

		factoryKeySet.remove(info.getId());
		factoryInfoArray[info.getId()] = false;
		getLeveldkeyset().remove(info.getId());

	

		infoArray = new ElementInformation[highestType + 1];
		validArray = new boolean[highestType + 1];
		lodShapeArray = new boolean[highestType + 1];
		
		
		
		for (Entry<Short, ElementInformation> e : informationKeyMap.entrySet()) {
			infoArray[e.getKey()] = e.getValue();
			validArray[e.getKey()] = true;
			lodShapeArray[e.getKey()] = e.getValue().hasLod();
		}
		categoryHirarchy.removeRecursive(info);

	}

	public static void reparseProperties() throws IOException {
		// Read properties file.
		properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(MediawikiExport.properties.getProperty("blockconfigpath")+"BlockTypes.properties");
		properties.load(fileInputStream);

	}

	public static void reparseProperties(String custom) throws IOException {
		// Read properties file.
		properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(custom);
		properties.load(fileInputStream);

	}

	public static short[] typeList() {
		return keyArray;
	}

	private static void writeCatToXML(ElementCategory h, Element root, Document doc) throws CannotAppendXMLException {

		org.w3c.dom.Element child = doc.createElement(h.getCategory());
		for (ElementCategory e : h.getChildren()) {
			writeCatToXML(e, child, doc);
		}
		for (ElementInformation info : h.getInfoElements()) {
			info.appendXML(doc, child);
		}
		root.appendChild(child);
	}

	public static File writeDocument(File file, ElementCategory h, FixedRecipes fixedRecipes) {
		try {
			// ///////////////////////////
			// Creating an empty XML Document

			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			org.w3c.dom.Element root = doc.createElement("Config");

			// //////////////////////
			// Creating the XML tree

			org.w3c.dom.Element elementRoot = doc.createElement(h.getCategory());

			Comment comment = doc
					.createComment("autocreated by the starmade block editor");
			elementRoot.appendChild(comment);

			for (ElementCategory g : h.getChildren()) {
				writeCatToXML(g, elementRoot, doc);
			}
			// create the root element and add it to the document

			org.w3c.dom.Element recipeRoot = doc.createElement("Recipes");

			writeRecipes(recipeRoot, doc, fixedRecipes);

			root.appendChild(elementRoot);
			root.appendChild(recipeRoot);

			doc.appendChild(root);
			doc.setXmlVersion("1.0");

			// create a comment and put it in the root element

			// ///////////////
			// Output the XML

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(file);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);

			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File writeDocument(String path, ElementCategory h, FixedRecipes fixedRecipes) {
		File file = new File(path);
		return writeDocument(file, h, fixedRecipes);
	}

	private static void writeRecipes(Element recipeRoot, Document doc, FixedRecipes fixedRecipes) throws DOMException, CannotAppendXMLException {
		fixedRecipes.appendDoc(recipeRoot, doc);

	}

	public static void removeDuplicateBuildIcons() {
		IntOpenHashSet h = new IntOpenHashSet();
		for (ElementInformation info : informationKeyMap.values()) {
			int i = info.getBuildIconNum();
			while (h.contains(i)) {
				i++;
			}
			info.setBuildIconNum(i);
			h.add(info.getBuildIconNum());
		}
	}

	public static String toString(short type) {
		return ElementKeyMap.exists(type) ? ElementKeyMap.getInfo(type).toString() : "Unknown(" + type + ")";
	}

	public static String toString(Collection<Short> controlling) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (Short s : controlling) {
			sb.append(toString(s) + ";");
		}
		sb.append("}");
		return sb.toString();
	}

	public static short getCollectionType(short type) {
		if (ElementKeyMap.isValidType(type) && ElementKeyMap.getInfo(type).isDoor()) {
			return ElementKeyMap.DOOR_ELEMENT;
		}
		return type;
	}

	public static boolean isDoor(short id) {
		return ElementKeyMap.isValidType(id) && ElementKeyMap.getInfo(id).isDoor();
	}

	public static boolean isMacroFactory(short type) {
		return type == ElementKeyMap.FACTORY_BASIC_ID || type == ElementKeyMap.FACTORY_STANDARD_ID || type == ElementKeyMap.FACTORY_ADVANCED_ID;
	}

	public static ElementInformation[] getInfoArray() {
		return infoArray;
	}

	public static void setInfoArray(ElementInformation[] infoArray) {
		ElementKeyMap.infoArray = infoArray;
	}

	public static boolean isGroupCompatible(short a, short b) {
		return ElementKeyMap.isValidType(a) && ElementKeyMap.isValidType(b) && ElementKeyMap.getInfo(a).getInventoryGroup().length() > 0 && ElementKeyMap.getInfo(a).getInventoryGroup().equals(ElementKeyMap.getInfo(b).getInventoryGroup());
	}




	public static boolean isInvisible(short id) {
		return id == ElementKeyMap.SIGNAL_TRIGGER_AREA;
	}

	public static boolean canOpen(short type) {
		return 
				type == ElementKeyMap.STASH_ELEMENT || 
						type == ElementKeyMap.RECYCLER_ELEMENT || 
						type == ElementKeyMap.SHIPYARD_COMPUTER || 
				ElementKeyMap.getFactorykeyset().contains(type);
	}

	public static void createBlockSlabs(ElementInformation info) throws ParseException {
		if(info.getSlab() != 0){
			throw new ParseException("Cannot create slab of slab");
		}
		assert(info.idName != null);
		String quarterName = info.idName + "_"+"QUARTER_SLAB";
		String halfName = info.idName + "_"+"HALF_SLAB";
		String tQuartName = info.idName + "_"+"THREE_QUARTER_SLAB";
		
		int quarterId = insertIntoProperties(quarterName);
		int halfId = insertIntoProperties(halfName);
		int threeQuarterId = insertIntoProperties(tQuartName);
		
		ElementInformation copyQuarter = new ElementInformation(info, (short)quarterId, info.name +" 1/4");
		ElementInformation copyHalf = new ElementInformation(info, (short)halfId, info.name +" 1/2");
		ElementInformation copyThreeQuarter = new ElementInformation(info, (short)threeQuarterId, info.name +" 3/4");
		
		
		
		copyThreeQuarter.name = info.name +" 3/4";
		copyHalf.name = info.name +" 1/2";
		copyQuarter.name = info.name +" 1/4";
		
		copyThreeQuarter.slab = (1);
		copyHalf.slab = (2);
		copyQuarter.slab = (3);
		
		copyThreeQuarter.slabReference = info.getId();
		copyHalf.slabReference = info.getId();
		copyQuarter.slabReference = info.getId();
		
		copyThreeQuarter.shoppable = false;
		copyHalf.shoppable = false;
		copyQuarter.shoppable = false;
		
		copyThreeQuarter.inRecipe = false;
		copyHalf.inRecipe = false;
		copyQuarter.inRecipe = false;
		
		copyThreeQuarter.orientatable = true;
		copyHalf.orientatable = true;
		copyQuarter.orientatable = true;
		
		copyThreeQuarter.producedInFactory = 0;
		copyHalf.producedInFactory = 0;
		copyQuarter.producedInFactory = 0;
		
		try {
			ElementKeyMap.addInformationToExisting(copyQuarter);
			ElementKeyMap.addInformationToExisting(copyHalf);
			ElementKeyMap.addInformationToExisting(copyThreeQuarter);
		
		
			info.slabIds = new short[]{copyThreeQuarter.getId(), copyHalf.getId(), copyQuarter.getId()};
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static int insertIntoProperties(String idName){
		assert(idName != null);
		if(properties.containsKey(idName)){
			return Integer.parseInt(properties.get(idName).toString());
		}else{
			for (int i = 500; i < 2048; i++) {
				if (!properties.values().contains(String.valueOf(i))) {
					properties.put(idName, String.valueOf(i));
					
					writePropertiesOrdered();
					return i;
				}
			}
			throw new NullPointerException("No Block ID Free");
		}
	}
	public static void removeByIdName(String idName, boolean removeFromProperties){
		if(properties.containsKey(idName)){
			int q;
			if(removeFromProperties){
				q = Integer.parseInt(properties.remove(idName).toString());
				writePropertiesOrdered();
			}else{
				q = Integer.parseInt(properties.get(idName).toString());
			}
			
			if(exists(q)){
				removeFromExisting(getInfo(q));
			}
			
		}
	}
	public static boolean isLodShape(int id){
		return id > 0 && id < lodShapeArray.length && lodShapeArray[id];
	}
	public static void writePropertiesOrdered(){
		try {
			Properties p = new Properties() {
			    /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
			    public synchronized Enumeration<Object> keys() {
			    	List<java.util.Map.Entry<Object, Object>> keys = new ArrayList<java.util.Map.Entry<Object, Object>>();
			    	
			    	for(java.util.Map.Entry<Object, Object> k : super.entrySet()){
			    		keys.add(k);
			    	}
			    	
			    	Collections.sort(keys, new Comparator<java.util.Map.Entry<Object, Object>>() {

						@Override
						public int compare(
								java.util.Map.Entry<Object, Object> o1,
								java.util.Map.Entry<Object, Object> o2) {
							return Integer.parseInt(o1.getValue().toString()) - Integer.parseInt(o2.getValue().toString());
						}
					});
			    	List<Object> ordered = new ArrayList<Object>();
			    	
			    	for(java.util.Map.Entry<Object, Object> e : keys){
			    		ordered.add(e.getKey());
			    	}
			        return Collections.enumeration(ordered);
			    }
			};
			p.putAll(properties);
			p.store(new FileWriter(propertiesPath), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void deleteBlockSlabs(ElementInformation info) {
		String quarterName = info.idName + "_"+"QUARTER_SLAB";
		String halfName = info.idName + "_"+"HALF_SLAB";
		String tQuartName = info.idName + "_"+"THREE_QUARTER_SLAB";		
		
		removeByIdName(quarterName, false);
		removeByIdName(halfName, false);
		removeByIdName(tQuartName, false);
	}

	public static boolean isToStashConnectable(short fromType) {
		return fromType == ElementKeyMap.SHIPYARD_COMPUTER || fromType == ElementKeyMap.SHOP_BLOCK_ID;
	}


}
