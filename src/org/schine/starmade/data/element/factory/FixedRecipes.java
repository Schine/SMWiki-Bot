package org.schine.starmade.data.element.factory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import org.schine.starmade.data.element.exception.CannotAppendXMLException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FixedRecipes {
	public final ObjectArrayList<FixedRecipe> recipes = new ObjectArrayList<FixedRecipe>();

	public void appendDoc(Element recipeRoot, Document doc) throws DOMException, CannotAppendXMLException {
		for (FixedRecipe f : recipes) {
			recipeRoot.appendChild(f.getNode(doc));
		}
	}
}
