package com.example.waterdongdong;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class FragDay extends Fragment {
    private View view;
    //String[] xAxisLables = new String[]{"1","2", "3", "4" ...};

    public static FragDay newInstance(){
        FragDay fragDay = new FragDay();
        return fragDay;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_day,container,false);
        BarChart chart = (BarChart) view.findViewById(R.id.barchart);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        entries.add(new BarEntry(4f, 50f));
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));
        entries.add(new BarEntry(7f,20f));
        entries.add(new BarEntry(8f, 30f));
        entries.add(new BarEntry(9f, 80f));
        entries.add(new BarEntry(10f, 60f));
        entries.add(new BarEntry(11f, 50f));
        entries.add(new BarEntry(12f, 50f));
        entries.add(new BarEntry(13f, 70f));
        entries.add(new BarEntry(14f, 60f));
        entries.add(new BarEntry(15f,20f));
        BarDataSet set = new BarDataSet(entries, "BarDataSet");


        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        set.setDrawValues(false); // 차트 위의 값 삭제
        chart.setExtraTopOffset(20f); //차트와 위의 간격
        chart.invalidate(); // refresh

        MyMarkerView mv = new MyMarkerView(this.getActivity(),R.layout.my_marker_view);
        chart.setMarker(mv);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setTextSize(15f);
        xAxis.setDrawGridLines(false);
        //chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));


        return view;
    }
}
