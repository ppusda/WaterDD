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

public class FragWeek extends Fragment {
    private View view;
    TextView drink_name, drink_intake;
    //String[] xAxisLables = new String[]{"1","2", "3", "4" ...};
    private DatabaseReference mDatabase;
    static int my_intake;
    int chart_intake,i=0;
    String date;
    BarChart chart;
    List<BarEntry> entries = new ArrayList<BarEntry>(); //차트의 값 연결 리스트

    public static FragWeek newInstance(){
        FragWeek fragWeek = new FragWeek();
        return fragWeek;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_week,container,false);
        drink_name = (TextView)view.findViewById(R.id.drink_name);
        drink_intake = (TextView) view.findViewById(R.id.drink_intake);
        chart = (BarChart) view.findViewById(R.id.barchart);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat_Day = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("dd", Locale.KOREA);
        date = dateFormat_Day.format(currentTime);
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -8);
//        String txt_date = null;

        //chart.setScaleEnabled(false);
        final int[] arr ={0,2700};
        mDatabase.child("record").child(date).child("d_intake").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intake intake = dataSnapshot.getValue(Intake.class);
                chart_intake = intake.getT_intake();
                for (i=0; i<2; i++) {
                    entries.add(new BarEntry(i,arr[i]));
                }
                entries.add(new BarEntry(2, chart_intake));
                BarDataSet set = new BarDataSet(entries, "시간당 음수량(ml)");
                BarData data = new BarData(set);
                data.setBarWidth(0.8f); // set custom bar width
                chart.setData(data);
                set.setDrawValues(false); // 차트 위의 값 삭제
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



        chart.setFitBars(true); // make the x-axis fit exactly all bars

        chart.setExtraTopOffset(20f); //차트와 위의 간격
//        data.setBarWidth(0.7f);

        XAxis x = chart.getXAxis();
        x.setAxisMinimum(0);
        x.setAxisMaximum(8);
        chart.invalidate(); // refresh

        MyMarkerView mv = new MyMarkerView(this.getActivity(),R.layout.my_marker_view);
        chart.setMarker(mv);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");
        xAxisLabel.add("");

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabel.get((int)value);
            }
        });
        xAxis.setTextSize(15f);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaxValue(2800);
        yAxis.setAxisMinValue(0);
        yAxis.setLabelCount(7);
        chart.getAxisRight().setEnabled(false);


        mDatabase.child("record").child(date).child("d_date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Log.d("MainActivity", "onChildAdded : " + snapshot.getValue());

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

        readIntake();
        return view;
    }


    private void readIntake(){
        mDatabase.child("record").child(date).child("d_intake").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intake intake = dataSnapshot.getValue(Intake.class);
                my_intake = intake.getT_intake();
                drink_intake.setText(my_intake+"ml");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

}
