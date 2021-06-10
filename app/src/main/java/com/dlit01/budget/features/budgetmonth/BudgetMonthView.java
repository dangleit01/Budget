package com.dlit01.budget.features.budgetmonth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.dlit01.budget.model.PresentationBalance;
import com.dlit01.budget.model.PresentationBudget;
import java.util.ArrayList;

/**
 * Created by 7h1b0
 */

interface BudgetMonthView {
  void onBudgetLoaded(@NonNull ArrayList<PresentationBudget> budgets);

  void onBalanceLoaded(@NonNull PresentationBalance balance);

  void onError(@Nullable String error);
}
