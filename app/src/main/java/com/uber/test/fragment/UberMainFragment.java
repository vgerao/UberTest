package com.uber.test.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.uber.test.R;
import com.uber.test.adapter.CustomAdapter;
import com.uber.test.model.PhotoItemData;
import com.uber.test.model.PhotoItemUrl;
import com.uber.test.model.Photos;
import com.uber.test.network.NetworkRequestor;
import com.uber.test.ui.MainActivity;
import com.uber.test.util.CommonUtility;
import com.uber.test.util.UberDataParser;
import com.uber.test.util.UpdateUIHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 7/21/2018.
 */
public class UberMainFragment extends Fragment implements UpdateUIHandler, UberDataParser.UberJsonParserResponse {

    private final String TAG = UberMainFragment.class.getName();
    private ProgressDialogFragment progressDialogFragment;
    private MainActivity mainActivity;
    private Button searchButton;
    private EditText searachEditText;
    private List<PhotoItemData> photoDataList;
    private ArrayList<PhotoItemUrl> adapterData = new ArrayList<>();
    private ProgressBar progressBar;
    private CustomAdapter customAdapter;
    private int visItemCount = 0;
    private int totItem = 0;
    private int pasVisItem = 0;
    private int prev_Total = 0;
    private int viewThrs = 15;
    private boolean isLoading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.search_list_fragment, container, false);
        searchButton = (Button) root.findViewById(R.id.submit_button);
        searachEditText = (EditText) root.findViewById(R.id.search_Text);
        progressDialogFragment = new ProgressDialogFragment();

        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);
        customAdapter = new CustomAdapter(getContext(), adapterData);
        recyclerView.setAdapter(customAdapter);

        //Add pagination logic to support endless logic

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visItemCount = gridLayoutManager.getChildCount();
                totItem = gridLayoutManager.getItemCount();
                pasVisItem = gridLayoutManager.findFirstVisibleItemPosition();
                if(dy > 0){
                    if(isLoading){
                        if(totItem > prev_Total){
                            prev_Total = totItem;
                            isLoading = false;
                        }
                    }

                    if(!isLoading && (totItem - visItemCount) <= (pasVisItem + viewThrs)){
                        progressBar.setVisibility(View.VISIBLE);
                        loadMoreElements();
                        isLoading = true;
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processServerResponse();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    private void processServerResponse() {
        progressDialogFragment.show(getFragmentManager(), "Add");
        NetworkRequestor myHttpPostRequestor = new NetworkRequestor(mainActivity, UberMainFragment.this);
        myHttpPostRequestor.execute(new String[]{CommonUtility.SERVER_URL + searachEditText.getText().toString(), "GET"});
    }

    @Override
    public void updateUI(String response) {
        Log.i("Response", response.toString());
        PhotoDataListParser photoListParser = new PhotoDataListParser(getContext(), response.toString(), UberMainFragment.this);
        photoListParser.execute();

    }

    @Override
    public void onJsonSuccess(Object bean) {
        if(bean instanceof Photos){
            Photos photos = (Photos) bean;
            photoDataList = photos.getPhotos().getResults();
            Log.i(TAG, "photoDataList size:"+photoDataList.size());

            ArrayList<PhotoItemUrl> nextPgData = getListData(totItem, viewThrs);
            customAdapter.addImages(nextPgData);
        }
        progressDialogFragment.dismiss();
    }

    private void loadMoreElements(){
        progressBar.setVisibility(View.VISIBLE);
        if(totItem < photoDataList.size()){
            ArrayList<PhotoItemUrl> nextPgData = getListData(totItem, viewThrs);
            customAdapter.addImages(nextPgData);
        }else{
            Toast.makeText(getContext(), "no more images", Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private ArrayList<PhotoItemUrl> getListData(int startInd, int endInd) {
        ArrayList<PhotoItemUrl> listMockData = new ArrayList<PhotoItemUrl>();

        for (int posInd = startInd; posInd < (startInd+endInd); posInd++) {
            Log.i(TAG, "i--"+posInd+"----(startInd+endInd)--"+((startInd+endInd)-1));
            if(posInd >= photoDataList.size()-1){
                return listMockData;
            }
            PhotoItemData data = photoDataList.get(posInd);
//            http://farm{farm}.static.flickr.com/{server}/{id}_{secret}.jpg
            StringBuilder builder = new StringBuilder();
            builder.append(getResources().getString(R.string.http_str));
            builder.append("farm"+data.getFarm());
            builder.append(getResources().getString(R.string.domain_str));
            String url = data.getServer() + "/" + data.getId() + "_" + data.getSecret() + ".jpg";
            builder.append(url);
            PhotoItemUrl newData = new PhotoItemUrl();
            newData.setImageURL(builder.toString());
            listMockData.add(newData);
        }
        return listMockData;
    }


    @Override
    public void onJsonError(Object error) {

    }

    @Override
    public void onException(Exception exception) {

    }

    public class PhotoDataListParser extends UberDataParser {

        private Object photoData;
        public PhotoDataListParser(Context context, String jsonString, UberJsonParserResponse response) {
            super(context, jsonString, response);
        }

        @Override
        protected Object parse(JsonObject jsonObject) {
            photoData = parseSimpleJson(jsonObject, Photos.class);
            return photoData;
        }
    }
}
