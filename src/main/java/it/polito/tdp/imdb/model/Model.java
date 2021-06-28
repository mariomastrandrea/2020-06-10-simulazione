package it.polito.tdp.imdb.model;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model 
{
	private final ImdbDAO dao;
	
	
	public Model()
	{
		this.dao = new ImdbDAO();
	}
}
