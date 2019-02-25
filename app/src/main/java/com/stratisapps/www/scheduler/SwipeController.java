package com.stratisapps.www.scheduler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;

public class SwipeController extends ItemTouchHelper.Callback {

    private Context context = null;
    private Activity activity = null;
    private EventAdapter eventAdapter = null;
    private boolean removalState = false;

    public SwipeController(Context context, Activity activity, EventAdapter eventAdapter){
        this.context = context;
        this.activity = activity;
        this.eventAdapter = eventAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT );
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View item = viewHolder.itemView;
        final int itemPos = viewHolder.getAdapterPosition();
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.colorDeleteBg));
        Drawable deleteIcon = context.getResources().getDrawable(R.drawable.delete);
        int barMid = (item.getTop() + item.getBottom())/2;
        int iconMid = deleteIcon.getMinimumHeight()/2;
        deleteIcon.setBounds(item.getRight() - deleteIcon.getMinimumWidth() - 80, barMid - iconMid, item.getRight()- 80, barMid + iconMid);
        if(item.getLeft() < item.getRight() + dX - 44){
            c.drawRoundRect(item.getRight() + dX - 44, item.getTop() + 20, item.getRight() - 24, item.getBottom() - 20, 20,20, paint);
            removalState = true;
        }
        else{
            c.drawRoundRect(item.getLeft() + 24, item.getTop() + 20, item.getRight() - 24, item.getBottom() - 20, 20,20, paint);
            if(removalState){
                new RemoveEventTask(item.getContext(), activity, eventAdapter, itemPos).execute();
                removalState = false;
            }
        }
        deleteIcon.draw(c);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
