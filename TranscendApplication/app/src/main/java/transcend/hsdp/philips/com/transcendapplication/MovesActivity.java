package transcend.hsdp.philips.com.transcendapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.philips.hsdp.adapters.ObservationListViewAdapter;
import com.philips.hsdp.feed.LoginManager;
import com.philips.hsdp.feed.ObservationFeed;
import com.philips.hsdp.feed.OrganizationFeed;
import com.philips.hsdp.model.HSDPObservation;
import com.philips.hsdp.model.HSDPOrganization;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by karthshe on 3/7/15.
 */
public class MovesActivity extends ActionBarActivity {

    public static String loginName = "mark.taylor", loginPwd = "Going4ther$";
    //public static String loginName = "sam.s.smith", loginPwd = "MyFood4Health!";

    ObservationFeed stepsFeed = null;
    private String mPatientUrlLink = null;

    private static String STEPS_STR = "https://rtmms.nist.gov|8454247";
    //private static String ENERGY_STR = "https://rtmms.nist.gov|8454263";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moves);

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.addObserver (new Observer()
        {
            @Override
            public void update(Observable observable, Object data)
            {
                handleLoginResponse();
            }
        });


        stepsFeed = ObservationFeed.getNewInstance();

        LoginManager.getInstance().authenticate(loginName, loginPwd, null);

    }

    private void handleLoginResponse()
    {

        if (LoginManager.getInstance().isAuthenticated())
        {
            Log.i("HS", "LOGGED IN");

            mPatientUrlLink = LoginManager.getInstance().getPatientUrlIdString();
            String before =null, after =null;

            String inWhatObservations = STEPS_STR;
            String glucoseObservationFeedUrl = ObservationFeed.generateObservationUrl(
                    mPatientUrlLink.replace("/Patient/", ""), inWhatObservations, before, after);
            Log.i("HS", "ObservationFeedUrl: " + glucoseObservationFeedUrl);
            stepsFeed.setFeedUrl(glucoseObservationFeedUrl);
            stepsFeed.addObserver(new Observer()
            {
                @Override
                public void update(Observable observable, Object data)
                {
                    Log.i("HS", "Got observation data ");
                    for(HSDPObservation observation : stepsFeed.getObservationList()) {
                        Log.i("HS", observation.getObservationName() + " -- " + observation.getObservationValue() + "--" + observation.getAppliedDateTimeHumanReadableString());
                        TextView text = (TextView) findViewById(R.id.stepsLabel);
                        String [] sub = observation.getObservationValue().split("\\s");
                        text.setText(sub[0]);
                        double stepsDbl = Double.parseDouble(sub[0]);
                        int steps = (int) stepsDbl;
                        int distance = (int) ((double)steps * 0.4);
                        TextView textDistance = (TextView) findViewById(R.id.dietLabel);
                        textDistance.setText(String.valueOf(distance) + " m");
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
        stepsFeed.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_move, menu);
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
