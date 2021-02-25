package eu.wanielista.notepadplus.adapter;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import eu.wanielista.notepadplus.EditNote;
import eu.wanielista.notepadplus.R;

import com.google.firebase.database.DataSnapshot;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String[] items;
    private String[] itemsDesc;
    private String[] itemsDescShow;
    private String[] itemsDate;
    private DataSnapshot dataSnapshot;

    public RecyclerAdapter(Context context, String[] items, String[] itemsDesc, String[] itemsDescShow, String [] itemsDate) {
        this.context = context;
        this.items = items;
        this.itemsDesc = itemsDesc;
        this.itemsDescShow = itemsDescShow;
        this.itemsDate = itemsDate;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        Item item = new Item(v);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ((Item)holder).textView.setText(items[position]);
        ((Item)holder).textViewDesc.setText(itemsDescShow[position]);
        ((Item)holder).textViewDate.setText(itemsDate[position]);


        final int pos = position;
        final int lastPos = getItemCount()-1;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditNote.class);
                String noteID = String.valueOf(pos);
                String lastPosString = String.valueOf(lastPos);
                if(pos == lastPos){
                    intent.putExtra("title", items[pos]);
                    intent.putExtra("note", itemsDesc[pos]);
                    intent.putExtra("noteID", noteID);
                    intent.putExtra("notePOS", pos);
                    intent.putExtra("lastID", lastPosString);

                    context.startActivity(intent);
                }else {
                    intent.putExtra("title", items[pos]);
                    intent.putExtra("note", itemsDesc[pos]);
                    intent.putExtra("noteID", noteID);


                    //last note
                    intent.putExtra("titleLast", items[lastPos]);
                    intent.putExtra("noteLast", itemsDesc[lastPos]);
                    intent.putExtra("dateLast", itemsDate[lastPos]);
                    intent.putExtra("lastID", lastPosString);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }


    public class Item extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textViewDesc;
        TextView textViewDate;

        public Item(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item);
            textViewDesc = (TextView) itemView.findViewById(R.id.description);
            textViewDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

}
