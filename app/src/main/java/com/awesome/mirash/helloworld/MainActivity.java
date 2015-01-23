//package com.awesome.mirash.helloworld;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//
//import com.couchbase.lite.CouchbaseLiteException;
//import com.couchbase.lite.Database;
//import com.couchbase.lite.Document;
//import com.couchbase.lite.Manager;
//import com.couchbase.lite.android.AndroidContext;
//import com.joanzapata.pdfview.PDFView;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class MainActivity extends Activity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        PDFView pdfView = (PDFView) findViewById(R.id.pdf_view);
//        pdfView.setVisibility(View.VISIBLE);
//
//        final String TAG = "HelloWorld";
//        Log.d(TAG, "Begin Hello World App");
//
//        Manager manager;
//        try {
//            manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
//            Log.d(TAG, "Manager created");
//        } catch (IOException e) {
//            Log.e(TAG, "Cannot create manager object");
//            return;
//        }
//        // create a name for the database and make sure the name is legal
//        String dbname = "hello";
//        if (!Manager.isValidDatabaseName(dbname)) {
//            Log.e(TAG, "Bad database name");
//            return;
//        }
//
//        // create a new database
//        Database database;
//        try {
//            database = manager.getDatabase(dbname);
//            Log.d(TAG, "Database created");
//
//        } catch (CouchbaseLiteException e) {
//            Log.e(TAG, "Cannot get database");
//            return;
//        }
//
//        // get the current date and time
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        Calendar calendar = GregorianCalendar.getInstance();
//        String currentTimeString = dateFormatter.format(calendar.getTime());
//
//        // create an object that contains data for a document
//        Map<String, Object> docContent = new HashMap<String, Object>();
//        docContent.put("message", "Hello Couchbase Lite");
//        docContent.put("creationDate", currentTimeString);
//
//        // display the data for the new document
//        Log.d(TAG, "docContent=" + String.valueOf(docContent));
//
//        // create an empty document
//        Document document = database.createDocument();
//
//        // add content to document and write the document to the database
//        try {
//            document.putProperties(docContent);
//            Log.d(TAG, "Document written to database named " + dbname + " with ID = " + document.getId());
//        } catch (CouchbaseLiteException e) {
//            Log.e(TAG, "Cannot write document to database", e);
//        }
//
//        // save the ID of the new document
//        String docID = document.getId();
//
//        // retrieve the document from the database
//        Document retrievedDocument = database.getDocument(docID);
//
//        // display the retrieved document
//        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
//
//        // update the document
//        Map<String, Object> updatedProperties = new HashMap<String, Object>();
//        updatedProperties.putAll(retrievedDocument.getProperties());
//        updatedProperties.put("message", "We're having a heat wave!");
//        updatedProperties.put("temperature", "95");
//
//        try {
//            retrievedDocument.putProperties(updatedProperties);
//            Log.d(TAG, "updated retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));
//        } catch (CouchbaseLiteException e) {
//            Log.e(TAG, "Cannot update document", e);
//        }
//
//        // delete the document
//        try {
//            retrievedDocument.delete();
//            Log.d(TAG, "Deleted document, deletion status = " + retrievedDocument.isDeleted());
//        } catch (CouchbaseLiteException e) {
//            Log.e(TAG, "Cannot delete document", e);
//        }
//
//        Log.d(TAG, "End Hello World App");
//
//    }
//}
