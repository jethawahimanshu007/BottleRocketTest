package org.bottlerocket;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/*This activity shows details of the store*/
public class StoreDetails extends AppCompatActivity {
    ///Took care of 1)Logo 2)Name 3)Phone 4)Id 5)Phone button 6) GPS button 7)Location icon 8)Location text

    private ImageView thumbnail,navigateButton,phoneCall,coordinatesIcon;
    private TextView name, phone,address,storeIdText,coordinatesText;
    Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Store Details");

        thumbnail=findViewById(R.id.thumbnail);
        phoneCall=findViewById(R.id.phoneCall);
        navigateButton=findViewById(R.id.navigate);
        coordinatesIcon=findViewById(R.id.coordinatesIcon);

        name = findViewById(R.id.name);
        phone= findViewById(R.id.phone);
        storeIdText= findViewById(R.id.storeId);
        address=findViewById(R.id.address);
        coordinatesText=findViewById(R.id.coorindatesText);

        store=Commons.selectedStore;
        phoneCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Navigation button pressed!!-- Send to map navigation activity
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+Commons.selectedStore.getPhone().replace("-","")));
                startActivity(intent);
            }

        });

        navigateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Navigation button pressed!!-- Send to map navigation activity
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Commons.selectedStore.getLatitude() + "," + Commons.selectedStore.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }

        });

        name.setText(store.getName());
        phone.setText(store.getPhone());
        storeIdText.setText("id:"+store.getStoreID());
        String addressString=store.getAddress()+"\n"+store.getCity()+" "+store.getState()+" "+store.getZipCode();
        address.setText(addressString);
        Glide.with(this)
                .load(store.getStoreLogoURL())
                //.apply(RequestOptions.circleCropTransform())
                .into(thumbnail);
        coordinatesText.setText(store.getLatitude()+","+store.getLongitude());

    }
}
