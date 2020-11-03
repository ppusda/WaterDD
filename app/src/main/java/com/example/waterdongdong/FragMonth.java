package com.example.waterdongdong;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragMonth extends Fragment {
    private View view;
    TextView drink_name, drink_intake;
    //String[] xAxisLables = new String[]{"1","2", "3", "4" ...};
    private DatabaseReference mDatabase;
    String chk_mod;

    public static FragMonth newInstance(){
        FragMonth fragMonth = new FragMonth();
        return fragMonth;
    }

    private void readData(){
        mDatabase.child("record").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data data = dataSnapshot.getValue(Data.class);
                drink_name.setText(data.getD_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void readMod(){
        mDatabase.child("mod").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Mod mod = dataSnapshot.getValue(Mod.class);
                chk_mod = mod.getMod();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_month,container,false);
        BarChart chart = (BarChart) view.findViewById(R.id.barchart);
        drink_name = (TextView)view.findViewById(R.id.drink_name);
        drink_intake = (TextView) view.findViewById(R.id.drink_intake);

        ArrayList NoOfEmp = new ArrayList();

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 1250f));
        entries.add(new BarEntry(2f, 800f));
        entries.add(new BarEntry(3f, 1500f));
        entries.add(new BarEntry(4f, 900f));
        entries.add(new BarEntry(5f, 1300f));

        BarDataSet set = new BarDataSet(entries, "주 평균 음수량(ml)");

        BarData data = new BarData(set);
        data.setBarWidth(0.7f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        set.setDrawValues(false); // 차트 위의 값 삭제
        chart.setExtraTopOffset(20f); //차트와 위의 간격

        XAxis x = chart.getXAxis();
        x.setAxisMinimum(0);
        x.setAxisMaximum(6);

        chart.invalidate(); // refresh

        MyMarkerView mv = new MyMarkerView(this.getActivity(),R.layout.my_marker_view);
        chart.setMarker(mv);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        xAxisLabel.add("week1");
        xAxisLabel.add("week2");
        xAxisLabel.add("week3");
        xAxisLabel.add("week4");
        xAxisLabel.add("week5");
        xAxisLabel.add("");


        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setTextSize(15f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaxValue(2400);
        yAxis.setAxisMinValue(0);
        yAxis.setLabelCount(6);
        chart.getAxisRight().setEnabled(false);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        readData();
        readMod();

        return view;
    }
}
