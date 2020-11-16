/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import java.io.File;


/* Written by: Sarah Kolanowski & Huy Hoang
 * MainActivity class for the contact application
 * Contains the ListView that the user will see when the application is loaded
 * Each element in the view will be the contact's last and first name
 * When a contact is clicked, their contact information will be displayed in another activity
 * New contacts can be added to the database by clicking the New Contact button
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener
{
    DatabaseHelper myDb;
    private static Context context;
    ArrayList<String> contactNames;
    ArrayList<Contact> contactInformation;
    ArrayList<String> idList;
    ArrayList<String> fileArray;
    ArrayList<Contact> contactFileArray;
    boolean newContact = false;
    ListView contactListView;
    Sensor accelerometer;
    static boolean ascending = true;
    private static final String TAG = "MainActivity";
    static DatabaseHelper database;
    boolean isImported = false;


    /* Written by: Sarah Kolanowski & Huy Hoang
     * Sets up the view for the main activity
     * Creates a ContactFileHelper object that will create the RandomAccessFile, and sets up the lists
     * ContactNames will be used to store the contact data from the file. This will be used for the listVIew
     * ContactInformation will contain all of the contact information to be displayed in the second activity
     * Setting the listViews clickable method will allow users to select the contact in the list
     * When the user clicks a contact, it will send them to the second activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contactListView = findViewById(R.id.contact_list_view);
        contactListView.setClickable(true);
        MainActivity.context = getApplicationContext();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        SensorManager sensorManager = (SensorManager) getSystemService((context.SENSOR_SERVICE));

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        database = new DatabaseHelper(this);
        contactInformation = new ArrayList<>();
        contactNames = new ArrayList<>();
        idList = new ArrayList<>();

        myDb = new DatabaseHelper(this);
        contactInformation = myDb.getAllContactData(" ");

        // ArrayList for id's in index with their listView positions
        idList = myDb.getIdList();

        final ContactFileHelper contact = new ContactFileHelper();
        contactFileArray = new ArrayList<>();
        fileArray = new ArrayList<>();

        try
        {
            File contactFile = contact.getCacheFile(MainActivity.context);
            fileArray = contact.storeContactsInArray(contactFile, fileArray);
            contactFileArray = contact.populateContactInfoArray(fileArray);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // ArrayList for last, first names in the ListView
        for (Contact name: contactInformation)
        {
            contactNames.add(name.getLastName() + ", " + name.getFirstName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
        contactListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, selectedItem, Toast.LENGTH_SHORT).show();

                Intent cInfo = new Intent(MainActivity.this, ContactInfo.class);
                cInfo.putExtra("position", position);
                cInfo.putExtra("idList", idList);
                cInfo.putExtra("contact_info", contactInformation);
                cInfo.putExtra("last_name_list", contactNames);
                cInfo.putExtra("newContact", newContact);
                startActivity(cInfo);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /* Written by: Sarah Kolanowski
     * If the button in the tool bar "new contact" is selected then we go to the new activity
     * where the user can input the new data for a new contact for their list.
     * For phase 3 we added 2 new buttons that stay in the collapsed view. One for importing
     * any existing contacts we had in our text file and another for re-initializing our database
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //if user hits new contact
        if (id == R.id.new_contact)
        {
            //go to new activity with the new contact form
            newContact = true;
            Intent intent = new Intent(MainActivity.this, ContactInfo.class);
            intent.putExtra("newContact", newContact);
            startActivity(intent);
            return true;
        }
        //user chooses to import old contacts
        else if(id == R.id.import_contacts)
        {
            importContacts();
            idList = myDb.getIdList();
        }
        //if user chooses to re-initialize the database, deleted all contacts and starting from scratch
        else if(id == R.id.reinitialize_contacts)
        {
            reinitializeDB();
        }
        return super.onOptionsItemSelected(item);
    }


    /* Written by: Sarah Kolanowski
     * We check the sensor type (make sure its the accelerometer and if it is we call our
     * accelerometer function and handle the change (sorting of contact) there
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            calculateAcceleration(event);
        }

    }


    /* Written by Sarah Kolanowski & Huy Hoang
     * If user wants to import old contacts from the text file, we check to see if the contacts have
     * already been imported. If not we call our import contact function in our database helper class
     * passing our parsed contact array of the Contact class. From there we get all the contact
     * data in our DB, populate our contactName array and update our list view.
     */
    public void importContacts()
    {
        //if contacts are not already imported we can go through with the import
        if(!isImported)
        {
            contactInformation.clear();
            contactNames.clear();

            //call our import function
            database.importContacts(contactFileArray);
            //get all our contacts, including the import, sorted in ascending order
            contactInformation = myDb.getAllContactData(" ");

            //populate our contact names array
            for (Contact name: contactInformation)
            {
                contactNames.add(name.getLastName() + ", " + name.getFirstName());
            }

            //update our array adapter for our list view
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
            contactListView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Contacts have been imported", Toast.LENGTH_SHORT).show();
            isImported = true; //set isImported to true so user cannot import the same contacts multiple times
        }
        else
        { //contacts are already imported, we do nothing
            Toast.makeText(this, "Contacts have already been imported", Toast.LENGTH_SHORT).show();
        }
    }


    /* Written by: Sarah Kolanowski
     * If user chooses to reinitialize the db we call the delete database function in our database
     * helper class. Set isImported to true so users can import old contacts again if they reset
     * the DB and we finish our current intent and start again so the view refreshes itself.
     */
    public void reinitializeDB()
    {
        if(database.deleteDatabase()){ //call our delete database function
            Toast.makeText(this, "Database has been reinitialized", Toast.LENGTH_SHORT).show();
            isImported = false; //set to false so user can import contacts again in blank database
            Intent intent = getIntent(); //get the current intent
            finish(); //ends the intent and starts new one so the list view refreshes as soon as user chooses to reinitialize
            startActivity(intent);
        }
        else //in case the database cannot be reinitialized the user knows
        {
            Toast.makeText(this, "Database could not be reinitialized", Toast.LENGTH_SHORT).show();
        }
    }


    /* Written by: Sarah Kolanowski & Huy Hoang
     * We take the sensor values of x, y, and z and we calculate the square root of the acceleration
     * If the acceleration calculated is greater than 2 then we sort the contacts in ascending order,
     * anything below 3 is just noise as these values are constantly changing,
     * setting our boolean 'ascending' to true. Then if the device is shaken again since 'ascending'
     * is set to true, the contacts will sort in descending order.
     */
    private void calculateAcceleration(SensorEvent event)
    {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        //if accelerated by a certain amount then do the sort
        float acceleration = ((x * x) + (y * y) + (z * z)) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        //if device accelerated over 3 then perform the sort
        if(acceleration >= 2)
        {
            //notification to user so they know the sort took place
            Toast.makeText(this, "Contacts have been resorted", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onSensorChanged: X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);

            contactNames = sortContact(contactNames);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactNames);
            contactListView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }


    /* Written by: Huy Hoang
     * Sorts the contacts in ascending and descending order
     * Reverse each sorting depending on the original sort
     */
    public ArrayList<String> sortContact(ArrayList<String> contactArrayList)
    {
        if(ascending)
        {
            database.getAllContactData("DESC");
            ascending = false;
        }
        else
        {
            database.getAllContactData("ASC");
            ascending = true;
        }
        return contactArrayList;
    }
}


