package org.schine.starmade.mediawiki;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.schine.starmade.common.StringTools;
import org.schine.starmade.data.element.ElementCategory;
import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;
import org.schine.starmade.data.element.factory.FactoryResource;
import org.schine.starmade.data.element.factory.RecipeInterface;
import org.schine.starmade.data.element.factory.RecipeProductInterface;


public class MediawikiExport {
	public static Properties properties;
	private Wiki wiki;

	public static void main(String[] args) throws IOException {
		File f = new File("./settings.properties");
		if(!f.exists()){
			System.out.println("Please copy the settingsTemplate.properties to settings.properties and fill in your data");
			return;
		}
		
		properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(f);
		properties.load(fileInputStream);
		
		
		
		ElementKeyMap.initializeData(null);


		MediawikiExport p = new MediawikiExport();
		
		if(args == null || args.length != 2){
			args = new String[]{ properties.getProperty("username"), properties.getProperty("password")};
		}
		p.execute(args);


	}

	public static void printTitle(String title, int lvl, StringBuffer sb) {
		for (int i = 0; i < lvl; i++) {
			sb.append("=");
		}
		sb.append(title);
		for (int i = 0; i < lvl; i++) {
			sb.append("=");
		}
		sb.append("\n");
	}

	public static String printCat(ElementCategory categoryHirarchy, StringBuffer sb, int lvl) {

		printTitle(categoryHirarchy.getCategory(), lvl, sb);

		sb.append("{| class=\"wikitable sortable alternance\"\n");
		sb.append("!ID!!Block Name!!class=\"unsortable\"|Picture\n");
		sb.append("|-\n");

		ArrayList<ElementInformation> s = new ArrayList<ElementInformation>(categoryHirarchy.getInfoElements());

		Collections.sort(s, new Comparator<ElementInformation>() {
			@Override
			public int compare(ElementInformation o1, ElementInformation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		boolean first = true;
		for (ElementInformation info : s) {
			if (!first) {
				sb.append("|-\n");
			} else {
				first = false;
			}
			sb.append("|");
			sb.append(String.valueOf(info.getId()));
			sb.append("||");
			sb.append("[[").append(info.getName()).append("]]");
			sb.append("||");
			sb.append("[[File:").append(info.getName().replaceAll(" ", "_")).append(".png").append("|").append(info.getName()).append("|center|50px]]\n");
		}
		sb.append("|}\n");
		sb.append("\n");
		for (ElementCategory c : categoryHirarchy.getChildren()) {
			printCat(c, sb, lvl + 1);
		}

		return sb.toString();
	}

	public ArrayList<Page> create() {
		ArrayList<Page> p = new ArrayList<MediawikiExport.Page>();
		for (short e : ElementKeyMap.keySet) {
			ElementInformation info = ElementKeyMap.getInfo(e);

			String title = info.getName().replaceAll(" ", "_");

			p.add(new Page(title, info.createWikiStub()));
		}
		return p;
	}
	public String getLink(ElementInformation info){
		return "[["+getLinkWo(info)+"]]";
	}
	public String getLinkOne(ElementInformation info){
		return "["+getLinkWo(info)+"]";
	}
	public String getLinkWo(ElementInformation info){
		return getTitle(info)+"|"+info.getName();
	}
	public String getTitle(ElementInformation info){
		return info.getName().replaceAll(" ", "_").replaceAll("/", "-");
	}
	private void execute(String[] args) {
		try {
			String URL = properties.getProperty("URL");
			System.out.println("Connecting to: "+URL);
			wiki = new Wiki(URL);
			System.out.println("Logging in with username: "+args[0]);
			wiki.login(args[0], args[1]);

//			putIndividualSites();
//
//			putBlockIdSize();

			for(short k : ElementKeyMap.keySet){
				if(ElementKeyMap.getInfo(k).slab == 0){
					uploadImage(ElementKeyMap.getInfo(k));
					uploadInfoBox(ElementKeyMap.getInfo(k));
					uploadProduction(ElementKeyMap.getInfo(k));
					uploadBasicStub(ElementKeyMap.getInfo(k));
				}
			
			}
			System.out.println("Logging out: "+args[0]);
			wiki.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LoginException e) {
			e.printStackTrace();
		}

	}

	

	private void putBlockIdSize() throws IOException, LoginException {

		StringBuffer sb = new StringBuffer();
		String title = "ID_list";

		ElementCategory categoryHirarchy = ElementKeyMap.getCategoryHirarchy();
		String pageText = printCat(categoryHirarchy, sb, 1);

		try {
			wiki.edit(title, pageText, "");
		} catch (FileNotFoundException e) {
			System.err.println("Page does not exit yet: " + title);
			wiki.edit(title, pageText, "");
		}

	}
	private String createTitleSection(ElementInformation info){
		StringBuffer s = new StringBuffer();
		
		
		s.append("{{Stub}}\n");
		s.append("{{infobox block/"+getTitle(info)+"}}\n\n");

		String[] parseDescription = info.parseDescription();
		for(String ss : parseDescription){
			s.append(ss+" ");
		}
		s.append("\n\n");
		s.append("{{production/"+getTitle(info)+"}}\n\n");

		s.append("[[Category:"+info.getType().getCategory()+"]] [[Category:"+info.getType().getParent().getCategory()+"]]\n\n");

		
		return s.toString();
		
	}
	private void uploadInfoBox(ElementInformation info) throws LoginException, IOException{
		String text = createInfoBox(info);
		wiki.edit("Template:Infobox_block/"+getTitle(info), text, "Bot created");
	}
	private void uploadBasicStub(ElementInformation info) throws LoginException, IOException {
		String text = createTitleSection(info);
		wiki.edit(getTitle(info), text, "Bot created");
	}

	private void uploadProduction(ElementInformation info) throws LoginException, IOException{
		String text = createProduction(info);
		wiki.edit("Template:Production/"+getTitle(info), text, "Bot created");
	}
	private String createInfoBox(ElementInformation info){
		StringBuffer s = new StringBuffer();
		
		s.append("{{infobox block");
		s.append("|type="+info.getName());
		s.append("|hp="+info.getMaxHitPoints());
		s.append("|armor="+StringTools.formatPointZero(info.getArmourPercent()*100f)+"%");
		s.append("|ahp="+info.armourHP);
		s.append("|shp="+info.structureHP);
		s.append("|mass="+StringTools.formatPointZeroZero(info.getMass()));
		s.append("|light="+(info.isLightSource() ? info.getLightSourceColor() : "none"));
		s.append("|dv="+info.getId());
		s.append("}}");
		
		return s.toString();
		
	}
	/*
	 * 
	 * ==Production==

{{mfg
| using= FACTORY BLOCK
| it1 = REQUIRED BLOCK#1
| ic1= REQUIRED BLOCK#1 AMOUNT
| it2 = REQUIRED BLOCK#2
| ic2 = REQUIRED BLOCK#2 AMOUNT
| ot1 = CREATED BLOCK
}}

===Resource===
This block is used in the production of:
{| class="wikitable"
!Factory
!colspan="2"|Creates!! Amount used
|-
!rowspan="3"| [[Basic Factory| Basic]]
|-
|[[BLOCK 1]]||[[File:BLOCK 1.png|BLOCK 1|center|50px]] || style="text-align:center" | x1
|-
|[[BLOCK 2]]||[[File:BLOCK 2.png|BLOCK 1|center|50px]] || style="text-align:center" | x1
|-
!rowspan="2"| [[Standard Factory| Standard]]
|-
|[[BLOCK 3]]||[[File:BLOCK 3.png|Lingot Fertikeen|center|50px]] || style="text-align:center" | x5
|-
|}

	 */
	private String createProduction(ElementInformation info){
		StringBuffer s = new StringBuffer();
		
		String f = "none";
		if(ElementKeyMap.isValidType(info.getProducedIn())){
			ElementInformation facInfo = ElementKeyMap.getInfo(info.getProducedIn());
			f = facInfo.getName();
		
		
			s.append("==Production==\n\n");
			s.append("{{mfg\n");
			s.append("|using="+f+"\n");
			
			RecipeInterface r = info.getProductionRecipe();
			
			for(RecipeProductInterface p : r.getRecipeProduct()){
				int i = 1;
				for(FactoryResource res : p.getInputResource()){
					ElementInformation el = ElementKeyMap.getInfo(res.type);
					int count = res.count;
					
					s.append("|it"+i+"="+el.getName()+"\n");
					s.append("|ic"+i+"="+count+"\n");
					
					i++;
				}
				i = 1;
				for(FactoryResource res : p.getOutputResource()){
					ElementInformation el = ElementKeyMap.getInfo(res.type);
					int count = res.count;
					
					s.append("|ot"+i+"="+el.getName()+"\n");
					
					i++;
				}
			}
			s.append("}}\n\n\n");
		}else if(ElementKeyMap.isValidType(info.basicResourceFactory)){
			ElementInformation facInfo = ElementKeyMap.getInfo(info.basicResourceFactory);
			f = facInfo.getName();
		
		
			s.append("==Production==\n\n");
			s.append("{{mfg\n");
			s.append("|using="+f+"\n");
			s.append("|it"+1+"=Raw Resources\n");
			s.append("|ic"+1+"="+1+"\n");
			s.append("|ot"+1+"="+info.getName()+"\n");
			s.append("}}\n\n\n");
		}
		Short2ObjectAVLTreeMap<Short2IntOpenHashMap> m = new Short2ObjectAVLTreeMap<Short2IntOpenHashMap>();
		
		for(short oth : ElementKeyMap.keySet){
			ElementInformation other = ElementKeyMap.getInfo(oth);
			if(ElementKeyMap.isValidType(other.getProducedIn())){
				RecipeInterface pr = other.getProductionRecipe();
				
				for(RecipeProductInterface p : pr.getRecipeProduct()){
					for(FactoryResource res : p.getInputResource()){
						if(res.type == info.getId()){
							Short2IntOpenHashMap lst = m.get(other.getProducedIn());
							if(lst == null){
								lst = new Short2IntOpenHashMap();
								m.put(other.getProducedIn(), lst);
							}
							
							lst.put(oth, res.count);
							break;
						}
					}
				}
			}
		}
		
	
		if(!m.isEmpty()){
		
			s.append("===Resource===\n");
			s.append("This element is used in the production of:\n");
			s.append("{| class=\"wikitable\"\n");
			s.append("!Factory\n");
			s.append("!colspan=\"2\"|Creates!! Amount used\n");
			s.append("|-\n");
			for(Entry<Short, Short2IntOpenHashMap> e : m.entrySet()){
				Short2IntOpenHashMap value = e.getValue();
				s.append("!rowspan=\""+(value.size()+1)+"\"| "+getLink(ElementKeyMap.getInfo(e.getKey()))+"\n");
				s.append("|-\n");
				
				
				for(Entry<Short, Integer> usedFor : value.entrySet()){
					ElementInformation use = ElementKeyMap.getInfo(usedFor.getKey());
					s.append("|"+getLink(use)+"||[[File:"+getTitle(use)+".png|"+use.getName()+"|center|50px]] || style=\"text-align:center\" | x"+usedFor.getValue()+"\n");
					s.append("|-\n");
				}
			}
			s.append("|}\n");
		}
		return s.toString();
		
	}
	private void putIndividualSites() throws IOException, LoginException {
		ArrayList<Page> create = create();
		for (int i = 0; i < create.size(); i++) {
			Page page = create.get(i);
			System.err.println("---------------------: Handling " + (i + 1) + "/" + create.size() + ": " + page.title);
			try {
				String pageText = wiki.getPageText(page.title);

				System.err.println("Handling existing page");

				StringBuffer b = new StringBuffer(pageText);

				int startBlock = b.indexOf("{{infobox block");
				if (startBlock >= 0) {
					int endBlock = b.indexOf("}}", startBlock);

					if (endBlock >= startBlock) {
						b.delete(startBlock, endBlock + 2);
						System.err.println("Removed main block");
					}
				}
//				int startDesc = b.indexOf("==Description==");
//				if(startDesc >= 0){
//					int endDesc = b.indexOf("-----", startDesc);
//
//					if(endDesc >= startDesc){
//						b.delete(startDesc, endDesc+5);
//						System.err.println("Removed description block");
//					}
//				}

				pageText = page.content + b.toString();

				wiki.edit(page.title, pageText, "");

			} catch (FileNotFoundException e) {
				System.err.println("Page does not exit yet: " + page.title);
				wiki.edit(page.title, "{{Stub}}\n" + page.content, "");
			}

			File f = new File("./iconbakery/150x150/" + page.title + ".png");
			if (f.exists()) {
				System.err.println("Uploading file: " + f.getName());
				wiki.upload(f, f.getName(), "", "");
			} else {
				System.err.println("File not found: " + f.getAbsolutePath());
			}
		}
	}
	private void uploadImage(ElementInformation info) throws LoginException, IOException {
		File f = new File("./iconbakery/150x150/" + getTitle(info) + ".png");
		if (f.exists()) {
			System.err.println("Uploading file: " + f.getName());
			wiki.upload(f, f.getName(), "", "");
		} else {
			System.err.println("File not found: " + f.getAbsolutePath());
		}		
	}
	private class Page {
		final String title;
		final String content;

		public Page(String title, String content) {
			super();
			this.title = title;
			this.content = content;
		}

	}

}
