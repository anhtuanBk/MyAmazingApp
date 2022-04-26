package com.my.awesome.app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DTBAdNetwork;
import com.amazon.device.ads.DTBAdNetworkInfo;
import com.amazon.device.ads.MRAIDPolicy;
import com.my.awesome.app.data.main.DemoMenuItem;
import com.my.awesome.app.data.main.FooterType;
import com.my.awesome.app.data.main.ListItem;
import com.my.awesome.app.data.main.SectionHeader;
import com.my.awesome.app.ui.MainRecyclerViewAdapter;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * The main {@link android.app.Activity} of this app.
 * <p>
 * Created by santoshbagadi on 2019-09-10.
 */
public class MainActivity
        extends AppCompatActivity
        implements MainRecyclerViewAdapter.OnMainListItemClickListener
{
    MaxInterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        final MainRecyclerViewAdapter adapter = new MainRecyclerViewAdapter( generateMainListItems(), this, this );
        final LinearLayoutManager manager = new LinearLayoutManager( this );
        final DividerItemDecoration decoration = new DividerItemDecoration( this, manager.getOrientation() );

        final RecyclerView recyclerView = findViewById( R.id.main_recycler_view );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( manager );
        recyclerView.addItemDecoration( decoration );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( adapter );

        // initialize Amazon sdk
        AdRegistration.getInstance( "AMAZON_APP_ID", this );
        AdRegistration.setAdNetworkInfo( new DTBAdNetworkInfo( DTBAdNetwork.MAX ) );
        AdRegistration.setMRAIDSupportedVersions( new String[] { "1.0", "2.0", "3.0" } );
        AdRegistration.setMRAIDPolicy( MRAIDPolicy.CUSTOM );

        AppLovinSdk.getInstance( this ).setMediationProvider( AppLovinMediationProvider.MAX );
        AppLovinSdk.getInstance( this ).initializeSdk(config -> {
        });

        interstitialAd = new MaxInterstitialAd( "30d77d0f6f295d5e", this );
        interstitialAd.loadAd();
    }

    private List<ListItem> generateMainListItems()
    {
        final List<ListItem> items = new ArrayList<>();
        items.add( new SectionHeader( "APPLOVIN" ) );
        items.add( new DemoMenuItem( "Show Interstitial", () -> { if(interstitialAd.isReady()) { interstitialAd.showAd(); } else { Log.d("AppLovin", "ad not ready"); } } ) );
        items.add( new DemoMenuItem( "Launch Mediation Debugger", () -> AppLovinSdk.getInstance( getApplicationContext() ).showMediationDebugger() ) );
        items.add( new FooterType() );
        return items;
    }

    @Override
    public void onItemClicked(final ListItem item)
    {
        if ( item.getType() == ListItem.TYPE_AD_ITEM )
        {
            final DemoMenuItem demoMenuItem = (DemoMenuItem) item;
            if ( demoMenuItem.getIntent() != null )
            {
                startActivity( demoMenuItem.getIntent() );
            }
            else if ( demoMenuItem.getRunnable() != null )
            {
                demoMenuItem.getRunnable().run();
            }
        }
    }
}
