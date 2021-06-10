package com.dlit01.budget.features.budgetmonth;

import com.dlit01.budget.util.BasePresenter;

/**
 * Created by 7h1b0
 */

interface BudgetMonthPresenter extends BasePresenter<BudgetMonthView> {
  void loadBudgets(int year, int month);

  void loadBalance(int month, int year);
}
