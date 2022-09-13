package com.example.to_do;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    BottomAppBar bottomAppBar;
    FloatingActionButton fab;
    ListView lv;
    List<String> list;
    CoordinatorLayout coordinatorLayout;
    ArrayAdapter arrayAdapter;
    String note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);


        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        lv = findViewById(R.id.lv);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, list);
        lv.setAdapter(arrayAdapter);
        deleteItem();
        loadItem();


    }

    @Override
    public void onClick(View v) {

        if(v == fab){
            createNoteDialog();
        }

    }

    public void deleteItem(){

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("delete this note?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lv.setItemChecked(position, false);
                        arrayAdapter.remove(lv.getItemAtPosition(position));
                        String str = lv.getItemAtPosition(position).toString();
                        list.remove(str);
                        }
                    });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lv.setItemChecked(position, false);
                    }
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                builder.show();
            }

        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();
        switch(id){
            case R.id.miHome:

                return  true;
            case R.id.miSearch:

                return true;
        }
        return false;
    }

    public void createNoteDialog(){
        AlertDialog.Builder noteDialog = new AlertDialog.Builder(MainActivity.this);
        EditText etDialog = new EditText(MainActivity.this);
        noteDialog.setView(etDialog);
        etDialog.setBackground(null);
        etDialog.setHint("Write your note");
        noteDialog.setCancelable(true);
        noteDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(etDialog.getText() != null) {
                    note = etDialog.getText().toString();
                    list.add(note);
                    saveItems();
                }
                else Toast.makeText(MainActivity.this, "Please name the note", Toast.LENGTH_SHORT).show();
            }
        });

        noteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        noteDialog.show();
    }
    public void saveItems(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : list){
            stringBuilder.append(s);
            stringBuilder.append(",");
        }
        SharedPreferences itemList = getSharedPreferences("PREFS", 0);
        SharedPreferences.Editor itemListEditor = itemList.edit();
        itemListEditor.clear();
        itemListEditor.putString("list", stringBuilder.toString());
        itemListEditor.commit();
    }
    public void loadItem(){
        SharedPreferences itemList = getSharedPreferences("PREFS", 0);
        String listString = itemList.getString("list", "");
        String[] arrWords = listString.split(",");
        for(int i = 0 ; i < arrWords.length ; i++)
            list.add(arrWords[i]);

    }


}
