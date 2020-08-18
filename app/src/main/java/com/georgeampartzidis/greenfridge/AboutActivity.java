package com.georgeampartzidis.greenfridge;

import android.content.Intent;
import android.content.res.Configuration;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Enable the links inside the TextViews to be able to be clicked on
        TextView iconLinkTextView = findViewById(R.id.tv_icon_link);
        iconLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView translationLinkTextView = findViewById(R.id.tv_translation_credits);
        translationLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar= getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainActivityIntent = new Intent(AboutActivity.this,
                        MainActivity.class);
                startActivity(MainActivityIntent);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent MainActivityIntent = new Intent(AboutActivity.this,
                            MainActivity.class);
                    startActivity(MainActivityIntent);
                }
            });
        }
    }
}
