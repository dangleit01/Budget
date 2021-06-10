package com.dlit01.budget.features.budgets;

import android.support.annotation.NonNull;
import com.dlit01.budget.model.Budget;
import com.dlit01.budget.util.BasePresenter;

/**
 * Created by 7h1b0.
 */

interface BudgetPresenter extends BasePresenter<BudgetView> {

  void loadBudgets();

  void deleteBudget(@NonNull Budget budget);
}
