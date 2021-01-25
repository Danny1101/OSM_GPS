package fr.univbrest.osmandroid;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class DataIO extends MqttAndroidClient {
    public static final String serverURI = "tcp://test.mosquitto.org";
    public static final String subscribeTo = "mqtt_gps/devices/broadcast";
    public static final String publishTo = "mqtt_gps/clients";

    private Context context;
    private MqttConnectOptions mqttConnectOptions;
    private int connected = 0; // 0 = disconnected, 1 = connecting, 2 = connected

    //===============================================================================================
    class MQTT_ActionListener implements IMqttActionListener {

        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d("debug", "MQTT_ActionListener.onSuccess() called");
            connected = 2;
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.d("debug", "MQTT_ActionListener.onFailure() called");
            Toast.makeText(context,
                    "Ce nom d'utilisateur est déjà utilisé, veuillez en choisir un autre !",
                    Toast.LENGTH_LONG).show();
            connected = 0;
        }
    }

    //===============================================================================================

    public int getConnected(){
        return connected;
    }

    public DataIO(Context context, String username) {
        super(context, serverURI, username);
        this.context = context;
        connected = 1;
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        try {
            Log.d("debug", "DataIO() : bp1");
            this.connect(mqttConnectOptions, null, new MQTT_ActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    DataIO.this.setBufferOpts(disconnectedBufferOptions);
                    Log.d("debug", "Connected successfully");
                    try {
                        DataIO.this.subscribe(subscribeTo, 0, null, new MQTT_ActionListener(){});
                    } catch (MqttException e) {
                        Log.d("debug", "MQTT exception : " + e.getMessage());
                    }
                }
            });
        } catch (MqttException e) {
            Log.d("debug", "MQTT exception : " + e.getMessage());
        }
    }
    //===============================================================================================
    public boolean publish(String message){
        try {
            super.publish(publishTo, message.getBytes(), 0 , false);
        } catch (MqttException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

}
