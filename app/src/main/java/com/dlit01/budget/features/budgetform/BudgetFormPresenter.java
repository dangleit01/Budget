package com.dlit01.budget.features.budgetform;

import android.support.annotation.NonNull;
import com.dlit01.budget.model.Budget;
import com.dlit01.budget.util.BasePresenter;

/**
 * Created by 7h1b0.
 */

interface BudgetFormPresenter extends BasePresenter<BudgetFormView> {

  void addBudget(@NonNull Budget budget);

  void updateBudget(@NonNull Budget budget);
}
