package com.helloapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ToolsActivity extends Activity {

    private String toolType;
    private TextView titleText;
    private LinearLayout catRow;
    private LinearLayout contentArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolType = getIntent().getStringExtra("tool");
        if (toolType == null) toolType = "";

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.gradient_bg);

        // Top bar with back button
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setPadding(20, 54, 20, 16);
        topBar.setGravity(Gravity.CENTER_VERTICAL);

        final ToolsActivity self = this;
        Button backBtn = new Button(this);
        backBtn.setText("<");
        backBtn.setTextColor(0xFFFFFFFF);
        backBtn.setTextSize(20);
        backBtn.setBackgroundResource(R.drawable.glass_icon_bg);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { self.finish(); }
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
    }

    // ======== 1. Bubble Level ========

    private void buildLevel() {
        titleText.setText("\u6c34\u5e73\u4eea");
        final SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        final LevelView levelView = new LevelView(this);
        contentArea.addView(levelView, new LinearLayout.LayoutParams(-1, 500));

        final SensorEventListener listener = new SensorEventListener() {
            @Override public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    levelView.setTilt(event.values[0], event.values[1]);
                }
            }
            @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        Sensor accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accel != null) sm.registerListener(listener, accel, SensorManager.SENSOR_DELAY_GAME);

        contentArea.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override public void onViewAttachedToWindow(View v) {}
            @Override public void onViewDetachedFromWindow(View v) { sm.unregisterListener(listener); }
        });

        addInfo("\u5c06\u624b\u673a\u5e73\u653e\uff0c\u6c14\u6ce1\u4f1a\u5c45\u4e2d\u8868\u793a\u6c34\u5e73\u3002");
    }

    private static class LevelView extends View {
        private float xTilt, yTilt;
        private final Paint bgPaint, circlePaint, bubblePaint, linePaint;

        public LevelView(Context context) {
            super(context);
            bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(0x20FFFFFF);
            circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePaint.setColor(0x40FFFFFF);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeWidth(2);
            bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bubblePaint.setColor(0xCCFFFFFF);
            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setColor(0x30FFFFFF);
            linePaint.setStrokeWidth(1);
        }

        public void setTilt(float x, float y) {
            xTilt = -x;
            yTilt = y;
            postInvalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f, cy = getHeight() / 2f;
            float r = Math.min(cx, cy) - 20;
            canvas.drawCircle(cx, cy, r, bgPaint);
            canvas.drawCircle(cx, cy, r, circlePaint);
            canvas.drawLine(cx - r, cy, cx + r, cy, linePaint);
            canvas.drawLine(cx, cy - r, cx, cy + r, linePaint);
            canvas.drawCircle(cx, cy, r * 0.5f, circlePaint);

            float bx = cx + (xTilt / 12f) * r * 0.7f;
            float by = cy + (yTilt / 12f) * r * 0.7f;
            float dist = (float) Math.sqrt((bx-cx)*(bx-cx) + (by-cy)*(by-cy));
            float maxDist = r * 0.7f;
            if (dist > maxDist) {
                bx = cx + (bx-cx)*maxDist/dist;
                by = cy + (by-cy)*maxDist/dist;
            }
            canvas.drawCircle(bx, by, r*0.15f, bubblePaint);
        }
    }

    // ======== 2. Ruler ========

    private void buildRuler() {
        titleText.setText("\u5c3a\u5b50");
        final float density = getResources().getDisplayMetrics().density;

        final RulerView ruler = new RulerView(this, density);
        contentArea.addView(ruler, new LinearLayout.LayoutParams(-1, 200));

        final TextView info = new TextView(this);
        info.setTextColor(0xB0FFFFFF);
        info.setTextSize(14);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, 16, 0, 0);
        info.setText("\u8bf7\u6ed1\u52a8\u5c4f\u5e55");
        contentArea.addView(info);

        ruler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float cm = event.getX() / density / (25.4f / 160f);
                if (cm < 0) cm = 0;
                if (cm > 15) cm = 15;
                info.setText(String.format("%.1f cm", cm));
                return true;
            }
        });

        addInfo("\u6ed1\u52a8\u5c4f\u5e55\u6d4b\u91cf\u957f\u5ea6\uff08\u6700\u591a15cm\uff09");
    }

    private static class RulerView extends View {
        private final float density;
        private final Paint linePaint, textPaint, markPaint;

        public RulerView(Context context, float density) {
            super(context);
            this.density = density;
            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setColor(0x60FFFFFF);
            linePaint.setStrokeWidth(1);
            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(0xB0FFFFFF);
            textPaint.setTextSize(28);
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            markPaint.setColor(0xCCFFFFFF);
            markPaint.setStrokeWidth(3);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float pxPerCm = density * (25.4f / 160f);
            float w = getWidth();
            int h = getHeight();

            Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
            bg.setColor(0x20FFFFFF);
            canvas.drawRect(0, 20, w, h-20, bg);
            bg.setColor(0x40FFFFFF);
            bg.setStyle(Paint.Style.STROKE);
            bg.setStrokeWidth(1);
            canvas.drawRect(0, 20, w, h-20, bg);

            for (int cm = 0; cm <= 15; cm++) {
                float x = cm * pxPerCm;
                if (x > w) break;
                canvas.drawLine(x, 20, x, h-20, markPaint);
                canvas.drawText(cm + "cm", x+4, h-28, textPaint);
                if (cm < 15) {
                    for (int mm = 1; mm < 10; mm++) {
                        float mx = x + mm * pxPerCm / 10;
                        float mh = (mm == 5) ? (h-20)*0.6f : (h-20)*0.4f;
                        canvas.drawLine(mx, h-20, mx, h-20-mh, linePaint);
                    }
                }
            }
        }
    }

    // ======== 3. Unit Converter ========

    private void buildConverter() {
        titleText.setText("\u5355\u4f4d\u6362\u7b97");
        final String[] cats = {"\u957f\u5ea6", "\u91cd\u91cf", "\u6e29\u5ea6"};
        final String[][] units = {
            {"cm", "m", "km", "in", "ft"},
            {"g", "kg", "lb", "oz"},
            {"\u00b0C", "\u00b0F", "K"}
        };
        final double[][] rates = {
            {0.01, 1, 1000, 0.0254, 0.3048},
            {1, 1000, 453.592, 28.3495},
            {1, 0.5556, 1}
        };

        catRow = new LinearLayout(this);
        catRow.setOrientation(LinearLayout.HORIZONTAL);
        catRow.setGravity(Gravity.CENTER);

        for (int c = 0; c < cats.length; c++) {
            final int ci = c;
            Button btn = new Button(this, null, android.R.attr.buttonStyleSmall);
            btn.setText(cats[c]);
            btn.setTextColor(0xFFFFFFFF);
            btn.setTextSize(13);
            btn.setBackgroundResource(R.drawable.glass_top_bar);
            btn.setPadding(16, 8, 16, 8);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, 44);
            lp.setMargins(4, 4, 4, 4);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rebuildConverter(ci, units, rates);
                }
            });
            catRow.addView(btn);
        }
        contentArea.addView(catRow);
        rebuildConverter(0, units, rates);
        addInfo("\u9009\u62e9\u7c7b\u522b\uff0c\u8f93\u5165\u6570\u503c\u81ea\u52a8\u6362\u7b97");
    }

    private void rebuildConverter(int cat, String[][] units, double[][] rates) {
        final String[] unitList = units[cat];
        final double[] rateList = rates[cat];
        final boolean isTemp = (cat == 2);

        // Remove old rows
        int childCount = contentArea.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = contentArea.getChildAt(i);
            if (child instanceof LinearLayout && child != contentArea.getChildAt(0)) {
                contentArea.removeViewAt(i);
            }
        }

        final ToolsActivity self = this;
        for (int i = 0; i < unitList.length; i++) {
            final int fi = i;
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, 4, 0, 4);
            row.setBackgroundResource(R.drawable.glass_top_bar);
            row.setPadding(8, 6, 8, 6);

            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setTextColor(0xFFFFFFFF);
            et.setHintTextColor(0x80FFFFFF);
            et.setHint("0");
            et.setTextSize(16);
            et.setBackgroundColor(0x15FFFFFF);
            et.setPadding(12, 8, 12, 8);
            et.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));

            TextView label = new TextView(this);
            label.setText(unitList[i]);
            label.setTextColor(0xB0FFFFFF);
            label.setTextSize(14);
            label.setPadding(12, 0, 0, 0);

            final EditText[] inputs = new EditText[unitList.length];
            inputs[i] = et;
            // Store for later: iterate to find all inputs - simplification
            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) return;
                    String str = et.getText().toString();
                    if (str.isEmpty()) return;
                    try {
                        double val = Double.parseDouble(str);
                        // Find all sibling EditTexts
                        LinearLayout parent = (LinearLayout) et.getParent();
                        if (parent == null) return;
                        // Scan all rows for edit texts
                        final double finalVal = val;
                        final int fromIdx = fi;
                        for (int j = 0; j < contentArea.getChildCount(); j++) {
                            View cv = contentArea.getChildAt(j);
                            if (cv instanceof LinearLayout && cv != catRow) {
                                LinearLayout row2 = (LinearLayout) cv;
                                View first = row2.getChildAt(0);
                                if (first instanceof EditText) {
                                    String hint = ((EditText)first).getHint().toString();
                                    if (hint.equals("0")) {
                                        String txt = ((EditText)first).getText().toString();
                                        if (!txt.isEmpty() && !txt.equals(et.getText().toString())) {
                                            continue;
                                        }
                                        double converted;
                                        if (isTemp) {
                                            converted = convertTemp(finalVal, fromIdx, j);
                                        } else {
                                            double base = finalVal * (rateList[0] / rateList[fromIdx]);
                                            converted = base / (rateList[0] / rateList[j % unitList.length]);
                                        }
                                        if (j % unitList.length < unitList.length) {
                                            ((EditText)first).setText(String.format("%.4f", converted));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            });

            row.addView(et);
            row.addView(label);
            contentArea.addView(row);
        }
    }

    // Keep for reference but converter will handle it inline above
    private double convertTemp(double val, int from, int to) {
        double c;
        if (from == 0) c = val;
        else if (from == 1) c = (val - 32) * 5 / 9;
        else c = val - 273.15;
        if (to == 0) return c;
        else if (to == 1) return c * 9 / 5 + 32;
        else return c + 273.15;
    }

    // ======== 4. Countdown ========

    private void buildCountdown() {
        titleText.setText("\u5012\u8ba1\u65f6");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final Calendar today = Calendar.getInstance();

        final TextView dateDisplay = new TextView(this);
        dateDisplay.setText(sdf.format(today.getTime()));
        dateDisplay.setTextColor(0xFFFFFFFF);
        dateDisplay.setTextSize(24);
        dateDisplay.setGravity(Gravity.CENTER);
        dateDisplay.setPadding(0, 20, 0, 10);
        dateDisplay.setBackgroundResource(R.drawable.glass_top_bar);
        contentArea.addView(dateDisplay);

        String[] presets = {"\u4eca\u5929", "\u660e\u5929", "7\u5929\u540e", "1\u4e2a\u6708"};
        for (final String preset : presets) {
            Button btn = new Button(this, null, android.R.attr.buttonStyleSmall);
            btn.setText(preset);
            btn.setTextColor(0xFFFFFFFF);
            btn.setTextSize(12);
            btn.setBackgroundResource(R.drawable.glass_top_bar);
            btn.setPadding(12, 6, 12, 6);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, 40);
            lp.setMargins(3, 3, 3, 3);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = (Calendar) today.clone();
                    if ("\u660e\u5929".equals(preset)) c.add(Calendar.DAY_OF_YEAR, 1);
                    else if ("7\u5929\u540e".equals(preset)) c.add(Calendar.DAY_OF_YEAR, 7);
                    else if ("1\u4e2a\u6708".equals(preset)) c.add(Calendar.MONTH, 1);
                    dateDisplay.setText(sdf.format(c.getTime()));
                    showCountdownResult(today, c);
                }
            });
            contentArea.addView(btn);
        }

        // Manual input
        LinearLayout manualRow = new LinearLayout(this);
        manualRow.setOrientation(LinearLayout.HORIZONTAL);
        manualRow.setPadding(0, 12, 0, 0);

        final EditText dateInput = new EditText(this);
        dateInput.setHint("yyyy-MM-dd");
        dateInput.setHintTextColor(0x80FFFFFF);
        dateInput.setTextColor(0xFFFFFFFF);
        dateInput.setTextSize(14);
        dateInput.setBackgroundColor(0x15FFFFFF);
        dateInput.setPadding(12, 8, 12, 8);
        dateInput.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));

        Button goBtn = new Button(this, null, android.R.attr.buttonStyleSmall);
        goBtn.setText("OK");
        goBtn.setTextColor(0xFFFFFFFF);
        goBtn.setBackgroundResource(R.drawable.glass_top_bar);
        goBtn.setPadding(16, 8, 16, 8);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date d = sdf.parse(dateInput.getText().toString());
                    Calendar c = Calendar.getInstance();
                    c.setTime(d);
                    dateDisplay.setText(sdf.format(c.getTime()));
                    showCountdownResult(today, c);
                } catch (Exception e) {
                    Toast.makeText(ToolsActivity.this, "Format: yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                }
            }
        });

        manualRow.addView(dateInput);
        manualRow.addView(goBtn);
        contentArea.addView(manualRow);

        showCountdownResult(today, today);
    }

    private void showCountdownResult(Calendar from, Calendar to) {
        long diffMs = to.getTimeInMillis() - from.getTimeInMillis();
        String prefix = (diffMs < 0) ? "Passed: " : "Remaining: ";
        String result = prefix + formatDuration(Math.abs(diffMs));

        for (int i = contentArea.getChildCount() - 1; i >= 0; i--) {
            View v = contentArea.getChildAt(i);
            if (v instanceof TextView && ((TextView)v).getGravity() == Gravity.CENTER) {
                if (((TextView)v).getCurrentTextColor() == 0xFFFFFFFF) {
                    ((TextView)v).setText(result);
                    ((TextView)v).setTextSize(20);
                    break;
                }
            }
        }
    }

    private String formatDuration(long ms) {
        long days = ms / (1000*60*60*24);
        long hours = (ms % (1000*60*60*24)) / (1000*60*60);
        long mins = (ms % (1000*60*60)) / (1000*60);
        return days + "d " + hours + "h " + mins + "m";
    }

    // ======== 5. White Noise ========

    private void buildWhiteNoise() {
        titleText.setText("White Noise");
        String[] noises = {"[~] Waves", "[*] Rain", "[~] Wind", "[o] Nature", "[#] White"};

        for (int i = 0; i < noises.length; i++) {
            final int fi = i;
            Button btn = new Button(this, null, android.R.attr.buttonStyleSmall);
            btn.setText(noises[i]);
            btn.setTextColor(0xFFFFFFFF);
            btn.setTextSize(16);
            btn.setGravity(Gravity.CENTER);
            btn.setPadding(20, 16, 20, 16);
            btn.setBackgroundResource(R.drawable.glass_card);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, 56);
            lp.setMargins(0, 6, 0, 6);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ToolsActivity.this, "Playing: " + noises[fi], Toast.LENGTH_SHORT).show();
                }
            });
            contentArea.addView(btn);
        }

        addInfo("Tap to play ambient sounds");
    }

    private void addInfo(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0x80FFFFFF);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 20, 0, 0);
        contentArea.addView(tv);
    }
}