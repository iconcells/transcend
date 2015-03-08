package transcend.hsdp.philips.com.transcendapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import com.philips.hsdp.feed.LoginManager;
import com.philips.hsdp.feed.ObservationFeed;
import com.philips.hsdp.model.HSDPObservation;

import java.util.Observable;
import java.util.Observer;


public class MeasureActivity extends ActionBarActivity {

    private static String loginName = "sam.s.smith", loginPwd = "MyFood4Health!";
    private boolean isLoggedIn = false;
    ObservationFeed mObservationFeed = null;
    private String mPatientUrlLink;
    private static String GLUCOSE_STR = "Blood Glucose", WEIGHT_STR = "Weight";

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
                //Log.i("HS", "HERE------");
                handleLoginResponse();
            }
        });

        mObservationFeed = ObservationFeed.getInstance();


        LoginManager.getInstance().authenticate(loginName, loginPwd, null);
    }

    private void handleLoginResponse()
    {

        if (LoginManager.getInstance().isAuthenticated())
        {
            Log.i("HS", "LOGGED IN");

            mPatientUrlLink = LoginManager.getInstance().getPatientUrlIdString();
            isLoggedIn = true;
            ((TextView)findViewById(R.id.pat_data)).setText(mPatientUrlLink);
            //String inWhatObservations = "http://loinc.org|8480-6, http://loinc.org|3141-9, http://loinc.org|2339-0, http://loinc.org|8867-4";
            String inWhatObservations = "http://loinc.org|8867-4";
            final String observationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, null, null);

            Log.i("HS", "ObservationFeedUrl: " + observationFeedUrl);
            mObservationFeed.setFeedUrl(observationFeedUrl);
            mObservationFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    //refreshObservationView();
                    Log.i("HS", "Got observation data ");
                    ((TextView)findViewById(R.id.pat_data)).setText(mObservationFeed.getObservationList().toString());


                    for(HSDPObservation observation : mObservationFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue());
                    }
                }
            });


            pollBloodPressure();
        }
        else
        {
            Log.i("HS", "NOT LOGGED IN");
        }

    }

    private void pollBloodPressure() {
        mObservationFeed.refresh();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
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
