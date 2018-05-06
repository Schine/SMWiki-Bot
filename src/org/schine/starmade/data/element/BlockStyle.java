package org.schine.starmade.data.element;

import org.schine.starmade.data.element.exception.ElementParserException;

public enum BlockStyle {
		NORMAL(0, true, false, false, 6, "normal", "A normal 6 sided block", "block"),
		WEDGE(1, false, true, false, 12, "wedge", "A wedged block", "wedge"),
		CORNER(2, false, true, false, 24, "corner", "A corner blcok with a square base", "corner"),
		SPRITE(3, false, false, true, 6, "sprite", "An X-shaped spritelike block", "sprite"),
		TETRA(4, false, true, false, 8, "tetra", "A corner angeled block with a triangle base", "tetra"),
		HEPTA(5, false, true, false, 8, "penta", "A block with a tetra cut off", "hepta"),
		NORMAL24(6, true, false, false, 24, "normal24", "A block with 24 orientation like rails", "24normal"), 
	;
	public final int id;
	public final String[] oldNames;
	public final String realName;
	public String desc;

	public final boolean solidBlockStyle;
	public final boolean blendedBlockStyle;
	public final boolean cube;
	public final int orientations;
	
	public static final String[] ids = getAsStringId();
	public static final String[] names = getAsStringName();
	
	public static final String[] getAsStringId(){
		String[] s = new String[values().length];
		for(int i = 0; i < values().length; i++){
			s[i] = String.valueOf(values()[i].id);
		}
		return s;
	}
	public static final String[] getAsStringName(){
		String[] s = new String[values().length];
		for(int i = 0; i < values().length; i++){
			s[i] = String.valueOf(values()[i].realName);
		}
		return s;
	}
	
	private BlockStyle(int id, boolean normal, boolean solidBlockStyle, boolean blendedBlockStyle, int orientations, String realName, String desc, String ... oldName){
		this.id = id;
		this.oldNames = oldName;
		this.realName = realName;
		this.desc = desc;
		this.cube = normal;
		this.solidBlockStyle = solidBlockStyle;
		this.blendedBlockStyle = blendedBlockStyle;
		this.orientations = orientations;
	}
	public static String getDescs() {
		StringBuffer f = new StringBuffer();
		for(int i = 0; i < values().length; i++){
			f.append(values()[i].realName+": "+values()[i].desc+";\n");
		}
		
		return f.toString();
	}
	public static BlockStyle getById(int blockStyle) throws ElementParserException {
		for(int i = 0; i < values().length; i++){
			if(values()[i].id == blockStyle){
				return values()[i];
			}
		}
		throw new ElementParserException("Block Style not found by id: "+blockStyle);
	}
}
