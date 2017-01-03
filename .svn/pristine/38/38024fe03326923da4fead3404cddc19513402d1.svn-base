/* File:      FloraSession.java
**
** Author(s): Aditi Pandit
**
** Contact:   flora-users@lists.sourceforge.net
** 
** Copyright (C) The Research Foundation of SUNY, 2005-2013.
** 
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**      http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
**
** 
*/

package net.sf.flora2.API;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;

import net.sf.flora2.API.util.FlrException;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.TermModel;

/** This class is a higher level wrapper to call the 
   lower level functions of the PrologFlora class
*/
public class FloraSession extends FloraConstants
{
    public PrologFlora flora;

    /* Constructor function. */
    public FloraSession()
    {
	flora = new PrologFlora();	
	if (debug)
	    System.out.println("Flora-2 session started");
    }
	

    /* Execute a command at the Flora-2 session
    ** True, if the command succeeds
    **
    ** command : the command to be executed
    */
    public boolean ExecuteCommand(String command)
    {
	boolean result = false;
	try {
	    result = flora.simpleFloraCommand(doubleEachQuote(command));
	    if (debug)
		System.out.println("ExecuteCommand: "+command);
	}
	catch (FlrException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new FlrException("Executing Flora-2 command " + command, e);
	}
	return result;
    }
	
    /* Execute a command at the Flora-2 session 
    ** The answer is a resultset that can be queried
    **
    ** query : Flora query to be executed 
    ** vars : Vector of variables to be bound
    */
    public Iterator<HashMap<String, FloraObject>> FindAllMatches(String query,Vector<String> vars)
    {
	Vector<HashMap<String,FloraObject>> retBindings = new Vector<HashMap<String,FloraObject>>();
	Object[] bindings = null;
		
	try {
	    if (debug) {
		System.out.println("FindAllMatches, before FloraCommand. Query = " + query);
		System.out.println("FindAllMatches, before FloraCommand. Vars = " + vars);
	    }
	    bindings = flora.FloraCommand(doubleEachQuote(query),vars);
	    if (debug) {
		System.out.println("FindAllMatches, after FloraCommand: " + bindings);
	    }
	 
	    for (int i=0; i<bindings.length; i++) {
				
		TermModel tm = (TermModel)bindings[i];
		HashMap<String,FloraObject> currBinding = new HashMap<String,FloraObject>();
		for (int j=0; j<vars.size(); j++) {
		    String varValue = vars.elementAt(j);

		    if (debug) {
			System.out.println("FindAllMatches, term model: " + tm);
			System.out.println("FindAllMatches, varValue="+varValue);
		    }

		    TermModel objName = PrologFlora.findValue(tm,varValue);
		    FloraObject obj = new FloraObject(objName,this);
		    currBinding.put(varValue,obj);		
		}		
		retBindings.add(currBinding);
	    }
	}
	catch (FlrException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new FlrException("Executing Flora-2 query " + query, e);
	}

	Iterator<HashMap<String, FloraObject>> iter = retBindings.iterator();
	return iter;
    }

    
    public void close()
    {
	flora.close();
    }

    
    /* Load a Flora-2 file into a module
    ** fileName : file to be loaded 
    ** moduleName : module name to load the file into
    */
    public boolean loadFile(String fileName,String moduleName)
    {
	return flora.loadFile(fileName,moduleName);
    }
    
    
    /* Compile a Flora-2 file for a module
    ** fileName : file to be compiled 
    ** moduleName : module name to compile the file for
    */
    public boolean compileFile(String fileName,String moduleName)
    {
	return flora.compileFile(fileName,moduleName);
    }


    /* Add a Flora-2 file to an existing module
    ** fileName : file to be added 
    ** moduleName : module name to add the file to
    */
    public boolean addFile(String fileName,String moduleName)
    {
	return flora.addFile(fileName,moduleName);
    }


    /* Compile a Flora-2 file for addition to a module
    ** fileName : file to be compiled 
    ** moduleName : module name to compile the file for
    */
    public boolean compileaddFile(String fileName,String moduleName)
    {
	return flora.compileaddFile(fileName,moduleName);
    }
    
    /** Delegates to same method in PrologFlora. 
    @see net.sf.flora2.API.PrologFlora#setLoadProgressHandler(String) */
    public void setLoadProgressHandler(String handler){
    	flora.setLoadProgressHandler(handler);
    }
    
    /** Delegates to same method in PrologFlora. 
    @see net.sf.flora2.API.PrologFlora#setLoadProgressHandler(String,double) */
    public void setLoadProgressHandler(String handler,double period){
    	flora.setLoadProgressHandler(handler,period);
    }
        
    /* Execute a query with variables
    **
    ** query : query to be executed
    ** vars : variables in the query
    */
    public Iterator<HashMap<String,FloraObject>> ExecuteQuery(String query,Vector<String> vars)
    {
	return FindAllMatches(query,vars);
    }

    
    /*
    ** Like ExecuteQuery/2 above, but is used only when a query has
    ** just one variable. This provides a simplified interface, since
    ** no variables need to be passed into ExecuteQuery/1 and the
    ** output is just an iterator, which contains just a list of
    ** bindings for that single variable.
    **
    ** query : query to be executed
    */
    public Iterator<FloraObject> ExecuteQuery(String query)
    {
	Vector<FloraObject> retBindings = new Vector<FloraObject>();
	Object[] bindings = null;
		
	Vector<String> vars = new Vector<String>();
	vars.add("?");
	try {
	    bindings = flora.FloraCommand(doubleEachQuote(query),vars);
			
	    for (int i=0; i<bindings.length; i++) {
		TermModel tm = (TermModel)bindings[i];
		TermModel objName = PrologFlora.findValue(tm,null);
		if (debug) {
		    System.out.println("ExecuteQuery/1, term model: " + tm);
		    System.out.println("ExecuteQuery/1, objName="+objName);
		}
		FloraObject obj = new FloraObject(objName,this);
		retBindings.add(obj);
	    }
	}
	catch(FlrException e) {
	    throw e;
	}
	catch(Exception e) {
	    throw new FlrException("Executing Flora-2 query " + query, e);
	}
	Iterator<FloraObject> iter = retBindings.iterator();
	return iter;
    }	
    
    public PrologEngine getEngine()
    {
    	return flora.engine;
    }

    /* Utility to double each quote */
    private String doubleEachQuote(String str)
    {
	String outstr = "";

	for (int i=0; i<str.length(); i++) {
	    char ch = str.charAt(i);
	    if (ch == '\'')
		outstr += "''";
	    else
		outstr += String.valueOf(ch);
	}
	return outstr;
    }
}
