package com.od.twins.absoftmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.od.twins.absoftmanager.R;
import com.squareup.picasso.Picasso;

import static com.od.twins.absoftmanager.Constants.URL;

public class FullScreenImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        Intent intent = getIntent();
        if (intent != null) {
            Picasso.with(this)
                    .load(intent.getStringExtra(URL))
                    .into((ImageView) findViewById(R.id.image_full));
        }
    }
}

