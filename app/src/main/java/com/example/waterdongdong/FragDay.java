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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragDay extends Fragment {
    private View view;
    TextView drink_name, drink_intake;
    //String[] xAxisLables = new String[]{"1","2", "3", "4" ...};
    private DatabaseReference mDatabase;
    String chk_mod;
    String date;

    public static FragDay newInstance(){
        FragDay fragDay = new FragDay();
        return fragDay;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_month,container,false);
        BarChart chart = (BarChart) view.findViewById(R.id.barchart);
        drink_name = (TextView)view.findViewById(R.id.drink_name);
        drink_intake = (TextView) view.findViewById(R.id.drink_intake);

        //chart.setScaleEnabled(false);
        ArrayList NoOfEmp = new ArrayList();

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 0f));
        entries.add(new BarEntry(1f, 1250f));
        entries.add(new BarEntry(2f, 2200f));
        entries.add(new BarEntry(3f, 1500f));
        entries.add(new BarEntry(4f, 1300f));
        entries.add(new BarEntry(5f, 1300f));
        entries.add(new BarEntry(6f, 1800f));
        entries.add(new BarEntry(7f, 1700f));
        entries.add(new BarEntry(6f, 1900f));

        BarDataSet set = new BarDataSet(entries, "시간당 음수량(ml)");

        BarData data = new BarData(set);
        data.setBarWidth(0.8f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        set.setDrawValues(false); // 차트 위의 값 삭제
        chart.setExtraTopOffset(20f); //차트와 위의 간격

        XAxis x = chart.getXAxis();
        x.setAxisMinimum(0);
        x.setAxisMaximum(30);
        chart.invalidate(); // refresh

        MyMarkerView mv = new MyMarkerView(this.getActivity(),R.layout.my_marker_view);
        chart.setMarker(mv);

        final ArrayList<String> xLabel = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd", Locale.KOREA);
        calendar.set(Calendar.DAY_OF_MONTH,0);
        xLabel.add("");
        for(int i=0;i<=29;i++){
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date date= calendar.getTime();
            String txtDate= dateFormat.format(date);
            xLabel.add(txtDate);
        }

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });
        xAxis.setTextSize(15f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaxValue(2400);
        yAxis.setAxisMinValue(0);
        yAxis.setLabelCount(6);
        chart.getAxisRight().setEnabled(false);

        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat_Day = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        date = dateFormat_Day.format(currentTime);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("record").child(date).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Data data = snapshot.getValue(Data.class);
                drink_name.setText(data.getD_name());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
