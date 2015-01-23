/*package com.pp.couchdblib;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pp.couchdblib.couchbase.lite.CouchbaseLiteException;
import com.pp.couchdblib.couchbase.lite.Database;
import com.pp.couchdblib.couchbase.lite.Document;
import com.pp.couchdblib.couchbase.lite.Emitter;
import com.pp.couchdblib.couchbase.lite.LiveQuery;
import com.pp.couchdblib.couchbase.lite.Manager;
import com.pp.couchdblib.couchbase.lite.Mapper;
import com.pp.couchdblib.couchbase.lite.Query;
import com.pp.couchdblib.couchbase.lite.Query.QueryCompleteListener;
import com.pp.couchdblib.couchbase.lite.QueryEnumerator;
import com.pp.couchdblib.couchbase.lite.QueryRow;
import com.pp.couchdblib.couchbase.lite.replicator.Replication;
import com.pp.couchdblib.couchbase.lite.replicator.Replication.ChangeEvent;
import com.pp.couchdblib.couchbase.lite.replicator.Replication.ChangeListener;
import com.pp.couchdblib.couchbase.lite.support.CouchbaseLiteApplication;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements ChangeListener {
    public static String TAG = "DarioCouchDB";

    //constants
    public static final String DATABASE_NAME = "dario-db";
    public static final String designDocName = "dario-local";
    public static final String byDateViewName = "ctext";

	private Database database;
	private Manager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button b=(Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				addtoDB();				
			}
		});
		Button b2=(Button) findViewById(R.id.button_start_replication);
		b2.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				startSync();
			}
		});
		Button b3=(Button) findViewById(R.id.button_query);
		b3.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				startQuery();
			}
		});
//		01-16 13:51:52.191: E/Couchdb:(16609): 
		//sql exception:android.database.sqlite.SQLiteException: 
		//no such collation sequence: JSON (code 1): , 
		
		//while compiling: CREATE TABLE maps (         
		//view_id INTEGER NOT NULL REFERENCES views(view_id) ON DELETE CASCADE,         
		//sequence INTEGER NOT NULL REFERENCES revs(sequence) ON DELETE CASCADE,
		//key TEXT NOT NULL COLLATE JSON,         value TEXT)
		try {
			loge("cbr initx");
			manager=new Manager(getApplicationContext().getFilesDir(), Manager.DEFAULT_OPTIONS);
			loge("cbr get db");
			database = manager.getDatabase(DATABASE_NAME);
			if(database==null){
				loge("NO DATABASE.panic)");
			}
			database.open();
			//CouchbaseLiteApplication application=(CouchbaseLiteApplication)getApplication();
			//application.setManager(manager);
			loge("cbr get db size:"+database.getDocumentCount());
		} catch (IOException e) {
			e.printStackTrace();
			loge(e.getMessage());
			return;
		}
		
		
        com.pp.couchdblib.couchbase.lite.View viewItemsByDate = database.getView(String.format("%s/%s", designDocName, byDateViewName));
        boolean result =viewItemsByDate.setMap(new Mapper() {
			@Override
			public void map(Map<String, Object> document, Emitter emitter) {
                Object createdAt = document.get("text");
                Object name = document.get("name");
                if(name!=null && name.toString().equals("VasiliyPetrov") && createdAt!=null){
                	emitter.emit(createdAt.toString(), document);
                }
			}
        }, "1.31");
        
        com.pp.couchdblib.couchbase.lite.View viewItemsByDate2 = database.getView(String.format("view/id-finder", designDocName));
//        result =viewItemsByDate2.setMap(new Mapper() {
//			@Override
//			public void map(Map<String, Object> document, Emitter emitter) {
//                Object id = document.get("_id");
//                if(id!=null && id.toString().equals("9c6282a2-2ba3-4a6d-95dd-f4e8429260dc")){
//                	emitter.emit(id, document);
//                }
//			}
//        }, "1.06");
        
        
        Log.e("cdb","result add map:"+result);
//        startLiveQuery(viewItemsByDate2);
	}
	

	private void startQuery() {
    //    properties.put("ctext", "zcustom text");
  //      properties.put("modifiedAt", Calendar.getInstance());
//        properties.put("name", "VasiliyPetrov");
		final String param="ctext";
		
		String DarioCDBObjectView_ByAuthor = "view/authornfinder2";
		com.pp.couchdblib.couchbase.lite.View viewByAuthorTypeKey = database.getView(DarioCDBObjectView_ByAuthor);
		viewByAuthorTypeKey.setMap(new Mapper() {
		    @Override
		    public void map(Map<String, Object> document, Emitter emitter) {
			String ctext = (String) document.get("ctext");
			Log.e("Dario","ctext: "+ctext+", "+param);
			if (ctext.equals(param)) {
			    emitter.emit(ctext, document);
				}
		    }
		}, "1.01");
		try {
			QueryEnumerator qe=viewByAuthorTypeKey.createQuery().run();
			Log.e("Dario","query result: "+qe.getCount());
		} catch (CouchbaseLiteException e) {
			Log.e("Dario","query exception "+e.getMessage());
			return;
		}
		
	}
	
	
	
	private LiveQuery liveQuery;
	private Query staticQuery;

	private Replication mPullReplication;
	private Replication mPushReplication;
	private void startLiveQuery(
			com.pp.couchdblib.couchbase.lite.View view) {
		Log.e("", "couchdblog init query");
		try {
			view.updateIndex();
		} catch (CouchbaseLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		staticQuery=view.createQuery();
		staticQuery.runAsync(new QueryCompleteListener() {
			@Override
			public void completed(QueryEnumerator rows, Throwable error) {
				if(error!=null){
					Log.e("", "couchdblog error :"+error.getMessage());	
				}	
				QueryRow row=rows.getRow(0);
				HashMap doc = (HashMap) row.getValue();
				Log.e("", "couchdblog:"+rows.getCount());
	//{id=9c6282a2-2ba3-4a6d-95dd-f4e8429260dc, 
//value={created_at=2014-01-20T14:37:03.892Z, temprehdfhgdhgfhrty55345435___ated_at=some time stringfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff, text=bzzaacustom text, check=false, ctext=zzaacustom text, name=VasiliyPetrov, _rev=1-68077096-41be-4f65-8fc0-0a167ceb1da1, _id=9c6282a2-2ba3-4a6d-95dd-f4e8429260dc}, key=bzzaacustom text}			
				//modifiedAt=1390224668449
			}
		});
        //final ProgressDialog progressDialog = showLoadingSpinner();
if(true)
	return;
        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();
            

            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(LiveQuery.ChangeEvent event) {
                	Log.e("", "couchdblog:"+event.getRows().getCount());
                	List<QueryRow> array=getRowsFromQueryEnumerator(event.getRows());
                	Log.e("", "couchdblog:"+array.size());
                	Log.e("", "couchdblog:"+array.size());
                }
            });
            liveQuery.start();
        }		
	}
    private void startSync() {
        URL syncUrl;
        try {
            syncUrl = new URL("http://192.168.112.135:5984/dariodb");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        mPullReplication = database.createPullReplication(syncUrl);
        mPullReplication.setContinuous(true);

        mPushReplication = database.createPushReplication(syncUrl);
        mPushReplication.setContinuous(true);

        mPullReplication.start();
        mPushReplication.start();

        mPullReplication.addChangeListener(this);
        mPushReplication.addChangeListener(this);
    }
    public void stopReplication(){
	if(mPullReplication!=null){
	    mPullReplication.stop();
	    mPullReplication=null;
	}
	if(mPushReplication!=null){	    
	    mPushReplication.stop();
	    mPushReplication=null;
	}
    }	
	private List<QueryRow> getRowsFromQueryEnumerator(QueryEnumerator queryEnumerator) {
        List<QueryRow> rows = new ArrayList<QueryRow>();
        for (Iterator<QueryRow> it = queryEnumerator; it.hasNext();) {
            QueryRow row = it.next();
            rows.add(row);
        }
        return rows;
    }	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		database.close();
		manager.close();
		
	}
	public void addtoDB(){		
		//loge("cbr qry count:"+database.exists());
		//loge("cbr doccount:"+database.getDocumentCount());
		loge("cbr add");
		// 500 elements 14.2 with transaction on each element
		// 				7.1 with one whole transaction
		// 2 elements in release with each transaction 200 msec
		
		//database.beginTransaction();
		//database.setTransactionsEnabled(false);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		for(int i=0;i<2;i++){
		Document doc=database.createDocument("112:aa");
		Map<String, Object> properties = new HashMap<String, Object>();
        Calendar calendar = GregorianCalendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        //properties.put("_rev", "11111111111");
        properties.put("text", "custom text");
        //properties.put("ctext", "zcustom text");
        //properties.put("modifiedAt", Calendar.getInstance());
        properties.put("name", "no nam");
        //properties.put("check", Boolean.FALSE);
        //properties.put("created_at", currentTimeString);
        //properties.put("temprehdfhgdhgfhrty55345435___ated_at", "teststr");
        
        try {
			doc.putProperties(properties);
			loge("doc writed ok "+doc.getProperties().toString());
			
		} catch (CouchbaseLiteException e) {
			e.printStackTrace();
			loge("cbexception:"+e.getMessage());
		}
		}
		
		//database.endTransaction(true);
		//database.setTransactionsEnabled(true);
		
		//Document doc=new Document(database, "lsdoc");
		//database.insertDocumentID(doc.getId());
		
		loge("cbr close");
        //database.close();
		loge("cbr done");

	}
	public void loge(String log){
		Log.e(TAG,log);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void changed(ChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
*/