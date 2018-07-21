package com.company.app;

import org.apache.commons.cli.*;

import com.company.persistence.DatabaseOperation;
import com.company.persistence.DatabaseOperationImpl;
import com.company.service.EventProcessor;
import com.company.service.Processor;

/**
 * MainApp Class
 * 
 * @author rqiu
 *
 */
public class MainApp {

	public static void main(String[] args) {
		
		Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        options.addOption(input);
        
        Option limit = new Option("l", "limit", true, "duration for a transaction");
        options.addOption(limit);

        Option create = new Option("c", "create", true, "create database schema");
        options.addOption(create);
        
        Option shutdown = new Option("s", "shutdown", true, "shutdown database");
        options.addOption(shutdown);
        
        Option mode = new Option("m", "mode", true, "database mode either file or mem");
        options.addOption(mode);
        
        Option file = new Option("f", "file", true, "database file name");
        options.addOption(file);
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        
        if (args.length == 0) {
        	 System.out.println("Command line options ERROR!");
        	 formatter.printHelp("command-line options", options);
             System.exit(1);	
        }

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
        	System.out.println("Command line options ERROR!");
        	e.printStackTrace();
        	formatter.printHelp("command-line options", options);
            System.exit(1);	
        }
        
        String inputFilePath = cmd.getOptionValue("input");
        String threshold = cmd.getOptionValue("limit"); 
        String createSchema = cmd.getOptionValue("create");
        String dbMode = cmd.getOptionValue("mode");
        String dbFile = cmd.getOptionValue("file");
        String dbShutdown = cmd.getOptionValue("shutdown");
        
        validateDatabaseOperationOptions(cmd, formatter, options);
        
        if(createSchema != null && createSchema.equals("yes")) {
        	createDatabaseSchema(dbMode, dbFile);
        }

        if (inputFilePath != null && threshold != null) {
        	int t = 0;
        	
        	try {
        	    t = Integer.parseInt(threshold);
        	} catch ( NumberFormatException e) {
        		System.out.println("Command line options ERROR!");
        		e.printStackTrace();
        		System.exit(1);
        	}
        	
        	execute(inputFilePath, t, dbMode, dbFile);
        } 
        
        if (dbShutdown != null && dbShutdown.equals("yes")) {
        	shutdownDatabase(dbMode, dbFile);
        }  
	}

	public static int execute(String filePath, int threshold, String dbMode, String dbFile) {		
		Processor ep = new EventProcessor(dbMode, dbFile);
		int numOfAlertEvents = ep.processFile(filePath, threshold);
		return numOfAlertEvents;
	}
	
	public static void createDatabaseSchema(String dbMode, String dbFile) {
		DatabaseOperation dbOp = new DatabaseOperationImpl(dbMode, dbFile);
		dbOp.createDabaseSchema();	
	}
	
	public static void shutdownDatabase(String dbMode, String dbFile) {
		DatabaseOperation dbOp = new DatabaseOperationImpl(dbMode, dbFile);
		dbOp.shutdownDatabase();	
	}
	
	private static void validateDatabaseOperationOptions(CommandLine cmd, HelpFormatter formatter, Options options) {
		String dbMode = cmd.getOptionValue("mode");
		String dbFile = cmd.getOptionValue("file");

		if (dbMode == null || dbFile == null) {
			System.out.println("Command line options ERROR!");
			System.out.println("Database mode or database file can not be NULL!");

			formatter.printHelp("command-line options", options);
			System.exit(1);
		}
	}
}