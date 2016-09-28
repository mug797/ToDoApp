package com.example.mkhade.todoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    private final int CODE_GET_EDITED_NAME = 7;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private final int ITEM_ADD = 1;
    private final int ITEM_MODIFIED = 2;
    private final int ITEM_REMOVED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView) findViewById(R.id.lvItems);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void readItems() {
        Set<String> h_set = sharedpreferences.getStringSet("key", null);

        if (h_set != null)
            items = new ArrayList<String>(h_set);
        else
            items = new ArrayList<String>();
    }

    private void writeItems(String newItem, int position, int action){
        itemsAdapter.notifyDataSetChanged();
        if (newItem.equals(""))
            return;

        switch (action) {
            case ITEM_ADD:
                if (!items.contains(newItem))
                    items.add(newItem);
                else {
                    Toast.makeText(this, newItem +" : already present! :-)", Toast.LENGTH_SHORT).show();
                }
                break;
            case ITEM_MODIFIED:
                items.set(position, newItem);
                break;
            case ITEM_REMOVED:
                items.remove(newItem);
                break;
            default:
                Log.e(TAG, "Unrecognised action");
                assert(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Set<String> h_set = new HashSet<String>();
        h_set.addAll(items);
        editor.putStringSet("key", h_set);
        editor.commit();
    }

    public void onAddItem(View v){
        EditText etNewText = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewText.getText().toString();
        etNewText.setText("");
        writeItems(itemText, -1, ITEM_ADD);
        Log.wtf(TAG, ""+Thread.currentThread().getStackTrace()[2].getLineNumber());
    }

    private void setupListViewListener(){
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt = (TextView) parent.getChildAt(position- lvItems.getFirstVisiblePosition());
                writeItems(txt.getText().toString(), position, ITEM_REMOVED);
                Log.wtf(TAG, ""+ txt.getText().toString() + ": " + position + " :"  + Thread.currentThread().getStackTrace()[2].getLineNumber());
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                intent.putExtra("position", position);
                TextView txt = (TextView) parent.getChildAt(position - lvItems.getFirstVisiblePosition());
                intent.putExtra("item_name", txt.getText().toString());
                startActivityForResult(intent, CODE_GET_EDITED_NAME);
            }
        });
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == CODE_GET_EDITED_NAME) {
            String original_item_name = data.getExtras().getString("original_name");
            String edited_item_name = data.getExtras().getString("edited_name");
            int position = data.getExtras().getInt("position");
            Toast.makeText(this, "Modified " + original_item_name + " to " + edited_item_name, Toast.LENGTH_SHORT).show();
            writeItems(edited_item_name, position, ITEM_MODIFIED);
        }
    }

    private void printItemsArray(){
        int pos = 0;
        for (String i:items) {
            Log.wtf(TAG, "Item = " + ++pos + "  : " + i);
        }
    }
}