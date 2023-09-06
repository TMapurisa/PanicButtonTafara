package contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

    public DBhelper(Context context) {
        super(context, "Userdata", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Userdata(id INTEGER primary key autoincrement ,name TEXT , contact TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Userdata");

    }
    public Boolean saveuserdata(String name, String contact){


        SQLiteDatabase sqLiteDatabase =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("contact",contact);
        long result =sqLiteDatabase.insert("Userdata",null,contentValues);
        if(result ==-1){
            return false;
        }
        else  return  true;

    }
    public Cursor getdata(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Userdata",null);

        return cursor;
    }
    public Boolean updatedata(String row_id,String name,String number){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name",name);
        cv.put("contact",number);
        long result = sqLiteDatabase.update("Userdata",cv,"id=?",new String[]{row_id});
        if (result == -1){
           return false;
        }
        else
            return true;

    }
    Boolean deleteOnerow(String row_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long result = sqLiteDatabase.delete("Userdata","id=?",new String[]{row_id});
        if(result == -1){
            return false;
        }
        else return true;
    }
}
