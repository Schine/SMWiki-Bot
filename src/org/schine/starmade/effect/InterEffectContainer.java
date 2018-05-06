package org.schine.starmade.effect;


public abstract class InterEffectContainer {
	protected final InterEffectSet[] sets;
	
	public InterEffectContainer(){
		sets = setupEffectSets();
	}
	
	
//	public abstract void update(ConfigEntityManager c);
	
//	public void addGeneral(ConfigEntityManager c, InterEffectSet aSet) {
//		aSet.add(c, StatusEffectType.GENERAL_DEFENSE_EM, InterEffectType.EM);
//		aSet.add(c, StatusEffectType.GENERAL_DEFENSE_KINETIC, InterEffectType.KIN);
//		aSet.add(c, StatusEffectType.GENERAL_DEFENSE_HEAT, InterEffectType.HEAT);
//	}
//	public void addShield(ConfigEntityManager c, InterEffectSet aSet) {
//		aSet.add(c, StatusEffectType.SHIELD_DEFENSE_EM, InterEffectType.EM);
//		aSet.add(c, StatusEffectType.SHIELD_DEFENSE_KINETIC, InterEffectType.KIN);
//		aSet.add(c, StatusEffectType.SHIELD_DEFENSE_HEAT, InterEffectType.HEAT);
//	}
//	public void addArmor(ConfigEntityManager c, InterEffectSet aSet) {
//		
//		aSet.add(c, StatusEffectType.ARMOR_DEFENSE_EM, InterEffectType.EM);
//		aSet.add(c, StatusEffectType.ARMOR_DEFENSE_KINETIC, InterEffectType.KIN);
//		aSet.add(c, StatusEffectType.ARMOR_DEFENSE_HEAT, InterEffectType.HEAT);
//	}
	public abstract InterEffectSet[] setupEffectSets();
//	public abstract InterEffectSet get(HitReceiverType type);


	public void reset() {
		for(InterEffectSet s : sets) {
			s.reset();
		}
	}
}
