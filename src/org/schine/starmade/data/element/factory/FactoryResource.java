package org.schine.starmade.data.element.factory;

import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;
import org.schine.starmade.data.element.exception.CannotAppendXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FactoryResource {
	public int count;
	public short type;

	public FactoryResource(int count, short type) {
		super();
		this.count = count;
		this.type = type;
	}

	public Element getNode(Document doc) throws CannotAppendXMLException {
		//		<Item count="1">TERRAIN_M9L3_ID</Item>
		Element e = doc.createElement("Item");
		e.setAttribute("count", String.valueOf(count));
		String keyId = ElementInformation.getKeyId(type);
		if (keyId == null) {
			throw new CannotAppendXMLException("[RecipeResource] Cannot find property key for Block ID " + type + "; Check your Block properties file");
		}
		e.setTextContent(keyId);

		return e;
	}

	@Override
	public String toString() {
		return count + " of " + ElementKeyMap.toString(type);
	}

}
