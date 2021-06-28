package it.polito.tdp.imdb.simulation;


public class ProducerDayEvent implements Comparable<ProducerDayEvent>
{
	public enum EventType
	{
		START_INTERVIEW,
		ORDINARY_INTERVIEW,
		BREAK
	}
	
	private int dayNum;
	private EventType type;
	
	
	public ProducerDayEvent(int dayNum)
	{
		this.dayNum = dayNum;
		this.type = null;
	}

	public EventType getType()
	{
		return this.type;
	}

	public void setType(EventType type) 
	{
		this.type = type;
	}

	public int getDayNum()
	{
		return this.dayNum;
	}

	@Override
	public int compareTo(ProducerDayEvent other)
	{
		return Integer.compare(this.dayNum, other.dayNum);
	}
	
}
