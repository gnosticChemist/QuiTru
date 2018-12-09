package quitru.gnosticchemist.com.github;

import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import quitru.gnosticchemist.com.github.Model.Starter;

public class AndroidLauncher extends AndroidApplication {
	WifiManager.MulticastLock itsLock = null;
	WifiManager itsWifiMngr = null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Starter(), config);

		itsWifiMngr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
		itsLock = itsWifiMngr.createMulticastLock("theLock");
		itsLock.acquire();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		itsLock.release();
	}
}
