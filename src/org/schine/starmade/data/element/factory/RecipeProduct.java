package org.schine.starmade.data.element.factory;


public class RecipeProduct implements RecipeProductInterface {
	public FactoryResource[] inputResource;
	public FactoryResource[] outputResource;

	/**
	 * @return the inputResource
	 */
	@Override
	public FactoryResource[] getInputResource() {
		return inputResource;
	}

	/**
	 * @return the outputResource
	 */
	@Override
	public FactoryResource[] getOutputResource() {
		return outputResource;
	}
}
