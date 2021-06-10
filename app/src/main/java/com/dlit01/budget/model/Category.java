package com.dlit01.budget.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import com.dlit01.budget.R;

/**
 * Created by 7h1b0
 */

public class Category implements Parcelable {

  public static final String CATEGORY = "category";
  public static final String CATEGORIES = "categories";
  public static final String ID = "c_id";
  public static final String TITLE = "c_title";
  public static final String DESCRIPTION = "c_description";
  public static final String COLOR = "c_color";
  public static final String ICON = "c_icon";
  public static final String ID_BUDGET = "c_id_budget";
  public static final String ID_FROM_BUDGET = "c_id_from_budget";

  private long id;
  private long idBudget;
  private String titleBudget;
  private long idFromBudget;
  private String titleFromBudget;
  private String title;
  private String description;
  @ColorInt private int color;
  @DrawableRes private int icon;

  public Category(Parcel in) {
    id = in.readLong();
    title = in.readString();
    description = in.readString();
    color = in.readInt();
    icon = in.readInt();
    idBudget = in.readLong();
    titleBudget = in.readString();
    idFromBudget = in.readLong();
    titleFromBudget = in.readString();
  }

  public Category(@ColorInt int color) {
    this(-1, -1, -1, null, null, color, R.mipmap.ic_category);
  }

  public Category(long id, long idBudget, long idFromBudget, String title, String description, @ColorInt int color, @DrawableRes int icon) {
    this.id = id;
    this.idBudget = idBudget;
    this.idFromBudget = idFromBudget;
    this.title = title;
    this.description = description;
    this.color = color;
    this.icon = icon;
  }

  public Category(long idBudget, long idFromBudget, String title, String description, @ColorInt int color, @DrawableRes int icon) {
    this(-1, idBudget, idFromBudget, title, description, color, icon);
  }

  public Category(String title, String description, @ColorInt int color, @DrawableRes int icon) {
    this(-1, -1, -1, title, description, color, icon);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isDescriptionDefined() {
    return description != "";
  }

  @ColorInt public int getColor() {
    return color;
  }

  public void setColor(@ColorInt int color) {
    this.color = color;
  }

  @DrawableRes public int getIcon() {
    return icon;
  }

  public void setIcon(@DrawableRes int icon) {
    this.icon = icon;
  }

  public long getIdBudget() {
    return idBudget;
  }

  public void setIdBudget(long idBudget) {
    this.idBudget = idBudget;
  }

  public String getTitleBudget() {
    return titleBudget;
  }

  public void setTitleBudget(String titleBudget) {
    this.titleBudget = titleBudget;
  }

  public boolean isBudgetIdDefined() {
    return idBudget < 1;
  }

  public long getIdFromBudget() {
    return idFromBudget;
  }

  public void setIdFromBudget(long idBudget) {
    this.idFromBudget = idBudget;
  }

  public String getTitleFromBudget() {
    return titleFromBudget;
  }

  public void setTitleFromBudget(String titleBudget) {
    this.titleFromBudget = titleBudget;
  }

  public boolean isFromBudgetIdDefined() {
    return idFromBudget < 1;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(id);
    dest.writeString(title);
    dest.writeString(description);
    dest.writeInt(color);
    dest.writeInt(icon);
    dest.writeLong(idBudget);
    dest.writeString(titleBudget);
    dest.writeLong(idFromBudget);
    dest.writeString(titleFromBudget);
  }

  public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
    @Override public Category createFromParcel(Parcel source) {
      return new Category(source);
    }

    @Override public Category[] newArray(int size) {
      return new Category[size];
    }
  };

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Category category = (Category) o;

    if (id != category.id) return false;
    if (idBudget != category.idBudget) return false;
    if (idFromBudget != category.idFromBudget) return false;
    if (color != category.color) return false;
    if (icon != category.icon) return false;
    if (description != null) {
      if (!description.equals(category.description)) {
        return false;
      }
    } else {
      if (category.description != null) {
        return false;
      }
    }
    return title != null ? title.equals(category.title) : category.title == null;
  }

  @Override public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (int) (idBudget ^ (idBudget >>> 32));
    result = 31 * result + (int) (idFromBudget ^ (idFromBudget >>> 32));
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + color;
    result = 31 * result + icon;
    return result;
  }
}
