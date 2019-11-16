package tw.sa.savirtualcurrencyapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlCon {

    // 插入會員註冊資料至資料庫
    public boolean insert_member_register_Data(String nn,String ac,String pw,String imageString) {
        boolean rs = false;
        try {
            // 生成 MySQL 連線資訊
            MysqlInfo info = new MysqlInfo();
            Connection con = info.getCon();
            // 資料庫處理要求
            String sql = "INSERT INTO `member` (`帳號`, `密碼`, `餘額`, `會員暱稱`, `照片`) VALUES ('" + ac + "', '" + pw + "', '0', '" + nn + "', '" + imageString + "')";
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            st.close();
            Log.v("DB", "成功寫入資料：" + sql);
            rs = true;
        } catch (SQLException e) {
            Log.e("DB", "寫入資料失敗");
            e.printStackTrace();
        }
        return rs;
    }

    // 插入會員註冊資料至資料庫
    public boolean insert_company_register_Data(String cn,String ac,String pw,String imageString) {
        boolean rs = false;
        try {
            // 生成 MySQL 連線資訊
            MysqlInfo info = new MysqlInfo();
            Connection con = info.getCon();
            // 資料庫處理要求
            String sql = "INSERT INTO `company` (`帳號`, `密碼`, `餘額`, `公司名稱`, `商標`) VALUES ('" + ac + "', '" + pw + "', '0', '" + cn + "', '" + imageString + "')";
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            st.close();
            Log.v("DB", "成功寫入資料：" + sql);
            rs = true;
        } catch (SQLException e) {
            Log.e("DB", "寫入資料失敗");
            e.printStackTrace();
        }
        return rs;
    }

}
