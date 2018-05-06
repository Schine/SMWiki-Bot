package org.schine.starmade;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import it.unimi.dsi.fastutil.shorts.Short2FloatOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2IntMap.Entry;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector3f;

import org.schine.starmade.data.element.ElementKeyMap;


public class ElementCountMap{

	private int[] counts;
	private int[] oreCounts; 
	private long currentPrice;
	private int existingTypeCount;
	private int lodShapes;

	public ElementCountMap() {
		counts = new int[ElementKeyMap.highestType + 1];
		oreCounts = new int[16];
	}

	public ElementCountMap(ElementCountMap c) {
		this();
		int existing = 0;
		for (int i = 0; i < counts.length; i++) {
			counts[i] = c.counts[i];
			if (counts[i] > 0) {
				existing++;
			}
		}
		for(int i = 0; i < oreCounts.length; i++){
			oreCounts[i] = c.oreCounts[i];
		}
		currentPrice = getPrice();
		existingTypeCount = existing;
	}
	/**
	 * rechecks array size. this is necessary if these maps reside in pools
	 * and the player joins a game with a different block config.
	 */
	public void checkArraySize() {
		if(counts.length != ElementKeyMap.highestType + 1){
			counts = new int[ElementKeyMap.highestType + 1];
		}
	}
	public void add(int[] nCounts, int[] oreCounts) {
		for (int i = 0; i < counts.length; i++) {
			int c = nCounts[i];
			if( c > 0){
				inc((short)i, c);
			}
		}
		for(int i = 0; i < oreCounts.length; i++){
			this.oreCounts[i] += oreCounts[i];
		}
	}
	public void add(ElementCountMap elementMap) {
		for (int i = 0; i < counts.length; i++) {
			if (elementMap.counts[i] > 0) {
				inc((short) i, elementMap.counts[i]);
			}
		}
		for(int i = 0; i < oreCounts.length; i++){
			oreCounts[i] += elementMap.oreCounts[i];
		}
	}
	public void add(ElementCountMap elementMap, double weight) {
		for (int i = 0; i < counts.length; i++) {
			if (elementMap.counts[i] > 0) {
				inc((short) i, (int)(elementMap.counts[i] * weight));
			}
		}
		for(int i = 0; i < oreCounts.length; i++){
			oreCounts[i] += (int)(elementMap.oreCounts[i] * weight);
		}
	}

	public void mult(int mult) {
		for (int i = 0; i < counts.length; i++) {
			if (this.counts[i] > 0) {
				mult((short) i, mult);
			}
		}
		for(int i = 0; i < oreCounts.length; i++){
			oreCounts[i] *= mult;
		}
	}
	
	public void decOre(int ore) {
		oreCounts[ore]--;
	}
	public void addOre(int ore) {
		oreCounts[ore]++;
	}
	public void dec(short type) {
		boolean wasPlus = counts[type] > 0;
		counts[type]--;

		if (wasPlus && counts[type] == 0) {
			existingTypeCount--;
		}

		currentPrice -= ElementKeyMap.getInfo(type).getPrice(false);
	}
	
	public void dec(short type, int count) {
		boolean wasPlus = counts[type] > 0;
		
		long newCount = Math.max(Integer.MIN_VALUE, (long)counts[type] - (long)count);
		counts[type] = (int) newCount;
		
		if (wasPlus && counts[type] <= 0) {
			existingTypeCount--;
		}
		if (!wasPlus && counts[type] > 0) {
			existingTypeCount++;
		}

		currentPrice -= (count * ElementKeyMap.getInfo(type).getPrice(false));
	}

	public void deserialize(DataInput stream) throws IOException {
		resetAll();
		int size = stream.readInt();
		int existing = 0;
		for (int i = 0; i < size; i++) {
			short type = stream.readShort();
			int count = stream.readInt();
			if (ElementKeyMap.exists(type)) {
				counts[type] = count;
				existing++;
			}
		}
		existingTypeCount = existing;
		currentPrice = getPrice();
	}

	public int get(short type) {
		return counts[type];
	}

	/**
	 * @return the currentPrice
	 */
	public long getCurrentPrice() {
		return currentPrice;
	}

	public long getPrice() {
		long price = 0;
		for (short d : ElementKeyMap.keySet) {
			price += ElementKeyMap.getInfo(d).getPrice(false) * counts[d];
		}
		if (price < 0) {
			return Long.MAX_VALUE;
		}
		return price;
	}

	public double getMass() {
		double mass = 0;
		for (short d : ElementKeyMap.keySet) {
			mass += (double)ElementKeyMap.getInfoFast(d).getMass() * (double)counts[d];
		}

		return mass;
	}

	public void inc(short type) {
		assert (type < counts.length) : "ERROR: " + type + "/" + counts.length + "  (" + ElementKeyMap.highestType + ")";

		boolean wasZero = counts[type] <= 0;
		long newCount = Math.min(Integer.MAX_VALUE, counts[type] + 1L);
		
		counts[type] = (int) newCount;
		if (wasZero && counts[type] > 0) {
			existingTypeCount++;
		}
		currentPrice += ElementKeyMap.getInfo(type).getPrice(false);
		if(ElementKeyMap.isLodShape(type)){
			lodShapes ++;
		}
	}

	public void inc(short type, int count) {
		assert (type < counts.length) : "ERROR: " + type + "/" + counts.length + "  (" + ElementKeyMap.highestType + ")";

		boolean wasZero = counts[type] <= 0;
		
		
		long newCount = Math.min(Integer.MAX_VALUE, (long)counts[type] + (long)count);
		
		counts[type] = (int) newCount;
		if (wasZero && counts[type] > 0) {
			existingTypeCount++;
		}
		if (!wasZero && counts[type] <= 0) {
			existingTypeCount--;
		}
		
		currentPrice += (count * ElementKeyMap.getInfo(type).getPrice(false));
		
		if(ElementKeyMap.isLodShape(type)){
			lodShapes += count;
		}
	}
	
	public void mult(short type, int mult){	
		if(mult < 1){
			return;
		}
		int oldCount = counts[type];
		long newCount = Math.min(Integer.MAX_VALUE, (long)counts[type] * (long) mult);
		System.out.println("mult " + ElementKeyMap.getInfo(type).name + " count " + oldCount + " with mult " + mult + " = " + newCount);
		counts[type] = (int) newCount;
		
		currentPrice += ((newCount - oldCount) * ElementKeyMap.getInfo(type).getPrice(false));
	}

	public void load(Short2IntArrayMap elementMap) {
		int existing = 0;
		for (Entry s : elementMap.short2IntEntrySet()) {
			counts[s.getShortKey()] = s.getIntValue();
			if (counts[s.getShortKey()] > 0) {
				existing++;
			}
		}
		existingTypeCount = existing;
		currentPrice = getPrice();
	}

	public void reset(short type) {
		currentPrice -= ElementKeyMap.getInfoFast(type).getPrice(false) * counts[type];
		int c = counts[type];
		counts[type] = 0;
		if(c > 0){
			existingTypeCount--;
		}
		if(ElementKeyMap.isLodShape(type)){
			lodShapes -= c;
		}

	}

	public void resetAll() {
		Arrays.fill(counts, 0);
		Arrays.fill(oreCounts, 0);
		currentPrice = 0;
		existingTypeCount = 0;
		lodShapes = 0;
	}

	public void serialize(DataOutputStream stream) throws IOException {
		assert (sizeOk());
		stream.writeInt(existingTypeCount);
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0) {
				stream.writeShort(i);
				stream.writeInt(counts[i]);
			}
		}
	}

	private boolean sizeOk() {
		int size = 0;
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0) {
				size++;
			}
		}
		if (size != existingTypeCount) {
			System.err.println("Size not ok: " + size + "; " + existingTypeCount);
		}
		return size == existingTypeCount;
	}

	public String getAmountListString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0) {
				sb.append(ElementKeyMap.getNameSave((short) i) + ": " + counts[i] + "; ");
			}
		}
		return sb.toString();
	}

	public int getExistingTypeCount() {
		return this.existingTypeCount;
	}

	public byte[] getByteArray() {
		try {
			//size plus one int and one short per entry
			byte[] b = new byte[4 + getExistingTypeCount() * 6];
			DataOutputStream out = new DataOutputStream(new FastByteArrayOutputStream(b));
			serialize(out);
			out.close();
			return b;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void readByteArray(byte[] b) {
		try {
			DataInputStream in = new DataInputStream(new FastByteArrayInputStream(b));
			deserialize(in);
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj != null && obj instanceof ElementCountMap) {
			ElementCountMap o = (ElementCountMap) obj;
			return Arrays.equals(counts, o.counts);
		}
		return true;
	}
	public String printList(){
		StringBuffer b = new StringBuffer();
		for(short type : ElementKeyMap.keySet){
			int am = get(type);
			if(am > 0){
				b.append(ElementKeyMap.getInfoFast(type).getName()+": "+am+"\n");
			}
		}
		return b.toString();
	}
	


	public long getTotalAmount() {
		long c = 0;
		for (short d : ElementKeyMap.keySet) {
			c += counts[d];
		}
		return c;
	}
	
	public boolean hasLod(){
		return lodShapes > 0;
	}
	/**
	 * returns a weighted representation of this map with amount/totalAmount per type in here
	 * @param out
	 */
	public void getWeights(Short2FloatOpenHashMap out) {
		double total = getTotalAmount();
		if(total > 0){
			for (short d : ElementKeyMap.keySet) {
				out.put(d, (float) (counts[d] / total));
			}
		}
	}
	

	public void put(short type, int amount) {
		inc(type, amount - get(type));
	}

	public boolean isEmpty() {
		return getExistingTypeCount() == 0;
	}

	public double getVolume() {
		double volume = 0;
		for (short d : ElementKeyMap.keySet) {
			volume += ElementKeyMap.getInfoFast(d).volume * (double)counts[d];
		}
		return volume;
	}

	public void transferFrom(ElementCountMap c, double totalCap) {
		double volume = 0;
		for (short d : ElementKeyMap.keySet) {
			if(c.counts[d] > 0){
				double toAdd = ElementKeyMap.getInfoFast(d).volume * (double)c.counts[d];
				if(volume + toAdd < totalCap){
					
					int a = c.counts[d];
					
					
					inc(d, a);
					c.inc(d, -a);
					volume += toAdd;
				}else{
					double left = totalCap - volume;
					int amountPossible = (int) (left/ElementKeyMap.getInfoFast(d).volume);
					int a = Math.min(amountPossible, c.counts[d]);
					
					if(a > 0){
						inc(d, a);
						c.inc(d, -a);
					}
					break;
				}
			}
		}
	}

	public int getTotalAdded(ElementCountMap map, short type) {
		return map.get(type) + get(type);
	}
	public boolean restrictedBlocks(ElementCountMap map) {
		return 
				getTotalAdded(map, ElementKeyMap.FACTION_BLOCK) > 1 ||
				getTotalAdded(map, ElementKeyMap.AI_ELEMENT) > 1 ||
				getTotalAdded(map, ElementKeyMap.SHOP_BLOCK_ID) > 1 ||
				getTotalAdded(map, ElementKeyMap.CORE_ID) > 1 
				;
	}
	public int getTotalAdded(int[] map, short type) {
		return map[type] + get(type);
	}
	public boolean restrictedBlocks(int[] map) {
		return 
				getTotalAdded(map, ElementKeyMap.FACTION_BLOCK) > 1 ||
				getTotalAdded(map, ElementKeyMap.AI_ELEMENT) > 1 ||
				getTotalAdded(map, ElementKeyMap.SHOP_BLOCK_ID) > 1 ||
				getTotalAdded(map, ElementKeyMap.CORE_ID) > 1 
				;
	}


	
}
