package com.newage.letstalk;

/**
 * Created by Newage_android on 5/11/2018.
 */

import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Juned on 2/8/2017.
 */

import java.util.List;
import com.android.volley.toolbox.ImageLoader;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    public  int HEADER_VIEW = 3;
    private final static int CONTENT_VIEW = 1;
    String username;
    Context context;
    private CardView cardView;
    private RelativeLayout rlayout;
    private TextView message;


    List<DataAdapter> dataAdapters;

    ImageLoader imageLoader;
    de.hdodenhof.circleimageview.CircleImageView circle;




    public RecyclerViewAdapter(List<DataAdapter> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        Updatecardview cardv = new Updatecardview();
        cardv.getme();
        cardv.getyou();

        int layoutRes = 0;
//        switch (viewType) {
//            case 1:
//                layoutRes = R.single_friend.cardview;
//                break;
//            case 2:
//                layoutRes = R.single_friend.cardview2;
//                break;
//            case 3:
//                layoutRes = R.single_friend.cardview3;
//                break;
//        }
//        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
//        ViewHolder viewHolder = new ViewHolder(view);
//        return viewHolder;

//
//        if ( HEADER_VIEW == 1){
//            layoutRes = R.single_friend.cardview;
//            //        View view = LayoutInflater.from(parent.getContext()).inflate(R.single_friend.cardview2, parent, false);
//            View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder(view);
//
//            return viewHolder;
//
//        }else if ( HEADER_VIEW == 2){
//            layoutRes = R.single_friend.cardview3;
//            //        View view = LayoutInflater.from(parent.getContext()).inflate(R.single_friend.cardview2, parent, false);
//            View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder(view);
//
//            return viewHolder;
//
//        }else{
//
//            layoutRes = R.single_friend.cardview2;
//
//
//
//
//            //        View view = LayoutInflater.from(parent.getContext()).inflate(R.single_friend.cardview2, parent, false);
//            View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder(view);
//
//            return viewHolder;
//        }

        layoutRes = R.layout.cardview2;




        //        View view = LayoutInflater.from(parent.getContext()).inflate(R.single_friend.cardview2, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;



    }

    @Override
    public int getItemViewType(int position) {

//if (HEADER_VIEW == 1){
//    return HEADER_VIEW;
//}else{
//    return CONTENT_VIEW;
//}

                return HEADER_VIEW;










    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        DataAdapter dataAdapterOBJ =  dataAdapters.get(position);

        imageLoader = ImageAdapter.getInstance(context).getImageLoader();

        imageLoader.get(dataAdapterOBJ.getImageUrl(),
                ImageLoader.getImageListener(
                        Viewholder.VollyImageView,//Server Image
                        R.mipmap.defaultuser,//Before loading server image the default showing image.
                        R.mipmap.defaultuser //Error image if requested image dose not found on server.


                )
        );

        Viewholder.VollyImageView.setImageUrl(dataAdapterOBJ.getImageUrl(), imageLoader);
        Viewholder.ImageTitleTextView.setText(dataAdapterOBJ.getImageTitle());
        Viewholder.audio.setText(dataAdapterOBJ.getImageaudio());
        Viewholder.video.setText(dataAdapterOBJ.getImageaudio());
if(dataAdapterOBJ.getImageSender().equalsIgnoreCase(dataAdapterOBJ.getImagesession())) {

//
//    Updatecardview cardv = new Updatecardview();
//    cardv.setme(1);
//    if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("nothingmtgcora")) {
//        Viewholder.VollyImageView.setVisibility(View.VISIBLE);
//        Viewholder.ImageTitleTextView.setVisibility(View.GONE);
//        cardView.setCardBackgroundColor(Color.TRANSPARENT);
//        rlayout.setBackgroundColor(Color.TRANSPARENT);
//        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        linearParams.setMargins(300, 0, 0, 0);
//        rlayout.setLayoutParams(linearParams);
//        rlayout.requestLayout();
//
//    }else if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("audiomtgcora")){
//        // CODE FOR ADD MARGINS
//        Viewholder.VollyImageView.setVisibility(View.GONE);
//        Viewholder.audio.setVisibility(View.GONE);
//        Viewholder.playaudio.setVisibility(View.VISIBLE);
////        Viewholder.ImageTitleTextView.setText("Play audio");
//        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        linearParams.setMargins(300, 0, 0, 0);
//        rlayout.setLayoutParams(linearParams);
//        rlayout.requestLayout();
//
//    }else{
//        // CODE FOR ADD MARGINS
//        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        linearParams.setMargins(300, 0, 0, 0);
//        rlayout.setLayoutParams(linearParams);
//        rlayout.requestLayout();
//
//    }
    if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("audiomtgcora")){
//        // CODE FOR ADD MARGINS
        Viewholder.VollyImageView.setVisibility(View.GONE);
        Viewholder.audio.setVisibility(View.GONE);
//        Viewholder.playaudio.setVisibility(View.VISIBLE);
        Viewholder.ImageTitleTextView.setText("►Play audio");
        message.setTextColor(Color.rgb(53, 53, 53));
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(300, 0, 0, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

    }else if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("connectedmtgcora")){
        rlayout.setBackgroundColor(Color.TRANSPARENT);
        Viewholder.ImageTitleTextView.setText("You are now connected to Messenger");
        message.setTextColor(Color.BLACK);
        message.setVisibility(View.GONE);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(300, 0, 300, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

    }else if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("nothingmtgcora")){
        //        // CODE FOR ADD MARGINS
        Viewholder.audio.setText(dataAdapterOBJ.getImageUrl());
        Viewholder.VollyImageView.setVisibility(View.GONE);
        Viewholder.audio.setVisibility(View.GONE);
//        Viewholder.playaudio.setVisibility(View.VISIBLE);
        Viewholder.ImageTitleTextView.setText("VIEW IMAGE");
        message.setTextColor(Color.rgb(53, 53, 53));
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(300, 0, 0, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

    }else{
        message.setTextColor(Color.rgb(0,0,0));
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(

                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(300, 0, 0, 0);
        rlayout.setBackgroundColor(Color.rgb(130, 127, 127));
        message.setTextColor(Color.rgb(53,53,53));
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();
    }
}else if(dataAdapterOBJ.getImageSender().equalsIgnoreCase("none")){


    message.setTextColor(Color.rgb(45,45,45));
    rlayout.setBackgroundColor(Color.rgb(188, 186, 186));
    // CODE FOR ADD MARGINS
    LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    linearParams.setMargins(300, 0, 0, 0);

    rlayout.setLayoutParams(linearParams);
    rlayout.requestLayout();

}else{

// cardView.setCardBackgroundColor(Color.WHITE);
//    rlayout.setBackgroundColor(Color.WHITE);
//    // CODE FOR ADD MARGINS
//    LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//    linearParams.setMargins(0, 0, 300, 0);
//    rlayout.setLayoutParams(linearParams);
//    rlayout.requestLayout();
//
//    message.setTextColor(Color.rgb(90, 17, 155));


//    if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("nothingmtgcora")) {
//        Viewholder.VollyImageView.setVisibility(View.VISIBLE);
//        Viewholder.ImageTitleTextView.setVisibility(View.GONE);
//        cardView.setCardBackgroundColor(Color.TRANSPARENT);
//        rlayout.setBackgroundColor(Color.TRANSPARENT);
//        rlayout.setBackgroundColor(Color.WHITE);
//        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//    linearParams.setMargins(0, 0, 300, 0);
//    rlayout.setLayoutParams(linearParams);
//    rlayout.requestLayout();
//
//    message.setTextColor(Color.rgb(90, 17, 155));
//
//    }else if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("audiomtgcora")){
//        // CODE FOR ADD MARGINS
//        Viewholder.VollyImageView.setVisibility(View.GONE);
//        Viewholder.audio.setVisibility(View.GONE);
//        Viewholder.playaudio.setVisibility(View.VISIBLE);
//        Viewholder.ImageTitleTextView.setText("Play audio");
//        rlayout.setBackgroundColor(Color.WHITE);
//        rlayout.setBackgroundColor(Color.WHITE);
//        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        linearParams.setMargins(0, 0, 300, 0);
//        rlayout.setLayoutParams(linearParams);
//        rlayout.requestLayout();
//
//        message.setTextColor(Color.rgb(90, 17, 155));
//
//    }else{
//        rlayout.setBackgroundColor(Color.WHITE);
//        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        linearParams.setMargins(0, 0, 300, 0);
//        rlayout.setLayoutParams(linearParams);
//        rlayout.requestLayout();
//
//        message.setTextColor(Color.rgb(90, 17, 155));
//
//    }
   


    if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("audiomtgcora")){
//        // CODE FOR ADD MARGINS
        Viewholder.VollyImageView.setVisibility(View.GONE);
        Viewholder.audio.setVisibility(View.GONE);
//        Viewholder.playaudio.setVisibility(View.VISIBLE);
        Viewholder.ImageTitleTextView.setText("►Play audio");
        rlayout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(0, 0, 300, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

        message.setTextColor(Color.rgb(53, 53, 53));

    }else if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("connectedmtgcora")){
        rlayout.setBackgroundColor(Color.TRANSPARENT);
        Viewholder.ImageTitleTextView.setText("You are now connected to Messenger");
        message.setTextColor(Color.BLACK);
        message.setVisibility(View.GONE);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(300, 0, 300, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

    }else if(dataAdapterOBJ.getImageTitle().equalsIgnoreCase("nothingmtgcora")){
 // CODE FOR ADD MARGINS
        Viewholder.audio.setText(dataAdapterOBJ.getImageUrl());
        Viewholder.VollyImageView.setVisibility(View.GONE);
        Viewholder.audio.setVisibility(View.GONE);
//        Viewholder.playaudio.setVisibility(View.VISIBLE);
        Viewholder.ImageTitleTextView.setText("VIEW IMAGE");
        message.setTextColor(Color.rgb(53, 53, 53));
        rlayout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(0, 0, 300, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

    }else  {
        rlayout.setBackgroundColor(Color.WHITE);
//        rlayout.setBackgroundResource(R.drawable.emoji);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(0, 0, 300, 0);
        rlayout.setLayoutParams(linearParams);
        rlayout.requestLayout();

        message.setTextColor(Color.rgb(53, 53, 53));
    }


}
    }

    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ImageTitleTextView;
        public TextView msgtype;
        public TextView sender;
        public TextView audio;
        public TextView video;
        public NetworkImageView VollyImageView ;
        public ImageButton playaudio;

        public ViewHolder(View itemView) {

            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview2);
            rlayout = (RelativeLayout) itemView.findViewById(R.id.rellay);
            ImageTitleTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView) ;
            msgtype = (TextView) itemView.findViewById(R.id.messagetype) ;
            sender = (TextView) itemView.findViewById(R.id.sender) ;
            audio = (TextView) itemView.findViewById(R.id.audio) ;
            video = (TextView) itemView.findViewById(R.id.video) ;
            VollyImageView = (NetworkImageView) itemView.findViewById(R.id.VolleyImageView) ;
            message = (TextView) itemView.findViewById(R.id.ImageNameTextView) ;
            playaudio = (ImageButton) itemView.findViewById(R.id.playaudio);


        }
    }
}
