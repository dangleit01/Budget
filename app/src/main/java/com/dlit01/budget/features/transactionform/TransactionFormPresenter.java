package com.dlit01.budget.features.transactionform;

import android.support.annotation.NonNull;
import com.dlit01.budget.model.Transaction;
import com.dlit01.budget.util.BasePresenter;

/**
 * Created by 7h1b0.
 */

interface TransactionFormPresenter extends BasePresenter<TransactionFormView> {

  void addTransaction(@NonNull Transaction transaction);

  void updateTransaction(@NonNull Transaction transaction);

  void loadCategoriesAndBudgets();
}
