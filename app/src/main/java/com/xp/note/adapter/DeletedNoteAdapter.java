package com.xp.note.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xp.note.R;
import com.xp.note.model.Note_Deleted;

import java.util.ArrayList;
import java.util.List;

public class DeletedNoteAdapter extends RecyclerView.Adapter<DeletedNoteAdapter.MyViewHolder> {

    private List<Note_Deleted> notes;

    private DeletedNoteAdapter.OnItemClickListener onItemClickListener;

    public DeletedNoteAdapter(List<Note_Deleted> notes) {
        this.notes = notes;
    }

    @Override
    public DeletedNoteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.notes_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // 绑定数据
        holder.title.setText(notes.get(position).getTitle());
        holder.content.setText(notes.get(position).getContent());
        holder.priority.setText("优先级："+ notes.get(position).getPriority());


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


    public Note_Deleted getItem(int position) {
        return notes.get(position);
    }




    public void updataView(List<Note_Deleted> newNotes) {
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
        private TextView title;
        private TextView content;
        private TextView priority;

        public MyViewHolder(View view)
        {
            super(view);
            title = view.findViewById(R.id.note_title);
            content = view.findViewById(R.id.note_content);
            priority = view.findViewById(R.id.note_priority);
        }
    }



    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(DeletedNoteAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

}
