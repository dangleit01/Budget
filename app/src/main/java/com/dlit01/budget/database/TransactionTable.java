package com.dlit01.budget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite2.BriteDatabase;
import com.dlit01.budget.model.Budget;
import com.dlit01.budget.model.Category;
import com.dlit01.budget.model.Transaction;
import com.dlit01.budget.util.DbUtil;
import io.reactivex.Observable;
import java.util.ArrayList;

/**
 * Created by 7h1b0.
 */

public final class TransactionTable extends Database {
  public TransactionTable(@NonNull Context context) {
    super(context);
  }

  public Observable<ArrayList<Transaction>> getAll(int limit) {
    return db.createQuery(TABLE_TRANSACTION, "SELECT "
        + Transaction.ID
        + ", "
        + Transaction.DAY
        + ", "
        + Transaction.MONTH
        + ","
        + Transaction.YEAR
        + ", "
        + Transaction.VALUE
        + ", "
        + Transaction.ID_CATEGORY
        + ", "
        + Transaction.DESCRIPTION
        + ", "
        + Transaction.ID_BUDGET
        + ", "
        + Transaction.ID_FROM_BUDGET
        + ", "
        + Transaction.ID_FROM_BUDGET_TRANSACTION
        + ", "
        + Category.COLOR
        + ", "
        + Category.ICON
        + " FROM "
        + TABLE_TRANSACTION
        + " AS t JOIN "
        + TABLE_CATEGORY
        + " AS c ON "
        + Transaction.ID_CATEGORY
        + " = "
        + Category.ID
        + " ORDER BY "
        + Transaction.YEAR
        + " DESC, "
        + Transaction.MONTH
        + " DESC, "
        + Transaction.DAY
        + " DESC, "
        + Transaction.ID
        + " DESC LIMIT "
        + limit).map(super::getCursor).map(cursor -> {
      try {
        ArrayList<Transaction> transactions = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
          transactions.add(getTransaction(cursor));
        }
        return transactions;
      } finally {
        cursor.close();
      }
    });
  }

  public Observable<ArrayList<Transaction>> getAll(int year, int month) {
    return db.createQuery(TABLE_TRANSACTION, "SELECT "
        + Transaction.ID
        + ", "
        + Transaction.DAY
        + ", "
        + Transaction.MONTH
        + ","
        + Transaction.YEAR
        + ", "
        + Transaction.VALUE
        + ", "
        + Transaction.ID_CATEGORY
        + ", "
        + Transaction.DESCRIPTION
        + ", "
        + Transaction.ID_BUDGET
        + ", "
        + Transaction.ID_FROM_BUDGET
        + ", "
        + Transaction.ID_FROM_BUDGET_TRANSACTION
        + ", "
        + Category.COLOR
        + ", "
        + Category.ICON
        + " FROM "
        + TABLE_TRANSACTION
        + " AS t JOIN "
        + TABLE_CATEGORY
        + " AS c ON "
        + Transaction.ID_CATEGORY
        + " = "
        + Category.ID
        + " WHERE "
        + Transaction.MONTH
        + " = ? AND "
        + Transaction.YEAR
        + " = ? "
        + " ORDER BY "
        + Transaction.YEAR
        + " DESC, "
        + Transaction.MONTH
        + " DESC, "
        + Transaction.DAY
        + " DESC, "
        + Transaction.ID
        + " DESC", String.valueOf(month), String.valueOf(year))
        .map(super::getCursor)
        .map(cursor -> {
          try {
            ArrayList<Transaction> transactions = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
              transactions.add(getTransaction(cursor));
            }
            return transactions;
          } finally {
            cursor.close();
          }
        });
  }

  public Observable<ArrayList<Transaction>> getAll(int year, int month, long idBudget) {
    return db.createQuery(TABLE_TRANSACTION, "SELECT "
        + Transaction.ID
        + ", "
        + Transaction.DAY
        + ", "
        + Transaction.MONTH
        + ","
        + Transaction.YEAR
        + ", "
        + Transaction.VALUE
        + ", "
        + Transaction.ID_CATEGORY
        + ", "
        + Transaction.DESCRIPTION
        + ", "
        + Transaction.ID_BUDGET
        + ", "
        + Transaction.ID_FROM_BUDGET
        + ", "
        + Transaction.ID_FROM_BUDGET_TRANSACTION
        + ", "
            + Category.COLOR
        + ", "
        + Category.ICON
        + " FROM "
        + TABLE_TRANSACTION
        + " AS t JOIN "
        + TABLE_CATEGORY
        + " AS c ON "
        + Transaction.ID_CATEGORY
        + " = "
        + Category.ID
        + " WHERE "
        + Transaction.MONTH
        + " = ? AND "
        + Transaction.YEAR
        + " = ? AND "
        + Transaction.ID_BUDGET
        + " = ? "
        + " ORDER BY "
        + Transaction.YEAR
        + " DESC, "
        + Transaction.MONTH
        + " DESC, "
        + Transaction.DAY
        + " DESC, "
        + Transaction.ID
        + " DESC", String.valueOf(month), String.valueOf(year), String.valueOf(idBudget))
        .map(super::getCursor)
        .map(cursor -> {
          try {
            ArrayList<Transaction> transactions = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
              transactions.add(getTransaction(cursor));
            }
            return transactions;
          } finally {
            cursor.close();
          }
        });
  }

  public long add(Transaction transaction) {
    long transactionId = 0;
    BriteDatabase.Transaction databaseTransaction = db.newTransaction();
    try {
      // insert transaction
      transactionId = db.insert(TABLE_TRANSACTION, getContentValues(transaction));

      //insert from budget transaction
      Transaction fromTransaction = new Transaction(transactionId, transaction);
      long fromBudgetTransactionId = db.insert(TABLE_TRANSACTION, getContentValues(fromTransaction));

      // update fromBudgetTransactionId for transaction
      transaction.setIdFromBudgetTransaction(fromBudgetTransactionId);
      db.update(TABLE_TRANSACTION, getContentValues(transaction), Transaction.ID + " = ?",
              String.valueOf(transactionId));

      databaseTransaction.markSuccessful();
    } finally {
      databaseTransaction.end();
    }
    return transactionId;
  }

  public int delete(Transaction transaction) {
    int iResult = 0;
    BriteDatabase.Transaction databaseTransaction = db.newTransaction();
    try {
      // delete transaction
      iResult = db.delete(TABLE_TRANSACTION, Transaction.ID + " = ?",
              String.valueOf(transaction.getId()));

      //delete from budget transaction
      db.delete(TABLE_TRANSACTION, Transaction.ID + " = ?",
              String.valueOf(transaction.getIdFromBudgetTransaction()));

      databaseTransaction.markSuccessful();
    } finally {
      databaseTransaction.end();
    }
    return iResult;
  }

  public int delete(Category category) {
    return db.delete(TABLE_TRANSACTION, Transaction.ID_CATEGORY + " = ?",
        String.valueOf(category.getId()));
  }

  public int removeIdBudget(long idBudget) {
    ContentValues values = new ContentValues();
    values.put(Transaction.ID_BUDGET, Budget.NOT_DEFINED);
    return db.update(TABLE_TRANSACTION, values, Transaction.ID_BUDGET + " = ?",
        String.valueOf(idBudget));
  }

  public int removeIdFromBudget(long idBudget) {
    ContentValues values = new ContentValues();
    values.put(Transaction.ID_FROM_BUDGET, Budget.NOT_DEFINED);
    return db.update(TABLE_TRANSACTION, values, Transaction.ID_FROM_BUDGET + " = ?",
            String.valueOf(idBudget));
  }

  public int update(Transaction transaction) {
    int iResult = 0;
    BriteDatabase.Transaction databaseTransaction = db.newTransaction();
    try {
      // update transaction
      iResult = db.update(TABLE_TRANSACTION, getContentValues(transaction), Transaction.ID + " = ?",
              String.valueOf(transaction.getId()));

      // update fromBudgetTransaction (not allow change fromBudget)
      Transaction fromTransaction = new Transaction(transaction.getId(), transaction);
      db.update(TABLE_TRANSACTION, getContentValues(fromTransaction), Transaction.ID + " = ?",
              String.valueOf(transaction.getIdFromBudgetTransaction()));

      databaseTransaction.markSuccessful();
    } finally {
      databaseTransaction.end();
    }
    return iResult;
  }

  private ContentValues getContentValues(Transaction transaction) {
    ContentValues values = new ContentValues();
    values.put(Transaction.DAY, transaction.getDay());
    values.put(Transaction.MONTH, transaction.getMonth());
    values.put(Transaction.YEAR, transaction.getYear());
    values.put(Transaction.VALUE, transaction.getValue());
    values.put(Transaction.ID_CATEGORY, transaction.getIdCategory());
    values.put(Transaction.DESCRIPTION, transaction.getDescription());
    values.put(Transaction.ID_BUDGET, transaction.getIdBudget());
    values.put(Transaction.ID_FROM_BUDGET, transaction.getIdFromBudget());
    values.put(Transaction.ID_FROM_BUDGET_TRANSACTION, transaction.getIdFromBudgetTransaction());

    return values;
  }

  private Transaction getTransaction(@NonNull Cursor cursor) {
    return new Transaction(DbUtil.getLong(cursor, Transaction.ID),
        DbUtil.getInt(cursor, Transaction.DAY), DbUtil.getInt(cursor, Transaction.MONTH),
        DbUtil.getInt(cursor, Transaction.YEAR), DbUtil.getDouble(cursor, Transaction.VALUE),
        DbUtil.getInt(cursor, Transaction.ID_CATEGORY),
        DbUtil.getString(cursor, Transaction.DESCRIPTION), DbUtil.getInt(cursor, Category.COLOR),
        DbUtil.getInt(cursor, Category.ICON), DbUtil.getLong(cursor, Transaction.ID_BUDGET),
        DbUtil.getLong(cursor, Transaction.ID_FROM_BUDGET),DbUtil.getLong(cursor, Transaction.ID_FROM_BUDGET_TRANSACTION));
  }

  public Observable<Boolean> isEmpty() {
    return db.createQuery(TABLE_TRANSACTION, "SELECT "
        + Transaction.ID
        + " FROM "
        + TABLE_TRANSACTION)
        .map(super::getCursor).map(cursor -> {
          try {
            return cursor.getCount() == 0;
          } finally {
            cursor.close();
          }
        });
  }
}
