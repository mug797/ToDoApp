package com.example.mkhade.todoapp;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditItemActivity extends AppCompatActivity {

    public static final String TAG = "EditActivity";
    String orig_name;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        orig_name = getIntent().getStringExtra("item_name");
        position = getIntent().getIntExtra("position", -1);
        Log.wtf(TAG, orig_name);

        EditText et = (EditText) findViewById(R.id.edText);
        et.setText(orig_name);
        et.setSelection(et.getText().length());
    }

    public void onSave(View v) {
        EditText etName = (EditText) findViewById(R.id.edText);
        Intent intent = new Intent(EditItemActivity.this, MainActivity.class);
        intent.putExtra("edited_name", etName.getText().toString());
        intent.putExtra("original_name", orig_name);
        intent.putExtra("position", position);

        setResult(RESULT_OK, intent);
        finish();
    }
}
