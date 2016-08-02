package org.schine.starmade.data.element.factory;

public class BlockFactory {

	public short enhancer;
	public FactoryResource[][] input;
	public FactoryResource[][] output;

	@Override
	public String toString() {
		return input != null ? "Block Factory Products: " + input.length : "INPUT";
	}
}
