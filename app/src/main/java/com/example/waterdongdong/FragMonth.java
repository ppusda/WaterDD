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

public class FragMonth extends Fragment {
    private View view;
    TextView drink_name, drink_intake;
    //String[] xAxisLables = new String[]{"1","2", "3", "4" ...};
    private DatabaseReference mDatabase;
    static int my_intake;
    int chart_intake,i=0;
    String date;
    BarChart chart;
    List<BarEntry> entries = new ArrayList<BarEntry>(); //차트의 값 연결 리스트

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_month,container,false);
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
        final int[] arr ={0, 2100, 2400, 1900, 2100, 2600,2100, 2000, 2200,2700};
        mDatabase.child("record").child(date).child("d_intake").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intake intake = dataSnapshot.getValue(Intake.class);
                chart_intake = intake.getT_intake();
                for (i=1; i<10; i++) {
                    entries.add(new BarEntry(i,arr[i]));
                }
                entries.add(new BarEntry(10, chart_intake));
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
        x.setAxisMaximum(30);
        chart.invalidate(); // refresh

        MyMarkerView mv = new MyMarkerView(this.getActivity(),R.layout.my_marker_view);
        chart.setMarker(mv);

        final ArrayList<String> xLabel = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
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
