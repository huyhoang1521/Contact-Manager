/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ContactInfo extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    TextView fName;
    TextView lName;
    TextView pNum;
    TextView DOB;
    TextView dMet;
    TextView addr1;
    TextView addr2;
    TextView city;
    TextView state;
    TextView zipCode;
    Button dobBtn;
    Button dateMetBtn;
    Button mapBtn;

    ArrayList<Contact> contactStuff;
    ArrayList<String> lastNameList;
    ArrayList<String> idList;
    private static Context context;
    boolean newContact;
    int listPosition;
    int id;
    String contactInformation;
    String lastNameString;
    String firstNameString;
    DatabaseHelper myDb;
    double userlat, userlng;


    /* Written by: Sarah Kolanowski & Huy Hoang
     * OnCreate we assign id's to all our buttons and textViews and get any extras from the last
     * activity if any are available. If there are extras we assign them to their corresponding
     * textView, if it is a new contact we present a blank contact that allows the user to add a
     * new contact to their list if they desire.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ContactInfo.context = getApplicationContext();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        fName = findViewById(R.id.fname);
        lName = findViewById(R.id.lname);
        pNum = findViewById(R.id.pnum);
        DOB = findViewById(R.id.bDay);
        dMet = findViewById(R.id.dateMet);
        dobBtn = findViewById(R.id.DOBBtn);
        dateMetBtn = findViewById(R.id.dateMetBtn);
        mapBtn = findViewById(R.id.mapAddr);
        addr1 = findViewById(R.id.Address1);
        addr2 = findViewById(R.id.Address2);
        city = findViewById(R.id.City);
        state = findViewById(R.id.State);
        zipCode = findViewById(R.id.Zip);

        listPosition = getIntent().getIntExtra("position", listPosition);
        contactStuff = getIntent().getParcelableArrayListExtra("contact_info");
        idList = getIntent().getStringArrayListExtra("idList");
        lastNameList = getIntent().getStringArrayListExtra("last_name_list");
        newContact = getIntent().getBooleanExtra("newContact", newContact);

        //here we get the users last known lat long
        GPSHelper help = new GPSHelper(getApplicationContext());
        Location userLoc = help.getLocation();
        if(userLoc != null) {
            userlat = userLoc.getLatitude();
            userlng = userLoc.getLongitude();
        }

        Bundle extras = getIntent().getExtras();

        if(extras != null && !newContact)
        {
            if(fName != null)
            {
                fName.setText(contactStuff.get(listPosition).getFirstName());
            }
            if(lName != null)
            {
                lName.setText(contactStuff.get(listPosition).getLastName());
            }
            if(pNum != null)
            {
                pNum.setText(contactStuff.get(listPosition).getPhoneNumber());
            }
            if(DOB != null)
            {
                DOB.setText(contactStuff.get(listPosition).getDob());
            }
            if(dMet != null)
            {
                dMet.setText(contactStuff.get(listPosition).getDateMet());
            }
            if(addr1 != null)
            {
                addr1.setText(contactStuff.get(listPosition).getAddr1());
            }
            if(addr2 != null)
            {
                addr2.setText(contactStuff.get(listPosition).getAddr2());
            }
            if(city != null)
            {
                city.setText(contactStuff.get(listPosition).getCity());
            }
            if(state != null)
            {
                state.setText(contactStuff.get(listPosition).getState());
            }
            if(zipCode != null)
            {
                zipCode.setText(contactStuff.get(listPosition).getZipCode());
            }

            lastNameString = lName.getText().toString();
            lastNameString = String.format("%-25s", lastNameString);

            firstNameString = fName.getText().toString();
            firstNameString = String.format("%-25s", firstNameString);

            contactInformation = lastNameString + firstNameString +
                    pNum.getText().toString() + DOB.getText().toString() + dMet.getText().toString();
        }
    }


    /* Written by: Sarah Kolanowski
     * Takes the date (month, day, year) the user has chosen and sets them. We then create a new
     * String and set the DOB or date met (based off the corresponding id)
     * accordingly with the date the user has selected.
     * Date output is in the format MM/DD/YYYY
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance(); //make a new calendar
        c.set(Calendar.YEAR, year); //set the calendar year with the yesr the user selected
        c.set(Calendar.MONTH, month); //set the calendar month with the month the user selected
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth); //set the calendar day with the day the user selected
        String newDate; //create the string we'll use to hold the date

        //if the month is less than 10 and the day is less than 10 we append a 0 to the front to
        //keep it in our desired format
        //month + 1 since Jan = 0
        if((month+1) < 10 && dayOfMonth < 10){
            newDate = "0" + (month+1) + "/0" + dayOfMonth + "/" + year;
        }
        //if just month is under 10 we only append a 0 to the month
        else if((month+1) < 10){
            newDate = "0" + (month+1) + "/" + dayOfMonth + "/" + year;
        }
        //if just day is under 10 we only append a 0 to the day
        else if(dayOfMonth < 10){
            newDate = (month+1) + "/0" + dayOfMonth + "/" + year;
        }
        //otherwise both month and day are > 10 and we build our string normally
        else {
            newDate = (month + 1) + "/" + dayOfMonth + "/" + year;
        }
        //if id == 1 the user selected the DOB button so we set the DOB textView with the new date
        //we reset the id variable for the next click
        if(id == 1) {
            DOB.setText(newDate);
            id = 0;
        }
        //otherwise if id == 2 then the user selected the date met button so we change the date met
        //textView with the new date
        //we reset the id variable for the next click
        else if (id == 2){
            dMet.setText(newDate);
            id = 0;
        }
    }


    /* Written by: Sarah Kolanowski
     * If user selects to change the date of the day they met this function handles it.
     * Set id to 2 so we know which date picker the user has selected and make a
     * new dialog fragment to show the calender for the
     * user to choose the date from.
     */
    public void dateMetChange(View view) {
        id = 2;
        DialogFragment datePicker = new com.example.contactmanager.DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "dobPicker");
    }


    /* Written by: Sarah Kolanowski
     * If user selects to change the DOB this function handles it. Set id to 1 so we know which date
     * picker the user has selected. Create a new dialog fragment to show the calendar for the user
     * to select their desired date/
     */
    public void DOBChange(View view) {
        id = 1;
        DialogFragment newFrag = new com.example.contactmanager.DatePickerFragment();
        newFrag.show(getSupportFragmentManager(), "dateMetPicker");
    }


    /* Written by: Sarah Kolanowski & Huy Hoang
     * Handles the action when the delete or save button is clicked
     * Creates a new ContactFileHelper object to create a RandomAccessFile
     * Calls deleteContact or saveContact depending on the button pressed
     * Once the action is completed we want to move back to the main activity
     */
    public void saveOrDelete(View view)
    {
        myDb = new DatabaseHelper(this);
        if(view.getId() == R.id.saveBtn)
        {
            boolean isInserted;
            if(newContact)
            {
                isInserted = myDb.addOrUpdateContact(new Contact(lName.getText().toString(),
                        fName.getText().toString(),
                        pNum.getText().toString(),
                        DOB.getText().toString(),
                        dMet.getText().toString(),
                        addr1.getText().toString(),
                        addr2.getText().toString(),
                        city.getText().toString(),
                        state.getText().toString(),
                        zipCode.getText().toString()),newContact, " ");
            }
            else
            {
                isInserted = myDb.addOrUpdateContact(new Contact(lName.getText().toString(),
                        fName.getText().toString(),
                        pNum.getText().toString(),
                        DOB.getText().toString(),
                        dMet.getText().toString(),
                        addr1.getText().toString(),
                        addr2.getText().toString(),
                        city.getText().toString(),
                        state.getText().toString(),
                        zipCode.getText().toString()),newContact, idList.get(listPosition));
            }

            if(isInserted == true)
            {
                Toast.makeText(this, "Data Inserted", Toast.LENGTH_LONG).show();
                backToContactListView(view);
            }
            else
            {
                Toast.makeText(ContactInfo.this, "Data not Inserted", Toast.LENGTH_LONG).show();
            }
        }
        else if(view.getId() == R.id.delBtn)
        {
            myDb.deleteData(idList.get(listPosition));
            Toast.makeText(ContactInfo.this, "Contact has been deleted", Toast.LENGTH_LONG).show();
            backToContactListView(view);
        }
    }


    /* Written by: Sarah Kolanowski
     * If back button is selected, no change needs to be made to the file and so we just go back
     * to our original contact list view
     */
    public void backToContactListView(View view)
    {
        Intent intent = new Intent(ContactInfo.this, MainActivity.class);
        startActivity(intent);
    }

    /* Written by: Sarah Kolanowski
     * Our onClick listener for our mapping button. Checks to see if all fields (addr1, city, state, zip)
     * are filled to find the address, if not then the user is notified through a toast notif. IF all
     * fields are entered then we take the input and put it into one string and call getCoordinates to
     * find the lat lng of the given address. From there we pass the lat lng of the given address and
     * the last known latlng of the user in an intent to the MapAddress class. If the address the user
     * enters did not produce a latlng from the getCoordinates function the user is notified and the
     * maps activity is not called.
     */
    public void mapAddress(View view) {
        //here if user selects AND address info is inputted then we go to a new activity and map the address provided
        //in new activity we show addr on map and calculate how far that is from where we currently are
        if(zipCode.getText().toString().isEmpty() || addr1.getText().toString().isEmpty() || state.getText().toString().isEmpty() || city.getText().toString().isEmpty()){
            Toast.makeText(ContactInfo.this, "Not enough information to map address!", Toast.LENGTH_LONG).show();
        }
        else{
            //otherwise take the info put it together and map the addr
            //put it into a single string and pass the string
            String stringAddr = "";
            stringAddr = addr1.getText().toString() + " " + city.getText().toString() + " " + state.getText().toString() + " " + zipCode.getText().toString();

            LatLng result;
            result = getCoordinates(stringAddr, context);
            if(result != null) {
                double lat = result.latitude;
                double lng = result.longitude;

                Intent intent = new Intent(ContactInfo.this, MapsActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("userlat", userlat);
                intent.putExtra("userlng", userlng);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "No result found for given address", Toast.LENGTH_LONG).show();
            }
        }
    }


    /* Written by: Sarah Kolanowski
     * Will get the coordinates from the addr provided by the user. We use a geocoder along with the
     * address provided by the user. We check to see if the address provided is null, if not then we
     * get the lat long from the address we passed and return that to the mapAddress function. Otherwise
     * we return null.
     */
    private LatLng getCoordinates(String stringAddr, Context context) {
        LatLng latlng = null;

        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> address = geocoder.getFromLocationName(stringAddr, 1);
            Address bestMatch = (address.isEmpty() ? null : address.get(0));

            if(bestMatch != null) {

                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                latlng = new LatLng(location.getLatitude(), location.getLongitude());
                return latlng;
            }
            else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
