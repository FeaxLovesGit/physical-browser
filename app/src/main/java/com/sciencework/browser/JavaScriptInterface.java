package com.sciencework.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.List;

public class JavaScriptInterface {
    private final Context context;
    private final WifiManager wifiManager;
    private final WebView webView;

    JavaScriptInterface(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(initWifiScanReceiver(),
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private BroadcastReceiver initWifiScanReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> wifis = wifiManager.getScanResults();
                    StringBuilder ssidsBuilder = new StringBuilder();
                    for(ScanResult wifi: wifis) {
                        ssidsBuilder.append(wifi.SSID);
                        ssidsBuilder.append(", ");
                    }
                    String ssids = ssidsBuilder.toString().replaceAll(", $", "");
                    webView.loadUrl("javascript:showAvailableWifis('" + ssids + "');");
                }
            }
        };
    }

    @JavascriptInterface
    public void showToast(String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public String getWiFiSSID() {
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }

    @JavascriptInterface
    public void startScanForAvailableWiFis() {
        wifiManager.startScan();
    }

}
