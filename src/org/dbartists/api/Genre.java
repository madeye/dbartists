package org.dbartists.api;

import java.util.ArrayList;

public class Genre {
	
	// genre name
	private String name;
	
	// genre number
	private int genreNumber;
	
	// artist list
	private ArrayList<Artist> artists;
	
	public Genre(String name, int genreNumber) {
		this.name = name;
		this.genreNumber = genreNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGenreNumber() {
		return genreNumber;
	}

	public void setGenreNumber(int genreNumber) {
		this.genreNumber = genreNumber;
	}

	public ArrayList<Artist> getArtists(int page) {
		return artists;
	}
	
	
}
