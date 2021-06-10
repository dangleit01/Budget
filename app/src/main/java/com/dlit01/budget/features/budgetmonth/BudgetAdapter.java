package com.dlit01.budget.features.budgetmonth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dlit01.budget.R;
import com.dlit01.budget.model.PresentationBudget;
import com.dlit01.budget.util.CurrencyUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by 7h1b0.
 */

final class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewBudget> {

  interface OnClickBudget {
    void onClickBudget(@NonNull PresentationBudget budget);
  }

  private ArrayList<PresentationBudget> mItems;
  private OnClickBudget mListener;

  BudgetAdapter(@NonNull OnClickBudget listener) {
    mItems = new ArrayList<>();
    mListener = listener;
    setHasStableIds(true);
  }

  @Override public BudgetAdapter.ViewBudget onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
    ViewBudget vh = new ViewBudget(view);

    view.setOnClickListener(v -> {
      final int position = vh.getAdapterPosition();
      if (position != NO_POSITION) {
        mListener.onClickBudget(mItems.get(position));
      }
    });
    return vh;
  }

  @Override public void onBindViewHolder(BudgetAdapter.ViewBudget holder, int position) {
    final Context context = holder.value.getContext();
    final PresentationBudget budget = mItems.get(position);
    final double res = budget.getValue() + budget.getOut();

    holder.detail.setText(CurrencyUtil.formatToUSD(budget.getValue()) + context.getString(R.string.adding) + CurrencyUtil.formatToUSD(budget.getOut()));
    //NumberFormat formatter = new DecimalFormat(context.getString(R.string.decimal_format));
    //String formattedDetail = formatter.format(budget.getValue()) + " of " + formatter.format(budget.getOut());
    //holder.detail.setText(formattedDetail);
    /*holder.detail.setText(
        context.getString(R.string.budget_of_goal, negation(budget.getOut()), budget.getValue()));*/
    holder.title.setText(budget.getTitle());

    holder.value.setText(CurrencyUtil.formatToUSD(res));
    //String formattedValue = formatter.format(res);
    //holder.value.setText(formattedValue);
    //holder.value.setText(context.getString(R.string.float_value, res));
    if (res >= 0) {
      holder.value.setTextColor(ContextCompat.getColor(context, R.color.green));
    } else {
      holder.value.setTextColor(ContextCompat.getColor(context, R.color.red));
    }
  }

  @Override public long getItemId(int position) {
    return mItems.get(position).hashCode();
  }

  @Override public int getItemCount() {
    return mItems.size();
  }

  class ViewBudget extends RecyclerView.ViewHolder {

    TextView detail;
    TextView title;
    TextView value;

    ViewBudget(View v) {
      super(v);
      title = v.findViewById(R.id.title);
      value = v.findViewById(R.id.value);
      detail = v.findViewById(R.id.detail);
    }
  }

  void addAll(ArrayList<PresentationBudget> items) {
    mItems = items;
    notifyDataSetChanged();
  }

  private double negation(double number) {
    return number == 0 ? number : -number;
  }
}

