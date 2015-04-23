package me.echeung.cdflabs.utils;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.echeung.cdflabs.adapters.ViewPagerAdapter;
import me.echeung.cdflabs.fragments.LabsFragment;
import me.echeung.cdflabs.labs.Lab;

public class LabDataScraper extends AsyncTask<Void, Void, Void> {

    private static final String USAGE_URL =
            "http://www.cdf.toronto.edu/usage/usage.html";

    private Document doc;

    private List<Lab> labs;

    public LabDataScraper() {
        this.labs = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Lab machine usage
            doc = Jsoup.connect(USAGE_URL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void items) {
        if (doc != null) {
            labs = parseLabData();

            LabsFragment labsFragment = ViewPagerAdapter.getLabsFragment();

            if (labsFragment != null) {
                labsFragment.updateAdapter(labs);
            }
        }
    }

    /**
     * Parse the web page document and return it as a list of objects.
     *
     * @return A list of Lab objects.
     */
    private List<Lab> parseLabData() {
        Elements links = doc.select("td");

        int i = 0;
        Lab lab = null;

        // Each lab has 6 elements:
        // name, avail, busy, total, % busy, timestamp
        for (Element link : links) {
            String text = link.text();
            switch (i) {
                case 0:
                    lab = new Lab();

                    if (!text.equals("NX"))
                        text = "BA " + text;

                    lab.setLab(text);
                    break;
                case 1:
                    lab.setAvail(Integer.parseInt(text));
                    break;
                case 2:
                    lab.setBusy(Integer.parseInt(text));
                    break;
                case 3:
                    lab.setTotal(Integer.parseInt(text));
                    break;
                case 4:
                    lab.setPercent(Double.parseDouble(text));
                    break;
                case 5:
                    lab.setTimestamp(text);
                    labs.add(lab);
                    i = -1;
                    break;
            }
            i++;
        }

        return labs;
    }
}
