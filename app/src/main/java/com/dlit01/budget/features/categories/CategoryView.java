package com.dlit01.budget.features.categories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.dlit01.budget.model.Category;
import java.util.ArrayList;

/**
 * Created by 7h1b0
 */

interface CategoryView {

  void onCategoryLoaded(@NonNull ArrayList<Category> categories);

  void onError(@Nullable String error);
}
