package ninja.poepoe.library;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashSet;

/**
 * Created by Poe Poe on 13/12/16.
 */

public class GridContentAdapter extends RecyclerView.Adapter<GridContentAdapter.ViewHolder> {

  private PanelAdapter stackAdapter;
  private HashSet<RecyclerView> observerList = new HashSet<>();
  private RecyclerView headerRecyclerView;
  private HashSet<Integer> rows = new HashSet<>();

  public GridContentAdapter(PanelAdapter stackAdapter, RecyclerView headerRecyclerView) {
    this.stackAdapter = stackAdapter;
    this.headerRecyclerView = headerRecyclerView;
    initRecyclerView(headerRecyclerView);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return stackAdapter.getRowCount() - 1;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ViewHolder viewHolder = new ViewHolder(new RecyclerView(parent.getContext()));
    initRecyclerView(viewHolder.recyclerView);
    return viewHolder;
  }

  @Override public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
    int position = holder.getAdapterPosition();
    if (rows.contains(position)) {
      rows.remove(position);
    }
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.rowItemAdapter.setData(stackAdapter, position + 1);

    if (!rows.contains(position)) {
      rows.add(position);

      LinearLayoutManager currentLayoutManager =
          (LinearLayoutManager) headerRecyclerView.getLayoutManager();
      int firstVisiblePosition = currentLayoutManager.findFirstVisibleItemPosition();
      View v = currentLayoutManager.getChildAt(0);

      if (v != null) {
        int offset;
        offset = currentLayoutManager.getDecoratedRight(v);

        for (RecyclerView rv : observerList) {
          if (headerRecyclerView != rv) {
            //scroll to position
            LinearLayoutManager otherLayoutManager = (LinearLayoutManager) rv.getLayoutManager();
            if (otherLayoutManager != null) {
              otherLayoutManager.scrollToPositionWithOffset(firstVisiblePosition + 1, offset);
            }
          }
        }
      }
    }
  }

  private void initRecyclerView(RecyclerView recyclerView) {
    observerList.add(recyclerView);
    recyclerView.addOnScrollListener(new ScrollListener(observerList, false));
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    RecyclerView recyclerView;
    RowItemAdapter rowItemAdapter = new RowItemAdapter();
    LinearLayoutManager manager;

    public ViewHolder(View view) {
      super(view);
      manager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
      this.recyclerView = (RecyclerView) view;
      this.recyclerView.setLayoutManager(manager);
      this.recyclerView.setAdapter(rowItemAdapter);
    }
  }
}
