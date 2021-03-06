package org.schine.starmade.data.element;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.schine.starmade.data.element.annotation.Element;
import org.schine.starmade.effect.InterEffectHandler.InterEffectType;
import org.schine.starmade.effect.InterEffectSet;


public class ElementCategory {
	private final String category;
	private final List<ElementCategory> children = new ObjectArrayList<ElementCategory>();
	private final List<ElementInformation> infoElements = new ObjectArrayList<ElementInformation>();
	private ElementCategory parent;

	public ElementCategory(String category, ElementCategory parent) {
		this.category = category;
		this.parent = parent;
	}

	public void clear() {
		children.clear();
		infoElements.clear();

	}

	public String find(ElementInformation info) {
		if (this.equals(info.getType())) {
			return category;
		} else {
			for (ElementCategory c : getChildren()) {
				String find = c.find(info);
				if (find != null) {
					return find;
				}

			}
		}
		return null;
	}

	public ElementCategory find(String info) {
		if (this.category.toLowerCase(Locale.ENGLISH).equals(info.toLowerCase(Locale.ENGLISH))) {
			return this;
		} else {
			for (ElementCategory c : getChildren()) {
				ElementCategory find = c.find(info);
				if (find != null) {
					return find;
				}
			}
		}
		return null;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		//		if(isRoot()){
		//			return Element.class;
		//		}
		return category;
	}

	/**
	 * @return the children
	 */
	public List<ElementCategory> getChildren() {
		return children;
	}

	/**
	 * @return the infoElements
	 */
	public List<ElementInformation> getInfoElements() {
		return infoElements;
	}

	public List<ElementInformation> getInfoElementsRecursive(List<ElementInformation> infos) {
		infos.addAll(infoElements);
		for (ElementCategory c : getChildren()) {
			c.getInfoElementsRecursive(infos);
		}
		return infos;
	}

	public boolean hasChildren() {
		return !getChildren().isEmpty();
	}

	public boolean insertRecusrive(ElementInformation info) {
		if (this.equals(info.getType())) {
			infoElements.add(info);
			return true;
		} else {
			for (ElementCategory c : getChildren()) {
				boolean in = c.insertRecusrive(info);
				if(in){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return category.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return category.equals(((ElementCategory) obj).category) && ((parent == null && ((ElementCategory) obj).parent == null) ||
				parent.equals(((ElementCategory) obj).parent));
	}

	

	@Override
	public String toString() {
		return category.toString();
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void print() {
		printRec(1);
	}

	private void printItems(int lvl) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lvl; i++) {
			sb.append("-");
		}
		for (int i = 0; i < getInfoElements().size(); i++) {
			ElementInformation elementInformation = getInfoElements().get(i);
			System.err.println(sb.toString() + " " + elementInformation.getName());
		}
	}

	private void printRec(int lvl) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lvl; i++) {
			sb.append("#");
		}
		System.err.println(sb.toString() + " " + category.toString());
		printItems(lvl);
		for (int i = 0; i < children.size(); i++) {
			children.get(i).printRec(lvl + 1);
		}
	}

	public void removeRecursive(ElementInformation info) {
		if (infoElements.contains(info)) {
			infoElements.remove(info);
		} else {
			for (ElementCategory c : getChildren()) {
				c.removeRecursive(info);
			}
		}

	}

	public boolean hasParent(String string) {
		if (category.toLowerCase(Locale.ENGLISH).equals(string.toLowerCase(Locale.ENGLISH))) {
			return true;
		}
		if (parent != null) {
			return parent.hasParent(string);
		}
		return false;
	}

	public ElementCategory getChild(String nodeName) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getCategory().equals(nodeName)) {
				return children.get(i);
			}
		}
		throw new NullPointerException(nodeName);
	}

	public void merge(ElementCategory rootCategory) {
		if (category.equals(rootCategory.category)) {
			for (int i = 0; i < rootCategory.infoElements.size(); i++) {
				if (!infoElements.contains(rootCategory.infoElements.get(i))) {
					infoElements.add(rootCategory.infoElements.get(i));
				}
			}
			boolean found = false;
			for (int i = 0; i < rootCategory.children.size(); i++) {
				for (int j = 0; j < children.size(); j++) {
					if (rootCategory.children.get(i).category.toLowerCase(Locale.ENGLISH).equals(children.get(j).category.toLowerCase(Locale.ENGLISH))) {
						children.get(j).merge(rootCategory.children.get(i));
						found = true;
						break;
					}

				}
				if (!found) {
					appendRecursive(rootCategory.children.get(i));
				}
			}
		}
	}

	private void appendRecursive(ElementCategory elementCategory) {
		ElementCategory cat = new ElementCategory(elementCategory.category, this);
		for (ElementInformation e : elementCategory.infoElements) {
			e.type = cat;
			cat.infoElements.add(e);
		}
		children.add(cat);
		for (int i = 0; i < elementCategory.children.size(); i++) {
			appendRecursive(elementCategory.children.get(i));
		}
	}

	
	
	private void addByType(final Element annotation, final Field f, final JPopupMenu popup, final Component parentComponent, final List<ElementInformation> infos) {
		try {
			if (annotation.canBulkChange() && (f.getType() == Boolean.TYPE ||
					f.getType() == Float.TYPE ||
					f.getType() == Long.TYPE ||
					f.getType() == Short.TYPE ||
					f.getType() == Integer.TYPE ||
					f.getType() == Byte.TYPE || 
					f.getType() == Double.TYPE ||
					f.getType().equals(InterEffectSet.class))) {
				JMenuItem jMenuItem = new JMenuItem("Bulk Set " + annotation.parser().tag);

				if(f.getType().equals(InterEffectSet.class)) {
					jMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							for(InterEffectType t : InterEffectType.values()) {
								String s = (String) JOptionPane.showInputDialog(
										parentComponent,
										"Enter new value for "+t.id,
										"Bulk Set " + annotation.parser().tag+" -> "+t.id,
										JOptionPane.PLAIN_MESSAGE,
										null,
										null,
										null);
		
								//If a string was returned, say so.
								if ((s != null) && (s.length() > 0)) {
									for (ElementInformation e : infos) {
										InterEffectSet set;
										try {
											set = (InterEffectSet)f.get(e);
											set.setStrength(t, Float.parseFloat(s));
										} catch (IllegalArgumentException e1) {
											e1.printStackTrace();
										} catch (IllegalAccessException e1) {
											e1.printStackTrace();
										}
										
									}
								}
							}
						}
					});
					
				}else {
					jMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							String s = (String) JOptionPane.showInputDialog(
									parentComponent,
									"Enter new value",
									"Bulk Set " + annotation.parser().tag,
									JOptionPane.PLAIN_MESSAGE,
									null,
									null,
									null);
	
							//If a string was returned, say so.
							if ((s != null) && (s.length() > 0)) {
								try {
	
									if (f.getType() == Boolean.TYPE) {
										boolean val = Boolean.parseBoolean(s);
										for (ElementInformation e : infos) {
											f.setBoolean(e, val);
										}
									} else if (f.getType() == Float.TYPE) {
										float val = Float.parseFloat(s);
										for (ElementInformation e : infos) {
											f.setFloat(e, val);
										}
									} else if (f.getType() == Integer.TYPE) {
										int val = Integer.parseInt(s);
										for (ElementInformation e : infos) {
											f.setInt(e, val);
										}
									} else if (f.getType() == Long.TYPE) {
										long val = Long.parseLong(s);
										for (ElementInformation e : infos) {
											f.setLong(e, val);
										}
									} else if (f.getType() == Short.TYPE) {
										short val = Short.parseShort(s);
										for (ElementInformation e : infos) {
											f.setShort(e, val);
										}
									} else if (f.getType() == Byte.TYPE) {
										byte val = Byte.parseByte(s);
										for (ElementInformation e : infos) {
											f.setByte(e, val);
										}
									} else if (f.getType() == Double.TYPE) {
										double val = Double.parseDouble(s);
										for (ElementInformation e : infos) {
											f.setDouble(e, val);
										}
									} else {
										throw new IllegalArgumentException("Unknown type: " + f.getType());
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								return;
							}
						}
					});
				}
				popup.add(jMenuItem);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public ElementCategory getParent() {
		return parent;
	}

	public void setParent(ElementCategory parent) {
		this.parent = parent;
	}

}
