package com.od.twins.absoftmanager.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.od.twins.absoftmanager.Application;
import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.fragments.chat.ChatFragment;
import com.od.twins.absoftmanager.fragments.room_list.MessageModel;
import com.od.twins.absoftmanager.fragments.room_list.RoomListFragment;
import com.od.twins.absoftmanager.models.RoomModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.od.twins.absoftmanager.fragments.room_list.MessageModel.TYPE_MESSAGE;

public class MainActivity extends AppCompatActivity implements RoomListFragment.OnListRoomListener, ChatFragment.OnChatListener {
    private Socket mSocket;
    private static final String TAG = "MainFragment";
    //    private String room = "room_1";
//    private String nameUsers;
//    private String message;
    private String name = "Manager_911";
    private List<RoomModel> listRoom;
    private ChatFragment chatFragment;
    private RoomListFragment roomListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("manager_android");

        Application app = (Application) getApplication();
        mSocket = app.getSocket();
        mSocket.on("message", onNewMessage);
        mSocket.on("room_list", onRoomList);
        mSocket.on("connected", onConnected);
        mSocket.on("connect_user", onConnect);
        mSocket.on("disconnect_user", onDisconnect);
        mSocket.connect();

//        if (getIntent() != null) {
//            room = getIntent().getStringExtra(ROOM);
//            nameUsers = getIntent().getStringExtra(NAME);
//            message = getIntent().getStringExtra(MESSAGE);
//        }

//        ((TextView) findViewById(R.id.name_view)).setText(nameUsers);
//        ((TextView) findViewById(R.id.room_view)).setText(room);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("message", onNewMessage);
        mSocket.off("room_list", onRoomList);
        mSocket.off("connected", onConnected);
        mSocket.off("connect_user", onConnect);
        mSocket.off("disconnect_user", onDisconnect);
    }

    public void onShowFragment(Fragment fragment, boolean addStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment);
        if (!addStack) {
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }
        } else {
            fragmentTransaction.addToBackStack(fragment.toString());
        }
        fragmentTransaction.commit();
    }

    private void sendInfo(String nameRoom) {
        Log.i("sendInfo", "sendInfo:start = " + name);
        if (null == name) return;
        if (!mSocket.connected()) return;

        mSocket.emit("info_user", name, nameRoom, "manager");
        Log.i("sendInfo", "sendInfo:finish = " + name);
    }

    private Emitter.Listener onRoomList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "onRoomList:**********************************");
            Log.i("onRoomList", args[0].toString());

            Type listType = new TypeToken<ArrayList<RoomModel>>() {
            }.getType();
            Gson gson = new Gson();
            listRoom = gson.fromJson(args[0].toString(), listType);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (roomListFragment == null) {
                        roomListFragment = RoomListFragment.newInstance();
                        roomListFragment.setList(listRoom);
                        onShowFragment(roomListFragment, false);
                    } else
                        roomListFragment.updateList();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "onNewMessage:**********************************");
//            Type listType = new TypeToken<ArrayList<RoomModel>>() {
//            }.getType();
//            Gson gson = new Gson();
//            listRoom = gson.fromJson(args[0].toString(), listType);
            //{"event":"messageReceived","name":"device  Samsung","text":"ааа","time":"18:56:15"}
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        Log.i("onNewMessage", data.toString());
                        username = data.getString("name");
                        message = data.getString("text");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                    chatFragment.setMessage(new MessageModel(message, username, TYPE_MESSAGE));
                }
            });
        }
    };

    private Emitter.Listener onConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "onConnected:**********************************");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendInfo(null);
                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "onConnect:**********************************");
            Log.i(TAG, (String) args[0]);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "onDisconnect:**********************************");
            Log.i(TAG, (String) args[0]);
        }
    };

    @Override
    public void onClick(RoomModel item) {
        Log.i(TAG, "onOutChat:**********************************");
        sendInfo(item.getRoomName());
        chatFragment = ChatFragment.newInstance();
        onShowFragment(chatFragment, true);
    }

    @Override
    public void onOutChat() {
        mSocket.emit("room_out", "manager");
    }
}
