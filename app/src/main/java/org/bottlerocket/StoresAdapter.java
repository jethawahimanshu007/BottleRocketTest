
package org.bottlerocket;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/*This is adapter for Recycler View*/

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Store> storeList;
    private List<Store> storeListFiltered;
    private StoresAdapterListener listener;
    private ImageView navigateButton,phoneCall;
    private PopupWindow mPopupWindow;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone,address,storeIdText;
        public ImageView thumbnail;
        public LinearLayout expandableLinearLayout;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            Logger.log_d("Name element assigned and is:"+name);
            phone = view.findViewById(R.id.phone);
            thumbnail = view.findViewById(R.id.thumbnail);
            storeIdText=view.findViewById(R.id.storeId);
            //address=view.findViewById(R.id.address);
            expandableLinearLayout = view.findViewById(R.id.expandableLayout);
            navigateButton=view.findViewById(R.id.navigate);
            phoneCall=view.findViewById(R.id.phoneCall);

            navigateButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //Navigation button pressed!!-- Send to map navigation activity
                    listener.onStoreSelected(storeListFiltered.get(getAdapterPosition()));
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + storeListFiltered.get(getAdapterPosition()).getLatitude() + "," + storeListFiltered.get(getAdapterPosition()).getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);

                }

            });

            phoneCall.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //Navigation button pressed!!-- Send to map navigation activity
                    listener.onStoreSelected(storeListFiltered.get(getAdapterPosition()));
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+storeListFiltered.get(getAdapterPosition()).getPhone().replace("-","")));
                    context.startActivity(intent);

                }

            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onStoreSelected(storeListFiltered.get(getAdapterPosition()));
                    Commons.selectedStore=storeListFiltered.get(getAdapterPosition());
                    Intent intent=new Intent(context,StoreDetails.class);
                    context.startActivity(intent);

                };
            });


        }
    }


    public StoresAdapter(Context context, List<Store> storeList, StoresAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.storeList = storeList;
        this.storeListFiltered = storeList;
        //Logger.log_d("Store List is :"+storeList.get(0).getName());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Store store = storeListFiltered.get(position);
        holder.name.setText(store.getName());
        holder.phone.setText(store.getPhone());
        holder.storeIdText.setText("id:"+store.getStoreID());
        //String addressString=store.getAddress()+"\n"+store.getCity()+" "+store.getState()+" "+store.getZipCode();

        //holder.address.setText(addressString);
        Glide.with(context)
                .load(store.getStoreLogoURL())
                //.apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return storeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    storeListFiltered = storeList;
                } else {
                    List<Store> filteredList = new ArrayList<>();
                    for (Store row : storeList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    storeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = storeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                storeListFiltered = (ArrayList<Store>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface StoresAdapterListener {
        void onStoreSelected(Store store);
    }



}


