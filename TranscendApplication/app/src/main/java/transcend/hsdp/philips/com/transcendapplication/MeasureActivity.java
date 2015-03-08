package transcend.hsdp.philips.com.transcendapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.util.Log;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.philips.hsdp.feed.LoginManager;
import com.philips.hsdp.feed.ObservationFeed;
import com.philips.hsdp.model.HSDPObservation;

import java.util.Observable;
import java.util.Observer;


public class MeasureActivity extends ActionBarActivity {

    public static String loginName = "sam.s.smith", loginPwd = "MyFood4Health!";
    ObservationFeed glucoseFeed = null, pressureFeed = null;
    ObservationFeed respiratoryFeed = null, weightFeed = null, temperatureFeed = null;
    private String mPatientUrlLink = null;

    private static String GLUCOSE_STR = "http://loinc.org|2339-0", PRESSURE_STR = "http://loinc.org|8478-0";
    private static String RESPIRATORY_RATE_STR = "https://rtmms.nist.gov|151562", WEIGHT_STR = "http://loinc.org|3141-9";
    private static String TEMPERATURE_STR = "http://loinc.org|8310-5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.addObserver (new Observer()
        {
            @Override
            public void update(Observable observable, Object data)
            {
                handleLoginResponse();
            }
        });

        glucoseFeed = ObservationFeed.getNewInstance();
        pressureFeed = ObservationFeed.getNewInstance();
        respiratoryFeed = ObservationFeed.getNewInstance();
        weightFeed = ObservationFeed.getNewInstance();
        temperatureFeed = ObservationFeed.getNewInstance();


        LoginManager.getInstance().authenticate(loginName, loginPwd, null);
    }

    private void handleLoginResponse()
    {

        if (LoginManager.getInstance().isAuthenticated())
        {
            Log.i("HS", "LOGGED IN");

            mPatientUrlLink = LoginManager.getInstance().getPatientUrlIdString();
            String before =null, after =null;

            String inWhatObservations = GLUCOSE_STR;
            String glucoseObservationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, before, after);
            Log.i("HS", "ObservationFeedUrl: " + glucoseObservationFeedUrl);
            glucoseFeed.setFeedUrl(glucoseObservationFeedUrl);
            glucoseFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    Log.i("HS", "Got observation data ");
                    for(HSDPObservation observation : glucoseFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue() + "--" + observation.getAppliedDateTimeHumanReadableString());
                        TextView text = (TextView) findViewById(R.id.glucoseLabel);
                        text.setText(observation.getObservationValue());
                        break;
                    }
                }
            });


            inWhatObservations = PRESSURE_STR;
            String pressureObservationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, before, after);
            Log.i("HS", "ObservationFeedUrl: " + pressureObservationFeedUrl);
            pressureFeed.setFeedUrl(pressureObservationFeedUrl);
            pressureFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    Log.i("HS", "Got observation data ");
                    for(HSDPObservation observation : pressureFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue() + "--" + observation.getAppliedDateTimeHumanReadableString());
                        TextView text = (TextView) findViewById(R.id.pressureLabel);
                        text.setText(observation.getObservationValue());
                        break;
                    }
                }
            });


            inWhatObservations = RESPIRATORY_RATE_STR;
            String rrObservationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, before, after);
            Log.i("HS", "ObservationFeedUrl: " + rrObservationFeedUrl);
            respiratoryFeed.setFeedUrl(rrObservationFeedUrl);
            respiratoryFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    Log.i("HS", "Got observation data ");
                    for(HSDPObservation observation : respiratoryFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue() + "--" + observation.getAppliedDateTimeHumanReadableString());
                        TextView text = (TextView) findViewById(R.id.respiratoryLabel);
                        text.setText(observation.getObservationValue());
                        break;
                    }
                }
            });


            inWhatObservations = WEIGHT_STR;
            String weightObservationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, before, after);
            Log.i("HS", "ObservationFeedUrl: " + weightObservationFeedUrl);
            weightFeed.setFeedUrl(weightObservationFeedUrl);
            weightFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    Log.i("HS", "Got observation data ");
                    for(HSDPObservation observation : weightFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue() + "--" + observation.getAppliedDateTimeHumanReadableString());
                        TextView text = (TextView) findViewById(R.id.weightLabel);
                        text.setText(observation.getObservationValue());
                        break;
                    }
                }
            });

            inWhatObservations = TEMPERATURE_STR;
            String temperatureObservationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, before, after);
            Log.i("HS", "ObservationFeedUrl: " + temperatureObservationFeedUrl);
            temperatureFeed.setFeedUrl(temperatureObservationFeedUrl);
            temperatureFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    Log.i("HS", "Got observation data ");
                    for(HSDPObservation observation : temperatureFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue() + "--" + observation.getAppliedDateTimeHumanReadableString());
                        TextView text = (TextView) findViewById(R.id.temperatureLabel);
                        text.setText(observation.getObservationValue());
                        break;
                    }
                }
            });




            pollCurrentObservations();
        }
        else
        {
            Log.i("HS", "NOT LOGGED IN");
        }

    }

    private void pollCurrentObservations() {
        glucoseFeed.refresh();
        pressureFeed.refresh();
        respiratoryFeed.refresh();
        weightFeed.refresh();
        temperatureFeed.refresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
