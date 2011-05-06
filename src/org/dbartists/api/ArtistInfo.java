package org.dbartists.api;

public class ArtistInfo {
	
	// artist company
	private String company;
	
	// artist member
	private String member;
	
	// artist name
	private String name;
	
	// artist site genre
	private String genre;
	
	public ArtistInfo(String name, String genre, String member, String company) {
		
		this.name = name;
		this.genre = genre;
		this.company = company;
		this.member = member;
	}
	
	public ArtistInfo() {
		this.name = "";
		this.genre = "";
		this.company = "";
		this.member = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	
}
