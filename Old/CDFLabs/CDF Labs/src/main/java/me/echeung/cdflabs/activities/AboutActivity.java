package me.echeung.cdflabs.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.echeung.cdflabs.R;

public class AboutActivity extends Activity {

    private ListView aboutList;
    private SimpleAdapter adapter;

    private List<Map<String, String>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getActionBar().setIcon(R.drawable.ic_white);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setupList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Populates and handles list. */
    private void setupList() {
        aboutList = (ListView) findViewById(R.id.about);

        // Handle web links
        aboutList.setClickable(true);
        aboutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
                long position = parent.getItemIdAtPosition(pos);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                if (position == 0)
                    intent.setData(Uri.parse(getString(R.string.website_faq)));
                else if (position == 1)
                    intent.setData(Uri.parse(getString(R.string.website_cats)));
                else if (position == 2)
                    intent.setData(Uri.parse(getString(R.string.website_me)));
                else if (position == 3)
                    intent.setData(Uri.parse(getString(R.string.website_github)));

                startActivity(intent);
            }
        });

        // Populate the list with the about info from an array
        final String[] aboutData = getResources().getStringArray(R.array.about);
        data = new ArrayList<Map<String, String>>();

        // 2 items per row
        for (int i = 0; i < aboutData.length; i += 2) {
            Map<String, String> item = new HashMap<String, String>();
            item.put("1", aboutData[i]);
            item.put("2", aboutData[i + 1]);
            data.add(item);
        }

        adapter = new SimpleAdapter(this, data, R.layout.about_list_item,
                new String[]{"1", "2"}, new int[]{android.R.id.text1, android.R.id.text2});

        aboutList.setAdapter(adapter);
    }
}
