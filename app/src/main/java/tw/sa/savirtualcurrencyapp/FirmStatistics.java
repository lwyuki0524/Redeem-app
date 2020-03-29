package tw.sa.savirtualcurrencyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import FirmStaticsList.FirmStaticsAdapter;
import FirmStaticsList.FirmStatics_card;

public class FirmStatistics extends AppCompatActivity  implements OnChartValueSelectedListener, View.OnClickListener {
    private PieChart mPieChart;

    int total=0;
    //資料
    final ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    final List<Integer> pieIntData =new ArrayList();
    final List<String> pieStringData = new ArrayList();



    //顯示百分比
    private Button btn_show_percentage;
    //顯示型別
    private Button btn_show_type;
    //x軸動畫
    private Button btn_anim_x;
    //y軸動畫
    private Button btn_anim_y;
    //xy軸動畫
    private Button btn_anim_xy;
    //儲存到sd卡
    private Button btn_save_pic;
    //顯示中間文字
    private Button btn_show_center_text;
    //旋轉動畫
    private Button btn_anim_rotating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firm_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //點擊左邊返回按鈕監聽事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//刪除此activity(可刪除)
            }
        });

        // 讀取廠商資訊
        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

        /*建一個串列，其形態是我們自訂的java檔，裡面是要填入的卡片資料結構*/
        final List<FirmStatics_card> activityList = new ArrayList<>();
        /*下面是要將 cardView 導入的視圖*/
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        new Thread(new Runnable() {
            //            int count=0;
            @Override
            public void run() {

                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT `活動名稱`,`活動ID` FROM `activity`  WHERE `activity`.`廠商ID` = '"+firmID+"'";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        String sql2 = "SELECT COUNT(`activity_registration`.`活動ID`) AS `數量` FROM `activity_registration` ";
                        sql2+="  WHERE `activity_registration`.`活動ID`='"+rs.getString("活動ID")+"' ";
                        Statement st2 = con.createStatement();
                        ResultSet rs2 = st2.executeQuery(sql2);
                        while (rs2.next()){
                            activityList.add(new FirmStatics_card( rs.getString("活動名稱"),rs2.getString("數量")));
                        }
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //設定視圖樣式
                            recyclerView.setLayoutManager(new LinearLayoutManager(FirmStatistics.this, LinearLayoutManager.VERTICAL,false));
                        }
                    });

                    rs = st.executeQuery(sql);
                    if(!rs.next()){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                LinearLayout nodata_layout = (LinearLayout)findViewById(R.id.nodata_layout);
                                nodata_layout.setVisibility(View.VISIBLE);
                                Toast.makeText(FirmStatistics.this, "目前無活動:" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "載入失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();


        //將 activityList(從資料庫取得的資料) 利用自訂的FirmActivityAdapter適配器填入 recyclerView 中
        recyclerView.setAdapter(new FirmStaticsAdapter(this, activityList));

        initView();
    }

    //初始化
    private void initView() {
        // 讀取廠商資訊
        final String firmID = getSharedPreferences("record", MODE_PRIVATE)
                .getString("firmID", "");

//        btn_show_percentage = (Button) findViewById(R.id.btn_show_percentage);
//        btn_show_percentage.setOnClickListener(this);
//        btn_show_type = (Button) findViewById(R.id.btn_show_type);
//        btn_show_type.setOnClickListener(this);
//        btn_anim_x = (Button) findViewById(R.id.btn_anim_x);
//        btn_anim_x.setOnClickListener(this);
//        btn_anim_y = (Button) findViewById(R.id.btn_anim_y);
//        btn_anim_y.setOnClickListener(this);
//        btn_anim_xy = (Button) findViewById(R.id.btn_anim_xy);
//        btn_anim_xy.setOnClickListener(this);
//        btn_save_pic = (Button) findViewById(R.id.btn_save_pic);
//        btn_save_pic.setOnClickListener(this);
//        btn_show_center_text = (Button) findViewById(R.id.btn_show_center_text);
//        btn_show_center_text.setOnClickListener(this);
//        btn_anim_rotating = (Button) findViewById(R.id.btn_anim_rotating);
//        btn_anim_rotating.setOnClickListener(this);

        //折現餅狀圖
        mPieChart = (PieChart) findViewById(R.id.mPieChart);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //繪製中間文字
        mPieChart.setCenterText(generateCenterSpannableText());
        mPieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // 觸控旋轉
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);


        // 新增一個選擇監聽器
        mPieChart.setOnChartValueSelectedListener(this);


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // 生成 MySQL 連線資訊
                    MysqlInfo info = new MysqlInfo();
                    Connection con = info.getCon();
                    // 資料庫處理要求
                    String sql = "SELECT `活動名稱`,`activity_registration`.`活動ID` ,COUNT(`activity_registration`.`活動ID`) AS `數量`  FROM `activity`,`activity_registration`  ";
                    sql+=" WHERE `activity`.`活動ID` IN (SELECT `activity`.`活動ID` FROM `activity`  WHERE `activity`.`廠商ID` ='"+firmID+"' ) ";
                    sql+=" AND `activity_registration`.`活動ID` =  `activity`.`活動ID`  GROUP BY  `activity`.`活動名稱` ";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(sql);

                    float float_init=1;
                    int datalength=0;
                    while (rs.next()) {
                        datalength++;

                        pieIntData.add(rs.getInt("數量"));
                        pieStringData.add(rs.getString("活動名稱"));


                        total += rs.getInt("數量");
                        Log.v("DB", "數量= "+rs.getInt("數量"));
                        Log.v("DB", "活動名稱= "+rs.getString("活動名稱"));
                    }

                    for (int k=0;k<pieIntData.size();k++){
                        entries.add(new PieEntry( pieIntData.get(k)*float_init , pieStringData.get(k) ) );
                        Log.v("DB", "數量2= "+pieIntData.get(k)*float_init);
                        Log.v("DB", "活動2= "+pieStringData.get(k) );
                    }

                    //設定資料
                    setData(entries);

                    //預設動畫
//        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

                    Legend l = mPieChart.getLegend();
                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                    l.setOrientation(Legend.LegendOrientation.VERTICAL);
                    l.setDrawInside(false);
                    l.setEnabled(false);


                    // 输入标签样式

                    mPieChart.setDrawEntryLabels(true);//设置是否绘制Label
                    mPieChart.setEntryLabelColor(Color.BLACK);//设置绘制Label的颜色
                    mPieChart.setEntryLabelTextSize(12f);//设置绘制Label的字体大小


                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //設定資料
    private void setData(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "活動報名資訊");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.5f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        mPieChart.setData(data);

        runOnUiThread(new Runnable() {
            public void run() {

                // 撤銷所有的亮點
                mPieChart.highlightValues(null);
                mPieChart.invalidate();
            }
        });
    }

    //繪製中心文字
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("報名人數佔比");
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            //顯示百分比
//            case R.id.btn_show_percentage:
//                for (IDataSet<?> set : mPieChart.getData().getDataSets())
//                    set.setDrawValues(!set.isDrawValuesEnabled());
//
//                mPieChart.invalidate();
//                break;
//            //顯示型別
//            case R.id.btn_show_type:
//                if (mPieChart.isDrawHoleEnabled())
//                    mPieChart.setDrawHoleEnabled(false);
//                else
//                    mPieChart.setDrawHoleEnabled(true);
//                mPieChart.invalidate();
//                break;
//            //x軸動畫
//            case R.id.btn_anim_x:
//                mPieChart.animateX(1400);
//                break;
//            //y軸動畫
//            case R.id.btn_anim_y:
//                mPieChart.animateY(1400);
//                break;
//            //xy軸動畫
//            case R.id.btn_anim_xy:
//                mPieChart.animateXY(1400, 1400);
//                break;
//            //儲存到sd卡
//            case R.id.btn_save_pic:
//                mPieChart.saveToPath("title" + System.currentTimeMillis(), "");
//                break;
//            //顯示中間文字
//            case R.id.btn_show_center_text:
//                if (mPieChart.isDrawCenterTextEnabled())
//                    mPieChart.setDrawCenterText(false);
//                else
//                    mPieChart.setDrawCenterText(true);
//                mPieChart.invalidate();
//                break;
//            //旋轉動畫
//            case R.id.btn_anim_rotating:
////                mPieChart.spin(1000, mPieChart.getRotationAngle(), mPieChart.getRotationAngle() + 360, Easing.EasingOption
////                        .EaseInCubic);
//                break;
//        }
    }
}
