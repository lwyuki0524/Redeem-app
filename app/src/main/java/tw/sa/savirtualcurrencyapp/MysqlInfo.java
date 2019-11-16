package tw.sa.savirtualcurrencyapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlInfo {

    // 資料庫定義
    String mysql_ip = "140.135.113.178";
    int mysql_port = 3307;
    String db_name = "sa";
    String url = "jdbc:mysql://" + mysql_ip + ":" + mysql_port + "/" + db_name + "?useSSL=false";
    String db_user = "sa11";
    String db_password = "sasasa11";

    public Connection getCon() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v("DB", "加載驅動成功");
        } catch (ClassNotFoundException e) {
            Log.e("DB", "加載驅動失敗");
            Log.e("DB", e.toString());
        }

        // 連接資料庫
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            Log.v("DB", "遠端連接成功");
        } catch (SQLException e) {
            Log.e("DB", "遠端連接失敗");
            Log.e("DB", e.toString());
        }

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, db_user, db_password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

}
