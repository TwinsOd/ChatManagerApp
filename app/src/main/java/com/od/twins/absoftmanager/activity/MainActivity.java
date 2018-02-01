package com.od.twins.absoftmanager.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.od.twins.absoftmanager.Application;
import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.callback.OnChatListener;
import com.od.twins.absoftmanager.fragments.chat.ChatFragment;
import com.od.twins.absoftmanager.fragments.room_list.RoomListFragment;
import com.od.twins.absoftmanager.models.MessageModel;
import com.od.twins.absoftmanager.models.RoomModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements RoomListFragment.OnListRoomListener, OnChatListener {
    private final int RESULT_LOAD_IMG = 23;
    private Socket mSocket;
    private static final String TAG = "MainFragment";
    //    private String room = "room_1";
//    private String nameUsers;
//    private String message;
    private String name;
    private List<RoomModel> listRoom;
    private ChatFragment chatFragment;
    private RoomListFragment roomListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("manager_android");

        Application app = (Application) getApplication();
        name = app.getNickName();
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

    private void sendInfo() {
        if (!mSocket.connected()) return;

        mSocket.emit("info_manager", name);
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
                    } else {
                        roomListFragment.setList(listRoom);
                        roomListFragment.updateList();
                    }
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i(TAG, "onNewMessage:**********************************");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    MessageModel messageModel = gson.fromJson(args[0].toString(), MessageModel.class);
                    chatFragment.setMessage(messageModel);
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
                    sendInfo();
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
        Log.i(TAG, "onIGonChat:**********************************");
        mSocket.emit("room_in", item.getRoomName());
        chatFragment = ChatFragment.newInstance();
        chatFragment.setChatListener(this);
        onShowFragment(chatFragment, true);
    }

    @Override
    public void onOutChat() {
        mSocket.emit("room_out", "manager");
    }

    @Override
    public void onSetImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void setTextMessage(String message) {
        if (!TextUtils.isEmpty(message))
            mSocket.emit("message", "text", message);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK && reqCode == RESULT_LOAD_IMG) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                mSocket.emit("message", "image", byteArray);
                MessageModel messageModel = new MessageModel(name);
                messageModel.setType("image");
                messageModel.setPath_local_image("file://" + getRealPathFromURI(imageUri));
                chatFragment.setMessage(messageModel);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = 0;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "empty";
    }
}
