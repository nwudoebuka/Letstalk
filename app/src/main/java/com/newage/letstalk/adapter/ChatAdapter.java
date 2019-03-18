package com.newage.letstalk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.newage.letstalk.R;
import com.newage.letstalk.interfaces.ChatType;
import com.newage.letstalk.model.MyChat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatType> chatTypes;

    public ChatAdapter() { }

    public void addItems(List<ChatType> newItems) {
        if (newItems == null) return;

        chatTypes.addAll(newItems);

        //            int last = recyclerView.getAdapter().getItemCount() - 1;
//            recyclerView.smoothScrollToPosition(last);
//            recyclerViewadapter.notifyDataSetChanged();


        // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    public void addItem(ChatType newItem) {
        if (newItem == null) return;

        chatTypes.add(newItem);

        //            int last = recyclerView.getAdapter().getItemCount() - 1;
//            recyclerView.smoothScrollToPosition(last);
//            recyclerViewadapter.notifyDataSetChanged();


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
            case ChatType.TYPE_FRIEND:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
                return new FriendChatItemViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview2, parent, false);
                return new MyChatItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ChatType chatType = chatTypes.get(position);
        if (chatType == null) return;

        holder.bindType(chatType);
        holder.setIsRecyclable(false);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ChatType item);
    }

    public class FriendChatItemViewHolder extends ViewHolder implements View.OnClickListener {
        public TextView ImageTitleTextView;
        public TextView msgtype;
        public TextView sender;
        public TextView audio;
        public TextView video;
        public NetworkImageView VollyImageView;
        public ImageButton playaudio;

        private FriendChatItemViewHolder(View itemView) {
            super(itemView);
//            cardView = (CardView) itemView.findViewById(com.newage.letstalk.R.id.cardview2);
//            rlayout = (RelativeLayout) itemView.findViewById(com.newage.letstalk.R.id.rellay);
//            ImageTitleTextView = (TextView) itemView.findViewById(com.newage.letstalk.R.id.ImageNameTextView);
//            msgtype = (TextView) itemView.findViewById(com.newage.letstalk.R.id.messagetype);
//            sender = (TextView) itemView.findViewById(com.newage.letstalk.R.id.sender);
//            audio = (TextView) itemView.findViewById(com.newage.letstalk.R.id.audio);
//            video = (TextView) itemView.findViewById(com.newage.letstalk.R.id.video);
//            VollyImageView = (NetworkImageView) itemView.findViewById(com.newage.letstalk.R.id.VolleyImageView);
//            message = (TextView) itemView.findViewById(com.newage.letstalk.R.id.ImageNameTextView);
//            playaudio = (ImageButton) itemView.findViewById(com.newage.letstalk.R.id.playaudio);
        }

        @Override
        public void bindType(ChatType item) {

        }

        @Override
        public void onClick(View v) {

        }
    }

    public class MyChatItemViewHolder extends ViewHolder implements View.OnClickListener {
        public TextView ImageTitleTextView;
        public TextView msgtype;
        public TextView sender;
        public TextView audio;
        public TextView video;
        public NetworkImageView VollyImageView;
        public ImageButton playaudio;

        private MyChatItemViewHolder(View itemView) {
            super(itemView);
            msgtype = itemView.findViewById(com.newage.letstalk.R.id.messagetype);

//            cardView = (CardView) itemView.findViewById(com.newage.letstalk.R.id.cardview2);
//            rlayout = (RelativeLayout) itemView.findViewById(com.newage.letstalk.R.id.rellay);
//            ImageTitleTextView = (TextView) itemView.findViewById(com.newage.letstalk.R.id.ImageNameTextView);
//            sender = (TextView) itemView.findViewById(com.newage.letstalk.R.id.sender);
//            audio = (TextView) itemView.findViewById(com.newage.letstalk.R.id.audio);
//            video = (TextView) itemView.findViewById(com.newage.letstalk.R.id.video);
//            VollyImageView = (NetworkImageView) itemView.findViewById(com.newage.letstalk.R.id.VolleyImageView);
//            message = (TextView) itemView.findViewById(com.newage.letstalk.R.id.ImageNameTextView);
//            playaudio = (ImageButton) itemView.findViewById(com.newage.letstalk.R.id.playaudio);
        }

        @Override
        public void bindType(ChatType item) {
            MyChat chat = (MyChat) item;

            msgtype.setText(chat.getMessage());
        }

        @Override
        public void onClick(View v) {

        }
    }




    //    @Override
//    public void onBindViewHolder(ViewHolder Viewholder, int position) {
//
//        DataAdapter dataAdapterOBJ = dataAdapters.get(position);
//
//        imageLoader = ImageAdapter.getInstance(context).getImageLoader();
//
//        imageLoader.get(dataAdapterOBJ.getImageUrl(),
//                ImageLoader.getImageListener(
//                        Viewholder.VollyImageView,//Server Image
//                        com.newage.letstalk.R.mipmap.defaultuser,//Before loading server image the default showing image.
//                        com.newage.letstalk.R.mipmap.defaultuser //Error image if requested image dose not found on server.
//                )
//        );
//
//        Viewholder.VollyImageView.setImageUrl(dataAdapterOBJ.getImageUrl(), imageLoader);
//        Viewholder.ImageTitleTextView.setText(dataAdapterOBJ.getImageTitle());
//        Viewholder.audio.setText(dataAdapterOBJ.getImageaudio());
//        Viewholder.video.setText(dataAdapterOBJ.getImageaudio());
//        if (dataAdapterOBJ.getImageSender().equalsIgnoreCase(dataAdapterOBJ.getImagesession())) {
//
//            if (dataAdapterOBJ.getImageTitle().equalsIgnoreCase("audiomtgcora")) {
////        // CODE FOR ADD MARGINS
//                Viewholder.VollyImageView.setVisibility(View.GONE);
//                Viewholder.audio.setVisibility(View.GONE);
////        Viewholder.playaudio.setVisibility(View.VISIBLE);
//                Viewholder.ImageTitleTextView.setText("►Play audio");
//                message.setTextColor(Color.rgb(53, 53, 53));
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(300, 0, 0, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//            } else if (dataAdapterOBJ.getImageTitle().equalsIgnoreCase("connectedmtgcora")) {
//                rlayout.setBackgroundColor(Color.TRANSPARENT);
//                Viewholder.ImageTitleTextView.setText("You are now connected to Messenger");
//                message.setTextColor(Color.BLACK);
//                message.setVisibility(View.GONE);
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(300, 0, 300, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//            } else if (dataAdapterOBJ.getImageTitle().equalsIgnoreCase("nothingmtgcora")) {
//                //        // CODE FOR ADD MARGINS
//                Viewholder.audio.setText(dataAdapterOBJ.getImageUrl());
//                Viewholder.VollyImageView.setVisibility(View.GONE);
//                Viewholder.audio.setVisibility(View.GONE);
////        Viewholder.playaudio.setVisibility(View.VISIBLE);
//                Viewholder.ImageTitleTextView.setText("VIEW IMAGE");
//                message.setTextColor(Color.rgb(53, 53, 53));
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(300, 0, 0, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//            } else {
//                message.setTextColor(Color.rgb(0, 0, 0));
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(300, 0, 0, 0);
//                rlayout.setBackgroundColor(Color.rgb(130, 127, 127));
//                message.setTextColor(Color.rgb(53, 53, 53));
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//            }
//        } else if (dataAdapterOBJ.getImageSender().equalsIgnoreCase("none")) {
//
//
//            message.setTextColor(Color.rgb(45, 45, 45));
//            rlayout.setBackgroundColor(Color.rgb(188, 186, 186));
//            // CODE FOR ADD MARGINS
//            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            linearParams.setMargins(300, 0, 0, 0);
//
//            rlayout.setLayoutParams(linearParams);
//            rlayout.requestLayout();
//
//        } else {
//
//            if (dataAdapterOBJ.getImageTitle().equalsIgnoreCase("audiomtgcora")) {
////        // CODE FOR ADD MARGINS
//                Viewholder.VollyImageView.setVisibility(View.GONE);
//                Viewholder.audio.setVisibility(View.GONE);
////        Viewholder.playaudio.setVisibility(View.VISIBLE);
//                Viewholder.ImageTitleTextView.setText("►Play audio");
//                rlayout.setBackgroundColor(Color.WHITE);
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(0, 0, 300, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//                message.setTextColor(Color.rgb(53, 53, 53));
//
//            } else if (dataAdapterOBJ.getImageTitle().equalsIgnoreCase("connectedmtgcora")) {
//                rlayout.setBackgroundColor(Color.TRANSPARENT);
//                Viewholder.ImageTitleTextView.setText("You are now connected to Messenger");
//                message.setTextColor(Color.BLACK);
//                message.setVisibility(View.GONE);
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(300, 0, 300, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//            } else if (dataAdapterOBJ.getImageTitle().equalsIgnoreCase("nothingmtgcora")) {
//                // CODE FOR ADD MARGINS
//                Viewholder.audio.setText(dataAdapterOBJ.getImageUrl());
//                Viewholder.VollyImageView.setVisibility(View.GONE);
//                Viewholder.audio.setVisibility(View.GONE);
////        Viewholder.playaudio.setVisibility(View.VISIBLE);
//                Viewholder.ImageTitleTextView.setText("VIEW IMAGE");
//                message.setTextColor(Color.rgb(53, 53, 53));
//                rlayout.setBackgroundColor(Color.WHITE);
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(0, 0, 300, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//            } else {
//                rlayout.setBackgroundColor(Color.WHITE);
////        rlayout.setBackgroundResource(R.drawable.emoji);
//                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                linearParams.setMargins(0, 0, 300, 0);
//                rlayout.setLayoutParams(linearParams);
//                rlayout.requestLayout();
//
//                message.setTextColor(Color.rgb(53, 53, 53));
//            }
//        }
//
//    }

}
