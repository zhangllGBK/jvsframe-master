package com.jarveis.frame.jdbc;

import java.sql.*;

public class EsprocTest {

    public static void main(String[] args) throws Exception {
//        readText();
//        readJson();
//        sumJson();
        groupJson();
    }

    public static void readText() {
        Connection con = null;
        try {
            Class.forName("com.esproc.jdbc.InternalDriver");
            con = DriverManager.getConnection("jdbc:esproc:local://?config=raqsoftConfig.xml");
            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery("=file(\"E:/files/employee.txt\").import@t(EID,NAME,BIRTHDAY,SALARY)");
            ResultSet rs = st.executeQuery("=file(\"E:/files/employee.txt\").import@t(EID,NAME,BIRTHDAY,SALARY).select(SALARY>10000)");

            while (rs.next()) {
                System.out.println("EID=" + rs.getInt("EID") + ", NAME=" + rs.getString("NAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readJson() {
        Connection con = null;
        try {
            Class.forName("com.esproc.jdbc.InternalDriver");
            con = DriverManager.getConnection("jdbc:esproc:local://?config=raqsoftConfig.xml");
            Statement st = con.createStatement();

//            ResultSet rs = st.executeQuery("=json(file(\"E:/files/employee.json\").read())");
            ResultSet rs = st.executeQuery("=json(file(\"E:/files/employee.json\").read()).select(SALARY>10000)");
            while (rs.next()) {
                System.out.println("EID=" + rs.getInt("EID") + ", NAME=" + rs.getString("NAME"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sumJson() {
        Connection con = null;
        try {
            Class.forName("com.esproc.jdbc.InternalDriver");
            con = DriverManager.getConnection("jdbc:esproc:local://?config=raqsoftConfig.xml");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("=json(file(\"E:/files/employee.json\").read()).sum(SALARY)");
            while (rs.next()) {
                System.out.println("SALARY=" + rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void groupJson() {
        String jsonStr = "[\n" +
                "{\"OrderID\": 32,\"Client\":\"JFS\",\"SellerId\":3,\"Amount\":468.0,\"OrderDate\":\"2009-08-13\"}, \n" +
                "{\"OrderID\": 33,\"Client\":\"JFS\",\"SellerId\":3,\"Amount\":512.0,\"OrderDate\":\"2009-08-13\"}, \n" +
                "{\"OrderID\": 70,\"Client\": \"DSG\",\"SellerId\": 7,\"Amount\": 288,\"OrderDate\": \"2009-09-30\"}, \n" +
                "{\"OrderID\": 71,\"Client\": \"DSG\",\"SellerId\": 7,\"Amount\": 675,\"OrderDate\": \"2009-09-30\"}, \n" +
                "{\"OrderID\": 72,\"Client\": \"DSG\",\"SellerId\": 7,\"Amount\": 785,\"OrderDate\": \"2009-09-30\"}, \n" +
                "{\"OrderID\": 130,\"Client\": \"FOL\",\"SellerId\": 7,\"Amount\": 249,\"OrderDate\": \"2009-09-30\"}, \n" +
                "{\"OrderID\": 131,\"Client\": \"FOL\",\"SellerId\": 7,\"Amount\": 103.2,\"OrderDate\": \"2009-12-10\"}\n" +
                "]\n";
        Connection con = null;

        try {
            Class.forName("com.esproc.jdbc.InternalDriver");
            con = DriverManager.getConnection("jdbc:esproc:local://");

            PreparedStatement pstmt = con.prepareStatement("=json(?).groups(Client; sum(Amount):amt, count(1):cnt)");
            pstmt.setString(1, jsonStr);
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                String Client = result.getString("Client");
                String amt = result.getString("amt");
                String cnt = result.getString("cnt");
                System.out.println("Client:" + Client + ", amt=" + amt + ", cnt=" + cnt);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
