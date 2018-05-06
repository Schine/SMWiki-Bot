package org.schine.starmade.data.element.annotation;

import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.exception.ElementParserException;
import org.w3c.dom.Node;

public interface NodeSetting {
	public void parse(Node node, ElementInformation info) throws ElementParserException;
}
