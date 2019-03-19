package com.newage.letstalk.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newage.letstalk.R;
import com.newage.letstalk.interfaces.ChatMessage;
import com.newage.letstalk.model.FriendChatMessage;
import com.newage.letstalk.model.MyChatMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> chatTypes = new ArrayList<>();

    public ChatAdapter() { }

    public void addItems(List<ChatMessage> newItems) {
        if (newItems == null) return;

        chatTypes.addAll(newItems);

        // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    public void addItem(ChatMessage newItem) {
        if (newItem == null) return;

        chatTypes.add(newItem);

        // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    public void swapItem(ChatMessage newItem) {
        if (newItem == null) return;

        if(chatTypes == null)
            chatTypes = new ArrayList<>();

        chatTypes.clear();
        chatTypes.add(newItem);

        // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return chatTypes.get(position).getChatViewType();
    }

    @Override
    public int getItemCount() {
        return chatTypes == null ? 0 : chatTypes.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ChatMessage.TYPE_FRIEND:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble_1, parent, false);
                return new FriendChatItemViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble_2, parent, false);
                return new MyChatItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ChatMessage chatMessage = chatTypes.get(position);
        if (chatMessage == null) return;

        holder.bindType(chatMessage);
        holder.setIsRecyclable(false);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ChatMessage item);
    }

    public class FriendChatItemViewHolder extends ViewHolder implements View.OnClickListener {
        TextView mBubbleTextView;
        ImageView mBubbleImageView;

        private FriendChatItemViewHolder(View itemView) {
            super(itemView);
            mBubbleTextView = itemView.findViewById(R.id.message_text);
            mBubbleImageView = itemView.findViewById(R.id.message_image);
        }

        @Override
        public void bindType(ChatMessage item) {
            FriendChatMessage chat = (FriendChatMessage) item;
            mBubbleTextView.setText(chat.getMessageText());
            if(!TextUtils.isEmpty(chat.getMessageImage())) {
                Picasso.with(mBubbleImageView.getContext())
                        .load(chat.getMessageImage()).into(mBubbleImageView);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class MyChatItemViewHolder extends ViewHolder implements View.OnClickListener {
        TextView mBubbleTextView;
        ImageView mBubbleImageView;

        private MyChatItemViewHolder(View itemView) {
            super(itemView);
            mBubbleTextView = itemView.findViewById(R.id.message_text);
            mBubbleImageView = itemView.findViewById(R.id.message_image);
        }

        @Override
        public void bindType(ChatMessage item) {
            MyChatMessage chat = (MyChatMessage) item;

            mBubbleTextView.setText(chat.getMessageText());

            if(!TextUtils.isEmpty(chat.getMessageImage())) {
                Picasso.with(mBubbleImageView.getContext())
                        .load(chat.getMessageImage()).into(mBubbleImageView);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

}