package com.xp.note.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xp.note.R;
import com.xp.note.model.Note;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private List<Note> notes;

    private ListAdapter.OnItemClickListener onItemClickListener;

    public ListAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.notes_row, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // 绑定数据
        holder.id.setText(notes.get(position).getId() + "");
        holder.title.setText(notes.get(position).getTitle());
        holder.content.setText(notes.get(position).getContent());
        holder.time.setText("编辑于"+notes.get(position).getTime());
        holder.priority.setText("优先级："+notes.get(position).getPriority());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public Note getItem(int position) {
        return notes.get(position);
    }




    public void updataView(List<Note> newNotes) {
        if(notes == null) {
            notes = new ArrayList<>();
        }
        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }


    public void removeAllItem() {
        notes.clear();
        notifyDataSetChanged();
    }

    //从List移除对象
    public void removeItem(int position) {
        if(notes == null || notes.isEmpty()) {
            return;
        }
        notes.remove(position);
        notifyItemRemoved(position);
    }



    class MyViewHolder extends RecyclerView.ViewHolder
    {
        public  TextView  id;
        private TextView title;
        private TextView content;
        private TextView time;
        private TextView priority;

        public MyViewHolder(View view)
        {
            super(view);
            id = view.findViewById(R.id.note_id);
            title = view.findViewById(R.id.note_title);
            content = view.findViewById(R.id.note_content);
            time = view.findViewById(R.id.note_time);
            priority = view.findViewById(R.id.note_priority);
        }
    }



    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(ListAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
