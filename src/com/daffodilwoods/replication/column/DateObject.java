/**
 * Copyright (c) 2003 Daffodil Software Ltd all rights reserved.
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 * There are special exceptions to the terms and conditions of the GPL
 * as it is applied to this software. See the GNU General Public License for more details.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.daffodilwoods.replication.column;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.daffodilwoods.replication.xml.*;
import com.daffodilwoods.replication.DBHandler.AbstractDataBaseHandler;

public class DateObject extends AbstractColumnObject
{

    int sqlType;
    AbstractDataBaseHandler abstractDBHandler;

    /**
     * sets the SQL type fro date datatype
     * @param sqlType0
     */
    public DateObject(int sqlType0,AbstractDataBaseHandler abstractDBHandler0)
    {
        sqlType = sqlType0;
        abstractDBHandler=abstractDBHandler0;
    }

    /**
     * set the value for the corresponding datatype i.e date
     * @param pst
     * @param element
     * @param index
     * @throws SQLException
     */
    public void setColumnObject(PreparedStatement pst, XMLElement element,
                                int index) throws SQLException
    {
        String value = element.elementValue;
        setColumnObject(pst, value, index);
    }

    public void setColumnObject(PreparedStatement pst, String value,
                                int index) throws SQLException
    {
        if (value.equalsIgnoreCase("NULL"))
        {
            pst.setNull(index, sqlType);
        }
        else
        {
            boolean isTime = value.indexOf(":") != -1;
            boolean isDate = value.indexOf("-") != -1;
            if (isTime && isDate)
            {
                pst.setTimestamp(index, Timestamp.valueOf(value));
            }
            else if (isTime)
            {
                pst.setTime(index, Time.valueOf(value));
            }
            else if (isDate)
            {
                pst.setDate(index, Date.valueOf(value));
            }
            else
            {
                pst.setObject(index, value);
            }
        }
    }

    //            else if(value.indexOf(" ")==-1){
//            pst.setObject(index, value);
//            } else {
//            String subvalue=value.substring(0, value.indexOf(" "));
    //System.out.println(" Inside DateObject Class In Sset Object Method ="+subvalue.trim());
//            pst.setObject(index, subvalue.trim());
//            }


    /**
     * writes a date values in XML file
     * @param os
     * @param rs
     * @param index
     * @throws IOException
     * @throws SQLException
     */
    public void write(Writer os, ResultSet rs, int index,ArrayList encodedCols,String col) throws
        SQLException, IOException
    {
        try
        {
          os.write( getObject(rs, index).toString() );
//            os.write("<![CDATA[" + getObject(rs, index).toString() + "]]>");
        }
        catch (NullPointerException ex)
        {
            os.write("NULL");
        }
    }

    private void write(Writer os, Object rowValue,ArrayList encodedCols,String col) throws
        SQLException, IOException
    {
        try
        {
            os.write("<![CDATA[" + rowValue.toString() + "]]>");
        }
        catch (NullPointerException ex)
        {
            os.write("NULL");
        }
    }

    /**
     * puts the value for the column aginst the column Name
     * @param os
     * @param rows
     * @param oldResultSet
     * @param index
     * @param modifiedColumns
     * @param columnName
     * @throws SQLException
     * @throws IOException
     */
    public void writeUpdate(Writer os, ResultSet rows,
                            ResultSet oldResultSet
                            , int index, HashMap modifiedColumns,
                            String columnName,ArrayList encodedCols) throws SQLException, IOException
    {
        Object newObject = null;
        try {
          newObject = rows.getObject(index);
        }
        catch (SQLException ex) {
        newObject =  new Date(00,00,00);
        }
        Object oldObject = null;
        try {
           oldObject = oldResultSet.getObject(index);
        }
        catch (SQLException ex1) {
          oldObject =  new Date(00,00,00);
        }

        if (newObject == null)
        {
            write(os, "NULL",encodedCols,columnName);
            if (oldObject != null)
            {
                modifiedColumns.put(columnName, "NULL");
            }
        }
        else
        {
            write(os, newObject,encodedCols,columnName);
            if (oldObject != null)
            {
                if (! (newObject.equals(oldObject)))
                {
                    modifiedColumns.put(columnName, newObject);
                }
            }
            else
            {
                modifiedColumns.put(columnName, newObject);
            }

        }
    }

    /**
     * returns the parsed value of String in date
     * @param value
     * @return
     */
    public Object getObject(String value) throws SQLException
    {
        if (value.equalsIgnoreCase("NULL"))
        {
            return null;
        }
        boolean isTime = value.indexOf(":") != -1;
        boolean isDate = value.indexOf("-") != -1;
        if (isTime && isDate)
        {
            return Timestamp.valueOf(value);
        }
        if (isTime)
        {
            return Time.valueOf(value);
        }
        if (isDate)
        {
            return Date.valueOf(value);
        }
        return value;
    }

//      else if(value.indexOf(" ")==-1)
//      {
//     return Date.valueOf(value);
//      }else {
//System.out.println("DateObject.getObject(value) ==="+value);
//        String subvalue=value.substring(0,value.indexOf(" "));
// System.out.println(" Inside DateObject Class In Sset Object Method ="+subvalue.trim());
//       return Date.valueOf(subvalue.trim());
//      }

//    }

    /**
     * returns the Object corresponding to the index passed from the resultSet
     * @param row
     * @param index
     * @return
     * @throws SQLException
     */

    private Object getObject(ResultSet row, int index) throws SQLException
    {
        try {
          return row.getObject(index);
        }
        catch (SQLException ex) {
          return new Date(00,00,00);
        }
    }
}
