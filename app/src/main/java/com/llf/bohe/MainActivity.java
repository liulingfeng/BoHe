package com.llf.bohe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private MeterView mMeterView;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMeterView = (MeterView) findViewById(R.id.meterView);
        tvContent = (TextView) findViewById(R.id.tv_content);
        mMeterView.setValue(60, 30, 200, 0.1f);
        SpannableString spannableString = new SpannableString(60 + "kg");
        SuperscriptSpan superscriptSpan = new SuperscriptSpan();
        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1f);
        RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(0.4f);
        spannableString.setSpan(sizeSpan01, 0, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan02, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(superscriptSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvContent.setText(spannableString);
        mMeterView.setOnValueChangeListener(new MeterView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                SpannableString spannableString = new SpannableString(value + "kg");
                SuperscriptSpan superscriptSpan = new SuperscriptSpan();
                RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1f);
                RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(0.4f);
                spannableString.setSpan(sizeSpan01, 0, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(sizeSpan02, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(superscriptSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvContent.setText(spannableString);
            }
        });
    }
}
