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
import com.newage.letstalk.dataLayer.local.tables.Friend;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<Friend> friendList;
    private FriendClickListener listener;

    public FriendListAdapter(FriendClickListener listener) {
        this.listener = listener;
    }

    public void swapItems(List<Friend> newItems) {
        if (newItems == null) return;

        if (friendList != null) friendList.clear();
        friendList = newItems;

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
        Friend friend = friendList.get(position);
        if (friend == null) return;

        holder.bindType(friend);
        //holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return friendList == null ? 0 : friendList.size();
    }


    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(Friend friend);
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
        public void bindType(Friend friend) {
            tvw1.setText(friend.getName());
            tvw2.setText(friend.getPhone());
            if (!TextUtils.isEmpty(friend.getDp())) {
                Picasso.with(profileDp.getContext()).load(friend.getDp()).into(profileDp);
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onFriendClick(friendList.get(getAdapterPosition()));
            }
        }
    }

    public interface FriendClickListener {
        void onFriendClick(Friend friend);
    }
}