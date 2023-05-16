
import java.sql.*;

public class mysqlDemo {

    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //static final String DB_URL = "jdbc:mysql://localhost:3306/runoob?characterEncoding=utf8&useSSL=true";

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    Connection conn = null;
    Statement stmt = null;
    static final String DB_URL = "jdbc:mysql://localhost:3306/books?useSSL=false&serverTimezone=UTC";


    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "wang13014081756.";
    public mysqlDemo() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("加载数据库驱动成功");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("连接数据库驱动成功");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void insert() {

        String sql1 = "insert into booktype(tid,name) values(1,'专利')";
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql1);
            ps.executeUpdate();
            System.out.println("插入成功！");

            System.out.println("插入结束！");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        mysqlDemo demo=new mysqlDemo();
        demo.insert();
    }}

