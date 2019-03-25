package com.newage.letstalk.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newage.letstalk.R;
import com.newage.letstalk.dataLayer.local.tables.ChatList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<ChatList> chatListList;
    private FriendClickListener listener;

    public FriendListAdapter(FriendClickListener listener) {
        this.listener = listener;
    }

    public void swapItems(List<ChatList> newItems) {
        if (newItems == null) return;

        if (chatListList != null) chatListList.clear();
        chatListList = newItems;

        // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_friend, parent, false);
        return new ItemVieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ChatList chatList = chatListList.get(position);
        if (chatList == null) return;

        holder.bindType(chatList);
        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return chatListList == null ? 0 : chatListList.size();
    }


    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ChatList chatList);
    }

    public class ItemVieHolder extends ViewHolder implements View.OnClickListener {
        TextView tvw1;
        TextView tvw2;
        ImageView profileDp;

        ItemVieHolder(View itemView) {
            super(itemView);
            tvw1 = itemView.findViewById(R.id.tvprofilename);
            tvw2 = itemView.findViewById(R.id.tvemail);
            profileDp = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindType(ChatList chatList) {
            tvw1.setText(chatList.getName());
            tvw2.setText(chatList.getPhone());
            if (!TextUtils.isEmpty(chatList.getDp())) {
                Picasso.with(profileDp.getContext()).load(chatList.getDp()).into(profileDp);
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onFriendClick(chatListList.get(getAdapterPosition()));
            }
        }
    }

    public interface FriendClickListener {
        void onFriendClick(ChatList chatList);
    }
}