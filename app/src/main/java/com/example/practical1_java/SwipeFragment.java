package com.example.practical1_java;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SwipeFragment extends Fragment {

    private List<Uri> imgUri = new ArrayList<>();
    private int position;
    public SwipeFragment(List<Uri> imageUri, int pos) {
        this.imgUri = imageUri;
        this.position = pos;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_swipe, container, false);
        ImageView img = root.findViewById(R.id.imageViewMain);
        img.setImageURI(imgUri.get(position));
        return root;
    }
}