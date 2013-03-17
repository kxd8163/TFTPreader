/**
* TFTPreader - reads files from server using TFTP protocol 
*
* @version   $Id$
*
* @author    Karina Damico: kxd8163
*
* Revisions:
*       $Log$
*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class TFTPreader {
 
    public static void main(String args[]) {
      String mode="",host="",fileName="",errorMsg="";
      int blockNum,errorNum;
      byte A=0;    //0 byte   
      byte RRQ=1;  //req code
      byte DATA=3;   //data code
      byte ACK=4;    //ack code
      byte ERR=5;    //error code

        if (args.length<3 || args.length>3)
          	  System.out.println("Usage: [java] TFTPreader [netascii|octet] tftp-host file");
        else{
            mode=args[0];
            host = args[1] ;
            fileName = args[2];
                   
     
            try {
 
            InetAddress server = InetAddress.getByName(host);
            DatagramSocket sock = new DatagramSocket();
 
            FileOutputStream outFile = new FileOutputStream(fileName);   //file that will be writen
 
            // Create the request packet and send it off
            byte[] reqData=new byte[4+fileName.length()+mode.length()];
            reqData[0]=A;
            reqData[1]=RRQ;
            byte[] FileNAr=fileName.getBytes();
            byte[] ModeAr=mode.getBytes();
            int j=0;
            for (int i=2; i<=FileNAr.length+1;i++,j++)
                reqData[i]=FileNAr[j];
        
            int k=0;
            for (int i=3+FileNAr.length; i<=2+FileNAr.length+ModeAr.length; i++, k++)
                reqData[i]=ModeAr[k];
          
            DatagramPacket pack=new DatagramPacket(reqData,reqData.length,server,69);
                sock.send(pack);
 

             // Create the ACK packet data array
              byte[] ackData=new byte[4];
              ackData[1]=ACK;
              DatagramPacket ackPack=new DatagramPacket(ackData,ackData.length,server,69);  
          
              // Create a packet to receive the data
              byte[] recData=new byte[516];

              pack=new DatagramPacket(recData,recData.length);
              //System.out.println("Receiving packet(s) from the server...");


              for(int packLen=516; packLen==516; ) {
                // System.out.println("Getting a packet from the server...") ;
                  sock.receive(pack);
                // System.out.println("Got a packet from server...") ;
                  packLen=pack.getLength();
 
                  if (recData[1]==DATA) {                           // If a DATA pak then...
                      outFile.write(recData,4,packLen-4);
                      ackData[2] = recData[2];
                      ackData[3] = recData[3];
                      ackPack = new DatagramPacket(ackData,ackData.length, server,pack.getPort()); 
                      sock.send(ackPack);
                      sock.setSoTimeout(40000);
                      // System.out.println("ACK is sent");      
                   }
                  else if (recData[1]==ERR) {        //// If a DATA pack then..
                    //System.out.println("else if");
              	      byte lo = recData[3];  //getting int out of 2 bytes, separating signed part out
                      int signBit = (int)(lo&0x80);
                      lo&=0x7f;
                      int highByte = (int)recData[2];
                      int lowByte = (int)lo;
                      //combined error number
                      errorNum=(highByte<<8)+signBit+lowByte;
                      StringBuffer strBuf = new StringBuffer();
                      int curPos = 4;
                      while (curPos<(recData.length-1)) {
                          strBuf.append((char)recData[curPos]);
                          curPos++;
                    }
                   
                    errorMsg = new String(strBuf);
              	    System.err.println("Error code "+errorNum+" : "+errorMsg);
              	    break;
              	  
                    }
		                else{
			                 throw new Exception();
		                }
              }
          
              outFile.close();
              sock.close();
          }
 
          catch (Exception e)
            {System.err.println("Error occured: "+ e.getMessage());}
        }
    }
}
