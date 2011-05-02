package org.dbartists.api;

import java.util.ArrayList;

public class Artist {
	
	// artist id
	private int id;
	
	// artist name
	private String name;
	
	// artist site url
	private String url;
	
	// artist image
	private String img;
	
	public Artist(int id, String name, String img, String url) {
		this.id = id;
		this.name = name;
		this.img = img;
		this.url = url;
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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
