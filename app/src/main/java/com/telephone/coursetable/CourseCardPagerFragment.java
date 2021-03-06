package com.telephone.coursetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.telephone.coursetable.Gson.CourseCard.CourseCardData;

public class CourseCardPagerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.course_card_view_pager, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CourseCardData data = MyApp.gson.fromJson(getActivity().getIntent().getStringExtra(CourseCard.EXTRA), CourseCardData.class);
        ((ViewPager2)view.findViewById(R.id.course_card_viewpager)).setAdapter(new CourseCardAdapter(this, data.getCards(), data));
    }
}
