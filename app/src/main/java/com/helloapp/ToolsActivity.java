package com.helloapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToolsActivity extends Activity {
    private static final int PERMISSION_REQUEST = 2001;
    private String toolType;
    private TextView titleText;
    private LinearLayout contentArea;
    private SensorManager sensorManager;
    private SensorEventListener levelListener;
    private TextView cpuText;
    private Runnable cpuUpdater;
    private TextView txText;
    private Runnable networkUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolType = getIntent().getStringExtra("tool");
        if (toolType == null) toolType = "";
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.gradient_bg);
        final ToolsActivity self = this;
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setPadding(20, 54, 20, 16);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        Button backBtn = new Button(this);
        backBtn.setText("<");
        backBtn.setTextColor(0xFFFFFFFF);
        backBtn.setTextSize(20);
        backBtn.setBackgroundResource(R.drawable.glass_icon_bg);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { self.finish(); }
        });
        titleText = new TextView(this);
        titleText.setTextColor(0xFFFFFFFF);
        titleText.setTextSize(20);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(16, 0, 0, 0);
        topBar.addView(backBtn, 48, 48);
        topBar.addView(titleText, new LinearLayout.LayoutParams(0, -2, 1));
        ScrollView scroll = new ScrollView(this);
        scroll.setPadding(20, 8, 20, 20);
        contentArea = new LinearLayout(this);
        contentArea.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(contentArea, new ViewGroup.LayoutParams(-1, -1));
        root.addView(topBar, new LinearLayout.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, -1));
        setContentView(root);
        if ("level".equals(toolType)) buildLevel();
        else if ("ruler".equals(toolType)) buildRuler();
        else if ("converter".equals(toolType)) buildConverter();
        else if ("countdown".equals(toolType)) buildCountdown();
        else if ("noise".equals(toolType)) buildWhiteNoise();
        else if ("cpu".equals(toolType)) buildCpuMonitor();
        else if ("network".equals(toolType)) buildNetworkMonitor();
        else if ("cleaner".equals(toolType)) buildCleaner();
        else if ("album".equals(toolType)) buildPrivateAlbum();
        else if ("wifi".equals(toolType)) buildWifiAnalyzer();
        else if ("floatball".equals(toolType)) showSysPerm("Floating Ball", Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        else if ("applock".equals(toolType)) showSysPerm("App Lock", Settings.ACTION_ACCESSIBILITY_SETTINGS);
        else showUnavail();
    }

    @Override
    protected void onDestroy() {
        if (sensorManager != null && levelListener != null) {
            sensorManager.unregisterListener(levelListener);
        }
        if (cpuText != null && cpuUpdater != null) {
            cpuText.removeCallbacks(cpuUpdater);
        }
        if (txText != null && networkUpdater != null) {
            txText.removeCallbacks(networkUpdater);
        }
        super.onDestroy();
    }

    // === LEVEL ===
    private void buildLevel() {
        titleText.setText("水平仪");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        LevelView lv = new LevelView(this);
        contentArea.addView(lv, new LinearLayout.LayoutParams(-1, 500));
        levelListener = new SensorEventListener() {
            @Override public void onSensorChanged(SensorEvent e) { if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER) lv.setTilt(e.values[0], e.values[1]); }
            @Override public void onAccuracyChanged(Sensor s, int a) {}
        };
        Sensor accel = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) : null;
        if (accel != null) sensorManager.registerListener(levelListener, accel, SensorManager.SENSOR_DELAY_GAME);
        else addInfo("No accelerometer found on this device");
        addInfo("将手机平放，气泡居中表示水平");
    }

    static class LevelView extends View {
        float xTilt, yTilt; Paint bg, circ, bub, ln;
        LevelView(Context c) {
            super(c);
            bg = mkP(0x20FFFFFF); circ = mkP(0x40FFFFFF); bub = mkP(0xCCFFFFFF); ln = mkP(0x30FFFFFF);
            circ.setStyle(Paint.Style.STROKE); circ.setStrokeWidth(2); ln.setStrokeWidth(1);
        }
        void setTilt(float x, float y) { xTilt = -x; yTilt = y; postInvalidate(); }
        @Override protected void onDraw(Canvas cv) {
            float cx = getWidth()/2f, cy = getHeight()/2f, r = Math.min(cx,cy)-20;
            cv.drawCircle(cx,cy,r,bg); cv.drawCircle(cx,cy,r,circ);
            cv.drawLine(cx-r,cy,cx+r,cy,ln); cv.drawLine(cx,cy-r,cx,cy+r,ln);
            cv.drawCircle(cx,cy,r*0.5f,circ);
            float bx = cx+(xTilt/12f)*r*0.7f, by = cy+(yTilt/12f)*r*0.7f, md = r*0.7f;
            float dist = (float)Math.sqrt((bx-cx)*(bx-cx)+(by-cy)*(by-cy));
            if (dist>md) { bx = cx+(bx-cx)*md/dist; by = cy+(by-cy)*md/dist; }
            cv.drawCircle(bx,by,r*0.15f,bub);
        }
    }

    // === RULER ===
    private void buildRuler() {
        titleText.setText("尺子");
        float d = getResources().getDisplayMetrics().density;
        RulerView rv = new RulerView(this, d);
        contentArea.addView(rv, new LinearLayout.LayoutParams(-1, 200));
        TextView info = new TextView(this);
        info.setTextColor(0xB0FFFFFF); info.setTextSize(14); info.setGravity(Gravity.CENTER);
        info.setPadding(0,16,0,0); info.setText("请滑动屏幕");
        contentArea.addView(info);
        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent e) {
                float cm = e.getX()/d/(25.4f/160f);
                if (cm<0) cm=0; if (cm>15) cm=15;
                info.setText(String.format("%.1f cm",cm)); return true;
            }
        });
        addInfo("滑动屏幕测量（最多15cm）");
    }

    static class RulerView extends View {
        float d; Paint lp, tp, mp;
        RulerView(Context c, float density) {
            super(c); d = density; lp = mkP(0x60FFFFFF); tp = mkP(0xB0FFFFFF); mp = mkP(0xCCFFFFFF);
            lp.setStrokeWidth(1); mp.setStrokeWidth(3); tp.setTextSize(28);
        }
        @Override protected void onDraw(Canvas cv) {
            float px = d*(25.4f/160f), w = getWidth(); int h = getHeight();
            Paint bkg = mkP(0x20FFFFFF); cv.drawRect(0,20,w,h-20,bkg);
            bkg = mkP(0x40FFFFFF); bkg.setStyle(Paint.Style.STROKE); bkg.setStrokeWidth(1);
            cv.drawRect(0,20,w,h-20,bkg);
            for (int cm=0; cm<=15; cm++) {
                float x = cm*px; if(x>w) break;
                cv.drawLine(x,20,x,h-20,mp); cv.drawText(cm+"cm",x+4,h-28,tp);
                if (cm<15) for (int mm=1;mm<10;mm++) {
                    float mx = x+mm*px/10, mh = mm==5 ? (h-20)*0.6f : (h-20)*0.4f;
                    cv.drawLine(mx,h-20,mx,h-20-mh,lp);
                }
            }
        }
    }

    // === CONVERTER ===
    private void buildConverter() {
        titleText.setText("单位换算");
        String[] cats = {"长度","重量","温度"};
        String[][] units = {{"cm","m","km","in","ft"},{"g","kg","lb","oz"},{"\u00b0C","\u00b0F","K"}};
        double[][] rates = {{0.01,1,1000,0.0254,0.3048},{1,1000,453.592,28.3495},{1,0.5556,1}};
        LinearLayout cr = new LinearLayout(this);
        cr.setOrientation(LinearLayout.HORIZONTAL); cr.setGravity(Gravity.CENTER);
        for (int c=0; c<cats.length; c++) {
            final int ci = c;
            Button b = new Button(this, null, android.R.attr.buttonStyleSmall);
            b.setText(cats[c]); b.setTextColor(0xFFFFFFFF); b.setTextSize(13);
            b.setBackgroundResource(R.drawable.glass_top_bar); b.setPadding(16,8,16,8);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2,44);
            lp.setMargins(4,4,4,4); b.setLayoutParams(lp);
            b.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { convRows(ci, units, rates); }
            });
            cr.addView(b);
        }
        contentArea.addView(cr);
        convRows(0, units, rates);
        addInfo("输入数值自动换算");
    }

    private void convRows(int cat, String[][] units, double[][] rates) {
        String[] u = units[cat]; double[] r = rates[cat]; boolean isTemp = (cat==2);
        for (int i=contentArea.getChildCount()-1; i>=0; i--) {
            View cv = contentArea.getChildAt(i);
            if (cv instanceof LinearLayout && cv != contentArea.getChildAt(0)) contentArea.removeViewAt(i);
        }
        for (int i=0; i<u.length; i++) {
            final int fi = i;
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL); row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.glass_top_bar); row.setPadding(8,6,8,6);
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setTextColor(0xFFFFFFFF); et.setHintTextColor(0x80FFFFFF); et.setHint("0");
            et.setTextSize(16); et.setBackgroundColor(0x15FFFFFF); et.setPadding(12,8,12,8);
            et.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
            TextView label = new TextView(this);
            label.setText(u[i]); label.setTextColor(0xB0FFFFFF); label.setTextSize(14); label.setPadding(12,0,0,0);
            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override public void onFocusChange(View v, boolean hf) {
                    if (!hf) return;
                    String s = et.getText().toString();
                    if (s.isEmpty()) return;
                    try {
                        double val = Double.parseDouble(s); int idx = 0;
                        for (int j=0; j<contentArea.getChildCount(); j++) {
                            View cv = contentArea.getChildAt(j);
                            if (!(cv instanceof LinearLayout) || cv==contentArea.getChildAt(0)) continue;
                            if (idx==fi) { idx++; continue; }
                            double conv;
                            if (isTemp) { double c = (fi==0)?val:(fi==1)?(val-32)*5/9:val-273.15; conv = (idx==0)?c:(idx==1)?c*9/5+32:c+273.15; }
                            else { double base = val*(r[0]/r[fi]); conv = base/(r[0]/r[idx]); }
                            ((EditText)((LinearLayout)cv).getChildAt(0)).setText(String.format("%.4f", conv));
                            idx++;
                        }
                    } catch (NumberFormatException e) {}
                }
            });
            row.addView(et); row.addView(label); contentArea.addView(row);
        }
    }

    // === COUNTDOWN ===
    private void buildCountdown() {
        titleText.setText("倒计时");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        TextView dd = new TextView(this);
        dd.setText(sdf.format(today.getTime())); dd.setTextColor(0xFFFFFFFF); dd.setTextSize(24);
        dd.setGravity(Gravity.CENTER); dd.setPadding(0,20,0,10);
        dd.setBackgroundResource(R.drawable.glass_top_bar); contentArea.addView(dd);
        String[] ps = {"今天","明天","7天后","1个月"};
        for (String p : ps) {
            final String fp = p;
            Button b = new Button(this, null, android.R.attr.buttonStyleSmall);
            b.setText(p); b.setTextColor(0xFFFFFFFF); b.setTextSize(12);
            b.setBackgroundResource(R.drawable.glass_top_bar); b.setPadding(12,6,12,6);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2,40);
            lp.setMargins(3,3,3,3); b.setLayoutParams(lp);
            b.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Calendar c = (Calendar)today.clone();
                    if (fp.equals("明天")) c.add(Calendar.DAY_OF_YEAR, 1);
                    else if (fp.equals("7天后")) c.add(Calendar.DAY_OF_YEAR, 7);
                    else if (fp.equals("1个月")) c.add(Calendar.MONTH, 1);
                    dd.setText(sdf.format(c.getTime()));
                    cdResult(today, c);
                }
            });
            contentArea.addView(b);
        }
    }
    private void cdResult(Calendar from, Calendar to) {
        long ms = to.getTimeInMillis() - from.getTimeInMillis();
        String prefix = ms < 0 ? "Passed: " : "Remaining: ";
        long d = Math.abs(ms)/(1000*60*60*24), h = (Math.abs(ms)%(1000*60*60*24))/(1000*60*60);
        for (int i=contentArea.getChildCount()-1; i>=0; i--) {
            View v = contentArea.getChildAt(i);
            if (v instanceof TextView && ((TextView)v).getCurrentTextColor() == 0xFFFFFFFF) {
                ((TextView)v).setText(prefix + d + "d " + h + "h"); ((TextView)v).setTextSize(20); break;
            }
        }
    }

    // === WHITE NOISE ===
    private void buildWhiteNoise() {
        titleText.setText("White Noise");
        String[] ns = {"Waves","Rain","Wind","Nature","White"};
        String[] icons = {"~~","**","~~","oo","##"};
        for (int i=0; i<ns.length; i++) {
            final int fi = i;
            Button b = new Button(this, null, android.R.attr.buttonStyleSmall);
            b.setText(icons[i] + " " + ns[i]); b.setTextColor(0xFFFFFFFF); b.setTextSize(16);
            b.setGravity(Gravity.CENTER); b.setPadding(20,16,20,16);
            b.setBackgroundResource(R.drawable.glass_card);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,56);
            lp.setMargins(0,6,0,6); b.setLayoutParams(lp);
            b.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { Toast.makeText(ToolsActivity.this, "Playing: " + ns[fi], 0).show(); }
            });
            contentArea.addView(b);
        }
        addInfo("Tap to play (demo - no audio files bundled)");
    }

    // === CPU MONITOR ===
    private void buildCpuMonitor() {
        titleText.setText("CPU Monitor");
        cpuText = new TextView(this);
        cpuText.setTextColor(0xFFFFFFFF); cpuText.setTextSize(32);
        cpuText.setGravity(Gravity.CENTER); cpuText.setPadding(0,30,0,10);
        contentArea.addView(cpuText);
        TextView info = new TextView(this);
        info.setTextColor(0xB0FFFFFF); info.setTextSize(14); info.setGravity(Gravity.CENTER);
        info.setText("Cores: " + Runtime.getRuntime().availableProcessors());
        contentArea.addView(info);
        cpuUpdater = new Runnable() {
            long prevIdle=0, prevTotal=0;
            @Override public void run() {
                try {
                    BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
                    String line = br.readLine(); br.close();
                    if (line != null && line.startsWith("cpu ")) {
                        String[] p = line.split("\\s+");
                        long idle = Long.parseLong(p[4]), total = 0;
                        for (int i=1; i<p.length; i++) total += Long.parseLong(p[i]);
                        if (prevTotal > 0) {
                            int pct = (int)((1.0 - (double)(idle-prevIdle)/(total-prevTotal)) * 100);
                            cpuText.setText(pct + "%");
                        }
                        prevIdle = idle; prevTotal = total;
                    }
                } catch (Exception e) { cpuText.setText("N/A"); }
                if (!isFinishing()) cpuText.postDelayed(this, 2000);
            }
        };
        cpuUpdater.run();
        addInfo("No permissions needed");
    }

    // === NETWORK MONITOR ===
    private void buildNetworkMonitor() {
        titleText.setText("Network Speed");
        txText = new TextView(this);
        txText.setTextColor(0xFFFFFFFF); txText.setTextSize(18);
        txText.setGravity(Gravity.CENTER); txText.setPadding(0,30,0,10);
        contentArea.addView(txText);
        final TextView rxText = new TextView(this);
        rxText.setTextColor(0xB0FFFFFF); rxText.setTextSize(18);
        rxText.setGravity(Gravity.CENTER);
        contentArea.addView(rxText);
        networkUpdater = new Runnable() {
            long prevTx = TrafficStats.getTotalTxBytes();
            long prevRx = TrafficStats.getTotalRxBytes();
            @Override public void run() {
                long tx = TrafficStats.getTotalTxBytes();
                long rx = TrafficStats.getTotalRxBytes();
                txText.setText("Upload: " + (tx-prevTx)/1024 + " KB/s");
                rxText.setText("Download: " + (rx-prevRx)/1024 + " KB/s");
                prevTx = tx; prevRx = rx;
                if (!isFinishing()) txText.postDelayed(this, 1000);
            }
        };
        networkUpdater.run();
        addInfo("No permissions needed");
    }

    // === CLEANER ===
    private void buildCleaner() {
        titleText.setText("Storage Info");
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long total = (long)stat.getTotalBytes();
        long free = (long)stat.getAvailableBytes();
        long used = total - free;
        TextView t = new TextView(this);
        t.setTextColor(0xFFFFFFFF); t.setTextSize(22);
        t.setGravity(Gravity.CENTER); t.setPadding(0,30,0,5);
        t.setText(Formatter.formatFileSize(this, total));
        contentArea.addView(t);
        TextView d = new TextView(this);
        d.setTextColor(0xB0FFFFFF); d.setTextSize(14);
        d.setGravity(Gravity.CENTER);
        d.setText("Used: " + Formatter.formatFileSize(this, used) + " / Free: " + Formatter.formatFileSize(this, free));
        contentArea.addView(d);
        addInfo("No permissions needed");
    }

    // === PRIVATE ALBUM ===
    private void buildPrivateAlbum() {
        titleText.setText("Private Album");
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST);
                permGate("READ_MEDIA_IMAGES"); return;
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                permGate("READ_EXTERNAL_STORAGE"); return;
            }
        }
        albumContent();
    }
    private void permGate(String perm) {
        TextView tv = new TextView(this);
        tv.setTextColor(0xFFFFFFFF); tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER); tv.setPadding(0,60,0,20);
        tv.setText("Need: " + perm); contentArea.addView(tv);
        Button b = new Button(this, null, android.R.attr.buttonStyleSmall);
        b.setText("Grant"); b.setTextColor(0xFFFFFFFF); b.setBackgroundResource(R.drawable.glass_card);
        b.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 33)
                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST);
                else
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        });
        contentArea.addView(b);
    }
    private void albumContent() {
        try {
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, null, null, null);
            int count = cursor != null ? cursor.getCount() : 0;
            if (cursor != null) cursor.close();
            TextView tv = new TextView(this);
            tv.setTextColor(0xFFFFFFFF); tv.setTextSize(24);
            tv.setGravity(Gravity.CENTER); tv.setPadding(0,60,0,10);
            tv.setText(count + " photos"); contentArea.addView(tv);
        } catch (Exception e) { permGate("Storage"); }
    }

    // === WIFI ANALYZER ===
    private void buildWifiAnalyzer() {
        titleText.setText("WiFi Analyzer");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            permGate("ACCESS_FINE_LOCATION"); return;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            android.location.LocationManager lm =
                    (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean locationEnabled = lm != null && lm.isLocationEnabled();
            if (!locationEnabled) {
                addInfo("Location service must be enabled for WiFi scan results on Android 9+.");
                Button b = new Button(this, null, android.R.attr.buttonStyleSmall);
                b.setText("Open Location Settings");
                b.setTextColor(0xFFFFFFFF);
                b.setBackgroundResource(R.drawable.glass_card);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                contentArea.addView(b);
                return;
            }
        }
        scanWifi();
    }
    private void scanWifi() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wm == null) { addInfo("WiFi N/A"); return; }
        boolean scanStarted = wm.startScan();
        List<ScanResult> results = wm.getScanResults();
        if (results == null || results.isEmpty()) {
            addInfo(scanStarted
                    ? "Scan started, but no cached networks are available yet. Try again in a few seconds."
                    : "WiFi scan is throttled or blocked by the system. Try again later.");
            return;
        }
        Collections.sort(results, (a,b) -> b.level - a.level);
        int max = Math.min(results.size(), 10);
        for (int i=0; i<max; i++) {
            ScanResult r = results.get(i);
            int sig = Math.min(100, Math.max(0, (r.level+100)*2));
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.glass_top_bar);
            row.setPadding(12,8,12,8);
            TextView name = new TextView(this);
            name.setText(r.SSID.isEmpty() ? "(hidden)" : r.SSID);
            name.setTextColor(0xFFFFFFFF); name.setTextSize(14);
            name.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));
            TextView st = new TextView(this);
            st.setText(sig + "%"); st.setTextSize(13);
            st.setTextColor(sig>60 ? 0xFFA5D6A7 : sig>30 ? 0xFFFFCC80 : 0xFFEF9A9A);
            row.addView(name); row.addView(st); contentArea.addView(row);
        }
        addInfo("Found " + results.size() + " networks");
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == PERMISSION_REQUEST && res.length>0 && res[0]==0) {
            Toast.makeText(this, "Permission granted", 0).show();
            recreate();
        } else if (code == PERMISSION_REQUEST) {
            Toast.makeText(this, "Permission denied", 0).show();
        }
    }

    private void showSysPerm(String name, String action) {
        titleText.setText(name);
        TextView tv = new TextView(this);
        tv.setTextColor(0xFFFFFFFF); tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER); tv.setPadding(0,60,0,20);
        tv.setText("Needs system permission"); contentArea.addView(tv);
        Button b = new Button(this, null, android.R.attr.buttonStyleSmall);
        b.setText("Open Settings"); b.setTextColor(0xFFFFFFFF);
        b.setBackgroundResource(R.drawable.glass_card);
        b.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { startActivity(new Intent(action)); }
        });
        contentArea.addView(b);
    }

    private void showUnavail() {
        titleText.setText("N/A");
        TextView tv = new TextView(this);
        tv.setTextColor(0xFFFFFFFF); tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER); tv.setPadding(0,60,0,20);
        tv.setText("Requires external libraries"); contentArea.addView(tv);
    }

    private void addInfo(String text) {
        TextView tv = new TextView(this);
        tv.setTextColor(0x80FFFFFF); tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER); tv.setPadding(0,20,0,0);
        tv.setText(text); contentArea.addView(tv);
    }

    private static Paint mkP(int c) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); p.setColor(c); return p;
    }
}
