/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;


/* Written by: Sarah Kolanowski & Huy Hoang
 * Helper class to access the Contact database
 * Provides functions to add, update, delete, and return contacts from a table
 * Create a constant string for each column name in the table and the database name
 * Creates the table using these strings. The primary key of each Contact is the ID
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static ArrayList<String> idList;
    public static final String DATABASE_NAME = "Contact.db";
    public static final String TABLE_NAME = "contacts";
    public static final String COL_1 = "id";
    public static final String COL_2 = "lastName";
    public static final String COL_3 = "firstName";
    public static final String COL_4 = "phoneNumber";
    public static final String COL_5 = "dob";
    public static final String COL_6 = "dateMet";
    public static final String COL_7 = "address1";
    public static final String COL_8 = "address2";
    public static final String COL_9 = "city";
    public static final String COL_10 = "state";
    public static final String COL_11 = "zipCode";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    /* Written by: Huy Hoang
     * Creates the database for the contact's
     * Each contact will have its own unique ID as their primary key
     * The other columns will contain the rest of their contact information
     * Executes the query to create the table
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2 + " TEXT,"
                + COL_3 + " TEXT,"
                + COL_4 + " INTEGER,"
                + COL_5 + " INTEGER,"
                + COL_6 + " INTEGER,"
                + COL_7 + " TEXT,"
                + COL_8 + " TEXT,"
                + COL_9 + " TEXT,"
                + COL_10 + " TEXT,"
                + COL_11 + " INTEGER"+ ")";
        db.execSQL(query);
    }


    /* Written by: Huy Hoang
     * Adds a new contact to the database or updates an existing contact's information
     * Uses contact information that is passed in to set each Contact value
     * If the user is trying to add a new contact, the insert query is used
     * If the user is trying to update an existing contact, the update query is used
     * Returns true if the contact was added or updated correctly
     */
    public boolean addOrUpdateContact(Contact newContact, boolean newContactBoolean, String id)
    {
        String lastName = newContact.getLastName();
        String firstName = newContact.getFirstName();
        String phoneNumber = newContact.getPhoneNumber();
        String dob = newContact.getDob();
        String dateMet = newContact.getDateMet();
        String address1 = newContact.getAddr1();
        String address2 = newContact.getAddr2();
        String city = newContact.getCity();
        String state = newContact.getState();
        String zipCode = newContact.getZipCode();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, lastName);
        contentValues.put(COL_3, firstName);
        contentValues.put(COL_4, phoneNumber);
        contentValues.put(COL_5, dob);
        contentValues.put(COL_6, dateMet);
        contentValues.put(COL_7, address1);
        contentValues.put(COL_8, address2);
        contentValues.put(COL_9, city);
        contentValues.put(COL_10, state);
        contentValues.put(COL_11, zipCode);
        long result;

        if(!newContactBoolean)
        {
            result = db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        }
        else
        {
            result = db.insert(TABLE_NAME,null, contentValues);
        }

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /* Written by: Huy Hoang
     * Selects all contacts from the table and their data to the ArrayList
     * Creates contacts from reading each row in the table
     * The contacts are sorted before they are added to the ArrayList
     * Each contact will have their information added to the ArrayList of contacts
     * Each ID is added to the ID ArrayList in order to keep track of the sorting order
     */
    public ArrayList<Contact> getAllContactData(String sortOrder)
    {
        idList = new ArrayList<>();
        ArrayList<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY " + COL_2 + " " + sortOrder,null);

        while(res.moveToNext())
        {
            String id = res.getString(0);
            String lastName = res.getString(1);
            String firstName = res.getString(2);
            String phoneNumber = res.getString(3);
            String dob = res.getString(4);
            String dateMet = res.getString(5);
            String address1 = res.getString(6);
            String address2 = res.getString(7);
            String city = res.getString(8);
            String state = res.getString(9);
            String zipCode = res.getString(10);

            Contact newContact = new Contact(lastName, firstName, phoneNumber, dob, dateMet, address1, address2, city, state, zipCode);
            idList.add(id);
            contactList.add(newContact);
        }
        return contactList;
    }


    /* Written by: Sarah Kolanowski & Huy Hoang
     * User wants to import contacts from a text file. We take our old contacts ArrayList and loop
     * through and adds each individual contact. If a contact is not added we return false,
     * otherwise we return true.
     */
    public boolean importContacts(ArrayList<Contact> oldContacts)
    {
        for(int i = 0; i < oldContacts.size(); i++)
        {
            boolean importedContact = addOrUpdateContact(oldContacts.get(i), true, " ");
            if(!importedContact)
            {
                return false;
            }
        }

        return true;
    }


    /* Written by: Sarah Kolanowski
     * When user chooses to reinitialize the database we delete the database, close and return
     * true.
     */
    public boolean deleteDatabase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, null, null);
        db.close();
        return true;
    }


    /* Written by: Huy Hoang
     * Drop older table if exist and create the table again
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    /* Written by: Huy Hoang
     * Deletes a contact in the database
     */
    public Integer deleteData (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }


    /* Written by: Huy Hoang
     * Returns the ArrayList containing the contact id's by their sorted last name order
     */
    public ArrayList<String> getIdList()
    {
        return idList;
    }
}
