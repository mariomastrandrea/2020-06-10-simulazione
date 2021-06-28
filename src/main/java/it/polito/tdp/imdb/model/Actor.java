package it.polito.tdp.imdb.model;

public class Actor
{
	private Integer id;
	private String firstName;
	private String lastName;
	private String gender;
	
	
	public Actor(Integer id, String firstName, String lastName, String gender) 
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
	}
	
	public Integer getId() 
	{
		return this.id;
	}

	public String getFirstName() 
	{
		return this.firstName;
	}

	public String getLastName() 
	{
		return this.lastName;
	}

	public String getGender() 
	{
		return this.gender;
	}

	@Override
	public String toString() {
		return this.lastName + ", " + this.firstName + " (" + this.id + ")";
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}
}
