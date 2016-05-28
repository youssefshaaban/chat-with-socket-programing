/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserverbroadcast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author joe
 */
public class MyServer {

    ArrayList al = new ArrayList();
    ArrayList users = new ArrayList();
    ServerSocket ss;
    Socket s;
    public MyServer() {
        try {
            ss = new ServerSocket(IShareData.PORT);
            System.out.println("Server Started " + ss);
            while (true) {
                s = ss.accept();
                Runnable r = new MyThread(s, al, users);
                Thread t = new Thread(r);
                t.start();
//	System.out.println("Total alive clients : "+ss.);
            }
        } catch (Exception e) {
            System.err.println("Server constructor" + e);
        }
    }
/////////////////////////

    public static void main(String[] args) {
        new MyServer();
    }
/////////////////////////
}

/**
 * **********************
 */
class MyThread implements Runnable {

    Socket s;
    ArrayList al;
    ArrayList users;
    String username;
///////////////////////

    MyThread(Socket s, ArrayList al, ArrayList users) {
        this.s = s;
        this.al = al;
        this.users = users;
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            username = dis.readUTF();
            al.add(s);
            users.add(username);
            tellEveryOne( username + " Logged in at             " +formateDate(new Date())  );
            sendNewUserList();
        } catch (Exception e) {
            System.err.println("MyThread constructor  " + e);
        }
    }
///////////////////////
    public void run() {
        String s1;
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            do {
                s1 = dis.readUTF();
                if (s1.toLowerCase().equals(IShareData.LOGOUT_MESSAGE)) {
                    break;
                }
        	System.out.println("received from "+s.getPort());
                tellEveryOne(username + " said: " + " : " + s1+"                "+ formateDate(new Date()));
            } while (true);
            DataOutputStream tdos = new DataOutputStream(s.getOutputStream());
            tdos.writeUTF(IShareData.LOGOUT_MESSAGE);
            tdos.flush();
            users.remove(username);
            tellEveryOne(username + " Logged out at               " + formateDate(new Date()));
            sendNewUserList();
            al.remove(s);
            s.close();

        } catch (Exception e) {
            System.out.println("MyThread Run" + e);
        }
    }
////////////////////////

    public void sendNewUserList() {
        tellEveryOne(IShareData.UPDATE_USERS + users.toString());

    }
////////////////////////

    public void tellEveryOne(String s1){
        Iterator i = al.iterator();
        while (i.hasNext()) {
            try {
                Socket temp = (Socket) i.next();
                DataOutputStream dos = new DataOutputStream(temp.getOutputStream());
                dos.writeUTF(s1);
                dos.flush();
                //System.out.println("sent to : "+temp.getPort()+"  : "+ s1);
            } catch (Exception e) {
                System.err.println("TellEveryOne " + e);
            }
        }
    }
///////////////////////
    
    public String formateDate(Date d){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(d);
    }
}
