package com.choicehotels.vpix;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    String[] VIRTUAL_TOURS = new String[] {
            "https://www.vpix.net/index.php?tour=445780",
            "https://www.vpix.net/index.php?tour=44645",
            "https://www.vpix.net/index.php?tour=446166",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        int paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView text = new TextView(this);
        text.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        text.setText("Click the items below to load the virtual tours. After loading several (exact number varies), the WebView in VirtualTourActivity will simply stop loading pages.");
        layout.addView(text);
        ListView list = new ListView(this);
        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, VIRTUAL_TOURS));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onVirtualTourClicked(position);
            }
        });
        layout.addView(list);
        setContentView(layout);
    }

    public void onVirtualTourClicked(int index) {
        Intent intent = new Intent(this, VirtualTourActivity.class);
        intent.setData(Uri.parse(VIRTUAL_TOURS[index]));
        startActivity(intent);
    }

}
