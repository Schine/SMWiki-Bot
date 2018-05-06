package org.schine.starmade.effect;

import org.schema.schine.resource.Translatable;

public abstract class InterEffectHandler {
	public enum InterEffectType{
		HEAT("Heat", new Translatable(){
			@Override
			public String getName(Enum en) {
				return ("Heat");
			}
		},new Translatable(){
			@Override
			public String getName(Enum en) {
				return ("Heat");
			}
		}),
		KIN("Kinetic", new Translatable(){
			@Override
			public String getName(Enum en) {
				return ("Kin");
			}
		},new Translatable(){
			@Override
			public String getName(Enum en) {
				return ("Kinetic");
			}
		}),
		EM("EM", new Translatable(){
			@Override
			public String getName(Enum en) {
				return ("EM");
			}
		},new Translatable(){
			@Override
			public String getName(Enum en) {
				return ("Electro Magnetic");
			}
		}),
		;
		
		public final Translatable fullName;
		public final Translatable shortName;
		public final String id;

		private InterEffectType(String id, Translatable shortName, Translatable name){
			this.id = id;
			this.fullName = name;
			this.shortName = shortName;
		}
		@Override
		public String toString() {
			return id;
		}
	}
	
	private static final InterEffectHandler[] EFFECTS = new InterEffectHandler[InterEffectType.values().length];
	static{
		for(int i = 0; i < InterEffectType.values().length; i++){
			switch(InterEffectType.values()[i]){
				case HEAT: EFFECTS[i] = new HeatEffectHandler(); break;
				case KIN: EFFECTS[i] = new KineticEffectHandler(); break;
				case EM: EFFECTS[i] = new EMEffectHandler(); break;
				default: assert false; break;
			}
			assert(EFFECTS[i].getType() == InterEffectType.values()[i]):InterEffectType.values()[i]+"; "+i;
		}
	}
	public abstract InterEffectType getType();
	
	
	
}
