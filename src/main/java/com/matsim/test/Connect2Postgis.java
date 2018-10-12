package com.matsim.test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Administrator on 2018/3/17.
 */
public class Connect2Postgis {

    String dataBaseName;
    String userName;
    String password;
    Connect2Postgis(){

    }

    public Connection Connect2Postgis(String dataBaseName, String userName, String password) throws Exception {
        this.dataBaseName = dataBaseName;
        this.userName = userName;
        this.password = password;

        String Driver = "org.postgresql.Driver";
        String URL = "jdbc:postgresql://119.23.251.152:5432/";
        Class.forName(Driver).newInstance();
        Connection connection= DriverManager.getConnection(URL+dataBaseName,userName,password);
        System.out.println("Database connected successfully!");
//System.out.println("Bonus maxim: The difference between friends and best friends is:" +
//" when you were in hospital, friends would say:'How are you dear?',");
//System.out.println("but best friends say:'How is the nurse?'");
        return connection;
    }
    public static void main(String args[]) throws Exception {
        Connection c = new Connect2Postgis().Connect2Postgis("postgres", "postgres", "8525233");

    }
}