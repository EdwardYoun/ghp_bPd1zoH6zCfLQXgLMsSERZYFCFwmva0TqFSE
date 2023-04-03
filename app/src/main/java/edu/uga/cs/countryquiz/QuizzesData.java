package edu.uga.cs.countryquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class is facilitates storing and restoring quizzes stored.
 */
public class QuizzesData {

    public static final String DEBUG_TAG = "QuizzesData";

    // this is a reference to our database; it is used later to run SQL commands
    private SQLiteDatabase   db;
    private SQLiteOpenHelper quizzesDbHelper;
    private static final String[] allColumns = {
            QuizzesDBHelper.QUIZZES_COLUMN_ID,
            QuizzesDBHelper.QUIZZES_COLUMN_DATE,
            QuizzesDBHelper.QUIZZES_COLUMN_RESULT,
            QuizzesDBHelper.QUIZZES_COLUMN_NUM,
            QuizzesDBHelper.QUIZZES_COLUMN_QUESTION1,
            QuizzesDBHelper.QUIZZES_COLUMN_QUESTION2,
            QuizzesDBHelper.QUIZZES_COLUMN_QUESTION3,
            QuizzesDBHelper.QUIZZES_COLUMN_QUESTION4,
            QuizzesDBHelper.QUIZZES_COLUMN_QUESTION5,
            QuizzesDBHelper.QUIZZES_COLUMN_QUESTION6
    };

    public QuizzesData( Context context ) {
        this.quizzesDbHelper = QuizzesDBHelper.getInstance( context );
    }

    // Open the database
    public void open() {
        db = quizzesDbHelper.getWritableDatabase();
        Log.d( DEBUG_TAG, "QuizzesData: db open" );
    }

    // Close the database
    public void close() {
        if( quizzesDbHelper != null ) {
            quizzesDbHelper.close();
            Log.d(DEBUG_TAG, "QuizzesData: db closed");
        }
    }

    public boolean isDBOpen()
    {
        return db.isOpen();
    }

    // Retrieve all quizzes and return them as a List.
    // This is how we restore persistent objects stored as rows in the quizzes table in the database.
    // For each retrieved row, we create a new Quiz (Java POJO object) instance and add it to the list.
    public List<Quiz> retrieveAllQuizzes() {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;

        try {
            // Execute the select query and get the Cursor to iterate over the retrieved rows
            cursor = db.query( QuizzesDBHelper.TABLE_QUIZZES, allColumns,
                    null, null, null, null, null );

            // collect all quizzes into a List
            if( cursor != null && cursor.getCount() > 0 ) {

                while( cursor.moveToNext() ) {

                    if( cursor.getColumnCount() >= 10) {

                        // get all attribute values of this quiz
                        columnIndex = cursor.getColumnIndex( QuizzesDBHelper.QUIZZES_COLUMN_ID );
                        long id = cursor.getLong( columnIndex );
                        columnIndex = cursor.getColumnIndex( QuizzesDBHelper.QUIZZES_COLUMN_DATE );
                        String date = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( QuizzesDBHelper.QUIZZES_COLUMN_RESULT );
                        String result = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( QuizzesDBHelper.QUIZZES_COLUMN_NUM );
                        long num = cursor.getLong( columnIndex );
                        columnIndex = cursor.getColumnIndex( QuizzesDBHelper.QUIZZES_COLUMN_QUESTION1 );
                        Question question1 = cursor.get


                        // create a new Quiz object and set its state to the retrieved values
                        Quiz quiz = new Quiz( date, result, num, question1, question2, question3, question4, question5, question6 );
                        quiz.setId(id); // set the id (the primary key) of this object
                        // add it to the list
                        quizzes.add( quiz );
                        Log.d(DEBUG_TAG, "Retrieved Quiz: " + quiz);
                    }
                }
            }
            if( cursor != null )
                Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
            else
                Log.d( DEBUG_TAG, "Number of records from DB: 0" );
        }
        catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved countries
        return quizzes;
    }

    // Store a new quiz in the database.
    public Quiz storeQuiz( Quiz quiz ) {

        // Prepare the values for all of the necessary columns in the table
        // and set their values to the variables of the Quiz argument.
        // This is how we are providing persistence to a Quiz (Java object) instance
        // by storing it as a new row in the database table representing countries.
        ContentValues values = new ContentValues();
        values.put( CountriesDBHelper.COUNTRIES_COLUMN_NAME, quiz.getName());
        values.put( CountriesDBHelper.COUNTRIES_COLUMN_CONTINENT, quiz.getContinent() );

        // Insert the new row into the database table;
        // The id (primary key) is automatically generated by the database system
        // and returned as from the insert method call.
        long id = db.insert( QuizzesDBHelper.TABLE_QUIZZES, null, values );

        // store the id (the primary key) in the Quiz instance, as it is now persistent
        quiz.setId( id );

        Log.d( DEBUG_TAG, "Stored new quiz with id: " + String.valueOf( quiz.getId() ) );

        return quiz;
    }
}
