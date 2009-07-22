package uk.co.brotherlogic.mdb;

/**
 * Class to represent a full groop with all the lineups
 * @author Simon Tucker
 */

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class Groop implements Comparable<Groop>, Builder<Groop>
{
	// Groop properties
	private String sortName = "";
	private String showName = "";
	private int groopNumber;
	private Collection<LineUp> lineUps = new LinkedList<LineUp>();

	public Groop()
	{

	}

	public Groop(String sortName, String showName)
	{
		this.sortName = sortName;
		this.showName = showName;
		groopNumber = -1;
	}

	public Groop(String sortName, String showName, int num)
	{
		// Set the variables
		this.sortName = sortName;
		this.showName = showName;
		groopNumber = num;
	}

	public Groop(String sortName, String showName, int num,
			Collection<LineUp> lineUps)
	{
		// Set the variables
		this.sortName = sortName;
		this.showName = showName;
		groopNumber = num;
		this.lineUps = new Vector<LineUp>();
		this.lineUps.addAll(lineUps);
	}

	public void addLineUp(LineUp in)
	{
		lineUps.add(in);
	}

	public void addLineUps(Collection<LineUp> lineUpsToAdd)
	{
		lineUps.addAll(lineUpsToAdd);
	}

	@Override
	public Groop build(String name)
	{
		return new Groop(name, name, -1);
	}

	@Override
	public int compareTo(Groop o)
	{
		return -sortName.toLowerCase().compareTo(o.sortName.toLowerCase());
	}

	private void fillLineUp()
	{
		try
		{
			this.lineUps = GetGroops.build().getSingleGroop(this.groopNumber)
					.getLineUps();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public LineUp getLineUp(int in)
	{
		if (lineUps == null)
			fillLineUp();

		LineUp ret = null;
		// Move and iterator to the right point
		boolean found = false;
		Iterator<LineUp> it = lineUps.iterator();
		while (!found && it.hasNext())
		{
			LineUp temp = it.next();
			if (temp.getLineUpNumber() == in)
			{
				ret = temp;
				found = true;
			}
		}

		return ret;
	}

	public Collection<LineUp> getLineUps()
	{
		if (lineUps == null)
			fillLineUp();
		return lineUps;
	}

	public int getNoLineUps()
	{
		return lineUps.size();
	}

	public int getNumber()
	{
		return groopNumber;
	}

	public String getShowName()
	{
		return showName;
	}

	public String getSimpRep()
	{
		return "G" + groopNumber;
	}

	public String getSortName()
	{
		return sortName;
	}

	public void setLineUps(Collection<LineUp> lineUpsIn)
	{
		// Clear and add lineUpsIn
		lineUps.clear();
		lineUps.addAll(lineUpsIn);
	}

	public void setNumber(int in)
	{
		groopNumber = in;
	}

	public void setShowName(String groopIn)
	{
		showName = groopIn;
	}

	// Set methods
	public void setSortName(String groopIn)
	{
		sortName = groopIn;
	}

	public String toString()
	{
		// Simple for now
		String ret = "";
		ret += showName;

		return ret;
	}

}
