package me.echeung.cdflabs.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.echeung.cdflabs.R;
import me.echeung.cdflabs.adapters.DataScraper;
import me.echeung.cdflabs.adapters.LabsListAdapter;
import me.echeung.cdflabs.labs.Lab;
import me.echeung.cdflabs.labs.LabsByAvail;
import me.echeung.cdflabs.labs.LabsByBuilding;

public class LabsFragment extends Fragment {

    private View rootView;

    private SwipeRefreshLayout mPullToRefresh;
    private RecyclerView listLabs;
    private LabsListAdapter adapter;
    private RelativeLayout labsView;
    private ProgressBar progress;
    private LinearLayout empty;
    private Button retry;
    private Spinner sort;
    private TextView timestamp;

    private List<Lab> labs;

    private Boolean sortAvail = true;

    public LabsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_labs, container, false);

        progress = (ProgressBar) rootView.findViewById(R.id.progress);
        empty = (LinearLayout) rootView.findViewById(R.id.empty_list);

        mPullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.labs_container);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkAvailable()) {
                    // No network connection: show retry
                    empty.setVisibility(View.VISIBLE);
                    labsView.setVisibility(View.GONE);
                    mPullToRefresh.setRefreshing(false);
                } else {
                    // Get data from website
                    DataScraper dataTask = new DataScraper(getActivity(), LabsFragment.this);
                    dataTask.execute();
                }
            }
        });

        labsView = (RelativeLayout) rootView.findViewById(R.id.labs_list);
        timestamp = (TextView) rootView.findViewById(R.id.timestamp);
        listLabs = (RecyclerView) rootView.findViewById(R.id.labs);
        listLabs.setLayoutManager(new LinearLayoutManager(getActivity()));
        sort = (Spinner) rootView.findViewById(R.id.sort);

        retry = (Button) rootView.findViewById(R.id.btn_retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeView(rootView);
            }
        });

        return initializeView(rootView);
    }

    private View initializeView(View rootView) {
        if (!isNetworkAvailable()) {
            // No network connection: show retry button
            empty.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);

            return rootView;
        } else {
            empty.setVisibility(View.GONE);

            // Get data from website
            DataScraper dataTask = new DataScraper(getActivity(), this);
            dataTask.execute();

            // The list of labs
            listLabs.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int topRowVerticalPosition =
                            (listLabs == null || listLabs.getChildCount() == 0) ? 0 : listLabs.getChildAt(0).getTop();
                    mPullToRefresh.setEnabled(topRowVerticalPosition >= 0);
                }
            });

            sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                           int position, long id) {
                    if (labs != null) {
                        if (position == 0) {
                            // Labs by availability
                            Collections.sort(labs, new LabsByAvail());
                            sortAvail = true;
                        } else {
                            // Labs by building
                            Collections.sort(labs, new LabsByBuilding());
                            sortAvail = false;
                        }
                        updateAdapter(labs);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }

        return rootView;
    }

    /** Update the adapter with the new list of labs. */
    public void updateAdapter(List<Lab> labs) {
        // Hide progress spinner, and show the list
        progress.setVisibility(View.GONE);
        labsView.setVisibility(View.VISIBLE);

        // Complete pull to refresh
        mPullToRefresh.setRefreshing(false);

        // Sort according to what the user has selected
        if (sortAvail)
            Collections.sort(labs, new LabsByAvail());
        else
            Collections.sort(labs, new LabsByBuilding());

        // Set the list
        this.labs = labs;

        if (adapter == null) {
            // Initialize adapter for the list of labs
            adapter = new LabsListAdapter(getActivity());
            adapter.setLabs(labs);
            listLabs.setAdapter(adapter);
        } else {
            // Update with new list
            adapter.setLabs(labs);
            adapter.notifyDataSetChanged();
        }

        timestamp.setText(String.format(getString(R.string.timestamp), labs.get(0).getTimestamp()));
    }

    /** Checks if there's an Internet connection and returns true iff there is. */
    private boolean isNetworkAvailable() {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
