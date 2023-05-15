package tdc.edu.vn.quanlynhansu.database_layer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import tdc.edu.vn.quanlynhansu.R;
import tdc.edu.vn.quanlynhansu.data_models.Person;

public class PersonDatabase extends SQLiteOpenHelper {
    // Database 's properties
    private static String DB_NAME = "persons";
    private static int DB_VERSION = 1;
    private static Activity context;

    ///////////////////////////////////////////////////////////////////////////////////////////
    //// Tables 's Properties
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 1. Person Table
    private String PER_TABLE_NAME = "person";
    private String PER_ID = "_id";
    private String PER_NAME = "name";
    private String PER_DEGREE = "degree";
    private String PER_HOBBIES = "hobbies";

    ///////////////////////////////////////////////////////////////////////////////////////////
    //// Constructors
    ///////////////////////////////////////////////////////////////////////////////////////////
    public PersonDatabase(Activity context) {
        super(context, DB_NAME, null, DB_VERSION);
        PersonDatabase.context = context;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //// Person Database 's Primitives
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 1. Create Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db != null) {
            // SQL Statement
            String sql = "CREATE TABLE " + PER_TABLE_NAME + "("
                    + PER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PER_NAME + " TEXT, "
                    + PER_DEGREE + " TEXT, "
                    + PER_HOBBIES + " TEXT);";
            // Execute the SQL Statement
            db.execSQL(sql);
            //db.close();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO when the databse version changes
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //// Person Database 's APIs
    ///////////////////////////////////////////////////////////////////////////////////////////
    // 1. Save persons to Person Database
    public void savePerson (Person person) {
        SQLiteDatabase database = getWritableDatabase();
        if (database != null) {
            if (!findPerson(person,database)){

                ContentValues values = new ContentValues();
                values.put(PER_NAME, person.getName());
                values.put(PER_DEGREE, person.getDegree());
                values.put(PER_HOBBIES, person.getHobbies());

                database.insert(PER_TABLE_NAME, null, values);
            }
            else {

            }
            database.close();
        }
    }
    // 2. Save list of person
    public void savePersons (ArrayList<Person> people) {
        SQLiteDatabase database = getWritableDatabase();
        long inserted = -1;
        int sum = 0;
        if (database != null) {
            for (Person person: people) {
                if (!findPerson(person,database)){
                    ContentValues values = new ContentValues();
                    values.put(PER_NAME, person.getName());
                    values.put(PER_DEGREE, person.getDegree());
                    values.put(PER_HOBBIES, person.getHobbies());

                    inserted = database.insert(PER_TABLE_NAME, null, values);
                    sum++;
                }
                else {

                }

            }
            if (inserted != -1) {
                // TODO Show dialog to user
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.lblDialogTitle));
                builder.setMessage("The people are "+ sum+ " persons inserted into the database!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
            database.close();
        }
    }
    // 3. Get persons from Person Database
    public void getPersons(ArrayList<Person> people) {
        SQLiteDatabase database = getWritableDatabase();
        if (database != null) {
            String[] selectionColunm = new String[] {PER_ID, PER_NAME, PER_DEGREE, PER_HOBBIES};
            String whereCondition = null;
            String[] whereAgr = null;
            String groupBy = null;
            String having = null;
            Cursor cursor = database.query(PER_TABLE_NAME, selectionColunm, whereCondition, whereAgr, groupBy, having, PER_NAME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    //1. Create new Person object
                    Person person = new Person();
                    //2. Read data from cursor and set to person object
                    String name = cursor.getString(cursor.getColumnIndex(PER_NAME));
                    String degree = cursor.getString(cursor.getColumnIndex(PER_DEGREE));
                    String hobbies = cursor.getString(cursor.getColumnIndex(PER_HOBBIES));
                    person.setName(name);
                    person.setDegree(degree);
                    person.setHobbies(hobbies);
                    //3. Add the new person to the people list
                    people.add(person);
                } while (cursor.moveToNext());
            }
            database.close();
        }
    }
    // 4. Update Database
    public void updatePerson(Person oldPerson, Person newPerson) {
        SQLiteDatabase database = getWritableDatabase();
        if (database != null){
            //value để chứa cái giá trị mới để đưa vào database
            ContentValues values = new ContentValues();
            //Viết câu điều kiện
            String where = PER_NAME + "=? AND " + PER_DEGREE + " =? AND "+PER_HOBBIES +" =?";
            //Chuyền giá trị vô ? giống câu prepareStament.setString(1, person.getName()) hoặc $sql->bind_pagram("sss", name degree,hobbies);
            String[] whereArgs = new String[]{oldPerson.getName(), oldPerson.getDegree(), oldPerson.getHobbies()};

            values.put(PER_NAME, newPerson.getName());
            values.put(PER_DEGREE, newPerson.getDegree());
            values.put(PER_HOBBIES,newPerson.getHobbies());
            int bool_Update = database.update(PER_TABLE_NAME, values,where, whereArgs);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.lblDialogTitle));
            if (bool_Update == 1) {
                builder.setMessage("Update successfull");
            }else {
                builder.setMessage("Update field");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
            database.close();
        }
    }
    // 5. Delete Person
    public void deletePerson (Person person) {
        SQLiteDatabase database = getWritableDatabase();
        if (database != null){

            //Viết câu điều kiện
            String where = PER_NAME + "=? AND " + PER_DEGREE + " =? AND "+PER_HOBBIES +" =?";
            //Chuyền giá trị vô ? giống câu prepareStament.setString(1, person.getName()) hoặc $sql->bind_pagram("sss", name degree,hobbies);
            String[] whereArgs = new String[]{person.getName(), person.getDegree(), person.getHobbies()};
            int bool_delete = database.delete(PER_TABLE_NAME, where, whereArgs);


                // TODO Show dialog to user
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.lblDialogTitle));
            if (bool_delete > 0) {
                builder.setMessage("The people is/are " + bool_delete + " persons deleted");
            }else {
                builder.setMessage("The people are/is no deleted");
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
             public void onClick(DialogInterface dialog, int which) {

            }
            });
            builder.create().show();

            database.close();
        }
    }
    // 6. Find person from database
    private boolean findPerson(Person person, SQLiteDatabase database){
        String[] selectionColunm = new String[] {PER_ID, PER_NAME, PER_DEGREE, PER_HOBBIES};
        String where = PER_NAME +" =? AND "+ PER_DEGREE + " = ? AND " + PER_HOBBIES + " =?";
        String[] whereAgr = new String[]{person.getName(),person.getDegree(),person.getHobbies()};
        String groupBy = null;
        String having = null;
        Cursor cursor = database.query(PER_TABLE_NAME, selectionColunm, where, whereAgr, groupBy, having, PER_NAME + " ASC");
        if (cursor.moveToFirst()) {
           return true;
        }

        return false;
    }
}
