package com.example.practical1_java;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    List<Uri> imgUri = new ArrayList<>();
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Uri> img) {
        super(fragmentActivity);
        this.imgUri = img;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new SwipeFragment(imgUri, position);
    }

    @Override
    public int getItemCount() {
        return imgUri.size();
    }
}
