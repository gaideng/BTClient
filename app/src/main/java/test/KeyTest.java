package test;

/**
 * Created：2018/6/1 on 14:44
 * Author:gaideng on dg
 * Description:
 */

    public class KeyTest {
        private static byte[] newkeys = {1,2,3,4};
        public static void main(String[] arg){
//            byte[] sendjiami = new byte[1];
//            sendjiami[0] = 0x01;
//            decodenewKey(sendjiami);
            getCheckSum(null);
        }
        public static byte[] decodenewKey(byte[] src) {
            if (src == null) return src;
            for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
            {
                src[j] = (byte) (src[j] ^ newkeys[j % 4]);
            }
            return src;
        }
    public static byte getCheckSum(byte[] buffer) {
            byte[] bufferNow = new byte[]{(byte) 0xA8, (byte) 0x8A,0x01 ,0x01 ,0x05 ,0x01 ,0x00 ,0x3B , (byte) 0xA8};
        int sum = 0;
        int length = bufferNow.length;
        for (int i = 0; i < length - 2; i++) {
            sum += (int) bufferNow[i];
        }
        byte end = (byte) (sum & 0xff);
        return end;
    }
    }
