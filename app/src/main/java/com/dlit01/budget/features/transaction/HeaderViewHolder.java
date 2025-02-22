package com.dlit01.budget.features.transaction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.dlit01.budget.R;
import com.dlit01.budget.model.TransactionItem;

/**
 * Created by 7h1b0.
 */

final class HeaderViewHolder extends RecyclerView.ViewHolder {
  public TextView text;

  HeaderViewHolder(View v) {
    super(v);
    text = v.findViewById(R.id.text);
  }

  void bindTo(TransactionItem transactionItem) {
    text.setText(transactionItem.getTitle());
  }
}
