package com.berat.artbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {
ImageView imageView;
EditText EserEditText;
EditText SanatciEditText;
EditText TarihEditText;
Button saveButton;
Bitmap selectedImage;
SQLiteDatabase  database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
imageView =findViewById(R.id.imageView);
SanatciEditText=findViewById(R.id.SanatciEditText);
        EserEditText=findViewById(R.id.EserEditText);
database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        TarihEditText=findViewById(R.id.TarihEditText);
        saveButton=findViewById(R.id.saveButton);
Intent intent=getIntent();
String info=intent.getStringExtra("info");
if ((info.matches("new"))){
SanatciEditText.setText("");
EserEditText.setText("");
TarihEditText.setText("");
saveButton.setVisibility(View.VISIBLE);
Bitmap selectImage= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher_background);
imageView.setImageBitmap(selectImage);
}
else{
    int artId=intent.getIntExtra("artId",1);
    saveButton.setVisibility(View.INVISIBLE);
try {
    Cursor cursor=database.rawQuery("SELECT * FROM ARTS WHERE id=?",new String[] {String.valueOf(artId)});
    int nameIx=cursor.getColumnIndex("artname");
    int idIx=cursor.getColumnIndex("id");
    int painterIx=cursor.getColumnIndex("paintername");
    int yearIx=cursor.getColumnIndex("year");
    int imageIx=cursor.getColumnIndex("image");
    while(cursor.moveToNext()){
        SanatciEditText.setText(cursor.getString(painterIx));
        EserEditText.setText(cursor.getString(nameIx));
        TarihEditText.setText(cursor.getString(yearIx));
        byte[] bytes=cursor.getBlob(imageIx);
        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
imageView.setImageBitmap(bitmap);

    }

}catch (Exception e){

}

}

    }
    //foto isaretine tikladigi an kullanicidan izin almak icin
    public void selectImage(View view){
        //eger onceden izin alinmamisssa kullanicidan izin alicaz galeriye erismek icin
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
else{
    //eger izin onceden verilmis ise bu kisim calisir ve direkt galeriye gider

            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//galeriye goturur
     startActivityForResult(intentToGallery,2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//galeriye goturur
                startActivityForResult(intentToGallery, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if  (requestCode == 2 && resultCode == RESULT_OK && data !=null) {


            Uri imageData=data.getData();
            try {
                if (Build.VERSION.SDK_INT>=28){}
                else{}
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                selectedImage = ImageDecoder.decodeBitmap(source);
                imageView.setImageBitmap(selectedImage);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }


//ilk once girilen fotoyu sqlLiteye kayit etmden once boyutunu ufaltmak icin fonksiyon
    public Bitmap SmallPhoto(Bitmap image,int size){
        int height=image.getHeight();
        int width=image.getWidth();
    float ImageSize=(float) height/(float) width;
    if(ImageSize>1){
    //bu duruma gore uzunluk genislikten fazladir
    height=size;
    width=(int)(height/ImageSize);

    }
    else{
        width=size;
        height=(int)(ImageSize*width);
    }


       return Bitmap.createScaledBitmap(image,width,height,true);

    }
    public void selectButton(View view){

        String EserText=EserEditText.getText().toString();
        String SanatciText =SanatciEditText.getText().toString();
        String TarihText =TarihEditText.getText().toString();
        Bitmap SmallImage=SmallPhoto(selectedImage,300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        SmallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
byte[] byteArray=outputStream.toByteArray();
//once datayi yarattik tablomuzu ve elemanlarimizi daha sonra degerler disaridan gelecegi icin soru isareteyiyle gosterdik ve yukarida bizim tanimladigimiz degerleri
//bind metoduyla birbirine baglamis olduk
try {
         database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB)");
String sqlString="INSERT INTO arts(artname, paintername,year,image)VALUES(? ,? ,? ,?)";
        SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
        sqLiteStatement.bindString(1,EserText);
        sqLiteStatement.bindString(2,SanatciText);
        sqLiteStatement.bindString(3,TarihText);
        sqLiteStatement.bindBlob(4,byteArray);
sqLiteStatement.execute();




    }catch (Exception e){}

finish();



    }
}