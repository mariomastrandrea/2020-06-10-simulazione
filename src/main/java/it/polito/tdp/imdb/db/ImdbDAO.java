package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.ActorsPair;

public class ImdbDAO 
{	
	public List<String> getAllGenres()
	{
		final String sqlQuery = String.format("%s %s %s",
									"SELECT DISTINCT genre",
									"FROM movies_genres",
									"ORDER BY genre ASC");
		
		List<String> allGenres = new ArrayList<>();
		
		try
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			ResultSet queryResult = statement.executeQuery();
			
			while(queryResult.next())
			{
				String genre = queryResult.getString("genre");
				allGenres.add(genre);
			}
			
			queryResult.close();
			statement.close();
			connection.close();
			return allGenres;
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Dao error in getAllGenres()", sqle);
		}
	}

	public Collection<Actor> getAllActorsOf(String selectedGenre, Map<Integer, Actor> actorsIdMap)
	{
		final String sqlQuery = String.format("%s %s %s %s %s %s",
				"SELECT id, first_name, last_name, gender",
				"FROM actors",
				"WHERE id IN (SELECT r.actor_id",
							 "FROM roles r, movies m, movies_genres mg",
							 "WHERE r.movie_id = m.id AND m.id = mg.movie_id",
							 	"AND mg.genre = ?)");

		Collection<Actor> allActors = new HashSet<>();

		try
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, selectedGenre);
			ResultSet queryResult = statement.executeQuery();

			while(queryResult.next())
			{
				int actorId = queryResult.getInt("id");
				
				if(!actorsIdMap.containsKey(actorId))
				{
					Actor newActor = new Actor(actorId, queryResult.getString("first_name"),
							queryResult.getString("last_name"), queryResult.getString("gender"));
					
					actorsIdMap.put(actorId, newActor);
				}
				
				allActors.add(actorsIdMap.get(actorId));
			}

			queryResult.close();
			statement.close();
			connection.close();
			return allActors;
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Dao error in getAllActorsOf()", sqle);
		}
	}

	public Collection<ActorsPair> getAllActorsPairsOf(String selectedGenre, 
										Map<Integer, Actor> actorsIdMap)
	{
		final String sqlQuery = String.format("%s %s %s %s %s %s",
				"SELECT r1.actor_id AS id1, r2.actor_id AS id2, COUNT(DISTINCT m1.id) AS numMovies",
				"FROM roles r1, movies m1, movies_genres mg1, roles r2, movies m2, movies_genres mg2",
				"WHERE r1.movie_id = m1.id AND m1.id = mg1.movie_id AND mg1.genre = ?",
					"AND r2.movie_id = m2.id AND m2.id = mg2.movie_id AND mg2.genre = ?",
					"AND m1.id = m2.id AND r1.actor_id < r2.actor_id",
				"GROUP BY r1.actor_id, r2.actor_id");

		Collection<ActorsPair> actorsPairs = new HashSet<>();

		try
		{
			Connection connection = DBConnect.getConnection();
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, selectedGenre);
			statement.setString(2, selectedGenre);
			ResultSet queryResult = statement.executeQuery();

			while(queryResult.next())
			{
				int actorId1 = queryResult.getInt("id1");
				int actorId2 = queryResult.getInt("id2");
				int numMoviesInCommon =  queryResult.getInt("numMovies");
				
				if(!actorsIdMap.containsKey(actorId1) || 
					!actorsIdMap.containsKey(actorId2))
					throw new RuntimeException("Error: actor id not found in id map");	//for debug
				
				Actor actor1 = actorsIdMap.get(actorId1);
				Actor actor2 = actorsIdMap.get(actorId2);
				
				ActorsPair newPair = new ActorsPair(actor1, actor2, numMoviesInCommon);
				actorsPairs.add(newPair);
			}

			queryResult.close();
			statement.close();
			connection.close();
			
			return actorsPairs;
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			throw new RuntimeException("Dao error in getAllActorsPairsOf()", sqle);
		}
	}
}
