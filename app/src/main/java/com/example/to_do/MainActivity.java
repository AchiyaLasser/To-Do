package com.example.to_do;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab;
    ListView lv;
    ArrayList<String> list;
    CoordinatorLayout coordinatorLayout;
    ArrayAdapter arrayAdapter;
    String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadItem();
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        lv = findViewById(R.id.lv);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, list);
        lv.setAdapter(arrayAdapter);
        deleteItem();
    }

    @Override
    public void onClick(View v) {
        if(v == fab){
            createNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    public void deleteItem(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String deletedNote = list.get(position);
                lv.setItemChecked(position, false);
                String str = lv.getItemAtPosition(position).toString();
                arrayAdapter.remove(lv.getItemAtPosition(position));
                list.remove(str);
                saveItems();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Note deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.add(position, deletedNote);
                        arrayAdapter.notifyDataSetChanged();
                        saveItems();
                        Snackbar.make(coordinatorLayout, "Note has been recovered", Snackbar.LENGTH_LONG).show();
                    }
                }).setActionTextColor(Color.RED);
                snackbar.show();
            }
        });
    }

    public void createNote(){
        AlertDialog.Builder noteDialog = new AlertDialog.Builder(MainActivity.this);
        EditText etDialog = new EditText(MainActivity.this);
        noteDialog.setView(etDialog);
        etDialog.setBackground(null);
        etDialog.setHint("Write your note");
        noteDialog.setCancelable(true);
        noteDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(etDialog.getText() == null)
                    Toast.makeText(MainActivity.this, "Please name the note", Toast.LENGTH_SHORT).show();
                else{
                    note = etDialog.getText().toString();
                    list.add(note);
                    saveItems();
                }
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
        SharedPreferences itemList = getSharedPreferences("ItemList", 0);
        SharedPreferences.Editor itemListEditor = itemList.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        itemListEditor.putString("items", json);
        itemListEditor.commit();
    }

    public void loadItem(){
        SharedPreferences itemList = getSharedPreferences("ItemList", 0);
        Gson gson = new Gson();
        String json = itemList.getString("items", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        list = gson.fromJson(json, type);

        if(list == null)
            list = new ArrayList<>();
    }
}
