package org.schema.schine.resource;

import java.io.File;
import java.net.URI;


public class FileExt extends File{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileExt(File parent, String child) {
		super(parent, child);
	}

	public FileExt(String parent, String child) {
		super(parent, child);
	}

	public FileExt(String pathname) {
		super(pathname);
	}

	public FileExt(URI uri) {
		super(uri);
	}

	@Override
	public boolean delete() {
		return super.delete();
	}

	@Override
	public void deleteOnExit() {
		super.deleteOnExit();
	}

	@Override
	public boolean renameTo(File dest) {
		return super.renameTo(dest);
	}

}
