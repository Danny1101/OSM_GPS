package fr.univbrest.osmandroid;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class UImodificator extends AsyncTask<Void, Location, Void> {

    private DataIO dataIO = null;
    private Context context = null;
    private String username = null;
    private MyLocation myLocation = null;
    private TextView central_text;
    private boolean keep_looping = true;
    private boolean init_ok = false;
    private String onRequestData = null;
    public static final int refresh_period_s = 5;

    public UImodificator(Context context, TextView central_text,final String username){
        this.context = context;
        this.username = username;
        this.central_text = central_text;
        myLocation = new MyLocation(UImodificator.this.context, refresh_period_s*1000);
        dataIO = new DataIO(UImodificator.this.context, username);
        init_ok = myLocation.init_is_ok();
    }

    public boolean isInit_ok(){
        return init_ok;// && dataIO.isConnected();
    }

    @Override
    protected void onPreExecute() {

        dataIO.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("debug","Received from " + topic + " message " + message);
                if(topic.equals("mqtt_gps/devices/broadcast") && message.toString().equals("Send your positions")) {
                    if (UImodificator.this.onRequestData != null) {
                        dataIO.publish(UImodificator.this.onRequestData);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    public void terminate(){
        keep_looping = false;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Location[] locations = {null};
        publishProgress(null);
        while(myLocation.getLocation() == null){
            wait_ms(1000);
        }
        // Wait for GPS updates
        while(keep_looping){
            locations[0] = sendMyCoordinates();

            // Gather all coordinates
            publishProgress(locations);

            this.wait_next_period();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Location... values) {
        if(values == null){
            central_text.setTextColor(Color.RED);
            central_text.setText("Veuillez attendre que les données soient récupérées du module GPS ...");
        }else{
            central_text.setTextColor(Color.BLACK);
            String str = "My location's latitude : " + values[0].getLatitude() + "\nMy location's longtitude : " + values[0].getLatitude() + "\n";
            central_text.setText(str);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        dataIO.close();
        myLocation = null;
    }

    // ====================================================================================

    private void wait_next_period(){
        this.wait_ms(1000*((refresh_period_s>5)? refresh_period_s : 5 ));
    }

    private void wait_ms(int wait_duration){
        try {
            Thread.sleep(wait_duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ====================================================================================

    private Location sendMyCoordinates(){
        Location location = myLocation.getLocation();
        onRequestData = username + " : " + location.getLatitude() + " " + location.getLongitude();
        Log.d("debug","sendMyCoordinates() : " + onRequestData);
        //dataIO.publish(onRequestData);
        return location;
    }


    // ====================================================================================

}
