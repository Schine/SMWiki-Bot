package org.schine.starmade.data.element.factory;

import java.util.Arrays;

import org.schine.starmade.data.element.exception.CannotAppendXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FixedRecipeProduct implements RecipeProductInterface {

	public FactoryResource[] input;
	public FactoryResource[] output;

	public Element getNode(Document doc) throws CannotAppendXMLException {
		Element e = doc.createElement("Product");
		Element input = doc.createElement("Input");
		Element output = doc.createElement("Output");

		for (FactoryResource i : this.input) {
			input.appendChild(i.getNode(doc));
		}
		for (FactoryResource i : this.output) {
			output.appendChild(i.getNode(doc));
		}
		e.appendChild(input);
		e.appendChild(output);
		return e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "INPUT: " + Arrays.toString(input) + "; OUTPUT: " + Arrays.toString(output);
	}

	public String toNiceString() {
		StringBuffer a = new StringBuffer();
		for (int i = 0; i < input.length; i++) {
			a.append(input[i].toString() + " will produce " + output[i].toString());
			a.append("\n");
		}
		return a.toString();
	}

	/**
	 * @return the inputResource
	 */
	@Override
	public FactoryResource[] getInputResource() {
		return input;
	}

	/**
	 * @return the outputResource
	 */
	@Override
	public FactoryResource[] getOutputResource() {
		return output;
	}

}
