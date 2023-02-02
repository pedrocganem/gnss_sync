import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const channelName = 'bluetooth.channel';
  static const bluetoothPlatform = MethodChannel(channelName);
  static const initializeBluetooth = "getBlue";
  static const disconnectDevice = "disconnectSocket";
  static const discoverDevice = "discoverBlue";
  static const pairedDevice = "allPaired";

  Future<void> initBluetooth() async {
    final result = await bluetoothPlatform.invokeMethod(initializeBluetooth);
    print(result);
  }

  Future<void> getBondedDevices() async {
    var devices = await bluetoothPlatform.invokeMethod(pairedDevice);
    debugPrint(devices);
  }

  Future<void> getDiscoveredDevices() async {
    var devices = await bluetoothPlatform.invokeMethod(discoverDevice);
    debugPrint(devices);
    if (devices != null) {
      debugPrint(devices);
    }
  }

  void disconnectFromDevice() async {
    await bluetoothPlatform.invokeMethod(disconnectDevice);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              'test',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          await initBluetooth();
          await getBondedDevices();
          await getDiscoveredDevices();
        },
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
