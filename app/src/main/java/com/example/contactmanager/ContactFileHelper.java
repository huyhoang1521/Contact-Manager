/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import androidx.appcompat.app.AppCompatActivity;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.FileOutputStream;
import android.content.Context;
import androidx.core.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.File;


/* Written by: Huy Hoang
 * Helper class to create and access the Contact file
 * Creates the a RandomAccessFile from reading in the txt file in the assets folder
 * Parses each row in the file to set the contact's information
 * Each contact will be added to an ArrayList containing all of their information
 */
public class ContactFileHelper extends AppCompatActivity
{
    static ArrayList<Pair<String, Integer>> nameIndex;
    static int bytesPerLine = 80 + 2;
    static String data = "";
    static int linesInFile;


    /* Written by: Huy Hoang
     * Calls createContactFile to create a file using the text file in the assets directory
     * The file will then be used to create a RandomAccessFile to access the contact data
     * Handles exceptions in case the file can not be opened
     * Returns the file holding the contact information
     */
    public static File getCacheFile(Context context)
    {
        File contactFile = null;

        try
        {
            contactFile = createContactFile(context, "Contacts.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contactFile;
    }


    /* Written by: Huy Hoang
     * Creates the RandomAccessFile that will be used to read and write contacts
     * Uses the cache file created in the createContactFile method
     * Keeps track of the number of lines in the file by checking for new line characters
     * Returns the RandomAccessFile when finished
     */
    public static RandomAccessFile createRandomAccessFile(File cacheFile) throws IOException
    {
        RandomAccessFile randomAccessFile = null;
        try
        {
            randomAccessFile = new RandomAccessFile(cacheFile, "rw");
            char newLineCharacter;
            linesInFile = 1;

            while(randomAccessFile.getFilePointer()<randomAccessFile.length())
            {
                newLineCharacter = (char)randomAccessFile.read();
                if(newLineCharacter == '\n')
                {
                    linesInFile++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return randomAccessFile;
    }


    /* Written by: Huy Hoang
     * Reads each line from the RandomAccessFile and stores it into an ArrayList
     * Calls createRandomAccessFile to create a RandomAccessFile and read from it
     * If there is a deleted contact, it does not read it in and goes to the next contact
     * Closes the randomAccessFile when it is done reading and returns it
     */
    public static ArrayList<String> storeContactsInArray(File file, ArrayList<String> contactArrayList) throws IOException
    {
        RandomAccessFile randomAccessFile;
        randomAccessFile = createRandomAccessFile(file);
        nameIndex = new ArrayList<>();

        for(int i = 0; i < linesInFile; i++)
        {
            randomAccessFile.seek(bytesPerLine * i);
            data = randomAccessFile.readLine();
            if(data.trim().length() > 0)
            {
                contactArrayList.add(data);
                String lastName = data.substring(0, 25);
                String firstName = data.substring(25, 50);
                String pNum = data.substring(50, 60);
                String dob =  data.substring(60, 70);
                String dMet = data.substring(70, 80);
                String contactString  = lastName + firstName + pNum + dob + dMet;
                nameIndex.add(new Pair(contactString, i));
            }
            else
            {
                String fullName = " ";
                fullName = String.format("%-10s", fullName);
                nameIndex.add(new Pair(fullName, i));
            }
        }

        randomAccessFile.close();

        return contactArrayList;
    }


    /* Written by: Huy Hoang
     * Creates an arrayList for that contains all of the contacts information
     * This ArrayList will be used to display in the ContactInfo activity
     * Takes in as input an ArrayList that separates contacts by line
     * Parses each string in the contact ArrayList to get each value
     * Creates a Contact object and adds it to the contact info ArrayList
     */
    public static ArrayList<Contact> populateContactInfoArray(ArrayList<String> contactLineArr)
    {
        ArrayList<Contact> contactInfoArrayList = new ArrayList<>();
        for(int i = 0; i < contactLineArr.size(); i++)
        {
            contactInfoArrayList.add(new Contact(contactLineArr.get(i).substring(0, 25).replaceAll("\\s",""),
                    contactLineArr.get(i).substring(25, 50).replaceAll("\\s",""),
                    contactLineArr.get(i).substring(50, 60),
                    contactLineArr.get(i).substring(60, 70),
                    contactLineArr.get(i).substring(70, 80)));
        }

        return contactInfoArrayList;
    }


    /* Written by: Huy Hoang
     * Creates an ArrayList that only contains the last and first name of each contact
     * This ArrayList will be used to display the contact names in the listView
     * Takes in as input an ArrayList that separates contacts by line
     * Create a string to hold first and last names of each contact
     * Each string will be used to store a name in the name ArrayList
     */
    public static ArrayList<String> populateContactNameArray(ArrayList<String> contactLineArr)
    {
        ArrayList<String> contactNameArr = new ArrayList<>();

        for(int i = 0; i < contactLineArr.size(); i++)
        {
            String lastName = contactLineArr.get(i).substring(0, 25);
            String firstName = contactLineArr.get(i).substring(25, 50);

            if(!lastName.isEmpty() && !firstName.isEmpty())
            {
                String name = lastName.replaceAll("\\s","") + ", "
                        + firstName.replaceAll("\\s","");
                contactNameArr.add(name);
            }
        }

        return contactNameArr;
    }


    /* Written by: Huy Hoang
     * Creates a new file in the cache directory from the file located in the assets folder
     * Uses an InputStream to read in the contacts as an ordered sequence of bytes
     * The FileOutputStream is used to write the bytes to the new cache file
     * While the InputStream can still be read, loop to write the contents to the file
     * Closes the streams and returns the file
     */
    public static File createContactFile(Context context, String fileName) throws IOException
    {
        File contactFile = new File(context.getCacheDir(), fileName);

        if (contactFile.exists())
        {
            return contactFile;
        }

        int length = -1;
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        InputStream inputStream = context.getAssets().open(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(contactFile);

        while ((length = inputStream.read(buffer)) > 0)
        {
            fileOutputStream.write(buffer,0,length);
        }

        fileOutputStream.close();
        inputStream.close();

        return contactFile;
    }
}