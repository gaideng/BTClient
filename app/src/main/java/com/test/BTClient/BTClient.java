package com.test.BTClient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//import android.view.Menu;            //如使用菜单加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;

public class BTClient extends Activity {
    private byte[] receives;
    private int[] keys = {0x5A, 0xA5, 0x5A, 0xA5};

    private byte[] newkeys = {1, 2, 3, 4};

    private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号

    private InputStream is;    //输入流，用来接收蓝牙数据
    //private TextView text0;    //提示栏解句柄
    private EditText edit0;    //发送数据输入句柄
    private TextView tv_in;       //接收数据显示句柄
    private ScrollView sv;      //翻页句柄
    private String smsg = "";    //显示用数据缓存
    private String fmsg = "";    //保存用数据缓存

    public String filename = ""; //用来保存存储的文件名
    BluetoothDevice _device = null;     //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;

    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   //设置画面为主画面 main.xml

				        /* 解决兼容性问题，6.0以上使用新的API*/
        final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
        final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
                Log.e("11111", "ACCESS_COARSE_LOCATION");
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                Log.e("11111", "ACCESS_FINE_LOCATION");
            }
        }
        ///---------------------------------------------------
        //text0 = (TextView)findViewById(R.id.Text0);  //得到提示栏句柄
        edit0 = (EditText) findViewById(R.id.Edit0);   //得到输入框句柄
        sv = (ScrollView) findViewById(R.id.ScrollView01);  //得到翻页句柄
        tv_in = (TextView) findViewById(R.id.in);      //得到数据显示句柄

        //如果打开本地蓝牙设备不成功，提示信息，结束程序
        if (_bluetooth == null) {
            Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 设置设备可以被搜索
        new Thread() {
            public void run() {
                if (_bluetooth.isEnabled() == false) {
                    _bluetooth.enable();
                }
            }
        }.start();
    }

    //发送按键响应
    public void onSendButtonClicked(View v) {
        int i = 0;
        int n = 0;
        if (_socket == null) {
            Toast.makeText(this, "请先连接HC模块", Toast.LENGTH_SHORT).show();
            return;
        }
//		if(edit0.getText().length()==0){
//			Toast.makeText(this, "请先输入数据", Toast.LENGTH_SHORT).show();
//			return;
//		}
        try {

            OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
//			byte[] bos = edit0.getText().toString().getBytes();
            if (receives == null) return;
//			for(i=0;i<bos.length;i++){
//				if(bos[i]==0x0a)n++;
//			}
//			byte[] bos_new = new byte[bos.length+n];
//			n=0;
//			for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
//				if(bos[i]==0x0a){
//					bos_new[n]=0x0d;
//					n++;
//					bos_new[n]=0x0a;
//				}else{
//					bos_new[n]=bos[i];
//				}
//				n++;
//			}

            os.write(receives);
        } catch (IOException e) {
        }
    }

    //接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
                // 响应返回结果
                if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                    // MAC地址，由DeviceListActivity设置返回
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // 得到蓝牙设备句柄
                    _device = _bluetooth.getRemoteDevice(address);

                    // 用服务号得到socket
                    try {
                        _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                    }
                    //连接socket
                    Button btn = (Button) findViewById(R.id.BtnConnect);
                    try {
                        _socket.connect();
                        Toast.makeText(this, "连接" + _device.getName() + "成功！", Toast.LENGTH_SHORT).show();
                        btn.setText("断开");
                    } catch (IOException e) {
                        try {
                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                            _socket.close();
                            _socket = null;
                        } catch (IOException ee) {
                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                        }

                        return;
                    }

                    //打开接收线程
                    try {
                        is = _socket.getInputStream();   //得到蓝牙数据输入流
                    } catch (IOException e) {
                        Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (bThread == false) {
                        readThread.start();
                        bThread = true;
                    } else {
                        bRun = true;
                    }
                }
                break;
            default:
                break;
        }
    }

    //接收数据线程
    Thread readThread = new Thread() {
        public void run() {
            int num = 0;
            byte[] buffer = new byte[1024];
            byte[] buffer_new = new byte[1024];
            int i = 0;
            int n = 0;
            bRun = true;
            //接收线程
            while (true) {
                try {
                    while (is.available() == 0) {
                        while (bRun == false) {
                        }
                    }
                    while (true) {
                        if (!bThread)//跳出循环
                            return;

                        num = is.read(buffer);         //读入数据
                        n = 0;

                        String s0 = new String(buffer, 0, num);
                        byte[] bufferNow = new byte[num];
                        System.arraycopy(buffer, 0, bufferNow, 0, num);
                        //crc校验
//						boolean isFlag = checkCRC(bufferNow);
                        //和检验
                        boolean isFlag = checkSum(bufferNow);
                        Log.i("myblue", String.valueOf(isFlag));
                        if (!isFlag) {
                            smsg += "检验错误！！\n";
                            return;
                        } else {
                            if (bufferNow.length > 34) {
                                byte[] oldReceives = new byte[bufferNow.length];
                                System.arraycopy(bufferNow, 0, oldReceives, 0, bufferNow.length);
                                //接收的数据解密
                                byte[] keyReceiveData = new byte[39];
                                System.arraycopy(oldReceives, 6, keyReceiveData, 0, 39);
                                keyReceiveData = decodeKey(keyReceiveData);

                                byte[] deviceIdKeySend = new byte[20];
                                System.arraycopy(keyReceiveData, 19, deviceIdKeySend, 0, 20);
                                //返回数据
                                oldReceives[5] = 25;
                                byte[] sendDate = new byte[25];
                                System.arraycopy(deviceIdKeySend, 0, sendDate, 5, 20);
                                //随机秘钥
                                sendDate[0] = 1;
                                sendDate[1] = 2;
                                sendDate[2] = 3;
                                sendDate[3] = 4;

                                //新的密钥
                                // System.arraycopy(sendDate, 0, newkeys, 0, 4);

                                //心跳
                                sendDate[4] = 10;
                                //加密
                                //加密前
                                Log.i("加密前：", bytesToHexString(sendDate));
                                System.arraycopy(sendDate, 0, oldReceives, 6, 25);
                                Log.i("加密前：", bytesToHexString(oldReceives));


                                sendDate = decodeKey(sendDate);
                                Log.i("加密后：", bytesToHexString(sendDate));
//								Log.i("解密：",bytesToHexString( decodeKey(sendDate)));
                                //赋值
                                System.arraycopy(sendDate, 0, oldReceives, 6, 25);
                                oldReceives[32] = (byte) 0x88;
                                byte[] newRec = new byte[33];
                                System.arraycopy(oldReceives, 0, newRec, 0, 33);
                                //检验位
                                newRec[31] = getCheckSum(newRec);
                                receives = newRec;
                                smsg += "\n待发送数据" + bytesToHexString(receives) + "--------------\n";
                                Log.i("待发送:", bytesToHexString(receives));
                                handler.sendMessage(handler.obtainMessage());
                            } else {
                                smsg += bytesToHexString(bufferNow) + "--------------\n";

                                String startmsg = bytesToHexString(bufferNow);
                                Log.i("启动接收的数据", startmsg);

                                byte[] jimi = new byte[1];
                                jimi[0]=bufferNow[6];
                                jimi =  decodenewKey(jimi);
                                Log.i("启动接收的数据解密", bytesToHexString(jimi));
                                //发送显示消息，进行显示刷新
                                handler.sendMessage(handler.obtainMessage());
                            }
                        }

                        String s_old = bytesToHexString(bufferNow);
//						fmsg+=s0;    //保存收到数据
                        fmsg += s_old;    //保存收到数据
                        Log.i("myblue", s_old);
                        for (i = 0; i < num; i++) {
                            if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
                                buffer_new[n] = 0x0a;
                                i++;
                            } else {
                                buffer_new[n] = buffer[i];
                            }
                            n++;
                        }
                        byte[] bufferNow2 = new byte[n];
                        System.arraycopy(buffer_new, 0, bufferNow2, 0, n);
                        //解密数组截取
                        int dataLen = bufferNow2.length - 8;
                        byte[] keySrc = new byte[dataLen];
                        System.arraycopy(buffer_new, 6, keySrc, 0, dataLen);
                        keySrc = decodeKey(keySrc);
                        System.arraycopy(keySrc, 0, bufferNow2, 6, dataLen);

                        //封装：测试用，后续用实体封装
                        //充电桩ID asicc
                        byte[] deviceId = new byte[18];
                        System.arraycopy(keySrc, 0, deviceId, 0, 18);
                        //充电模式
                        byte[] deviceMode = new byte[1];
                        deviceMode[0] = keySrc[18];
                        //序列秘钥ID asicc
                        byte[] deviceIdKey = new byte[20];
                        System.arraycopy(keySrc, 18, deviceIdKey, 0, 20);
                        //协议前数组
                        byte[] startBytes = new byte[6];
                        System.arraycopy(buffer_new, 0, startBytes, 0, 6);
                        //协议后数组
                        byte[] endBytes = new byte[2];
                        System.arraycopy(buffer_new, 45, endBytes, 0, 2);


                        String s_new = bytesToHexString(bufferNow2);
                        String s_new_asicc = bytesToHexString(startBytes) + "--充电桩ID:" + new String(deviceId, "ascii") + "--充电模式:" + bytesToHexString(deviceMode) + "--序列秘钥ID:" + new String(deviceIdKey, "ascii") + bytesToHexString(endBytes);
                        Log.i("myblue", s_new);
                        Log.i("myblue", s_new_asicc);
                        String s = new String(buffer_new, 0, n);
//						smsg+=s;   //写入接收缓存
                        smsg += s_new_asicc + "---------------------\n";   //写入接收缓存
                        if (is.available() == 0) break;  //短时间没有数据才跳出进行显示
                    }
                    //发送显示消息，进行显示刷新
                    handler.sendMessage(handler.obtainMessage());
                } catch (Exception e) {
                }
            }
        }
    };

    /**
     * 数据校验：1字节，和校验，取低字节
     *
     * @param bufferNow
     * @return
     */
    private boolean checkSum(byte[] bufferNow) {
        int sum = 0;
        int length = bufferNow.length;
        for (int i = 0; i < length - 2; i++) {
            sum += (int) bufferNow[i];
        }
        byte end = (byte) (sum & 0xff);
        if (end == bufferNow[length - 2]) {
            return true;
        } else {
            return false;
        }
    }

    private byte getCheckSum(byte[] bufferNow) {
        int sum = 0;
        int length = bufferNow.length;
        for (int i = 0; i < length - 2; i++) {
            sum += (int) bufferNow[i];
        }
        byte end = (byte) (sum & 0xff);
        return end;

    }

    /**
     * 检查crc以及数据格式是否正确
     *
     * @param crcBytes：包含数服协议和crc:01 03 02 数据 crc1 crc2
     * @return
     */
    public static boolean checkCRC(byte[] crcBytes) {
        //获取crc效验
//        if (crcBytes == null || crcBytes.length < 2) return false;
//        int lenAll = crcBytes.length;
//        byte[] noCrcBytes = new byte[lenAll - 2];
//        System.arraycopy(crcBytes, 0, noCrcBytes, 0, lenAll - 2);
//        int crc = CRC16.calcCrc16(noCrcBytes);
//        byte[] newCrcs = MyByte.hexStringToBytes(String.format("%04x", crc));
//        if (newCrcs[1] == crcBytes[lenAll - 2] || newCrcs[0] == crcBytes[lenAll - 2]) {
//            return true;
//        } else {
            return false;
//        }
    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    public byte[] decodeKey(byte[] src) {
        if (src == null) return src;
        for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
        {
            src[j] = (byte) (src[j] ^ keys[j % 4]);
        }
        return src;
    }


    public byte[] decodenewKey(byte[] src) {
        if (src == null) return src;
        for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
        {
            src[j] = (byte) (src[j] ^ newkeys[j % 4]);
        }
        Log.i("加密后", "sendjiami: " + bytesToHexString(src));
        return src;
    }


    //消息处理队列
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//			try {
//				tv_in.setText(URLDecoder.decode(smsg, "GBK"));   //显示数据
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
            tv_in.setText(smsg);
            sv.scrollTo(0, tv_in.getMeasuredHeight()); //跳至数据最后一页
        }
    };

    //关闭程序掉用处理部分
    public void onDestroy() {
        super.onDestroy();
        if (_socket != null)  //关闭连接socket
            try {
                _socket.close();
            } catch (IOException e) {
            }
        //	_bluetooth.disable();  //关闭蓝牙服务
    }

    //菜单处理部分
  /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {//建立菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) { //菜单响应函数
        switch (item.getItemId()) {
        case R.id.scan:
        	if(_bluetooth.isEnabled()==false){
        		Toast.makeText(this, "Open BT......", Toast.LENGTH_LONG).show();
        		return true;
        	}
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.quit:
            finish();
            return true;
        case R.id.clear:
        	smsg="";
        	ls.setText(smsg);
        	return true;
        case R.id.save:
        	Save();
        	return true;
        }
        return false;
    }*/

    //连接按键响应函数
    public void onConnectButtonClicked(View v) {

        if (_bluetooth.isEnabled() == false) {  //如果蓝牙服务不可用则提示
            Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
            return;
        }


        //如未连接设备则打开DeviceListActivity进行设备搜索
        Button btn = (Button) findViewById(R.id.BtnConnect);
        if (_socket == null) {
            Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
        } else {
            //关闭连接socket
            try {
                bRun = false;
                Thread.sleep(2000);

                is.close();
                _socket.close();
                _socket = null;

                btn.setText("连接");
            } catch (IOException e) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    //启动按键相应函数
    public void onStartButtonClicked(View view) {
        Start();
    }


    //停止按键相应函数
    public void onStopButtonClicked(View view) {
        Stop();
    }


    //保存按键响应函数
    public void onSaveButtonClicked(View v) {
        Save();
    }

    //清除按键响应函数
    public void onClearButtonClicked(View v) {
        smsg = "";
        fmsg = "";
        tv_in.setText(smsg);
        return;
    }

    //退出按键响应函数
    public void onQuitButtonClicked(View v) {

        //---安全关闭蓝牙连接再退出，避免报异常----//
        if (_socket != null) {
            //关闭连接socket
            try {
                bRun = false;
                Thread.sleep(2000);

                is.close();
                _socket.close();
                _socket = null;
            } catch (IOException e) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        finish();
    }

    //启动充电桩

    private void Start() {
        if (_socket == null) {
            Toast.makeText(this, "请先连接HC模块", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
            //启动命令9个字节
            byte[] start = new byte[9];
            //帧头和尾部
            start[0] = (byte) 0xA8;
            start[1] = (byte) 0x8A;
            //协议类型
            start[2] = (byte) 0x01;
            //加密类型
            start[3] = (byte) 0x01;
            //消息类型
            start[4] = (byte) 0x05;
            //数据长度
            start[5] = (byte) 0x01;
            //有效数据
            start[6] = (byte) 0x01;
            //校验数据
            start[7] = getCheckSum(start);
            //结束符
            start[8] = (byte) 0xA8;
            Log.i("启动请求", "加密前: " + bytesToHexString(start));
            //加密前有效数据
            byte[] sendjiami = new byte[1];
            sendjiami[0] = 0x01;
            //加密后
            sendjiami = decodenewKey(sendjiami);

            System.arraycopy(sendjiami, 0, start, 6, 1);
            Log.i("启动请求", "加密后: " + bytesToHexString(start));
            os.write(start);
        } catch (Exception e) {
        }
    }


    //停止

    private void Stop() {
        if (_socket == null) {
            Toast.makeText(this, "请先连接HC模块", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
            //启动命令9个字节
            byte[] start = new byte[9];
            //帧头和尾部
            start[0] = (byte) 0xA8;
            start[1] = (byte) 0x8A;
            //协议类型
            start[2] = (byte) 0x01;
            //加密类型
            start[3] = (byte) 0x01;
            //消息类型
            start[4] = (byte) 0x05;
            //数据长度
            start[5] = (byte) 0x01;
            //加密前有效数据
            byte[] sendjiami = new byte[1];
            sendjiami[0] = 0x02;
            //加密后
            sendjiami = decodenewKey(sendjiami);
            System.arraycopy(sendjiami, 0, start, 6, 1);
            //校验数据
            start[7] = getCheckSum(start);
            //结束符
            start[8] = (byte) 0xA8;
            os.write(start);
        } catch (IOException e) {
        }
    }


    //保存功能实现
    private void Save() {
        //显示对话框输入文件名
        LayoutInflater factory = LayoutInflater.from(BTClient.this);  //图层模板生成器句柄
        final View DialogView = factory.inflate(R.layout.sname, null);  //用sname.xml模板生成视图模板
        new AlertDialog.Builder(BTClient.this)
                .setTitle("文件名")
                .setView(DialogView)   //设置视图模板
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() //确定按键响应函数
                        {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText text1 = (EditText) DialogView.findViewById(R.id.sname);  //得到文件名输入框句柄
                                filename = text1.getText().toString();  //得到文件名

                                try {
                                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  //如果SD卡已准备好

                                        filename = filename + ".txt";   //在文件名末尾加上.txt
                                        File sdCardDir = Environment.getExternalStorageDirectory();  //得到SD卡根目录
                                        File BuildDir = new File(sdCardDir, "/data");   //打开data目录，如不存在则生成
                                        if (BuildDir.exists() == false) BuildDir.mkdirs();
                                        File saveFile = new File(BuildDir, filename);  //新建文件句柄，如已存在仍新建文档
                                        FileOutputStream stream = new FileOutputStream(saveFile);  //打开文件输入流
                                        stream.write(fmsg.getBytes());
                                        stream.close();
                                        Toast.makeText(BTClient.this, "存储成功！\n\r" + saveFile, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(BTClient.this, "没有存储卡！", Toast.LENGTH_LONG).show();
                                    }

                                } catch (IOException e) {
                                    return;
                                }


                            }
                        })
                .setNegativeButton("取消",   //取消按键响应函数,直接退出对话框不做任何处理
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();  //显示对话框
    }
}