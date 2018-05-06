/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.schema.schine.resource;

/**
 *
 * @author brent
 */
public interface Translatable {
	
	public String getName(Enum en);
	
	public static final Translatable DEFAULT = new Translatable() {
		@Override
		public String getName(Enum en) {
			return en.name();
		}
	};
}
