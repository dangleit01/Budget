package com.dlit01.budget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import com.dlit01.budget.model.Budget;
import com.dlit01.budget.model.Category;
import com.dlit01.budget.model.Transaction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 7h1b0.
 */

abstract class Database extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 103;
  private static final String DATABASE_NAME = "budget";
  static final String TABLE_TRANSACTION = "transaction_table";
  static final String TABLE_CATEGORY = "category_table";
  static final String TABLE_BUDGET = "budget_table";

  static BriteDatabase db;

  Database(@NonNull Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    SqlBrite sqlBrite = new SqlBrite.Builder().build();
    db = sqlBrite.wrapDatabaseHelper(this, Schedulers.io());
  }

  @Override public void onCreate(SQLiteDatabase db) {
    String CREATE_TRANSACTION_TABLE = "CREATE TABLE "
        + TABLE_TRANSACTION
        + " ( "
        + Transaction.ID
        + " INTEGER PRIMARY KEY, "
        + Transaction.DAY
        + " INTEGER, "
        + Transaction.MONTH
        + " INTEGER, "
        + Transaction.YEAR
        + " INTEGER, "
        + Transaction.VALUE
        + " INTEGER, "
        + Transaction.ID_CATEGORY
        + " INTEGER, "
        + Transaction.ID_BUDGET
        + " INTEGER, "
        + Transaction.ID_FROM_BUDGET
        + " INTEGER, "
        + Transaction.ID_FROM_BUDGET_TRANSACTION
        + " INTEGER, "
        + Transaction.DESCRIPTION
        + " TEXT ) ";

    String CREATE_CATEGORY_TABLE = "CREATE TABLE "
        + TABLE_CATEGORY
        + " ( "
        + Category.ID
        + " INTEGER PRIMARY KEY, "
        + Category.TITLE
        + " TEXT, "
        + Category.DESCRIPTION
        + " TEXT, "
        + Category.COLOR
        + " INTEGER, "
        + Category.ICON
        + " INTEGER, "
        + Category.ID_BUDGET
        + " INTEGER, "
        + Category.ID_FROM_BUDGET
        + " INTEGER )";

    String CREATE_BUDGET_TABLE = "CREATE TABLE "
        + TABLE_BUDGET
        + " ( "
        + Budget.ID
        + " INTEGER PRIMARY KEY, "
        + Budget.TITLE
        + " TEXT, "
        + Budget.VALUE
        + " INTEGER ) ";

    db.execSQL(CREATE_TRANSACTION_TABLE);
    db.execSQL(CREATE_CATEGORY_TABLE);
    db.execSQL(CREATE_BUDGET_TABLE);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
    onCreate(db);
  }

  Cursor getCursor(@NonNull SqlBrite.Query query) {
    return query.run();
  }

  ContentValues getContentValues(@NonNull Category category) {
    ContentValues values = new ContentValues();
    values.put(Category.TITLE, category.getTitle());
    values.put(Category.DESCRIPTION, category.getDescription());
    values.put(Category.COLOR, category.getColor());
    values.put(Category.ICON, category.getIcon());
    values.put(Category.ID_BUDGET, category.getIdBudget());
    values.put(Category.ID_FROM_BUDGET, category.getIdFromBudget());

    return values;
  }

  ContentValues getContentValues(@NonNull Budget budget) {
    ContentValues values = new ContentValues();
    values.put(Budget.TITLE, budget.getTitle());
    values.put(Budget.VALUE, budget.getValue());
    return values;
  }
}
