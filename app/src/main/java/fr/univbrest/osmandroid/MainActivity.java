package fr.univbrest.osmandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private UImodificator uImodificator = null;
    private boolean is_stopped = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText username = (EditText)findViewById(R.id.username);
        final Button validate = ((Button)findViewById(R.id.start));
        username.setText(android.os.Build.MODEL);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_stopped){
                    uImodificator = new UImodificator(MainActivity.this,
                            (TextView)MainActivity.this.findViewById(R.id.coordinates),
                            username.getText().toString());

                    Log.d("debug", "MainActivity() : bp1 " + uImodificator.isInit_ok());
                    if(uImodificator.isInit_ok()){
                        username.setEnabled(false);
                        uImodificator.execute();
                        validate.setText("Stop");
                        validate.setBackgroundColor(Color.RED);
                        is_stopped = false;
                    }

                }else{
                    username.setEnabled(true);
                    uImodificator.terminate();
                    validate.setText("Start");
                    validate.setBackgroundColor(Color.GREEN);
                    is_stopped = true;
                }


            }
        });
    }


}