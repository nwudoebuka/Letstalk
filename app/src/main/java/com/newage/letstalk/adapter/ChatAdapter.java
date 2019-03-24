package com.newage.letstalk.adapter;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    public void releaseResources(){

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

    public class MyChatItemViewHolder extends ViewHolder implements View.OnClickListener, View.OnTouchListener,
            MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
        TextView mMessageTextView;
        ImageView mMessageImageView;
        RelativeLayout mAudioLayout;
        SeekBar mSeekBar;
        ImageButton mUploadPlay;


        private MediaPlayer mediaPlayer;
        private int audioDuration;
        private final Handler handler = new Handler();

        private MyChatItemViewHolder(View itemView) {
            super(itemView);
            mMessageTextView = itemView.findViewById(R.id.message_text);
            mMessageImageView = itemView.findViewById(R.id.message_image);
            mAudioLayout = itemView.findViewById(R.id.audio_layout);
            mUploadPlay = itemView.findViewById(R.id.upload_play);
            mSeekBar = itemView.findViewById(R.id.seekbar);

            mMessageImageView.setOnClickListener(this);
            mUploadPlay.setOnClickListener(this);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void bindType(ChatMessage item) {
            MyChatMessage chat = (MyChatMessage) item;

            if(chat.getMessageText() != null) {
                mMessageTextView.setText(chat.getMessageText());
                mMessageTextView.setVisibility(View.VISIBLE);
            }

            if(!TextUtils.isEmpty(chat.getMessageImage())) {
                mMessageImageView.setVisibility(View.VISIBLE);

                Glide.with(mMessageImageView).load(chat.getMessageImage()).into(mMessageImageView);

//                GlideApp.with(mMessageImageView.getContext()).load(chat.getMessageImage())
//                        .placeholder(R.drawable.ic_image_placeholder)
//                        .error(R.drawable.ic_broken_image)
//                        .into(mMessageImageView);
            }

            if(chat.getMessageAudio() != null){
                mAudioLayout.setVisibility(View.VISIBLE);
                mSeekBar.setMax(99);
                mSeekBar.setOnTouchListener(this);

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnBufferingUpdateListener(this);
                mediaPlayer.setOnCompletionListener(this);

                try {
                    mediaPlayer.setDataSource(chat.getMessageAudio());
                    mediaPlayer.prepare();
                    audioDuration = mediaPlayer.getDuration();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.upload_play) {
                playAudio();
            }
        }

        private void playAudio() {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                mUploadPlay.setBackgroundResource(R.drawable.ic_pause);
            } else {
                mediaPlayer.pause();
                mUploadPlay.setBackgroundResource(R.drawable.ic_play);
            }

            seekBarProgressUpdater();
        }

        private void seekBarProgressUpdater() {
            mSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / audioDuration) * 100));
            if (mediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        seekBarProgressUpdater();
                    }
                };
                handler.postDelayed(notification, 1000);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if (v.getId() == R.id.seekbar) {
                if (mediaPlayer.isPlaying()) {
                    SeekBar sb = (SeekBar) v;

                    int seconds = (audioDuration / 100) * sb.getProgress();
                    mediaPlayer.seekTo(seconds);
                }
            }
            return false;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
            mSeekBar.setSecondaryProgress(percent);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
            //mUploadPlay.setImageResource(R.drawable.playaudio);
            mUploadPlay.setBackgroundResource(R.drawable.ic_play);
            mSeekBar.setProgress(1);
        }
    }

}