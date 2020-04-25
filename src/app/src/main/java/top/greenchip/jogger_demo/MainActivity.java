package top.greenchip.jogger_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("ALL")
@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toast toast;
    ListView listView;
    LinearLayout heartLayout;
    TextView heart, bindText, button, tips;
    ProgressBar progressBar;
    String electricity;
    String devicesName;
    List<Map<String, Object>> listData = new ArrayList<>(); // 表示数据源
    SimpleAdapter adapter;
    //////
    Map<String, Object> map;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothGatt bluetoothGatt;
    final UUID HEART_RATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"); // 通用的蓝牙心率服务service的UUID
    final UUID HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"); // 通用的蓝牙心率服务service的UUID，里面的characteristic特征UUID
    final UUID BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb"); // 通用的蓝牙电池服务service的UUID
    final UUID BATTERY_ENERGY = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"); // 通用的蓝牙电池服务service的UUID，里面的characteristic特征UUID(电池当前电量)
    final UUID STEP = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"); // 蓝牙计步服务service的UUID
    final UUID STEP_COUNTER_SEND = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb"); // 蓝牙计步服务service的UUID，里面的characteristic特征UUID(发送命令)
    final UUID STEP_COUNTER_RECEIVE = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb"); // 蓝牙计步服务service的UUID，里面的characteristic特征UUID(返回数据)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toast = Toast.makeText(MainActivity.this, null, Toast.LENGTH_LONG);
        listView = this.findViewById(R.id.listView);
        heartLayout = this.findViewById(R.id.heartLayout);
        heart = this.findViewById(R.id.heart);
        bindText = this.findViewById(R.id.bindText);
        button = this.findViewById(R.id.button);
        tips = this.findViewById(R.id.tips);
        progressBar = this.findViewById(R.id.progressBar);
        //////
        adapter = new SimpleAdapter(MainActivity.this, listData, R.layout.bind_text, new String[]{"devices", "address"}, new int[]{R.id.devices, R.id.address});
        listView.setAdapter(adapter);
        button.setOnClickListener(this);
        //////
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Map<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);
                if (map != null) {
                    button.setBackgroundColor(0xff666666);
                    button.setText("断开设备");
                    tips.setText("");
                    tips.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    heartLayout.setVisibility(View.VISIBLE);
                    heart.setText("");
                    bindText.setText("正在连接 " + map.get("devices").toString() + " ...");
                    devicesName = map.get("devices").toString();
                    //////
                    cancelSearch(); // 停止搜索蓝牙设备
                    connect(map.get("address").toString()); // 连接蓝牙设备
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startHeartBeat(); // 开始心跳动画
    }

    @Override
    public void onPause() {
        super.onPause();
        stopHeartBeat(); // 停止心跳动画
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (button.getText().toString().equals("搜索蓝牙设备")) {
                    // 获取蓝牙适配器
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) { // 弹出对话框提示用户是后打开
                        Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enabler, 20201);
                        // 不做提示，直接打开，不建议用下面的方法，有的手机会有问题。
                        // mBluetoothAdapter.enable();
                    } else {
                        getPermission();
                    }
                } else if (button.getText().toString().equals("停止搜索")) {
                    cancelSearch(); // 停止搜索蓝牙设备
                    //////
                    button.setBackgroundColor(0xff008577);
                    button.setText("搜索蓝牙设备");
                    tips.setText("");
                    tips.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    heartLayout.setVisibility(View.GONE);
                } else if (button.getText().toString().equals("断开设备")) {
                    disconnect(); // 手动断开蓝牙设备
                    //////
                    button.setVisibility(View.GONE);
                    tips.setText("请稍候 ...");
                    tips.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() { // 延时1秒执行
                        public void run() {
                            heart.setText("");
                            bindText.setText("蓝牙设备已断开");
                            button.setVisibility(View.VISIBLE);
                            button.setBackgroundColor(0xff008577);
                            button.setText("搜索蓝牙设备");
                            tips.setText("");
                            tips.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            listData.removeAll(listData);
                            adapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        if (requestCode == 20201) {
            switch (resultCode) {
                // 点击确认按钮
                case Activity.RESULT_OK: // 用户选择开启 Bluetooth，Bluetooth 会被开启
                    getPermission();
                    break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED: // 用户拒绝打开 Bluetooth, Bluetooth 不会被开启
                    toast.setText("开启蓝牙才能搜索设备哦");
                    toast.show();
                    break;
            }
        }
    }

    /* 申请权限 start */
    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        20202);// 自定义常量,任意整型
            } else {
                joggerSearch();
            }
        } else {
            joggerSearch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20202) {
            if (hasAllPermissionGranted(grantResults)) {
                joggerSearch();
            }
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    /* 申请权限 end */

    public void joggerSearch() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 获取蓝牙适配器
        bluetoothAdapter.startDiscovery(); // 开始搜索蓝牙设备
        IntentFilter filter = new IntentFilter(); // 注册广播接受者，可以监听是否搜到设备
        filter.addAction(BluetoothDevice.ACTION_FOUND); // 发现蓝压设备，每搜到一个设备发送一条广播
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); // 蓝牙状态改变(连接蓝牙，断开蓝牙)
        registerReceiver(mBluetoothReceiver, filter);
        //////
        heartLayout.setVisibility(View.GONE);
        button.setBackgroundColor(0xff666666);
        button.setText("停止搜索");
        tips.setText("搜索中 ...");
        tips.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (listData.size() == 0) {
                        Map<String, Object> map = new HashMap<>();
                        if (device.getName() == null) {
                            map.put("devices", "Unknown");
                        } else {
                            map.put("devices", device.getName().trim());
                        }
                        map.put("address", device.getAddress());
                        listData.add(map);
                        adapter.notifyDataSetChanged();
                    } else {
                        boolean add_ok = true;
                        for (int i = 0; i < listData.size(); i++) {
                            Map<String, Object> map_s = listData.get(i);
                            if (map_s.get("address").toString().equals(device.getAddress())) {
                                add_ok = false;
                            }
                        }
                        if (add_ok) {
                            Map<String, Object> map = new HashMap<>();
                            if (device.getName() == null) {
                                map.put("devices", "Unknown");
                            } else {
                                map.put("devices", device.getName().trim());
                            }
                            map.put("address", device.getAddress());
                            listData.add(map);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };

    // 连接蓝牙设备
    public void connect(final String device) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery(); // 停止搜索蓝牙设备
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(device); // 获得选择的蓝牙设备
        bluetoothGatt = bluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) { // 蓝牙连接成功
                    gatt.discoverServices(); // 开始搜索蓝牙设备的服务，一定要调用此方法，否则获取不到服务
                    //////
                    Message message = new Message();
                    message.what = 1;
                    message.obj = device;
                    handler.sendMessage(message);
                }
                if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 蓝牙连接已断开
                    gatt.close();
                    Message message = new Message();
                    message.what = 2;
                    message.obj = device;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if (characteristic.getUuid().equals(STEP_COUNTER_SEND)) { // 计步服务
                    characteristic.setValue(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x03});
                    gatt.writeCharacteristic(characteristic);
                    characteristic.setValue(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x81, (byte) 0x88});
                    gatt.writeCharacteristic(characteristic);
                    //////
                    BluetoothGattCharacteristic characteristic_01 = gatt.getService(STEP).getCharacteristic(STEP_COUNTER_RECEIVE);
                    gatt.setCharacteristicNotification(characteristic_01, true);
                    gatt.readCharacteristic(characteristic_01);
                    /* 解决onCharacteristicChanged在部分设备上没有回调 start */
                    for (BluetoothGattDescriptor clientConfig : characteristic_01.getDescriptors()) {
                        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(clientConfig);
                    }
                    /* 解决onCharacteristicChanged在部分设备上没有回调 end */
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                if (characteristic.getUuid().equals(BATTERY_ENERGY)) { // 电量服务
                    final byte[] data = characteristic.getValue();
                    if (data != null && data.length > 0) {
                        map = new HashMap<>();
                        map.put("device", device);
                        map.put("electric", new BigInteger(1, data).toString(10)); // 把byte[]转换为10进制数显示
                        Message message = new Message();
                        message.what = 3;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                if (characteristic.getUuid().equals(STEP_COUNTER_RECEIVE)) { // 计步服务
                    if (characteristic.getValue()[19] == 0x03) {
                        int hex_01 = characteristic.getValue()[0];
                        int hex_02 = characteristic.getValue()[1];
                        int hex_03 = characteristic.getValue()[2];
                        int hex_04 = characteristic.getValue()[3];
                        int hex = hex_01 * 65536 + hex_02 * 4096 + hex_03 * 256 + hex_04;
                        //////
                        map = new HashMap<>();
                        map.put("device", device);
                        map.put("step", hex);
                        Message message = new Message();
                        message.what = 3;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                }
                if (characteristic.getUuid().equals(HEART_RATE_MEASUREMENT)) { // 心率服务
                    byte[] data = characteristic.getValue();
                    int index = ((data[0] & 0x01) == 1) ? 2 : 1;
                    int format = (index == 1) ? BluetoothGattCharacteristic.FORMAT_UINT8 : BluetoothGattCharacteristic.FORMAT_UINT16;
                    int value = characteristic.getIntValue(format, index);
                    String description = value + " bpm"; // 获取心率值
                    //////
                    map = new HashMap<>();
                    map.put("device", device);
                    map.put("heart_rate", description);
                    Message message = new Message();
                    message.what = 3;
                    message.obj = map;
                    handler.sendMessage(message);
                    //////
                    BluetoothGattCharacteristic characteristic_01 = gatt.getService(BATTERY).getCharacteristic(BATTERY_ENERGY);
                    gatt.setCharacteristicNotification(characteristic_01, true);
                    gatt.readCharacteristic(characteristic_01);
                    /* 解决onCharacteristicChanged在部分设备上没有回调 start */
                    for (BluetoothGattDescriptor clientConfig : characteristic_01.getDescriptors()) {
                        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(clientConfig);
                    }
                    /* 解决onCharacteristicChanged在部分设备上没有回调 end */
                }
                if (characteristic.getUuid().equals(BATTERY_ENERGY)) { // 电量服务
                    final byte[] data = characteristic.getValue();
                    if (data != null && data.length > 0) {
                        map = new HashMap<>();
                        map.put("device", device);
                        map.put("electric", new BigInteger(1, data).toString(10)); // 把byte[]转换为10进制数显示
                        Message message = new Message();
                        message.what = 3;
                        message.obj = map;
                        handler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) { // 发现服务
//                    if (map.get("name").toString().equals("QH6801B")) { // 计步的蓝牙硬件
//                        BluetoothGattCharacteristic characteristic = gatt.getService(STEP).getCharacteristic(STEP_COUNTER_SEND);
//                        gatt.setCharacteristicNotification(characteristic, true);
//                        gatt.readCharacteristic(characteristic);
//                        characteristic.setValue(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x03});
//                        gatt.writeCharacteristic(characteristic);
//                        characteristic.setValue(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x81, (byte) 0x88});
//                        gatt.writeCharacteristic(characteristic);
//                    } else {
                    BluetoothGattCharacteristic characteristic = gatt.getService(HEART_RATE).getCharacteristic(HEART_RATE_MEASUREMENT);
                    gatt.setCharacteristicNotification(characteristic, true);
                    gatt.readCharacteristic(characteristic);
                    /* 解决onCharacteristicChanged在部分设备上没有回调 start */
                    for (BluetoothGattDescriptor clientConfig : characteristic.getDescriptors()) {
                        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(clientConfig);
                    }
                    /* 解决onCharacteristicChanged在部分设备上没有回调 end */
//                    }
                }
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                bindText.setText("已连接 " + devicesName);
            }
            if (msg.what == 2) {
                heart.setText("");
                bindText.setText("蓝牙设备已断开");
                button.setVisibility(View.VISIBLE);
                button.setBackgroundColor(0xff008577);
                button.setText("搜索蓝牙设备");
                tips.setText("");
                tips.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                listData.removeAll(listData);
                adapter.notifyDataSetChanged();
            }
            if (msg.what == 3) {
                if (map.get("electric") != null) {
                    electricity = "  ( 电量 " + map.get("electric").toString() + "% )";
                } else if (map.get("heart_rate") != null) {
                    heart.setText(map.get("heart_rate").toString());
                } else if (map.get("step") != null) {

                }
                bindText.setText("已连接 " + devicesName + electricity);
            }
            super.handleMessage(msg);
        }
    };

    // 停止搜索蓝牙设备
    public void cancelSearch() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery(); // 停止搜索蓝牙设备
    }

    // 手动断开蓝牙设备
    public void disconnect() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect(); // 手动断开蓝牙设备
            bluetoothGatt.close();
        }
    }

    /* 心跳动画 start */
    // 模拟心脏跳动
    private void playHeartbeatAnimation(final View heartbeatView) {
        AnimationSet swellAnimationSet = new AnimationSet(true);
        swellAnimationSet.addAnimation(new ScaleAnimation(1.0f, 1.8f, 1.0f, 1.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)); // 控制大小及方向
        swellAnimationSet.addAnimation(new AlphaAnimation(1.0f, 0.8f)); // 控制透明度
        swellAnimationSet.setDuration(800);
        swellAnimationSet.setInterpolator(new AccelerateInterpolator());
        swellAnimationSet.setFillAfter(true);
        swellAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet shrinkAnimationSet = new AnimationSet(true);
                shrinkAnimationSet.addAnimation(new ScaleAnimation(1.8f, 1.0f, 1.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
                shrinkAnimationSet.addAnimation(new AlphaAnimation(0.8f, 1.0f));
                shrinkAnimationSet.setDuration(800);
                shrinkAnimationSet.setInterpolator(new DecelerateInterpolator());
                shrinkAnimationSet.setFillAfter(false);
                heartbeatView.startAnimation(shrinkAnimationSet); // 动画结束时重新开始，实现心跳的View
            }
        });
        heartbeatView.startAnimation(swellAnimationSet); // 实现心跳的View
    }

    // 开一个线程来处理心跳动画
    private class HeatbeatThread extends Thread {
        public void run() {
            while (heartbeatThread != null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        playHeartbeatAnimation(heart);
                    }
                });
                try {
                    Thread.sleep(1600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Thread heartbeatThread;

    // 开始心跳
    private void startHeartBeat() {
        if (heartbeatThread == null) {
            heartbeatThread = new HeatbeatThread();
        }
        if (!heartbeatThread.isAlive()) {
            heartbeatThread.start();
        }
    }

    // 停止心跳
    private void stopHeartBeat() {
        if (heartbeatThread != null && !heartbeatThread.isInterrupted()) {
            heartbeatThread.interrupt();
            heartbeatThread = null;
            System.gc();
        }
    }
    /* 心跳动画 end */

}
