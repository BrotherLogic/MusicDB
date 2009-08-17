package uk.co.brotherlogic.mdb.format;

/**
 * Class to deal with getting formats
 * @author Simon Tucker
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import uk.co.brotherlogic.mdb.Category;
import uk.co.brotherlogic.mdb.GetCategories;
import uk.co.brotherlogic.mdb.Persistent;

public class GetFormats
{
	// Maps format name to format
	Collection<Format> formats;
	Set<String> baseFormats;
	Persistent p;

	private static GetFormats singleton;

	private GetFormats() throws SQLException
	{
		// Set the required parameters
		p = Persistent.create();
		formats = new LinkedList<Format>();
	}

	private void fillAll() throws SQLException
	{
		// Get a statement and run the query
		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT FormatName,FormatNumber,baseformat  FROM Formats");

		baseFormats = new TreeSet<String>();

		// Fill the set
		while (rs.next())
		{
			// Construct the new format
			Format temp = new Format(rs.getInt(2), rs.getString(1), rs
					.getString(3));

			// Now add the corresponding categories
			temp.setCategories(getCategories(temp.getNumber()));

			baseFormats.add(temp.getBaseFormat());
			formats.add(temp);
		}

		// Close the database objects
		rs.close();
		s.close();
	}

	public Collection<String> getBaseFormats()
	{
		if (baseFormats == null)
			try
			{
				fillAll();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

		return baseFormats;
	}

	public Collection<Category> getCategories(int num) throws SQLException
	{
		// Vector to be returned
		Vector<Category> ret = new Vector<Category>();

		Statement s = p.getConnection().getStatement();
		ResultSet rs = s
				.executeQuery("SELECT DISTINCT CategoryName,CategoryNumber FROM Categories, Records WHERE Categories.CategoryNumber = Records.category AND format = "
						+ num);

		while (rs.next())
			ret.add(GetCategories.build().getCategory(rs.getString(1),
					rs.getInt(2)));

		rs.close();
		return ret;
	}

	public Format getFormat(int formatNumber) throws SQLException
	{
		// Get a statement and run the query
		PreparedStatement s = p
				.getConnection()
				.getPreparedStatement(
						"SELECT formatname,baseformat FROM Formats WHERE formatnumber = ?");
		s.setInt(1, formatNumber);
		ResultSet rs = s.executeQuery();

		Format toReturn = null;
		if (rs.next())
			toReturn = new Format(formatNumber, rs.getString(1), rs
					.getString(2));

		rs.close();
		return toReturn;
	}

	public Collection<Format> getFormats()
	{
		try
		{
			if (formats.size() == 0)
				fillAll();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return formats;
	}

	public int save(Format in) throws SQLException
	{
		// Add the new format and commit the update
		PreparedStatement ps = p.getConnection().prepState(
				"INSERT INTO formats (formatname, baseformat) VALUES (?,?)");
		ps.setString(1, in.getName());
		ps.setString(2, in.getBaseFormat());
		ps.execute();

		// Get the new format number
		PreparedStatement ps2 = p.getConnection().prepState(
				"SELECT FormatNumber FROM Formats WHERE FormatName = ?");
		ps2.setString(1, in.getName());
		ResultSet rs = ps2.executeQuery();
		rs.next();

		int val = rs.getInt(1);

		// Close the database objects
		rs.close();

		return val;

	}

	public static GetFormats create() throws SQLException
	{
		if (singleton == null)
			singleton = new GetFormats();
		return singleton;
	}
}
