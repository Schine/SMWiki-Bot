package org.schine.starmade.mediawiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import org.schine.starmade.data.element.ElementCategory;
import org.schine.starmade.data.element.ElementInformation;
import org.schine.starmade.data.element.ElementKeyMap;


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
