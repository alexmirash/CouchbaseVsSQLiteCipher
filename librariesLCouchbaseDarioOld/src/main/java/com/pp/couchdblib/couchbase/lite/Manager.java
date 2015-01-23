package com.pp.couchdblib.couchbase.lite;

import com.pp.couchdblib.couchbase.lite.internal.InterfaceAudience;
import com.pp.couchdblib.couchbase.lite.replicator.Replication;
import com.pp.couchdblib.couchbase.lite.support.HttpClientFactory;
import com.pp.couchdblib.couchbase.lite.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Top-level CouchbaseLite object; manages a collection of databases as a CouchDB server does.
 */
public class Manager {

    public static final String VERSION =  "1.0.0-beta";
    public static final String HTTP_ERROR_DOMAIN =  "CBLHTTP";

    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String DATABASE_SUFFIX_OLD = ".touchdb";
    public static final String DATABASE_SUFFIX = ".cblite";
    public static final ManagerOptions DEFAULT_OPTIONS = new ManagerOptions();
    public static final String LEGAL_CHARACTERS = "[^a-z]{1,}[^a-z0-9_$()/+-]*$";

    private ManagerOptions options;
    private File directoryFile;
    private Map<String, Database> databases;
    private List<Replication> replications;
    private ScheduledExecutorService workExecutor;
    private HttpClientFactory defaultHttpClientFactory;

    @InterfaceAudience.Private
    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * Constructor
     * @throws UnsupportedOperationException - not currently supported
     */
    @InterfaceAudience.Public
    public Manager() {
        final String detailMessage = "Parameterless constructor is not a valid API call on Android. " +
                " Pure java version coming soon.";
        throw new UnsupportedOperationException(detailMessage);
    }

    /**
     * Constructor
     *
     * @throws java.lang.SecurityException - Runtime exception that can be thrown by File.mkdirs()
     */
    @InterfaceAudience.Public
    public Manager(File directoryFile, ManagerOptions options) throws IOException {
        this.directoryFile = directoryFile;
        this.options = (options != null) ? options : DEFAULT_OPTIONS;
        this.databases = new HashMap<String, Database>();
        this.replications = new ArrayList<Replication>();

        directoryFile.mkdirs();
        if (!directoryFile.isDirectory()) {
            throw new IOException(String.format("Unable to create directory for: %s", directoryFile));
        }

        upgradeOldDatabaseFiles(directoryFile);
        workExecutor = Executors.newSingleThreadScheduledExecutor();

    }

    /**
     * Returns YES if the given name is a valid database name.
     * (Only the characters in "abcdefghijklmnopqrstuvwxyz0123456789_$()+-/" are allowed.)
     */
    @InterfaceAudience.Public
    public static boolean isValidDatabaseName(String databaseName) {
        if (databaseName.length() > 0 && databaseName.length() < 240 &&
                containsOnlyLegalCharacters(databaseName) &&
                Character.isLowerCase(databaseName.charAt(0))) {
            return true;
        }
        return databaseName.equals(Replication.REPLICATOR_DATABASE_NAME);
    }

    /**
     * Releases all resources used by the Manager instance and closes all its databases.
     */
    @InterfaceAudience.Public
    public void close() {
        Log.i(Database.TAG, "Closing " + this);
        for (Database database : databases.values()) {
            List<Replication> replicators = database.getAllReplications();
            if (replicators != null) {
                for (Replication replicator : replicators) {
                    replicator.stop();
                }
            }
            database.close();
        }
        databases.clear();
        Log.i(Database.TAG, "Closed " + this);
    }


    /**
     * Returns the database with the given name, or creates it if it doesn't exist.
     * Multiple calls with the same name will return the same Database instance.
     */
    @InterfaceAudience.Public
    public Database getDatabase(String name) {
        boolean mustExist = false;
        Database db = getDatabaseWithoutOpening(name, mustExist);
        if (db != null) {
            db.open();
        }
        return db;
    }

    @InterfaceAudience.Public
    public HttpClientFactory getDefaultHttpClientFactory() {
        return defaultHttpClientFactory;
    }

    @InterfaceAudience.Private
    private static boolean containsOnlyLegalCharacters(String databaseName) {
        Pattern p = Pattern.compile("^[abcdefghijklmnopqrstuvwxyz0123456789_$()+-/]+$");
        Matcher matcher = p.matcher(databaseName);
        return matcher.matches();
    }

    @InterfaceAudience.Private
    private void upgradeOldDatabaseFiles(File directory) {
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(DATABASE_SUFFIX_OLD);
            }
        });

        for (File file : files) {
            String oldFilename = file.getName();
            String newFilename = filenameWithNewExtension(oldFilename, DATABASE_SUFFIX_OLD, DATABASE_SUFFIX);
            File newFile = new File(directory, newFilename);
            if (newFile.exists()) {
                String msg = String.format("Cannot rename %s to %s, %s already exists", oldFilename, newFilename, newFilename);
                Log.w(Database.TAG, msg);
                continue;
            }
            boolean ok = file.renameTo(newFile);
            if (!ok) {
                String msg = String.format("Unable to rename %s to %s", oldFilename, newFilename);
                throw new IllegalStateException(msg);
            }
        }
    }

    @InterfaceAudience.Private
    private String filenameWithNewExtension(String oldFilename, String oldExtension, String newExtension) {
        String oldExtensionRegex = String.format("%s$",oldExtension);
        return oldFilename.replaceAll(oldExtensionRegex, newExtension);
    }


    @InterfaceAudience.Private
    Future runAsync(Runnable runnable) {
        return workExecutor.submit(runnable);
    }

    @InterfaceAudience.Private
    private String pathForName(String name) {
        if((name == null) || (name.length() == 0) || Pattern.matches(LEGAL_CHARACTERS, name)) {
            return null;
        }
        name = name.replace('/', ':');
        String result = directoryFile.getPath() + File.separator + name + Manager.DATABASE_SUFFIX;
        return result;
    }


    @InterfaceAudience.Private
    public Database getDatabaseWithoutOpening(String name, boolean mustExist) {
        Database db = databases.get(name);
        if(db == null) {
            if (!isValidDatabaseName(name)) {
                throw new IllegalArgumentException("Invalid database name: " + name);
            }
            String path = pathForName(name);
            if (path == null) {
                return null;
            }
            db = new Database(path, this);
            if (mustExist && !db.exists()) {
                String msg = String.format("mustExist is true and db (%s) does not exist", name);
                Log.w(Database.TAG, msg);
                return null;
            }
            db.setName(name);
            databases.put(name, db);
        }
        return db;
    }



    @InterfaceAudience.Private
    public ScheduledExecutorService getWorkExecutor() {
        return workExecutor;
    }


}

