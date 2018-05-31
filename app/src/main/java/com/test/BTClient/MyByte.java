package com.test.BTClient;

import java.io.UnsupportedEncodingException;

/**
 * Created by dg on 2017/9/21.
 */

public class MyByte {
    public static void main(String[] args) {
//                    getString2();
//        System.out.println(new String(getBytes()));
//        String ascii = StringToA("1234567890");
//        System.out.println(ascii);
//        byte b = Byte.parseByte("20");
//        String s = Integer.toHexString(42);
//        byte a = 0x20;
//        byte[] bytes = hexStringToBytes("a");
//        System.out.println(bytes.length + ":"+bytes[0]);
//        byte[] crc = hexStringToBytes("0104020002");
//        byte[] crc = hexStringToBytes("010300050010");//
//        String c = CRCUtil.getCRC(crc);
//        System.out.println(c);
//        int crc2 = CRC16.calcCrc16(hexStringToBytes("010300050010"));
//        System.out.println(String.format("%04x", crc2));
        String str = "32";
        isLenOne(str);
        System.out.println(str);

    }

    /**
     * 10进制转16
     */
    public static boolean isLenOne(String str){
        try {
            str= Integer.toHexString(Integer.parseInt(str));
            if (str != null && str != ""){
                int len = str.length();
                if (len == 1){
                    str = "0" + str;
                    return true;
                }else if (len == 2){
                    return true;
                }else if (len == 3){
                    str = "0" + str;
                    return false;
                }else if (len == 4){
                    return false;
                }else {
                    str = "ffff";
                    return false;
                }
            }else {
                str = "0";
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        if (hexString.length() == 1){
            hexString = "0" + hexString;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    public static byte hexStringToByte(String hexString) {

       return hexStringToBytes(hexString)[0];
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static  byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //字符串转换为ascii
    public static String StringToA(String content){
        String result = "";
        int max = content.length();
        for (int i=0; i<max; i++){
            char c = content.charAt(i);
            int b = (int)c;
            result = result + b;
        }
        return result;
    }
        public static String getString() throws UnsupportedEncodingException {
                //字节转字符:两个数据：32
                byte[] bytes = new byte[32];
                bytes[0] = 0x00;
                bytes[1] = 0x01;
                bytes[2] = 0x00;
                bytes[3] = 0x03;
                byte[] dataLen = "32".getBytes();
                for (int i = 0;i<dataLen.length;i++){
                        bytes[4+i] = dataLen[i];
                }
//                bytes[4] = 0x00;
//                bytes[5] = 0x02;
                bytes[6] = 0x01;
                bytes[7] = 0x18;
                byte[] sn = "0000000000".getBytes();
                System.out.println(sn.length);
                System.out.println(sn[0]);
                for (int i = 0;i<sn.length;i++){
                        bytes[8+i] = sn[i];
                }
                byte[] params = "01".getBytes();
                for (int i = 0;i<params.length;i++){
                        bytes[18+i] = params[i];
                }
//                bytes[18] = 0x00;
//                bytes[19] = 0x01;
                //数据长度：10
                byte[] dataLenS = "10".getBytes();
                for (int i = 0;i<dataLenS.length;i++){
                        bytes[20+i] = dataLenS[i];
                }
//                bytes[20] = 0x03;
//                bytes[21] = 0x02;
                //设置数据
                byte[] data = "android123".getBytes();
                for (int i = 0;i<data.length;i++){
                        bytes[22+i] = data[i];
                }
//                bytes[22] = 0x01;
//                bytes[23] = 0x02;
                String str1 = bytesToString(bytes,0,2);

                System.out.println(str1);
                String head1 = bytesToString(bytes,0,2);
                System.out.println("h1:"+head1);
                String head2 = bytesToString(bytes,2,4);
                String head3 = bytesToString(bytes,4,6);
                System.out.println("h3:"+head3);
                String head4 = bytesToString(bytes,6,7);
                String head5 = bytesToString(bytes,7,8);
//                String head6 = bytesToString(bytes,8,18);
                String head6 = "0000000000";
                String head7 = bytesToString(bytes,18,20);
                String head8 = bytesToString(bytes,20,22);
//                String head9 = bytesToString(bytes,22,32);
                String head9 = "android123";


                head1 = new String(new byte[]{0x00,0x01});
                String total = head1 + head2 + head3 + head4 + head5 + head6 + head7 + head8 + head9;
                System.out.println("数据：" + total);
                System.out.println("数据：" + new String(head6.getBytes()));

                return total;
        }

        public static String getString2(){
                String head1  = new String(new byte[]{0x00,0x01});
                String head2  = new String(new byte[]{0x00,0x03});
                String head3  = new String(new byte[]{0x00,0x20});
//                String head3 = "32";
                String head4  = new String(new byte[]{0x01});
                String head5  = new String(new byte[]{0x08});
                String head6 = "0000000000";
                        head6 = StringToA(head6);
                String head7 = "01";
//                String head8 = "10";
                String head8 = new String(new byte[]{0x00,0x0a});
                String head9 = "android123";
                 head9 = StringToA(head9);
                System.out.println("head1：" + head1);
                String total = head1 + head2 + head3 + head4 + head5 + head6 + head7 + head8 + head9;
                System.out.println("total：" + total);

                return total;
        }
    public static byte[] getBytes() throws UnsupportedEncodingException {
        byte[] head1  = new byte[]{0x00,0x01};
        byte[] head2  = new byte[]{0x00,0x03};
        byte[] head3  = new byte[]{0x00,0x16};
//        byte[] head3  = new byte[]{0x03,0x02};
//                String head3 = "32";
        byte[] head4  = new byte[]{0x01};
        byte[] head5  = new byte[]{0x18};
        byte[] head6 = "0000000000".getBytes("ASCII");
//        byte[] head6 =  new byte[]{0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30};
//        head6 = StringToA(head6);
//        byte[] head7 = "01".getBytes();
        byte[] head7 = new byte[]{0x00,0x00};
//                String head8 = "10";
        byte[] head8 = new byte[]{0x00,0x0a};
//        byte[] head8 = new byte[]{0x01,0x00};
        byte[] head9 = "android123".getBytes("ASCII");
//        byte[] head9 = new byte[]{0x61,0x6e,0x64,0x31,0x32,0x33,0x34,0x35,0x36,  0x37};
//        head9 = StringToA(head9);
//        String total = head1 + head2 + head3 + head4 + head5 + head6 + head7 + head8 + head9;
//        System.out.println("total：" + total);
        byte[] total = new byte[32];
        byte[] b12 = unitByteArray(head1,head2);
        byte[] b34 = unitByteArray(head3,head4);
        byte[] b56 = unitByteArray(head5,head6);
        byte[] b78 = unitByteArray(head7,head8);
        byte[] b1234 = unitByteArray(b12,b34);
        byte[] b5678 = unitByteArray(b56,b78);
        byte[] b1_8 = unitByteArray(b1234,b5678);
        byte[] b1_9 = unitByteArray(b1_8,head9);
        return b1_9;
    }
    public static char[] getChar(){
        char[] b = new char[]{0x00,0x01,0x00,0x03,0x00,0x16,
                0x01,0x17,0x30,0x30,0x30,0x30,
                0x30,0x30,0x30,0x30,0x30,0x30,
                0x00,0x08,0x01,0x03,0x00,0x2b,
                0x00,0x01,0xf4,0x02
        };
        return b;
    }
    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }
        //将字节数组转化为string
        public static String bytesToString(byte [] bytes,int start,int end) throws UnsupportedEncodingException {
                String str = "";
                if(bytes.length<end-start){
                        return str;
                }
                byte [] bs = new byte[end-start];
                for(int i = 0;i<end-start;i++){
                        bs[i] = bytes[start++];
                }
                str = new String(bs);
//                str = new String(bs,"ASCII");
                return str;
        }


}
