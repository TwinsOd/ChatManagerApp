package com.od.twins.absoftmanager.fragments.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.fragments.room_list.MessageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnChatListener}
 * interface.
 */
public class ChatFragment extends Fragment {
    private OnChatListener mListener;
    private List<MessageModel> listMessage = new ArrayList<>();
    private ChatRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new ChatRecyclerViewAdapter(listMessage, getContext());
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChatListener) {
            mListener = (OnChatListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null)
            mListener.onOutChat();
        mListener = null;
    }

    public void setMessage(MessageModel model) {
        listMessage.add(model);
        adapter.notifyDataSetChanged();
    }

    public interface OnChatListener {
        // TODO: Update argument type and name
        void onOutChat();
    }
}
