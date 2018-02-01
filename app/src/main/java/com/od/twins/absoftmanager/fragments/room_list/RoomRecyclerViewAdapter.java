package com.od.twins.absoftmanager.fragments.room_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.models.RoomModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.ViewHolder> {

    private List<RoomModel> mValues;
    private final RoomListFragment.OnListRoomListener mListener;
    private final SimpleDateFormat formatterDate;

    public RoomRecyclerViewAdapter(List<RoomModel> items, RoomListFragment.OnListRoomListener listener) {
        mValues = items;
        mListener = listener;
        formatterDate = new SimpleDateFormat("HH:mm", Locale.US);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getRoomName());
        holder.mContentView.setText(holder.mItem.getNicknameCreator());
        holder.mTimeView.setText(formatterDate.format(new Date(holder.mItem.getTimeCreated())));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onClick(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues != null)
            return mValues.size();
        else
            return 0;
    }

    public void setList(List<RoomModel> list) {
        mValues = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mTimeView;
        public final TextView mContentView;
        public RoomModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
            mTimeView = view.findViewById(R.id.time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
