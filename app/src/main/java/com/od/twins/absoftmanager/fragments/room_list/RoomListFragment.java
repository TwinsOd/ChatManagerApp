package com.od.twins.absoftmanager.fragments.room_list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.models.RoomModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListRoomListener}
 * interface.
 */
public class RoomListFragment extends Fragment {
    private OnListRoomListener mListener;
    private List<RoomModel> listRoomModel;
    private RoomRecyclerViewAdapter adapter;

    public RoomListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RoomListFragment newInstance() {
        return new RoomListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new RoomRecyclerViewAdapter(listRoomModel, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public void setList(List<RoomModel> list) {
        listRoomModel = list;
    }

    public void updateList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListRoomListener) {
            mListener = (OnListRoomListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListRoomListener {
        void onClick(RoomModel item);
    }
}
