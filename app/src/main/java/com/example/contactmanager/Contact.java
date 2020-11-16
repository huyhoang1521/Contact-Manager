/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import android.os.Parcel;
import android.os.Parcelable;

/* Written by: Sarah Kolanowski & Huy Hoang
 * Set up our contact item that holds the first name, last name, phone number, DOB, and date met
 */
public class Contact implements Parcelable
{
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private String dob;
    private String dateMet;
    private String addr1;
    private String addr2;
    private String city;
    private String state;
    private String zipCode;

    public Contact(String l, String f, String p, String d, String dm)
    {
        lastName = l;
        firstName = f;
        phoneNumber = p;
        dob = d;
        dateMet = dm;
    }

    public Contact(String l, String f, String p, String d, String dm, String a1, String a2, String c, String s, String z)
    {
        lastName = l;
        firstName = f;
        phoneNumber = p;
        dob = d;
        dateMet = dm;
        addr1 = a1;
        addr2 = a2;
        city = c;
        state = s;
        zipCode = z;
    }

    protected Contact(Parcel in)
    {
        lastName = in.readString();
        firstName = in.readString();
        phoneNumber = in.readString();
        dob = in.readString();
        dateMet = in.readString();
        addr1 = in.readString();
        addr2 = in.readString();
        city = in.readString();
        state = in.readString();
        zipCode = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>()
    {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getLastName(){
        return lastName;
    }

    public void setLname(){
        this.lastName = lastName;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setFname(){
        this.firstName = firstName;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPnumber(){
        this.phoneNumber = phoneNumber;
    }

    public String getDob(){
        return dob;
    }

    public void setDob(){
        this.dob = dob;
    }

    public String getDateMet(){
        return dateMet;
    }

    public void setDateMet(){
        this.dateMet = dateMet;
    }

    public String getAddr1(){
        return addr1;
    }

    public void setAddr1(){
        this.addr1 = addr1;
    }

    public String getAddr2(){
        return addr2;
    }

    public void setAddr2(){
        this.addr2 = addr2;
    }

    public String getCity(){
        return city;
    }

    public void setCity(){
        this.city = city;
    }

    public String getState(){
        return state;
    }

    public void setState(){
        this.state = state;
    }

    public String getZipCode(){
        return zipCode;
    }

    public void setZipCode(){
        this.zipCode = zipCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(lastName);
        dest.writeString(firstName);
        dest.writeString(phoneNumber);
        dest.writeString(dob);
        dest.writeString(dateMet);
        dest.writeString(addr1);
        dest.writeString(addr2);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zipCode);
    }
}
