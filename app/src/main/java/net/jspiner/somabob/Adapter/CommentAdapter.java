package net.jspiner.somabob.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.jspiner.somabob.Model.CommentModel;
import net.jspiner.somabob.Model.ReviewModel;
import net.jspiner.somabob.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project SomaBob
 * @since 2016. 7. 17.
 */
public class CommentAdapter extends BaseAdapter {


    //로그에 쓰일 tag
    public static final String TAG = CommentAdapter.class.getSimpleName();

    LayoutInflater inflater;

    private SparseArray<WeakReference<View>> viewArray;

    public ArrayList<CommentModel.CommentObject> commentList;
    Context context;

    ProgressDialog mProgressDialog;

    public CommentAdapter(Context context, ArrayList<CommentModel.CommentObject> commentList){
        this.context = context;
        this.commentList = commentList;
        this.viewArray = new SparseArray<WeakReference<View>>(commentList.size());
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(viewArray != null && viewArray.get(position) != null) {
            convertView = viewArray.get(position).get();
            if(convertView != null)
                return convertView;
        }

        try {

            CommentModel.CommentObject commentObject = commentList.get(position);

            convertView = inflater.inflate(R.layout.item_comment_row, null);

            ViewBinder binder = new ViewBinder(convertView, position);

            binder.tvName.setText(commentObject.userName);
            binder.tvText.setText( Html.fromHtml(commentObject.commentText));
            binder.tvTime.setText(commentObject.writeTime);

            Picasso.with(context)
                    .load(commentObject.userImage)
                    .resize(200, 200)
                    .centerCrop()
                    .into(binder.imvProfile);

        } finally {
            viewArray.put(position, new WeakReference<View>(convertView));
        }
        return convertView;
    }

    class ViewBinder {

        @Bind(R.id.tv_comment_name)
        TextView tvName;
        @Bind(R.id.tv_comment_text)
        TextView tvText;
        @Bind(R.id.tv_comment_time)
        TextView tvTime;
        @Bind(R.id.imv_comment_profile)
        ImageView imvProfile;


        int position;

        public ViewBinder(View view, int position){
            this.position = position;
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void update() {
        viewArray.clear();
        notifyDataSetChanged();
    }
}
