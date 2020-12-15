package com.berat.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
ListView listView;
ArrayList<String> nameArray;
ArrayList<Integer> idArray;
ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    nameArray=new ArrayList<String>();
    idArray=new ArrayList<Integer>();
        listView=findViewById(R.id.listView);
         arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,nameArray);
listView.setAdapter(arrayAdapter);
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    //nereye tiklandigini
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//tiklaninca nolcak
        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
        intent.putExtra("artId",idArray.get(position));
        intent.putExtra("info","old");
    startActivity(intent);
    }
});
         getData();
    }
public void  getData(){
try {
    SQLiteDatabase database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
    Cursor cursor= database.rawQuery("SELECT * FROM arts",null);

    int nameIx=cursor.getColumnIndex("artname");
    int idIx=cursor.getColumnIndex("id");

    while (cursor.moveToNext()){
        nameArray.add(cursor.getString(nameIx));
        idArray.add(cursor.getInt(idIx));
        listView.setAdapter(arrayAdapter);

    }
    arrayAdapter.notifyDataSetChanged();
    cursor.close();

}catch (Exception e){e.printStackTrace();}



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //xml dosyasini cagirmak icin inflater kullanirlir

        MenuInflater menuInflater=getMenuInflater ();
        menuInflater.inflate(R.menu.add_art,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.add_art_item){
            Intent intent=new Intent(MainActivity.this,MainActivity2.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}