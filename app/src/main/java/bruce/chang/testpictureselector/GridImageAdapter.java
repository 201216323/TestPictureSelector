package bruce.chang.testpictureselector;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: BruceChang
 * Date on : 2017/2/8.
 * Time on: 11:52
 * Progect_Name:TestPictureSelector
 * Source Github：
 * Description:
 */

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {

    public final int TYPE_CAMERA = 1;
    public final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private Context mContext;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;
    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    /**
     * 添加图片的回调接口
     */
    public interface onAddPicClickListener {
        void onAddPicClick(int type, int position);
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }


    protected OnItemClickListener mItemClickListener;

    /**
     * 图片的点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.gv_filter_image,
                parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        //itemView 的点击事件
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(viewHolder.getAdapterPosition(), v);
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.mipmap.addimg_1x);
            viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAddPicClickListener.onAddPicClick(0, viewHolder.getAdapterPosition());
                }
            });
            viewHolder.ll_del.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.ll_del.setVisibility(View.VISIBLE);
            viewHolder.ll_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnAddPicClickListener.onAddPicClick(1, viewHolder.getAdapterPosition());
                }
            });
            LocalMedia media = list.get(position);
            int type = media.getType();
            String path = media.getPath();
            switch (type) {
                case 1:
                    // 图片
                    if (media.isCompressed()) {
                        Log.i("compress image result", new File(media.getCompressPath()).length() / 1024 + "k");
                    }

                    Glide.with(mContext)
                            .load(path)
                            .asBitmap().centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(viewHolder.mImg);
                    break;
                case 2:
                    // 视频
                    Glide.with(mContext).load(path).thumbnail(0.5f).into(viewHolder.mImg);
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 是否显示添加按钮,
     * 条件是判断当前位置position和list的size是否相等
     * 相等则证明有增加图片按钮
     *
     * @param position
     * @return
     */
    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        LinearLayout ll_del;

        public ViewHolder(View view) {
            super(view);
            mImg = (ImageView) view.findViewById(R.id.fiv);
            ll_del = (LinearLayout) view.findViewById(R.id.ll_del);
        }
    }

}
