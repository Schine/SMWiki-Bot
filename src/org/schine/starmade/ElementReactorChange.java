package org.schine.starmade;

import java.util.Observable;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;


public class ElementReactorChange extends Observable{

	public final ElementInformation info;
	public ElementInformation parent;
	public ElementInformation root;
	public boolean upgrade;
	public ElementReactorChange(ElementInformation elementInformation) {
		this.info = elementInformation;
		
		parent = ElementKeyMap.isValidType(this.info.chamberParent) ? ElementKeyMap.getInfo(this.info.chamberParent) : null;
		root = ElementKeyMap.isValidType(this.info.chamberRoot) ? ElementKeyMap.getInfo(this.info.chamberRoot) : null;
		upgrade = parent != null && parent.chamberUpgradesTo == info.id;
	}

	public void openDialog(JFrame f){
		JDialog d = new JDialog(f, "Reactor", true);
		d.setSize(500, 300);
	}

	public void setParent(ElementInformation oth) {
		parent = oth;		
		setChanged();
		notifyObservers();
	}

	public void setUpgrade(boolean b) {
		upgrade = b;
		setChanged();
		notifyObservers();
		
	}
	public void setRoot(ElementInformation oth) {
		root = oth;
		setChanged();
		notifyObservers();
	}
	private void reinitGeneral(ElementInformation info){
		for(short s : ElementKeyMap.keySet){
			ElementInformation o = ElementKeyMap.getInfo(s);
			if(o.chamberParent == 0 && o.chamberRoot == info.id){
				info.chamberChildren.add(s);
			}
		}
	}
	public void clear() {
		//remove this element from everything
		for(short s : ElementKeyMap.keySet){
			if(s != info.id){
				ElementInformation o = ElementKeyMap.getInfo(s);
				if(!o.isReactorChamberGeneral()){
					o.chamberChildren.remove(info.id);
					o.chamberPrerequisites.remove(info.id);
					if(o.chamberUpgradesTo == info.id){
						o.chamberUpgradesTo = 0;
					}
				}
			}
		}
		
		info.chamberChildren.clear();
		info.chamberPrerequisites.clear();
		info.chamberUpgradesTo = 0;
		info.chamberRoot = 0;
		info.chamberParent = 0;
	}
	public void apply() {
		if(root == null){
			System.err.println("NOT APPLIED. MISSING FIELDS");
			return;
		}
		clear();
		
		info.chamberRoot = root.id;
		info.chamberParent = parent != null ? parent.id : 0;
		
		if(parent != null){
			parent.chamberChildren.add(info.id);
			info.chamberPrerequisites.add(parent.id);
			if(upgrade){
				parent.chamberUpgradesTo = info.id;
			}
		}
		for(short s : ElementKeyMap.keySet){
			ElementInformation o = ElementKeyMap.getInfo(s);
			if(o.isReactorChamberGeneral()){
				o.chamberChildren.clear();
				o.chamberPrerequisites.clear();
				o.chamberUpgradesTo = 0;
				o.chamberParent = 0;
				reinitGeneral(o);
			}
		}
	}
	
}
