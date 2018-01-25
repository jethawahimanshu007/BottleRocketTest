package org.bottlerocket;

/**
 * Created by Himanshu on 1/22/2018.
 */

public class Store {
    String address, city,name,latitude,zipcode,storeLogoURL,phone,longitude,storeID,state;

    //Setter methods
    public void setAddress(String address){
        this.address=address;
    }
    public void setCity(String city){
        this.city=city;
    }
    public void setName(String name){
        this.name=name;
    }public void setLatitude(String latitude){
        this.latitude=latitude;
    }
    public void setZipCode(String zipcode){
        this.zipcode=zipcode;
    }
    public void setStoreLogoURL(String storeLogoURL){
        this.storeLogoURL=storeLogoURL;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }
    public void setStoreId(String storeID){
        this.storeID=storeID;
    }
    public void setState(String state){
        this.state=state;
    }

    //Getter methods
    public String getAddress(){return address;};
    public String getCity(){return city;};
    public String getName(){return name;};
    public String getLatitude(){return latitude;};
    public String getZipCode(){return zipcode;};
    public String getStoreLogoURL(){return storeLogoURL;};
    public String getPhone(){return phone;};
    public String getLongitude(){return longitude;};
    public String getStoreID(){return storeID;};
    public String getState(){return state;};


}
