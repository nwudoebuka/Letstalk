package com.newage.letstalk.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newage.letstalk.R;
import com.newage.letstalk.model.Group;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private List<Group> groupList;
    private GroupClickListener listener;

    public GroupListAdapter(GroupClickListener listener) {
        this.listener = listener;
    }

    public void swapItems(List<Group> newItems) {
        if (newItems == null) return;

        if (groupList != null) groupList.clear();
        groupList = newItems;

        // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_group, parent, false);
        return new ItemVieHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Group friend = groupList.get(position);
        if (friend == null) return;

        holder.bindType(friend);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }


    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(Group group);
    }

    public class ItemVieHolder extends ViewHolder implements View.OnClickListener {
        TextView tv;
        ImageView img;

        ItemVieHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.textView1);
            img = itemView.findViewById(R.id.profile_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindType(Group group) {
            tv.setText(group.getName());
            img.setImageResource(group.getImage());
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onGroupClick(groupList.get(getAdapterPosition()));
            }
        }
    }

    public interface GroupClickListener {
        void onGroupClick(Group group);
    }
}