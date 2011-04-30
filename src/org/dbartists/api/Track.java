package org.dbartists.api;

public class Track {
	
	// track name
	private String name;
	
	// mp3 url
	private String url;
	
	public Track(String name, String url) {
		this.name = name;
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

	
	

}
