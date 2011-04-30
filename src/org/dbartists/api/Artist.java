package org.dbartists.api;

import java.util.ArrayList;

public class Artist {
	
	// artist name
	private String name;
	
	// artist site url
	private String url;
	
	// track list
	private ArrayList<Track> tracks;
	
	public Artist(String name, String url) {
		this.name = name;
		this.url = url;
	}
	
	// TODO: Implement this
	public void loadTracks() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
