/* File:      PrologFlora.java
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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;

import net.sf.flora2.API.util.FlrException;

import com.declarativa.interprolog.PrologEngine;
import com.declarativa.interprolog.PrologOutputListener;
import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.XSBSubprocessEngine;
import com.declarativa.interprolog.util.IPException;
import com.declarativa.interprolog.util.OutputListener;

/** This class is used to call Flora-2 commands 
    at a low level from JAVA using Interprolog libraries */
public class PrologFlora extends FloraConstants
{
    public static String sFloraRootDir = null;
    PrologEngine engine;
    String commands[];

    //private static Logger logger = Logger.getLogger(PrologFlora.class);

    
    HashMap<Integer,Exception> exceptionStore = new HashMap<Integer,Exception>();
    int numExceptions = 0;
    
    private String loadProgressHandlerPredicate = null;
    private long loadProgressPeriod = 0; // mS
    
    /* Function for setting Initialization commands */
    void initCommandStrings(String FloraRootDir)
    {
	commands = (new String[] {
		"(import '_#flmakesetup'/0 from flora2)",
		String.valueOf(String.valueOf((new StringBuffer("asserta(library_directory('")).append(FloraRootDir).append("'))"))),
		"consult(flora2)",
		"'_#flmakesetup'",
		"consult(flrimportedcalls)",
		"import flora_call_string_command/5 from flrcallflora",
		"import flora_decode_oid_as_atom/2 from flrdecode"
	    });
    }
    

    /* Function to execute the initialization commands */
    void executeInitCommands()
    {
        if(commands == null)
            throw new FlrException("System bug, please report");

	for(int i = 0; i < commands.length; i++) {
	    boolean cmdsuccess = simplePrologCommand(commands[i]);
	    if(!cmdsuccess)
		throw new FlrException("Flora-2 startup failed");
	}
    }


    /*
    ** Function to use load{...} to load Flora-2 file into moduleName
    ** fileName   : name of the file to load
    ** moduleName : name of Flora-2 module in which to load;
    ** beware this is parsed by Prolog, so it may need quoting
    */
    public boolean loadFile(String fileName,String moduleName)
    {
	boolean cmdsuccess = false; 
	String cmd = wrapAsTimedCall("flrutils:flora_load_module_util('"+fileName + "' , '" + moduleName+"', null)");
	try {
	    cmdsuccess = engine.deterministicGoal(cmd);
	    // Don't use command: it is not error-sensitive
	    //cmdsuccess = engine.command(cmd);
	}
	catch(IPException e) {
	    throw new FlrException("Command "+ cmd + " failed", e);
	}
        return cmdsuccess;
    }


    /* Function to use \compile to compile Flora-2 file for moduleName
    ** fileName   : name of the file to compile
    ** moduleName : name of Flora-2 module for which to compile
    */
    public boolean compileFile(String fileName,String moduleName)
    {
	boolean cmdsuccess = false; 
	String cmd = wrapAsTimedCall("'\\compile'('"+fileName + "' >> '" + moduleName+"')");
	try {
	    cmdsuccess = engine.deterministicGoal(cmd);
	    // command is not error-sensitive
	    //cmdsuccess = engine.command(cmd);
	}
	catch(IPException e) {
	    throw new FlrException("Command "+ cmd + " failed", e);
	}
        return cmdsuccess;
    }


    /* Function to use \add to add Flora-2 file to moduleName
    ** fileName   : name of the file to add
    ** moduleName : name of Flora-2 module to which to add
    */
    public boolean addFile(String fileName,String moduleName)
    {
    boolean cmdsuccess = false;
	String cmd = wrapAsTimedCall("flrutils:flora_add_module_dyn('"+fileName + "' , '" + moduleName+"' , null)");
	try {
	    cmdsuccess = engine.deterministicGoal(cmd);
	    //cmdsuccess = engine.command(cmd);
	}
	catch(IPException e) {
	    throw new FlrException("Command "+ cmd + " failed", e);
	}
        return cmdsuccess;
    }

    
    /* Function to use \compileadd to compile Flora-2 file for adding to moduleName
    ** fileName   : name of the file to compile for addition
    ** moduleName : name of Flora-2 module for which to compile-add
    */
    public boolean compileaddFile(String fileName,String moduleName)
    {
	boolean cmdsuccess = false; 
	String cmd = wrapAsTimedCall("'\\compileadd'('"+fileName + "'>>" + moduleName+")");
	try {
	    cmdsuccess = engine.deterministicGoal(cmd);
	    //cmdsuccess = engine.command(cmd);
	}
	catch(IPException e) {
	    throw new FlrException("Command "+ cmd + " failed", e);
	}
        return cmdsuccess;
    }
    
    /** Causes Prolog goal handler(_G) to be called periodically every period seconds during file compilation and loading command goals.
     _G will be bound to the current command. If handler is null, progress will not be reported */
    public void setLoadProgressHandler(String handler,double period){
    	loadProgressHandlerPredicate = handler;
    	loadProgressPeriod = Math.round(period*1000);
    }
        
    public void setLoadProgressHandler(String handler){
    	setLoadProgressHandler(handler,1.0);
    }
        
	private String wrapAsTimedCall(String G) {
		if (loadProgressHandlerPredicate==null) return G;
		else {
		    //logger.info("preparing timed_call to "+G);
		    return "timed_call(( "+
			loadProgressHandlerPredicate+"(("+G+"),"+HEARTBEAT_STAGE_BEGIN+"),("+G+ ")), repeating("+loadProgressPeriod+"), "+
			loadProgressHandlerPredicate+"(("+G+"),"+HEARTBEAT_STAGE_RUN+"), nesting)";
		}
	}
	
    /* Call the flora_call_string_command/5 predicate of Flora-2
    ** Binds Flora-2 variables to the returned values
    ** and returns an array of answers. Each answer is an Interprolog
    ** term model from which variable bindings can be obtained.
    ** (See FindAllMatches and ExecuteQuery/1 for how to do this.)
    **
    ** cmd : Flora query to be executed 
    ** vars : Variables in the Flora query that need to be bound
    */
    public Object[] FloraCommand(String cmd,Vector<String> vars)
    {
    	StringBuffer sb = new StringBuffer();
    	String varsString = "";
   	
    	//add other variables
    	for (int i=0; i<vars.size(); i++) {
    		String floravar = vars.elementAt(i);
    		if (!floravar.startsWith("?"))
    			throw new FlrException("Illegal variable name "
    					+ floravar
    					+ ". Variables passed to ExecuteQuery "
    					+ "must be Flora-2 variables and "
    					+ "start with a `?'");
    		if (i > 0)
    			varsString += ",";
    		if (floravar.equals("?XWamState"))
    			varsString += "'" + "?XWamState" + "'=_XWamState";
    		else
    			varsString += "'" + vars.elementAt(i) + "'=__Var" + i;
    	}

    	//add var to capture exception
		if (!(varsString.equals("")))
			varsString += ",";
    	varsString += "'" + "?Ex" + "'=_Ex";
    	
    	varsString = "[" + varsString + "]";

    	String listString = "L_rnd=" + varsString + ",";
    	//String queryString = "S_rnd='" + cmd + "',";
		// Serialize the command atom instead, to avoid atom escaping complications:
    	String queryString = "S_rnd=Cmd,";
    	String floraQueryString =
    		//"findall(TM_rnd,(flora_call_string_command(S_rnd,L_rnd,_St,_XWamState,_Ex),buildTermModel(L_rnd,TM_rnd)),BL_rnd),ipObjectSpec('ArrayOfObject',BL_rnd,LM)";
    		(loadProgressHandlerPredicate==null?"":loadProgressHandlerPredicate+"(Cmd,"+HEARTBEAT_STAGE_BEGIN+"), ") + // send a first message, so we get some feedback even for fast queries
    		
/*    		"findall(TM_rnd,("+
    			"flora_call_string_command(S_rnd,L_rnd,_St,_XWamState,_Ex)"+
    			",buildInitiallyFlatTermModel(L_rnd,TM_rnd)),BL_rnd),ipObjectSpec('ArrayOfObject',BL_rnd,LM)" + */
			// Use instead a single InitiallyFlatTermModel object for less overhead:
    		"E = exception(normal), findall(L_rnd,(flora_call_string_command(S_rnd,L_rnd,_St,_XWamState,_Ex),  (_Ex\\==normal -> machine:term_set_arg(E,1,_Ex,1) ; true) ),L_rnds), "+
    		"E = exception(Exception), buildTermModel(Exception,ExceptionM), buildInitiallyFlatTermModel(L_rnds,LM)" +
    			
    		(loadProgressHandlerPredicate==null?"":", "+loadProgressHandlerPredicate+"(Cmd,"+HEARTBEAT_STAGE_END+")"); // send last message, so we get some final feedback even for fast queries
    		
    	sb.append(queryString);
    	sb.append(listString);
    	sb.append(floraQueryString);
	
	if (debug)
	    System.out.println("FloraCommand: " + sb);

	try {
	    // Object solutions[] =
		//(Object[])engine.deterministicGoal(sb.toString(), "[LM]")[0];
		Object[] bindings = engine.deterministicGoal(sb.toString(), "[string(Cmd)]",new Object[]{cmd},"[ExceptionM,LM]");
		if (bindings==null)
			throw new IPException("Unexpected problem, probably malformed query");
		TermModel exception = (TermModel)bindings[0];
		TermModel[] solutions = TermModel.flatList((TermModel)bindings[1]);
	    findExceptionCore(exception); 
	    return solutions;
	}
	catch(Exception e) {
	    throw new FlrException("Error in query "+cmd, e);
	    //return null;
	}
    }
    /* A simpler way to call Flora-2 commands that are not queries
    **
    ** cmd : Command to be executed
    */
    //TODO - may want to add a variable to get exception information back?
    //But that would kind of defeat the simplicity advantage...
    public boolean simpleFloraCommand(String cmd)
    {
	String queryString = "S_rnd=Cmd,";
	String listString = "L_rnd=[],";

        StringBuffer sb = new StringBuffer();
	sb.append(queryString);
	sb.append(listString);
        sb.append("flora_call_string_command(S_rnd,L_rnd,_St,_XWamState,_Ex), _Ex == normal"); // make sure success... means it.

	if (debug)
	    System.out.println("simpleFloraCommand: " + sb);

	try {
	    return engine.deterministicGoal(sb.toString(),"[string(Cmd)]",new Object[]{cmd});
	}
	catch(IPException e) {
	    throw new FlrException("Command " + cmd + " failed", e);
	}
    }


    public boolean simplePrologCommand(String cmd)
    {
        try {
	    return engine.deterministicGoal(cmd);
	    //return engine.command(cmd);
	}
        catch(IPException ipe) {
	    throw ipe;
	}
    }

    /**
     ** A simplified version of TermModel.toString() that doesn't put commas
     ** between nested children.
     */
    public String toStringNoCommas(TermModel tm)
    {
	if (tm.getChildCount()==0)
	    return tm.node.toString();
	else if (tm.isList())
	    return tm.toString();
	else if ((tm.node.equals("/") && (tm.children.length == 2)))
	    return "" + tm.children[0] + tm.node + tm.children[1];
	else 
	    {
		StringBuffer retval = new StringBuffer();

		for (int i=0; i < tm.children.length; i++)
		    {
			retval.append(toStringNoCommas(tm.children[i]));
		    }

		return retval.toString();
	    }
    }

    public void findException(Object[] solutions) {
    	//look for exception (?Ex binding) in solution and throw it
    	//An exception will look like error(type-of-error(message,...) ...)
    	TermModel objName = null;
    	if (solutions != null && solutions.length != 0) {
	    TermModel tm = (TermModel)solutions[0];
	    objName = PrologFlora.findValue(tm,"?Ex");
	    //logger.info("findException:  " + objName);
	    findExceptionCore(objName);
	    }
    }

    private void findExceptionCore(TermModel objName) {
	    if (objName == null)
		throw new FlrException("Flora returned no exception info - probably a bug");
	    else if (objName.node instanceof String) {
		if (objName.toString().equals("normal"))
		    {}  //no exception
		else if (objName.node != null && objName.node.toString().equals("builtin_exception")) {
		    Integer id = (Integer) ((TermModel) objName.getChild(0)).node;
		    Exception ex = exceptionStore.remove(id);
		    throw (FlrException) ex;
		} 
		else {
		    if (objName.children == null) {
			// message from XSB throw(message)
			throw new FlrException(objName.node.toString());
		    } else {
			switch (objName.children.length) {
			case 2:
			    // Flora exception
			    throw new FlrException(objName.node,
						   ((TermModel) objName.getChild(0)).node,
						   ((TermModel) objName.getChild(1)).node);
			case 3:
			    // XSB exception - objName.node.equals("error")
			    throw new FlrException(((TermModel) objName.getChild(0)).toString(),
						   // ((TermModel) objName.getChild(1)),
						   toStringNoCommas((TermModel) objName.getChild(1)),
						   ((TermModel) objName.getChild(2)));
			default:
			    throw new FlrException("Flora returned non-standard exception with " + objName.children.length + " children - " + objName);
			}
		    }
		}
	    }
	    else
		throw new FlrException("Flora returned non-standard exception info - probably a bug - " + objName.toString());
    	
	}
	    
    public int storeException(Exception e) {
    	exceptionStore.put(++numExceptions,e);
    	return numExceptions;
    }
    
    public void removeException(int key) {
    	exceptionStore.remove(key);
    }
    
    /* Query the term model structure 
    **
    ** tm : TermModel to be queried 
    ** name : binding variable name to be queried 
    */
    public static TermModel findValue(TermModel tm, String name)
    {
	if (debug)
	    System.out.println("in findValue, Term model: " + tm);

        for( ; tm.isList(); tm = (TermModel)tm.getChild(1)) {
	    TermModel item = (TermModel)tm.getChild(0);
	    
	    if(name == null
	       || (item.getChild(0).toString().compareTo(name) == 0)) {
		TermModel val = (TermModel)item.getChild(1);
		return val;
	    }

	    if (debug)
		System.out.println("name in findValue: " + name);

	}
	return new TermModel();
    }


    /* Initialise the PrologEngine */
    void initEngine()
    {
	String BrologBinDir = System.getProperty("PROLOGDIR");
	String CmdFloraRootDir = System.getProperty("FLORADIR");
	String PrologExecutable = BrologBinDir + File.separator + "xsb";

        if(BrologBinDir == null || BrologBinDir.trim().length() == 0)
            throw new FlrException("Must define PROLOGDIR property");
        
        String FloraRootDir;	
	if (CmdFloraRootDir == null || CmdFloraRootDir.trim().length() == 0) {
	    throw new FlrException("Must define FLORADIR property");
	    }
	else
	    FloraRootDir = CmdFloraRootDir;


	try {
	    String args = "";
	    
	    engine = new XSBSubprocessEngine(systemSpecificFilePath(PrologExecutable) + args, debug);
	    
	}
	catch(Exception e2) {
	    throw new FlrException("InterProlog failed to start the engine",e2);
	}
	if (File.separatorChar=='\\')
	    // Windows hack: to avoid incorrect escaping on the Prolog side
	    FloraRootDir = FloraRootDir.replace('\\','/');
	initCommandStrings(FloraRootDir);
	executeInitCommands();
	//load configuration file containing debugger config
	File file = new File(CmdFloraRootDir+"/java/API/javaAPI/initFloraAPI.P");
	engine.load_dynAbsolute(file);
    }


    /* Constructor function */
    public PrologFlora()
    {
        commands = null;
	initEngine();
	return;
    }
    

    /* Shut down the Prolog Engine */
    public void close()
    {
	// don't exit: let the Java program continue after the shutdown
	engine.shutdown();
    }
    
    public String systemSpecificFilePath(String p){
    	char nonSeparatorChar = 'a';
    	if (File.separator.equals("\\"))
    		nonSeparatorChar = '/';
    	else
    		nonSeparatorChar = '\\';
    			
    	StringBuffer newPath = new
    	StringBuffer(p.length()+10);
    	for(int c=0;c<p.length();c++){
    		char ch = p.charAt(c);
    		if (ch==nonSeparatorChar)
    			newPath.append(File.separatorChar); 
    		//backslashes to forward
    		else newPath.append(ch);
    	}
    	return newPath.toString();
}
    
}
