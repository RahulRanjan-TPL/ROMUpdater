package com.tplab.romupdater;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ROMUpdaterActivity extends ActionBarActivity {

    private ListView listView;

    private ArrayList<String> zipFilePath;

    private String selectedRomPath ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_romupdater);
        listView = (ListView) findViewById(R.id.listview);
        scanStorage();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected ROM is =>" + zipFilePath.get(position), Toast.LENGTH_SHORT).show();
                selectedRomPath =zipFilePath.get(position);
            }
        });
    }

    private void scanStorage(){
        List<StorageUtils.StorageInfo> storageInfos= StorageUtils.getStorageList();
        if(storageInfos!=null) {
            zipFilePath = new ArrayList<>();
            for (int i=0 ; i<storageInfos.size();i++) {
                zipFilePath.addAll(new Utils().getFilePaths(this,storageInfos.get(i).path));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,zipFilePath);
            listView.setAdapter(adapter);
        }
    }

    public void scanBtnClick(View v){
        scanStorage();
    }

    public void updateBtnClick(View v){
        if(selectedRomPath!=null && !TextUtils.isEmpty(selectedRomPath)){
            new UpdateTask().execute(selectedRomPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_romupdater, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
         if (id == R.id.action_aboutus) {
             showAbout();
        }
        return super.onOptionsItemSelected(item);
    }

    class UpdateTask extends AsyncTask<String, Void, String> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ROMUpdaterActivity.this);
            pd.setMessage("loading");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // Do your request
            try {
                new Utils().copy(new File(params[0]));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            if(s!=null){
                installROM();
            }
        }
    }

    private void installROM(){
        try {
            RecoverySystem.installPackage(this,new File(Utils.INTERNAL_ROM_PATH));
            rebootBOX();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rebootBOX() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            pm.reboot(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.aboutus, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }
}
