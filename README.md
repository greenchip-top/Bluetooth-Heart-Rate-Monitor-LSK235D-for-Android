

# Bluetooth-Heart-Rate-Monitor-LSK235D-for-Android

### Introduction
LSK235D-DIY Bluetooth Heart Rate Monitor is a bluetooth 4.0 heart rate monitor chest strap product which can be directly connected to the 4.0 bluetooth on the mobile phone.Download the APP to your phone,and you can monitor your heart rate in real time, display the current sporting heart rate condition and help you control your exercise intensity (burn fat,aerobic exercise,anaerobic exercise etc.)to reach your athletic goals.It is also a sporting test DIY ideal accessory.

### Features
Using 2.4GHz bluetooth 4.0 connect to APP on the phone;
Low power consumption, powered by CR2032 battery;
Low voltage detection function;
When you wear it on your chest, it will automatically wake up and wait for the phone to connect to the bluetooth;
Take off the belt, the bluetooth will turn off automatically, 15 seconds will enter into power saving function.
Communication distance is up to 10M (no obstacles);

[LSK235D hardware](https://dwz.cn/Y4kKOnlY)

### Wearing Picture

![image](https://github.com/greenchip-top/Bluetooth-Heart-Rate-Monitor-LSK235D-for-Android/raw/master/image/1.png)

![image](https://github.com/greenchip-top/Bluetooth-Heart-Rate-Monitor-LSK235D-for-Android/raw/master/image/2.png)

### Chest Strap

![image](https://github.com/greenchip-top/Bluetooth-Heart-Rate-Monitor-LSK235D-for-Android/raw/master/image/3.jpg)

### Setup Instruction

Before wear the Bluetooth heart rate monitor chest strap,firstly wet the two sensor strips on the inside and outside of the two soft gels.In order to facilitate good contact between the chest strap and the chest, and to ensure that the heart rate monitor is worn close to the heart, it is beneficial to improve the accuracy of heart rate detection while avoiding wearing on the chest hair, so as not to affect the accurate measurement.

The PCBA board of the LSK235D-DIY Bluetooth heart rate monitor will be buckled onto the chest strap.Please pay attention to the left and right direction of the PCBA board are the same with ourselves.If it is in dry and cold weather condition, it may take a few minutes of sport exercises to improve its accuracy.

The PCBA of LSK235D-DIY bluetooth heart rate monitor has heart rate signal indicator light ,when it inspects the normal heart rate signal, LED light will flash regularly indicating that the LSK235D-DIY Bluetooth heart rate monitor has been working properly and you can do exercise.

![image](https://github.com/greenchip-top/Bluetooth-Heart-Rate-Monitor-LSK235D-for-Android/raw/master/image/4.jpg)

![image](https://github.com/greenchip-top/Bluetooth-Heart-Rate-Monitor-LSK235D-for-Android/raw/master/image/5.jpg)

### Universal Heart Rate APP As Follows

★ Polar beat

★ Runtastic

★ Wahoo fitness

★ Run keeper

★ JoiiSports

★ Jogger

★ Heart Rate OR（60Beat）

### Using Instructions

###### Using Step

The phone needs to download the Bluetooth heart rate APP (see the fourth point for detailed information) and install it.

Please wear the LSK235D-DIY bluetooth heart rate monitor on your chest.

Click the bluetooth to“ON”on the phone “SETTING”.

Open the downloaded bluetooth APP,search for the LSK235D-DIY bluetooth heart rate monitor,connect and bound to the device.

###### ATTENTION

When the low-power indicator is displayed in the upper left corner of the interface,it indicated that the battery voltage of the LSK235D-DIY bluetooth heart rate meter has been lower than 2.5V. Need to replace the battery recently.

When you take off LSK235D-DIY bluetooth heart rate monitor, it will automatically enter into SLEEP power saving mode after 15 seconds,and the bluetooth will be disconnected.

The program shows that the heart rate range is 30~250 Bpm, if it is out of range, it will not be displayed.

In the standby state, the CR2032 battery life is about 24 months, if you exercise an hour a day, the battery life is about six months.

### Electrical Parameter

Environment temperature is 25℃

| **Parameter**           | Symbol | **Working condition** | **Minimum value** | **Standard value** | **Maximum value** | **Unit** |
| ----------------------- | ------ | --------------------- | ----------------- | ------------------ | ----------------- | -------- |
| Working voltage         | VDD    | -                     | 2．4              | 3                  | 3．6              | V        |
| Working current         | Iss    | VDD = 3V              | 0.6               | 0.24               | 0.44              | mA       |
| Communication frequency | F      |                       | -                 | 2．4               | -                 | GHz      |

### Demo Design Description

##### **The main type of bluetooth**

| Class             | Desc                                                         |
| ----------------- | ------------------------------------------------------------ |
| Bluetooth Device  | Indicates  a remote Bluetooth device.  You can use it to query  information about the device, such as the device's name, address, class, and  binding status. |
| Bluetooth Gatt    | Configurate file agent  related to low consumption bluetooth communication. |
| Bluetooth Adapter | Local bluetooth adapter You can discover other Bluetooth devices, query the list of bound (paired) devices, and instantiate a BluetoothDevice using a known Mac address. |

##### **UUID** **value**

| **Bluetooth Heart rate Service**           |                                      |
| ------------------------------------------ | ------------------------------------ |
| **UUID**                                   | 0000180d-0000-1000-8000-00805f9b34fb |
| **Characteristic**  **（UUID****）**       | 00002a37-0000-1000-8000-00805f9b34fb |
| **Bluetooth Battery Service**              |                                      |
| **UUID**                                   | 0000180f-0000-1000-8000-00805f9b34fb |
| **Characteristic**  **（UUID****）**       | 00002a19-0000-1000-8000-00805f9b34fb |
| **Bluetooth Pedometer Service  (Useless)** |                                      |
| **UUID**                                   | 0000fff0-0000-1000-8000-00805f9b34fb |
| **Characteristic**  **（UUID Send) **      | 0000fff3-0000-1000-8000-00805f9b34fb |
| **Characteristic**  **（UUID Receive)**    | 0000fff4-0000-1000-8000-00805f9b34fb |

##### Bluetooth Operation

First define 8 global variables to facilitate Bluetooth related operations at any location.

```java
BluetoothAdapter bluetoothAdapter;

BluetoothDevice bluetoothDevice;

BluetoothGatt bluetoothGatt;

Map<String, Object> map;

UUID HEART_RATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");

UUID HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

UUID BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");

UUID BATTERY_ENERGY = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
```

##### **Bluetooth Permissions**

To use the Bluetooth function in an app, you must declare Bluetooth permissions. Add the following code in the AndroidManifest.xml file.

```xml
<!-- Allows applications to connect to paired bluetooth devices -->
<uses-permission android:name="android.permission.BLUETOOTH" />
<!-- Allow programs to discover and pair bluetooth devices -->
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<!-- This permission is used for network location -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<!-- This permission is used to access GPS positioning -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

##### **Turn on the bluetooth on the phone**

Call the BluetoothAdapter's isEnabled () method to check whether Bluetooth is currently enabled. If this method returns false, it means that Bluetooth is disabled.To enable Bluetooth, you need to set the Intent Action to ACTION_REQUEST_ENABLE, and then start Bluetooth via startActivityForResult (). The specific code is as follows.

```java
// Get Bluetooth adapter
bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
if (!bluetoothAdapter.isEnabled()) {
    Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enabler, 20201);
}
```

When the user clicks "Refuse" or "Allow" , Activity will receive the result code in the onActivityResult () callback.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 20201) {
        switch (resultCode) {
            // Click the confirm button
            case Activity.RESULT_OK:
                break;
            // Click cancel or return
            case Activity.RESULT_CANCELED:
                break;
        }
    }
}
```

##### **Apply for Bluetooth Permission**

It is recommended to apply for permission every time before searching for Bluetooth, and make sure that the system has open permissions. For the convenience of calling, the action of applying permissions is put into the custom getPermission () method.

```java
private void getPermission() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        int permissionCheck = 0;
        permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCheck += checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( // Request authorization
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    20202);
        }
    }
}
```

When the user clicks "Refuse" or "Allow" , the Activity will receive the result code in the onRequestPermissionsResult () callback.

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 20202) {
        if (hasAllPermissionGranted(grantResults)) {
            // Request authorization pass
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
```

##### **Search for Bluetooth Devices**

```java
bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
bluetoothAdapter.startDiscovery();
IntentFilter filter = new IntentFilter();
filter.addAction(BluetoothDevice.ACTION_FOUND);
filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
registerReceiver(mBluetoothReceiver, filter);
```

After the adapter searches for the Bluetooth device, the result is broadcasted, so you need to customize a class that inherits the broadcast and obtain and process the search result of the Bluetooth device in the onReceive method.

```java
private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // Device found
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                // device.getName() // Get Bluetooth device name
                // device.getAddress() // Get Bluetooth device address
            }
        }
    }
};
```

##### **Connect a Bluetooth Device**

The following code implements Bluetooth connection and data acquisition process.

```java
bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
bluetoothAdapter.cancelDiscovery(); // Stop searching for Bluetooth devices
bluetoothDevice = bluetoothAdapter.getRemoteDevice(“Bluetooth Device Address”);
bluetoothGatt = bluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) { // Bluetooth connection successful
            gatt.discoverServices(); // Start to search the service of Bluetooth device, make sure to call this method, otherwise the service cannot be obtained
            //////
            Message message = new Message();
            message.what = 1;
            message.obj = device;
            handler.sendMessage(message);
        }
        if (newState == BluetoothProfile.STATE_DISCONNECTED) { // Bluetooth disconnected
            gatt.close();
            Message message = new Message();
            message.what = 2;
            message.obj = device;
            handler.sendMessage(message);
        }
    }
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        if (characteristic.getUuid().equals(BATTERY_ENERGY)) { // Electricity service
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                map = new HashMap<>();
                map.put("device", device);
                map.put("electric", new BigInteger(1, data).toString(10));
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
        if (characteristic.getUuid().equals(HEART_RATE_MEASUREMENT)) { // Heart rate service
            byte[] data = characteristic.getValue();
            int index = ((data[0] & 0x01) == 1) ? 2 : 1;
            int format = (index == 1) ? BluetoothGattCharacteristic.FORMAT_UINT8 : BluetoothGattCharacteristic.FORMAT_UINT16;
            int value = characteristic.getIntValue(format, index);
            String description = value + " bpm"; // Get heart rate
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
            /* Solve oncharacteristicchanged no callback on some devices */
            for (BluetoothGattDescriptor clientConfig : characteristic_01.getDescriptors()) {
                clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(clientConfig);
            }
            /* Solve oncharacteristicchanged no callback on some devices */
        }
        if (characteristic.getUuid().equals(BATTERY_ENERGY)) { // Electricity service
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                map = new HashMap<>();
                map.put("device", device);
                map.put("electric", new BigInteger(1, data).toString(10));
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
        if (status == BluetoothGatt.GATT_SUCCESS) { // Discovery services
            BluetoothGattCharacteristic characteristic = gatt.getService(HEART_RATE).getCharacteristic(HEART_RATE_MEASUREMENT);
            gatt.setCharacteristicNotification(characteristic, true);
            gatt.readCharacteristic(characteristic);
            /* Solve oncharacteristicchanged no callback on some devices */
            for (BluetoothGattDescriptor clientConfig : characteristic.getDescriptors()) {
                clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(clientConfig);
            }
            /* Solve oncharacteristicchanged no callback on some devices */
        }
    }
});
Handler handler = new Handler() {
    public void handleMessage(Message msg) {
        // msg.what // Data mark
        // msg.obj // Data value
        super.handleMessage(msg);
    }
};
```

All data needs to be called back to the main thread through the Handler, and the data interaction is transferred by the global variable Map <String, Object>.

Illustration of data obtained through Handler：

| Message                          | Using                                                        |
| -------------------------------- | ------------------------------------------------------------ |
| msg.obj.toString()               | Handler callback data value                                  |
| map.get("device").toString()     | Bluetooth  device name                                       |
| map.get("electric").toString()   | The  value of quantity electricity（%）  Remarks: The value transmitted by the  Bluetooth terminal is in byte [] format, and the value is directly converted  into a decimal number, which is the value of the quantity electricity. The  sample code already contains the conversion process. |
| map.get("heart_rate").toString() | Heart  rate value（bpm）  Remarks: The value  transmitted by the Bluetooth terminal is byte [] format, first determine the  type of heart rate is UINT8 or UINT16, and then convert byte [] to the value  of this type according to the type of heart rate. The sample code already  contains the conversion process. |
| msg.what                         | Handler callback data tag                                    |