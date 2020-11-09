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
    static int my_intake;
    int chart_intake,i=0;
    List<BarEntry> entries = new ArrayList<>();
    String date;
    BarChart chart;

    public static FragDay newInstance(){
        FragDay fragDay = new FragDay();
        return fragDay;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_day,container,false);
        drink_name = (TextView)view.findViewById(R.id.drink_name);
        drink_intake = (TextView) view.findViewById(R.id.drink_intake);
        chart = (BarChart) view.findViewById(R.id.barchart);

        //chart.setScaleEnabled(false);



//        entries.add(new BarEntry(0f, 0f));
//        entries.add(new BarEntry(1, 1250));
//        entries.add(new BarEntry(2f, 2200f));
//        entries.add(new BarEntry(3f, 1500f));
//        entries.add(new BarEntry(4f, 1300f));
//        entries.add(new BarEntry(5f, 1300f));
//        entries.add(new BarEntry(6f, 1800f));
//        entries.add(new BarEntry(7f, 1700f));
//        entries.add(new BarEntry(6f, 1900f));

//        BarDataSet set = new BarDataSet(entries, "시간당 음수량(ml)");

 //       BarData data = new BarData(set);
//        data.setBarWidth(0.8f); // set custom bar width
//        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
//        set.setDrawValues(false); // 차트 위의 값 삭제
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
        readData();
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

        private void readData () {
            for (i=0; i<9; i++) {
            Calendar cal = Calendar.getInstance(); // 캘린더 객체 생성
            cal.add(Calendar.DATE, -8+i); //오늘 날짜(Date)에서 8일전으로 날짜 할당
            Date date = cal.getTime();
            SimpleDateFormat dateFormat_Day = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String txtDate = dateFormat_Day.format(date); //위의 simpel 포맷으로 날짜 형식 할당

                mDatabase.child("record").child(txtDate).child("d_intake").addValueEventListener(new ValueEventListener() {
                    @Override //데이터베이스 내 txtDate와 일치하는 날짜의 d_intake 값을 불러 오는 listenr
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intake intake = dataSnapshot.getValue(Intake.class); //intake 클래스에서 음수값 연결
                        chart_intake = intake.getT_intake(); //char_intake는 int형으로 intake 클래스에서 getT_intake를 이용하여 값을 가져옴
                        List<BarEntry> entries = new ArrayList<BarEntry>(); //차트의 값 연결 리스트
                        for (i = 1; i <= 8; i++) {
                            entries.add(new BarEntry(i, chart_intake)); //add로 값을 연결 i는 순서이고 chart_intake는 받아온 음수량
                        }

                        BarDataSet set = new BarDataSet(entries, "시간당 음수량(ml)");
                        BarData data = new BarData(set);
                        data.setBarWidth(0.8f); // set custom bar width
                        chart.setData(data);
                        set.setDrawValues(false); // 차트 위의 값 삭제
                        data.setBarWidth(0.7f);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                cal.add(Calendar.DATE, -8+i);
            }

    }

}//fragment
