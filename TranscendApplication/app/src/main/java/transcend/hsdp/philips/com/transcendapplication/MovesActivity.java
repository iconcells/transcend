package transcend.hsdp.philips.com.transcendapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.philips.hsdp.adapters.ObservationListViewAdapter;
import com.philips.hsdp.feed.LoginManager;
import com.philips.hsdp.feed.ObservationFeed;
import com.philips.hsdp.feed.OrganizationFeed;
import com.philips.hsdp.model.HSDPOrganization;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by karthshe on 3/7/15.
 */
public class MovesActivity extends Activity {
    String mPatientUrlLink = null;

    ObservationListViewAdapter mObservationAdapter;

    TextView mObservationCount;

    OrganizationFeed mOrganizationFeed = null;
    ObservationFeed mObservationFeed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moves);

        mPatientUrlLink = LoginManager.getInstance().getPatientUrlIdString(); // this.getIntent().getStringExtra(Constants.PATIENT_URL_LINK);

        mOrganizationFeed = OrganizationFeed.getNewInstance();
        String organizationFeedUrl = OrganizationFeed.generateOrganizationFeedUrl (LoginManager.getInstance().getOrganizationUrlIdString());
        mOrganizationFeed.setFeedUrl (organizationFeedUrl);
        mOrganizationFeed.addObserver(new Observer()
        {
            @Override
            public void update(Observable observable, Object data)
            {
                if (mOrganizationFeed.getOrganizationList() != null)
                {
                    List<HSDPOrganization> orgList = mOrganizationFeed.getOrganizationList();
                    HSDPOrganization organization = orgList.get(0);
//                    ((TextView) findViewById(R.id.patientOrganization)).setText(organization.getOrganizationName());
                }
            }
        });
        mOrganizationFeed.refresh();

        mObservationFeed = ObservationFeed.getInstance();
        String observationFeedUrl = ObservationFeed.generateObservationUrl(mPatientUrlLink.replace("/Patient/", ""), null, null, null);
        //Log.i("HS", "ObservationFeedUrl: " + observationFeedUrl);
        mObservationFeed.setFeedUrl(observationFeedUrl);
        mObservationFeed.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data)
            {
                refreshObservationView();
            }
        });


    }

    private void refreshObservationView() {

        mObservationAdapter.notifyDataSetChanged();
        int count = mObservationAdapter.getCount(); // mObservationFeed.getObservationList().size();
        mObservationCount.setText("Page: " + mObservationFeed.getCurrentPageNo() + "/" + mObservationFeed.getTotalNoPages()
                + " (" + mObservationFeed.getTotalRecordsOnServer() +" Records)");

//            mNextPageObservationButton.setVisibility(mObservationFeed.getNextPageLinkUrl() != null ? View.VISIBLE : View.GONE);
//            mPrevPageObservationButton.setVisibility(mObservationFeed.getPreviousPageLinkUrl() != null ? View.VISIBLE : View.GONE);
//            findViewById(R.id.observationProgressBar).setVisibility(View.GONE);
    }
}
