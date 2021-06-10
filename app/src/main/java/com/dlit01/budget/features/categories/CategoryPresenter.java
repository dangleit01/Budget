package com.dlit01.budget.features.categories;

import android.support.annotation.NonNull;
import com.dlit01.budget.model.Category;
import com.dlit01.budget.util.BasePresenter;

/**
 * Created by 7h1b0
 */

interface CategoryPresenter extends BasePresenter<CategoryView> {
  void loadCategory();

  void deleteCategory(@NonNull Category category);
}
