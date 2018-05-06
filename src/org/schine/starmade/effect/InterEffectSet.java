package org.schine.starmade.effect;

import java.util.Arrays;
import java.util.Locale;

import org.schine.starmade.effect.InterEffectHandler.InterEffectType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class InterEffectSet {
	public static final int length = InterEffectType.values().length;
	private float[] strength = new float[length];
	
	public InterEffectSet() {
		
	}
	public InterEffectSet(InterEffectSet from) {
		set(from);
	}

	public float getStrength(InterEffectType t){
		return strength[t.ordinal()];
	}
	
	public void reset(){
		Arrays.fill(strength, 0f);
	}
	
	public void set(final InterEffectSet s){
		for(int i = 0; i < length; i++){
			strength[i] = s.strength[i];
		}
	}
	public void setStrength(final InterEffectType t, float val){
		strength[t.ordinal()] = val;
	}
	public void scaleAdd(final InterEffectSet s, final float v) {
		for(int i = 0; i < length; i++){
			strength[i] += s.strength[i] * v;
		}
	}
	public boolean hasEffect(InterEffectType t){
		return getStrength(t) > 0f;
	}

	public boolean isZero() {
		for(float s : strength) {
			if(s != 0) {
				return false;
			}
		}
		return true;
	}
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		InterEffectType[] types = InterEffectType.values();
		b.append("EFFECT[");
		for(int i = 0; i < length; i++) {
			b.append("(");
			b.append(types[i].id);
			b.append(" = ");
			b.append(strength[i]);
			b.append(")");
			if(i < length-1) {
				b.append(", ");
			}
		}
		b.append("]");
		return b.toString();
	}
	
}
