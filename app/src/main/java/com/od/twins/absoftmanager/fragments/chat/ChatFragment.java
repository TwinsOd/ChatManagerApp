package com.od.twins.absoftmanager.fragments.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.callback.OnChatListener;
import com.od.twins.absoftmanager.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private List<MessageModel> listMessage = new ArrayList<>();
    private ChatRecyclerViewAdapter adapter;
    private OnChatListener chatListener;
    private EditText mInputMessageView;

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

        RecyclerView recyclerView = view.findViewById(R.id.list_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatRecyclerViewAdapter(listMessage, getContext());
        recyclerView.setAdapter(adapter);

        mInputMessageView = view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    chatListener.setTextMessage(getMessage());
                    return true;
                }
                return false;
            }
        });
        view.findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatListener.setTextMessage(getMessage());
            }
        });
        view.findViewById(R.id.send_image_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatListener.onSetImage();
            }
        });
//        mInputMessageView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (null == mUserName) return;
//                if (!mSocket.connected()) return;
//
//                if (!mTyping) {
//                    mTyping = true;
//                    mSocket.emit("typing");
//                }
//
//                mTypingHandler.removeCallbacks(onTypingTimeout);
//                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
        return view;
    }

    public void setMessage(MessageModel model) {
        listMessage.add(model);
        adapter.notifyDataSetChanged();
    }

    public void setChatListener(OnChatListener chatListener) {
        this.chatListener = chatListener;
    }

    public String getMessage() {
        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return null;
        }
        mInputMessageView.setText("");
        if (!TextUtils.isEmpty(message)) {
            setMessage(new MessageModel("My message", message, "text"));
        }
        return message;
    }
}
