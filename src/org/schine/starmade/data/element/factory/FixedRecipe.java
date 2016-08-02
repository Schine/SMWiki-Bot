package org.schine.starmade.data.element.factory;

import java.util.Arrays;

import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;
import org.schine.starmade.data.element.exception.CannotAppendXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FixedRecipe implements RecipeInterface {

	public FixedRecipeProduct[] recipeProducts;

	public short costType = -1;
	public int costAmount = 0;
	public String name = "undef";



	public Element getNode(Document doc) throws CannotAppendXMLException {
		Element e = doc.createElement("Recipe");

		e.setAttribute("costAmount", String.valueOf(costAmount));

		if (costType == -1) {
			e.setAttribute("costType", "CREDITS");
		} else {
			String keyId = ElementInformation.getKeyId(costType);
			if (keyId == null) {
				throw new CannotAppendXMLException("[RecipeResource] Cannot find property key for Block ID " + costType + "; Check your Block properties file");
			}
			e.setAttribute("costType", keyId);
		}

		e.setAttribute("name", name);
		for (FixedRecipeProduct p : recipeProducts) {
			e.appendChild(p.getNode(doc));
		}
		return e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[RECIPE: '" + name + "' COST: " + costAmount + " of " + (costType == -1 ? "CREDITS" : ElementKeyMap.getInfo(costType).getName()) + "; " + Arrays.toString(recipeProducts) + "]";
	}

	@Override
	public FixedRecipeProduct[] getRecipeProduct() {
		return recipeProducts;
	}


	@Override
	public float getBakeTime() {
		if (recipeProducts != null && recipeProducts.length > 0 && recipeProducts[0].output != null && recipeProducts[0].output.length > 0) {
			return ElementKeyMap.getInfo(recipeProducts[0].output[0].type).factoryBakeTime;
		} else {
			return 10.0f;
		}
	}

	public boolean isBuyable() {
		return costAmount >= 0;
	}

	public String getInfoString() {
		StringBuffer b = new StringBuffer("This factory produces the \"" + name + "\" product line\n\nHere are the product you can do:\n");
		for (FixedRecipeProduct p : recipeProducts) {
			b.append(p.toString());
			b.append("\n");
		}

		return b.toString();
	}

	
}
