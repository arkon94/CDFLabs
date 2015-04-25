package me.echeung.cdflabs.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.echeung.cdflabs.R;
import me.echeung.cdflabs.holders.PrinterHeadingHolder;
import me.echeung.cdflabs.holders.PrinterJobHolder;
import me.echeung.cdflabs.holders.TimestampHolder;
import me.echeung.cdflabs.printers.PrintQueue;
import me.echeung.cdflabs.printers.Printer;

public class PrintersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Some constants
    private static final int JOB = 0;
    private static final int HEADING = 1;
    private static final int TIMESTAMP = 2;

    private Activity mContext;
    private List<Printer> mPrinters;
    private List<PrintQueue> mQueue;
    private int headingCount;

    public PrintersListAdapter(Activity context) {
        this.mContext = context;
        this.mPrinters = new ArrayList<>();
        this.mQueue = new ArrayList<>();
        this.headingCount = 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case TIMESTAMP:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timestamp_item, parent, false);
                return new TimestampHolder(v);
            case HEADING:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.printer_heading_item, parent, false);
                return new PrinterHeadingHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.printer_list_item, parent, false);
                return new PrinterJobHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return TIMESTAMP;

        if (mQueue.get(position) == null)
            return HEADING;

        return JOB;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int index) {
        if (index == getItemCount() - 1) {
            TimestampHolder timestampHolder = (TimestampHolder) holder;

            timestampHolder.timestampView.setText(String.format(mContext.getString(R.string.timestamp),
                    mPrinters.get(0).getTimestamp()));
        } else if (mQueue.get(index) == null) {
            PrinterHeadingHolder headingHolder = (PrinterHeadingHolder) holder;

            headingHolder.headingView.setText(
                    String.format(mContext.getString(R.string.bahen_room),
                            mPrinters.get(headingCount++).getName()));
        } else {
            PrinterJobHolder jobHolder = (PrinterJobHolder) holder;

            final PrintQueue job = mQueue.get(index);

            jobHolder.ownerView.setText(job.getOwner());
            jobHolder.infoView .setText(
                    String.format("Rank: %s | Job ID: %s | Started at %s",
                            job.getRank(), job.getJob(), job.getTime()));
            jobHolder.filesView.setText(
                    String.format("Files: %s (%s B)",
                            job.getFiles(), job.getSize()));
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 for the timestamp "footer"
        return mQueue.size() + 1;
    }

    public void setPrinters(List<Printer> printers) {
        this.mPrinters = printers;

        this.mQueue.clear();
        for (Printer p : printers) {
            this.mQueue.add(null);  // For the heading
            this.mQueue.addAll(p.getPrintQueue());
        }

        this.headingCount = 0;
    }
}