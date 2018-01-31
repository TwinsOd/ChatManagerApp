package com.od.twins.absoftmanager.fragments.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.od.twins.absoftmanager.R;
import com.od.twins.absoftmanager.models.MessageModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.od.twins.absoftmanager.Constants.BASIC_IMAGE_URL;
import static com.od.twins.absoftmanager.Constants.FILE_TYPE;
import static com.od.twins.absoftmanager.Constants.IMAGE_TYPE;
import static com.od.twins.absoftmanager.Constants.TEXT_TYPE;


public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private final List<MessageModel> mValues;
    private int[] mUsernameColors;

    public ChatRecyclerViewAdapter(List<MessageModel> items, Context context) {
        mValues = items;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case TEXT_TYPE:
                layout = R.layout.item_message;
                break;
            case IMAGE_TYPE:
                layout = R.layout.item_image;
                break;
            case FILE_TYPE:

                break;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MessageModel model = mValues.get(position);
        switch (model.getIntType()) {
            case TEXT_TYPE:
                holder.setMessage(model.getText());
                holder.setUsername(model.getName_client());
                break;
            case IMAGE_TYPE:
                holder.setImageView(model.getName_image());
                break;
            case FILE_TYPE:

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).getIntType();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mUsernameView;
        TextView mMessageView;
        ImageView mImageView;


        public ViewHolder(View view) {
            super(view);
            mUsernameView = itemView.findViewById(R.id.username);
            mMessageView = itemView.findViewById(R.id.message);
            mImageView = itemView.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " ?????";
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

        public void setImageView(String nameFile) {
            if (null == mImageView) return;
            Picasso
                    .with(mImageView.getContext())
                    .load(BASIC_IMAGE_URL + nameFile)
                    .into(mImageView);
            Log.i("ChatRecyclerViewAdapter", BASIC_IMAGE_URL + nameFile);
        }

        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }
    }
}
