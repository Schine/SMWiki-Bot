package org.schine.starmade.data.element;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.schine.starmade.data.element.annotation.Element;
import org.schine.starmade.data.element.exception.ParseException;

public class ElementCategory {
	private final String category;
	private final List<ElementCategory> children = new ObjectArrayList<ElementCategory>();
	private final List<ElementInformation> infoElements = new ObjectArrayList<ElementInformation>();
	private final ElementCategory parent;

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

	public void insertRecusrive(ElementInformation info) {
		if (this.equals(info.getType())) {
			infoElements.add(info);
		} else {
			for (ElementCategory c : getChildren()) {
				c.insertRecusrive(info);
			}
		}
	}

	@Override
	public int hashCode() {
		return category.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return category.equals(((ElementCategory) obj).category) && ((getParent() == null && ((ElementCategory) obj).getParent() == null) ||
				getParent().equals(((ElementCategory) obj).getParent()));
	}

	

	@Override
	public String toString() {
		return category.toString();
	}

	public boolean isRoot() {
		return getParent() == null;
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
		if (getParent() != null) {
			return getParent().hasParent(string);
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

	public void addContextMenu(JPopupMenu popup, Component parentComponent) {

		List<ElementInformation> infos = new ObjectArrayList<ElementInformation>();
		getInfoElementsRecursive(infos);

		Field[] fields = ElementInformation.class.getFields();
		{
			JMenuItem jBBMenuItem = new JMenuItem("Bulk Create Slabs");
			jBBMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Object[] options = {"Ok", "Cancel"};
					String title = "Create Slabs";
					final JFrame jFrame = new JFrame(title);
					jFrame.setUndecorated(true); // set frame undecorated, so the frame
					// itself is invisible
					SwingUtilities.invokeLater(new Runnable() {
	
						@Override
						public void run() {
							jFrame.setVisible(true);
						}
					});
					// appears in the task bar
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					jFrame.setLocation(screenSize.width / 2, screenSize.height / 2);
					int n = JOptionPane.showOptionDialog(jFrame, "Are you sure you want to create slabs\nfor all items in this category and its subcategories?", "Confirm",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
							null, options, options[1]);
					switch (n) {
						case 0:
							bulkCreateSlabs(false);
							break;
						case 1:
							break;
					}
	
				}
	
				
			});
			popup.add(jBBMenuItem);
		}
		{
			JMenuItem jBBMenuItem = new JMenuItem("Bulk Reinitialize Existing Slabs");
			jBBMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Object[] options = {"Ok", "Cancel"};
					String title = "Reinitialize slabs";
					final JFrame jFrame = new JFrame(title);
					jFrame.setUndecorated(true); // set frame undecorated, so the frame
					// itself is invisible
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							jFrame.setVisible(true);
						}
					});
					// appears in the task bar
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					jFrame.setLocation(screenSize.width / 2, screenSize.height / 2);
					int n = JOptionPane.showOptionDialog(jFrame, "This will reinitialize all existing slabs", "Confirm",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
							null, options, options[1]);
					switch (n) {
						case 0:
							bulkCreateSlabs(true);
							break;
						case 1:
							break;
					}

				}

				
			});
			popup.add(jBBMenuItem);
		}
		
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			f.setAccessible(true);
			Element annotation = f.getAnnotation(Element.class);

			if (annotation != null) {
				addByType(annotation, f, popup, parentComponent, infos);

			}
		}
		
	}
	private void bulkCreateSlabs(boolean reinitializeOnly) {
		List<ElementInformation> elements = getInfoElementsRecursive(new ObjectArrayList<ElementInformation>());
		for(ElementInformation info : elements){
			if(info.getSlab() == 0 && info.getBlockStyle() == 0 && (!reinitializeOnly || info.slabIds != null)){
				
				
				
				try {
					ElementKeyMap.deleteBlockSlabs(info);
					System.err.println((reinitializeOnly ? "REINITIALIZING " : "CREATING ")+"SLABS FOR: "+info);
					ElementKeyMap.createBlockSlabs(info);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void addByType(final Element annotation, final Field f, final JPopupMenu popup, final Component parentComponent, final List<ElementInformation> infos) {
		try {
			if (annotation.canBulkChange() && (f.getType() == Boolean.TYPE ||
					f.getType() == Float.TYPE ||
					f.getType() == Long.TYPE ||
					f.getType() == Short.TYPE ||
					f.getType() == Integer.TYPE ||
					f.getType() == Byte.TYPE
					|| f.getType() == Double.TYPE)) {
				JMenuItem jMenuItem = new JMenuItem("Bulk Set " + annotation.tag());

				jMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						String s = (String) JOptionPane.showInputDialog(
								parentComponent,
								"Enter new value",
								"Bulk Set " + annotation.tag(),
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
				popup.add(jMenuItem);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public ElementCategory getParent() {
		return parent;
	}

}
